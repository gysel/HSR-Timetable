package ch.scythe.hsr.api;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.xml.SaxTimetableParser;

public class TimeTableAPI {
	// _SOAP Webservice info
	private static final String URL = "https://stundenplanws.hsr.ch:4434/Service/SASTimeTable.asmx";
	private static final String NAMESPACE = "\"http://tempuri.org/GetOwnTimeTableOfDay\"";
	// _Cache details
	private static final String TIMETABLE_CACHE_XML = "timetable_cache.xml";
	private static final String TIMETABLE_CACHE_INFO = "timetable_cache.txt";
	// _Helper
	private final SaxTimetableParser parser = new SaxTimetableParser();
	private final Context context;

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
	 * @throws ParseException
	 *             If result contains not parsable data.
	 */
	public Day retrieve(Date date, String login, String password, boolean forceRequest) throws RequestException {
		Day result = null;

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormatter.format(date);

		// create cache if the cache is not present yet
		if (forceRequest || !cacheFilesExist(context.fileList(), TIMETABLE_CACHE_XML, TIMETABLE_CACHE_INFO)) {
			updateCache(dateString, login, password);

		}

		FileInputStream cachedRequest = null;
		try {
			// read the cached day
			String cachedDay = getCachedDay();

			// update the cache if necessary
			if (!dateString.equals(cachedDay)) {
				updateCache(dateString, login, password);
			}

			// parse the timetable from the cache
			cachedRequest = context.openFileInput(TIMETABLE_CACHE_XML);
			List<Lesson> lessons = parser.parse(cachedRequest);
			result = new Day(lessons, date);

		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(cachedRequest);
		}

		return result;
	}

	private String getCachedDay() throws RequestException {
		String cacheInfo = null;
		FileInputStream cachedRequestInfo = null;
		try {
			cachedRequestInfo = context.openFileInput(TIMETABLE_CACHE_INFO);
			BufferedReader reader = new BufferedReader(new InputStreamReader(cachedRequestInfo));
			cacheInfo = reader.readLine();
		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(cachedRequestInfo);
		}
		return cacheInfo;
	}

	private void updateCache(String dateString, String login, String password) throws RequestException {
		FileOutputStream xmlCacheOutputStream = null;
		FileOutputStream cacheInfoOutputStream = null;
		InputStream xmlInputStream = null;
		try {
			xmlInputStream = readTimeTableFromServer(dateString, login, password);
			xmlCacheOutputStream = context.openFileOutput(TIMETABLE_CACHE_XML, Context.MODE_PRIVATE);
			cacheInfoOutputStream = context.openFileOutput(TIMETABLE_CACHE_INFO, Context.MODE_PRIVATE);

			int c;
			while ((c = xmlInputStream.read()) != -1) {
				xmlCacheOutputStream.write(c);
			}
			cacheInfoOutputStream.write(dateString.getBytes());
		} catch (FileNotFoundException e) {
			throw new RequestException(e);
		} catch (IOException e) {
			throw new RequestException(e);
		} finally {
			safeCloseStream(xmlCacheOutputStream);
			safeCloseStream(xmlInputStream);
			safeCloseStream(cacheInfoOutputStream);
		}
	}

	private InputStream readTimeTableFromServer(String dateString, String login, String password)
			throws RequestException {

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
			throw new RequestException(e);
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
		result += "      <tem:GetOwnTimeTableOfDay>";
		result += "         <!--Optional:-->";
		result += "         <tem:strUserName>" + login + "</tem:strUserName>";
		result += "         <!--Optional:-->";
		result += "         <tem:strPassword>" + password + "</tem:strPassword>";
		result += "         <tem:reqDate>" + date + "</tem:reqDate>";
		result += "      </tem:GetOwnTimeTableOfDay>";
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
