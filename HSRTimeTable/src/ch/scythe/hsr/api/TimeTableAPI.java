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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import net.iharder.base64.Base64;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import android.content.Context;
import android.os.Build;
import ch.scythe.hsr.api.ui.DataAssembler;
import ch.scythe.hsr.api.ui.UiWeek;
import ch.scythe.hsr.error.ResponseParseException;
import ch.scythe.hsr.error.ServerConnectionException;
import ch.scythe.hsr.helper.AndroidHelper;
import ch.scythe.hsr.json.GsonParser;
import ch.scythe.hsr.json.JsonTimetableWeek;

public class TimeTableAPI {
	// _Webservice
	private static final String URL = "https://stundenplanws.hsr.ch:4443/api/Timetable/";
	// _API Headers
	private final String userAgent;
	private final String operatingSystem;

	public TimeTableAPI(Context context) {
		String appVersionName = AndroidHelper.getAppVersionName(context);
		userAgent = "HSRAndroidTimetable/" + appVersionName;
		operatingSystem = "Android/" + Build.VERSION.RELEASE;
	}

	/**
	 * @throws RequestException
	 *             If the timetable could not be successfully requested.
	 * @throws ResponseParseException
	 *             If result contains not parsable data.
	 * @throws ServerConnectionException
	 *             If the connection to the server if aborted
	 */
	public UiWeek retrieve(Date date, String login, String password) throws RequestException, ResponseParseException, ServerConnectionException {
		UiWeek result = new UiWeek();
		try {

			HttpGet httppost = createHttpGet(URL + login, login, password);
			HttpClient httpclient = new DefaultHttpClient();

			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
			InputStream jsonStream = null;
			int httpStatus = httpResponse.getStatusLine().getStatusCode();
			if (httpStatus == 200) {
				jsonStream = httpResponse.getEntity().getContent();
			} else {
				throw new RequestException("Request not successful. \nHTTP Status: " + httpStatus);
			}

			JsonTimetableWeek serverData = new GsonParser().parse(jsonStream);

			result = DataAssembler.convert(serverData);

		} catch (UnsupportedEncodingException e) {
			throw new RequestException(e);
		} catch (ClientProtocolException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new ServerConnectionException(e);
		}

		return result;
	}

	private HttpGet createHttpGet(String url, String login, String password) throws UnsupportedEncodingException {
		String basicAuth = "Basic " + Base64.encodeBytes((login + ":" + password).getBytes());
		HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type", "text/json;charset=UTF-8");
		get.setHeader("User-Agent", userAgent); // TODO add app version
		get.setHeader("Operating-System", operatingSystem); // TODO add android version
		get.setHeader("Authorization", basicAuth);
		return get;
	}
}
