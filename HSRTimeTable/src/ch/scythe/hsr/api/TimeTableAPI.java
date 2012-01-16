/* 
 * Copyright (C) 2011 Michi Gysel <michael.gysel@gmail.com>
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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.error.ResponseParseException;
import ch.scythe.hsr.error.ServerConnectionException;
import ch.scythe.hsr.helper.DateHelper;
import ch.scythe.hsr.xml.SaxTimetableParser;

public class TimeTableAPI {
	// _SOAP Webservice info
	private static final String URL = "https://stundenplanws.hsr.ch:4434/Service/SASTimeTable.asmx";
	private static final String METHOD = "GetOwnTimeTableOfDate";
	private static final String NAMESPACE = "\"http://tempuri.org/" + METHOD + "\"";
	// _Cache details
	private static final String TIMETABLE_CACHE_XML = "timetable_cache.xml";
	private static final String TIMETABLE_CACHE_TIMESTAMP = "timetable_timestamp.txt";
	// _Helper
	private final SaxTimetableParser parser = new SaxTimetableParser();
	private final Context context;
	// _Logging details
	private static final String LOGGING_TAG = "TimeTableAPI";

	public TimeTableAPI(Context context) {
		this.context = context;
	}

	/**
	 * TODO implement Exception handling
	 * 
	 * @param forceRequest
	 *            Skips the caching mechanism and loads the data always from the
	 *            web.
	 * 
	 * @throws RequestException
	 *             If the timetable could not be successfully requested.
	 * @throws ResponseParseException
	 *             If result contains not parsable data.
	 * @throws ServerConnectionException
	 *             If the connection to the server if aborted
	 */
	public TimetableWeek retrieve(Date requestedDate, String login, String password, boolean forceRequest)
			throws RequestException, ResponseParseException, ServerConnectionException {
		TimetableWeek result = null;

		// create cache if the cache is not present yet
		if (forceRequest || !cacheFilesExist(context.fileList(), TIMETABLE_CACHE_XML, TIMETABLE_CACHE_TIMESTAMP)) {
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
			cachedRequest = context.openFileInput(TIMETABLE_CACHE_XML);
			long before = System.currentTimeMillis();
			result = parser.parse(cachedRequest);
			Log.i(LOGGING_TAG, "Parsed xml data in " + new Long(System.currentTimeMillis() - before) + "ms.");

			result.setLastUpdate(cacheTimestamp);

		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(cachedRequest);
		}

		return result;
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

	private void updateCache(String dateString, Date cacheTimestamp, String login, String password)
			throws RequestException, ServerConnectionException {
		FileOutputStream xmlCacheOutputStream = null;
		DataOutputStream cacheTimestampOutputStream = null;
		InputStream xmlInputStream = null;
		long before = System.currentTimeMillis();
		try {
			xmlInputStream = readTimeTableFromServer(dateString, login, password);
			xmlCacheOutputStream = context.openFileOutput(TIMETABLE_CACHE_XML, Context.MODE_PRIVATE);
			cacheTimestampOutputStream = new DataOutputStream(context.openFileOutput(TIMETABLE_CACHE_TIMESTAMP,
					Context.MODE_PRIVATE));

			int c;
			while ((c = xmlInputStream.read()) != -1) {
				xmlCacheOutputStream.write(c);
			}
			cacheTimestampOutputStream.writeLong(new Date().getTime());
		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(xmlCacheOutputStream);
			safeCloseStream(xmlInputStream);
			safeCloseStream(cacheTimestampOutputStream);
		}
		Log.i(LOGGING_TAG, "Read data from the server in " + (System.currentTimeMillis() - before) + "ms.");
	}

	private InputStream readTimeTableFromServer(String dateString, String login, String password)
			throws RequestException, ServerConnectionException {

		InputStream result;
		try {
			String xml = createSoapXml(dateString, login, password);
			HttpPost httppost = createHttpPost(xml);

			HttpClient httpclient = new DefaultHttpClient();
			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);

			int httpStatus = httpResponse.getStatusLine().getStatusCode();
			if (httpStatus == 200) {
				result = httpResponse.getEntity().getContent();
			} else {
				throw new RequestException("Request not successful. \nHTTP Status: " + httpStatus);
			}

		} catch (UnsupportedEncodingException e) {
			throw new RequestException(e);
		} catch (ClientProtocolException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new ServerConnectionException(e);
		}
		return result;
	}

	private HttpPost createHttpPost(String xml) throws UnsupportedEncodingException {
		StringEntity se = new StringEntity(xml, HTTP.UTF_8);
		se.setContentType("text/xml");
		HttpPost httppost = new HttpPost(URL);
		httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
		httppost.setHeader("SOAPAction", NAMESPACE);
		httppost.setEntity(se);
		return httppost;
	}

	private String createSoapXml(String date, String login, String password) {
		String result = "";
		result += "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">";
		result += "   <soapenv:Header/>";
		result += "   <soapenv:Body>";
		result += "      <tem:" + METHOD + ">";
		result += "         <!--Optional:-->";
		result += "         <tem:strUserName>" + login + "</tem:strUserName>";
		result += "         <!--Optional:-->";
		result += "         <tem:strPassword>" + password + "</tem:strPassword>";
		result += "         <tem:reqDate>" + date + "</tem:reqDate>";
		result += "      </tem:" + METHOD + ">";
		result += "   </soapenv:Body>";
		result += "</soapenv:Envelope>";
		return result;
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
