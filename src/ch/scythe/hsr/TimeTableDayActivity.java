package ch.scythe.hsr;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ch.scythe.hsr.api.RequestException;
import ch.scythe.hsr.api.TimeTableAPI;
import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimeUnit;

public class TimeTableDayActivity extends Activity {
	// _UI
	private TextView resultbox;
	private TextView datebox;
	private SharedPreferences preferences;
	private ProgressDialog progress;
	// _Helpers
	private final DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
	// _State
	private Boolean dataTaskRunning = false;
	private Date currentDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_day);

		resultbox = (TextView) findViewById(R.id.result);
		datebox = (TextView) findViewById(R.id.date_value);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.setIndeterminate(true);
		progress.setCancelable(false);

		currentDate = new Date();

		startRequest();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainmenu, menu);
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
			startRequest();
			break;

		}
		return true;
	}

	public void showPrevDay(View view) {
		currentDate = addDays(currentDate, -1);
		startRequest();
	}

	public void showToday(View view) {
		currentDate = new Date();
		startRequest();
	}

	public void showNextDay(View view) {
		currentDate = addDays(currentDate, 1);
		startRequest();
	}

	private Date addDays(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);
		return c.getTime();
	}

	private synchronized void startRequest() {
		synchronized (dataTaskRunning) {
			if (Boolean.FALSE.equals(dataTaskRunning)) {

				String login = preferences.getString(getString(R.string.key_login), null);
				String password = preferences.getString(getString(R.string.key_password), null);

				if (login == null || password == null) {
					resultbox.setText("Please set login and password in preferences.");
				} else {
					progress.show();
					resultbox.setText("");
					dataTaskRunning = true;
					new FetchDataTask().execute(currentDate, login, password);
				}
			}
		}
	}

	private void updateTimeTable(Boolean hasError, String errorMessage, Day day) {

		String dateLocale = mediumDateFormat.format(day.getDate());
		datebox.setText(dateLocale);

		String result = "";
		if (hasError) {
			result = errorMessage;
		} else {
			StringBuilder output = new StringBuilder();
			for (Entry<TimeUnit, Lesson> entry : day.getLessons().entrySet()) {
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
			result = output.toString();
		}

		resultbox.setText(result);

		synchronized (dataTaskRunning) {
			progress.hide();
			dataTaskRunning = false;
		}

	}

	class FetchDataTask extends AsyncTask<Object, Integer, Day> {

		private final TimeTableAPI api = new TimeTableAPI();
		private Boolean hasError = false;
		private String errorMessage = null;

		@Override
		protected Day doInBackground(Object... params) {
			Date date = (Date) params[0];
			String login = (String) params[1];
			String password = (String) params[2];

			Day result = null;
			try {
				result = api.retrieve(date, login, password);
			} catch (RequestException e) {
				e.printStackTrace();
				hasError = true;
				errorMessage = e.getMessage();
				result = new Day(currentDate);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Day day) {
			updateTimeTable(hasError, errorMessage, day);
		}
	}

}
