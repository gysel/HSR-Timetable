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
package ch.scythe.hsr;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ch.scythe.hsr.api.RequestException;
import ch.scythe.hsr.api.TimeTableAPI;
import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.enumeration.WeekDay;
import ch.scythe.hsr.helper.DateHelper;

public class TimeTableActivity extends FragmentActivity {
	// _Pager
	public static final int NUM_ITEMS = 6;
	private ViewPager dayPager;
	private MyAdapter fragmentPageAdapter;
	// _UI
	// private TextView statusMessage;
	private TextView datebox;
	private TextView weekbox;
	// private TableLayout timeTable;
	private SharedPreferences preferences;
	private static final int DIALOG_NO_USER_PASS = 0;
	private static final int DIALOG_ERROR_PASS = 1;
	// _State
	private Boolean dataTaskRunning = false;
	// private Day day = null;
	public TimetableWeek week = new TimetableWeek();
	private WeekDay currentWeekDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_main);

		currentWeekDay = WeekDay.getByDate(new Date());

		fragmentPageAdapter = new MyAdapter(getSupportFragmentManager());

		dayPager = (ViewPager) findViewById(R.id.day_pager);
		dayPager.setAdapter(fragmentPageAdapter);

		datebox = (TextView) findViewById(R.id.date_value);
		weekbox = (TextView) findViewById(R.id.week_value);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Date date = new Date();
		weekbox.setText(DateHelper.formatToWeekNumber(date));
		datebox.setText(DateHelper.formatToUserFriendlyFormat(date));

		TimetableWeek lastInstance = (TimetableWeek) getLastCustomNonConfigurationInstance();
		if (lastInstance == null) {
			startRequest(date, false);
			scrollToDay(currentWeekDay);
		} else {
			// there was a screen orientation change. we can just continue...
			week = lastInstance;
		}

	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return week;
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
			startActivity(new Intent(this, UserPreferencesActivity.class));
			break;
		case R.id.refresh:
			startRequest(new Date(), true);
			break;
		}
		return true;
	}

	public void showToday(View view) {
		scrollToDay(currentWeekDay);
	}

	private void scrollToDay(WeekDay weekDay) {
		dayPager.setCurrentItem(weekDay.getId() - 1);
	}

	private synchronized void startRequest(Date date, boolean forceRequest) {
		String login = preferences.getString(getString(R.string.key_login), null);
		String password = preferences.getString(getString(R.string.key_password), null);

		if (inNullOrEmpty(login) || inNullOrEmpty(password)) {
			showDialog(DIALOG_NO_USER_PASS);
		} else {
			TimeTableAPI api = new TimeTableAPI(getApplicationContext());
			try {
				week = api.retrieve(date, login, password, forceRequest);
			} catch (RequestException e) {
				showDialog(DIALOG_ERROR_PASS);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_NO_USER_PASS:
			builder.setMessage("Please enter your username and password in the preferences.").setCancelable(true)
					.setPositiveButton("Open preferences", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(TimeTableActivity.this, UserPreferencesActivity.class));
						}
					}).setNegativeButton("Cancel", null);
			result = builder.create();
			break;
		case DIALOG_ERROR_PASS:
			// TODO add option to retry?
			builder.setMessage("Error while fetching data.").setPositiveButton("Ok", null);
			result = builder.create();
			break;
		default:
			result = null;
		}
		return result;
	}

	private boolean inNullOrEmpty(String login) {
		return login == null || login.isEmpty();
	}

	class FetchDataTask extends AsyncTask<Object, Integer, TimetableWeek> {

		private final TimeTableAPI api = new TimeTableAPI(TimeTableActivity.this);
		private Boolean hasError = false;
		private String errorMessage = null;

		@Override
		protected TimetableWeek doInBackground(Object... params) {
			Date date = (Date) params[0];
			String login = (String) params[1];
			String password = (String) params[2];
			boolean forceRequest = (Boolean) params[3];

			TimetableWeek result = null;
			try {
				result = api.retrieve(date, login, password, forceRequest);
			} catch (RequestException e) {
				e.printStackTrace();
				hasError = true;
				errorMessage = e.getMessage();
				result = new TimetableWeek(new ArrayList<Day>());
			}
			return result;
		}

		@Override
		protected void onPostExecute(TimetableWeek week) {
			TimeTableActivity.this.week = week;

			synchronized (dataTaskRunning) {
				dataTaskRunning = false;
			}
		}
	}

	public class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			DayFragment fragment = new DayFragment();

			WeekDay weekDay = WeekDay.getById(position + 1);
			Date date = DateHelper.addDays(new Date(), weekDay.getId() - currentWeekDay.getId());

			Bundle args = new Bundle();
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_DATA, week);
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_WEEKDAY, weekDay);
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_DATE, date);
			fragment.setArguments(args);

			return fragment;
		}
	}

}
