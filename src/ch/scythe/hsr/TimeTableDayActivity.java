package ch.scythe.hsr;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.xml.SaxTimetableParser;

public class TimeTableDayActivity extends Activity {
	private static final String URL = "https://stundenplanws.hsr.ch:4434/Service/SASTimeTable.asmx";
	// _Helper
	private SaxTimetableParser parser = new SaxTimetableParser();
	// _UI
	private TextView resultbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_day);

		resultbox = (TextView) findViewById(R.id.result);

		Bundle params = getIntent().getExtras();

		String login = (String) params.get(LoginActivity.PARAM_LOGIN);
		String password = (String) params.get(LoginActivity.PARAM_PASSWORD);
		// String requestDate = (String) params
		// .get(LoginActivity.PARAM_REQUEST_DATE);

		resultbox.setText("Loading timetable");

		requestTimetable(login, password, null);

	}

	private void requestTimetable(String login, String password, String date) {

		date = "2011-09-28";

		HttpPost httppost = new HttpPost(URL);
		try {
			String xml = createSoapXml(date, login, password);
			StringEntity se = new StringEntity(xml, HTTP.UTF_8);

			se.setContentType("text/xml");
			httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			httppost.setHeader("SOAPAction",
					"\"http://tempuri.org/GetOwnTimeTableOfDay\"");

			httppost.setEntity(se);

			// ProgressDialog dialog = ProgressDialog.show(
			// TimeTableDayActivity.this, "", "Loading. Please wait...",
			// true);
			// dialog.show();

			HttpClient httpclient = new DefaultHttpClient();
			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient
					.execute(httppost);

			// dialog.hide();

			int httpStatus = httpResponse.getStatusLine().getStatusCode();
			if (httpStatus == 200) {
				List<Lesson> lessons = parser.parse(httpResponse.getEntity()
						.getContent());
				resultbox.setText("Date: " + date + "\nLessons: "
						+ lessons.toString());
			} else {
				resultbox.setText("HTTP Status: " + httpStatus);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		result += "         <tem:strPassword>" + password
				+ "</tem:strPassword>";
		result += "         <tem:reqDate>" + date + "</tem:reqDate>";
		result += "      </tem:GetOwnTimeTableOfDay>";
		result += "   </soapenv:Body>";
		result += "</soapenv:Envelope>";
		return result;
	}
}
