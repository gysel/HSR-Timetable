/* 
 * Copyright (C) 2011 - 2012 Michi Gysel <michael.gysel@gmail.com>
 *
 * This file is part of the HSR Timetable.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.scythe.hsr.api;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.iharder.base64.Base64;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import ch.scythe.hsr.api.ui.DataAssembler;
import ch.scythe.hsr.api.ui.UiWeek;
import ch.scythe.hsr.error.AccessDeniedException;
import ch.scythe.hsr.error.ResponseParseException;
import ch.scythe.hsr.error.ServerConnectionException;
import ch.scythe.hsr.helper.AndroidHelper;
import ch.scythe.hsr.helper.DateHelper;
import ch.scythe.hsr.json.GsonParser;
import ch.scythe.hsr.json.JsonTimetableWeek;

public class TimeTableAPI {
	// _SOAP Webservice info
	private static final String URL = "https://stundenplanws.hsr.ch:4443/api/";
	private static final String METHOD_GET_TIMETABLE = "Timetable/";
	private static final String METHOD_GET_TIMEPERIOD = "Timeperiod/";

	// _Cache details
	private static final String TIMETABLE_CACHE_SERIALIZED = "timetable_cache.ser";
	private static final String TIMETABLE_CACHE_TIMESTAMP = "timetable_timestamp.txt";
	// _Helper
	private final Context context;
	// _Logging details
	private static final String LOGGING_TAG = "TimeTableAPI";
	// _API Headers
	private final String userAgent;
	private final String operatingSystem;

	public TimeTableAPI(Context context) {
		this.context = context;
		String appVersionName = AndroidHelper.getAppVersionName(context);
		userAgent = "HSRAndroidTimetable/" + appVersionName;
		operatingSystem = "Android/" + Build.VERSION.RELEASE;

	}

	/** @param forceRequest
	 *            Skips the caching mechanism and loads the data always from the web.
	 * 
	 * @throws RequestException
	 *             If the timetable could not be successfully requested.
	 * @throws ResponseParseException
	 *             If result contains not parsable data.
	 * @throws ServerConnectionException
	 *             If the connection to the server if aborted
	 * @throws AccessDeniedException
	 *             If the server returns a 401 (HTTP Error 401 Unauthorized Explained) */

	public UiWeek retrieve(Date requestedDate, String login, String password, boolean forceRequest) throws RequestException, ResponseParseException,
			ServerConnectionException, AccessDeniedException {

		UiWeek result = null;

		// create cache if the cache is not present yet
		if (cacheUpdateRequired(forceRequest)) {
			if (forceRequest) {
				Log.i(LOGGING_TAG, "Started forced cache reloading.");
			} else {
				Log.i(LOGGING_TAG, "Started initial cache loading.");
			}
			String dateString = DateHelper.formatToTechnicalFormat(requestedDate);
			updateCache(dateString, null, login, password);
		}

		FileInputStream cachedRequest = null;
		try {
			// read the cached data
			Date cacheTimestamp = getCacheTimestamp();

			// parse the timetable from the cache
			long before = System.currentTimeMillis();
			cachedRequest = context.openFileInput(TIMETABLE_CACHE_SERIALIZED);
			ObjectInputStream dataStream = new ObjectInputStream(cachedRequest);
			result = (UiWeek) dataStream.readObject();
			Log.i(LOGGING_TAG, "Deserialized data in " + (System.currentTimeMillis() - before) + "ms.");

			result.setLastUpdate(cacheTimestamp);

		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} catch (StreamCorruptedException e) {
			throw new ResponseParseException(e);
		} catch (IOException e) {
			throw new ResponseParseException(e);
		} catch (ClassNotFoundException e) {
			throw new ResponseParseException(e);
		} finally {
			safeCloseStream(cachedRequest);
		}

		return result;
	}

	public boolean validateCredentials(String login, String password) throws ServerConnectionException {
		boolean result = false;
		try {
			HttpGet get = createHttpGet(URL + METHOD_GET_TIMEPERIOD, login, password);
			HttpClient httpclient = new DefaultHttpClient();

			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(get);
			int httpStatus = httpResponse.getStatusLine().getStatusCode();

			result = HttpStatus.SC_OK == httpStatus;

		} catch (Exception e) {
			throw new ServerConnectionException(e);
		}

		return result;
	}

	public boolean retrieveRequiresBlockingCall(boolean forceRequest) {
		return true;
	}

	private boolean cacheUpdateRequired(boolean forceRequest) {
		return forceRequest || !cacheFilesExist(context.fileList(), TIMETABLE_CACHE_SERIALIZED, TIMETABLE_CACHE_TIMESTAMP);
	}

	private Date getCacheTimestamp() throws RequestException {
		Date cacheTimestamp = null;
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(context.openFileInput(TIMETABLE_CACHE_TIMESTAMP));
			cacheTimestamp = new Date(inputStream.readLong());
		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(inputStream);
		}
		return cacheTimestamp;
	}

	private void updateCache(String dateString, Date cacheTimestamp, String login, String password) throws RequestException, ServerConnectionException,
			ResponseParseException, AccessDeniedException {

		Log.i(LOGGING_TAG, "Starting to read data from the server.");
		long before = System.currentTimeMillis();

		try {

			HttpGet get = createHttpGet(URL + METHOD_GET_TIMETABLE + login, login, password);
			HttpClient httpclient = new DefaultHttpClient();

			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(get);
			InputStream jsonStream = null;
			int httpStatus = httpResponse.getStatusLine().getStatusCode();
			if (httpStatus == HttpStatus.SC_OK) {
				jsonStream = httpResponse.getEntity().getContent();
			} else if (httpStatus == HttpStatus.SC_UNAUTHORIZED) {
				throw new AccessDeniedException();
			} else {
				throw new RequestException("Request not successful. \nHTTP Status: " + httpStatus);
			}

			Log.i(LOGGING_TAG, "Finished reading from server.");

			// convert JSON to Java objects
			JsonTimetableWeek serverData = new GsonParser().parse(jsonStream);
			UiWeek uiWeek = DataAssembler.convert(serverData);

			// open streams to cache the files
			DataOutputStream cacheTimestampOutputStream = new DataOutputStream(context.openFileOutput(TIMETABLE_CACHE_TIMESTAMP, Context.MODE_PRIVATE));
			FileOutputStream xmlCacheOutputStream = context.openFileOutput(TIMETABLE_CACHE_SERIALIZED, Context.MODE_PRIVATE);

			// write data to streams
			ObjectOutputStream out = new ObjectOutputStream(xmlCacheOutputStream);
			out.writeObject(uiWeek);
			cacheTimestampOutputStream.writeLong(new Date().getTime());

			safeCloseStream(xmlCacheOutputStream);
			safeCloseStream(cacheTimestampOutputStream);

		} catch (UnsupportedEncodingException e) {
			throw new RequestException(e);
		} catch (IllegalStateException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new ServerConnectionException(e);
		}

		Log.i(LOGGING_TAG, "Read and parsed data from the server in " + (System.currentTimeMillis() - before) + "ms.");
	}

	private HttpGet createHttpGet(String url, String login, String password) throws UnsupportedEncodingException {
		String basicAuth = "Basic " + Base64.encodeBytes((login + ":" + password).getBytes());
		HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type", "text/json;charset=UTF-8");
		get.setHeader("User-Agent", userAgent);
		get.setHeader("Operating-System", operatingSystem);
		get.setHeader("Authorization", basicAuth);
		return get;
	}

	private void safeCloseStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	private boolean cacheFilesExist(String[] existingFiles, String... filesToCheck) {
		boolean result = true;
		List<String> existingFilesList = Arrays.asList(existingFiles);
		for (String fileToCheck : filesToCheck) {
			if (!existingFilesList.contains(fileToCheck)) {
				result = false;
				break;
			}
		}

		return result;
	}
}
