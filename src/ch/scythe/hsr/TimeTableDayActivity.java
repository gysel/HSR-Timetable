package ch.scythe.hsr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimeUnit;
import ch.scythe.hsr.xml.SaxTimetableParser;

public class TimeTableDayActivity extends Activity {
	private static final String URL = "https://stundenplanws.hsr.ch:4434/Service/SASTimeTable.asmx";
	// _Helper
	private SaxTimetableParser parser = new SaxTimetableParser();
	// _UI
	private TextView resultbox;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_day);

		resultbox = (TextView) findViewById(R.id.result);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		requestTimetable();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			Intent i = new Intent(this, UserPreferencesActivity.class);
			startActivity(i);
			break;
		case R.id.refresh:
			requestTimetable();
			break;

		}
		return true;
	}

	private void requestTimetable() {

		String login = preferences.getString(getString(R.string.key_login),
				null);
		String password = preferences.getString(
				getString(R.string.key_password), null);
		String date = null;

		if (login == null || password == null) {
			resultbox.setText("Please set login and password in preferences.");
		} else {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			date = dateFormatter.format(new Date());

			HttpPost httppost = new HttpPost(URL);
			try {
				String xml = createSoapXml(date, login, password);
				StringEntity se = new StringEntity(xml, HTTP.UTF_8);

				se.setContentType("text/xml");
				httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
				httppost.setHeader("SOAPAction",
						"\"http://tempuri.org/GetOwnTimeTableOfDay\"");

				httppost.setEntity(se);

				HttpClient httpclient = new DefaultHttpClient();
				BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient
						.execute(httppost);

				int httpStatus = httpResponse.getStatusLine().getStatusCode();
				if (httpStatus == 200) {
					List<Lesson> lessons = parser.parse(httpResponse
							.getEntity().getContent());
					Day day = new Day(lessons);

					StringBuilder output = new StringBuilder();
					output.append("Date: ").append(date).append("\n\n");
					for (Entry<TimeUnit, Lesson> entry : day.getLessons()
							.entrySet()) {
						TimeUnit timeUnit = entry.getKey();
						Lesson lesson = entry.getValue();
						output.append(timeUnit.getStartTime()).append(" - ");
						output.append(timeUnit.getEndTime()).append("\n");
						if (lesson != null) {
							output.append(lesson.getIdentifier()).append(" (");
							output.append(lesson.getType()).append(")\n");
							output.append("Room: ").append(lesson.getRoom());
						} else {
							output.append("-");
						}
						output.append("\n\n");
					}
					resultbox.setText(output.toString());

				} else {
					resultbox.setText("Request not successful. \nHTTP Status: "
							+ httpStatus);
				}

				// } catch (ClientProtocolException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				resultbox.setText("Error!\n" + e.getMessage());
			}

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
