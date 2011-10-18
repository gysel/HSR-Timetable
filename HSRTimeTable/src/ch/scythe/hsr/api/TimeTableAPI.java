package ch.scythe.hsr.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.xml.SaxTimetableParser;

public class TimeTableAPI {
	// _SOAP Webservice info
	private static final String URL = "https://stundenplanws.hsr.ch:4434/Service/SASTimeTable.asmx";
	private static final String NAMESPACE = "\"http://tempuri.org/GetOwnTimeTableOfDay\"";
	// _Helper
	private final SaxTimetableParser parser = new SaxTimetableParser();

	/**
	 * TODO implement Exception handling
	 * 
	 * @throws RequestException
	 *             if the timetable could not be successfully requested.
	 * @throws ParseException
	 *             if result contains not parsable data.
	 */
	public Day retrieve(Date date, String login, String password) throws RequestException {
		Day result = null;

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormatter.format(date);

		try {
			String xml = createSoapXml(dateString, login, password);
			HttpPost httppost = createHttpPost(xml);

			HttpClient httpclient = new DefaultHttpClient();
			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);

			int httpStatus = httpResponse.getStatusLine().getStatusCode();
			if (httpStatus == 200) {
				List<Lesson> lessons = parser.parse(httpResponse.getEntity().getContent());

				result = new Day(lessons, date);
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
}
