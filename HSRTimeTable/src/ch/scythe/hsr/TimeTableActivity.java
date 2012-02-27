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
package ch.scythe.hsr;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ch.scythe.hsr.api.RequestException;
import ch.scythe.hsr.api.TimeTableAPI;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.enumeration.WeekDay;
import ch.scythe.hsr.error.ResponseParseException;
import ch.scythe.hsr.error.ServerConnectionException;
import ch.scythe.hsr.helper.DateHelper;

public class TimeTableActivity extends FragmentActivity {
	// _Pager
	public static final int NUM_ITEMS = 6;
	private ViewPager dayPager;
	private MyAdapter fragmentPageAdapter;
	// _UI
	private TextView datebox;
	private TextView weekbox;
	private ProgressDialog progress;
	private SharedPreferences preferences;
	private static final int DIALOG_NO_USER_PASS = 0;
	private static final int DIALOG_ERROR_FETCH = 1;
	private static final int DIALOG_ERROR_CONNECT = 2;
	private static final int DIALOG_ERROR_PARSE = 3;
	private static final int DIALOG_ABOUT = 4;
	// _State
	public TimetableWeek week = new TimetableWeek();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_main);

		fragmentPageAdapter = new MyAdapter(getSupportFragmentManager());

		dayPager = (ViewPager) findViewById(R.id.day_pager);
		dayPager.setAdapter(fragmentPageAdapter);

		datebox = (TextView) findViewById(R.id.date_value);
		weekbox = (TextView) findViewById(R.id.week_value);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Date date = new Date();
		weekbox.setText(DateHelper.formatToWeekNumber(date));

		TimetableWeek lastInstance = (TimetableWeek) getLastCustomNonConfigurationInstance();
		if (lastInstance == null) {
			startRequest(date, false);
		} else {
			// there was a screen orientation change.
			// we can don't have to create the ui...
			week = lastInstance;
			Date lastUpdate = week.getLastUpdate();
			String lastUpdateAsString = (lastUpdate == null) ? getString(R.string.default_novalue) : DateHelper
					.formatToUserFriendlyFormat(lastUpdate);
			// lastUpdate can be null just after entering the credentials
			datebox.setText(lastUpdateAsString);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		scrollToToday();
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
		case R.id.about:
			showDialog(DIALOG_ABOUT);
			break;
		}
		return true;
	}

	public void showToday(View view) {
		scrollToToday();
	}

	private void scrollToToday() {
		WeekDay today = WeekDay.getByDate(new Date());
		if (today == WeekDay.SUNDAY) {
			today = WeekDay.MONDAY;
		}
		dayPager.setCurrentItem(today.getId() - 1);
	}

	private synchronized void startRequest(Date date, boolean forceRequest) {
		String login = preferences.getString(getString(R.string.key_login), null);
		String password = preferences.getString(getString(R.string.key_password), null);

		if (inNullOrEmpty(login) || inNullOrEmpty(password)) {
			showDialog(DIALOG_NO_USER_PASS);
		} else {
			progress = ProgressDialog.show(this, "", getString(R.string.message_loading_data));
			new FetchDataTask().execute(date, login, password, forceRequest);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_NO_USER_PASS:
			builder.setMessage(getString(R.string.message_configure_credentials))
					.setCancelable(true)
					.setPositiveButton(getString(R.string.button_open_preferences),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									startActivity(new Intent(TimeTableActivity.this, UserPreferencesActivity.class));
								}
							}).setNegativeButton(getString(R.string.button_cancel), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_CONNECT:
			// TODO add option to retry?
			builder.setMessage(getString(R.string.message_error_while_connecting)).setPositiveButton(
					getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_FETCH:
			builder.setMessage(getString(R.string.message_error_while_fetching)).setPositiveButton(
					getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_PARSE:
			builder.setMessage(getString(R.string.message_error_while_parsing)).setPositiveButton(
					getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ABOUT:
			result = new Dialog(this);
			result.setContentView(R.layout.about);
			result.setTitle(getString(R.string.app_title) + " v" + getPackageInfo().versionName);
			linkify((TextView) result.findViewById(R.id.aboutAuthor));
			linkify((TextView) result.findViewById(R.id.aboutContact));

			// LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT);
			// result.setContentView(text, params);
			// result.show();
			break;
		default:
			result = null;
		}
		return result;
	}

	private void linkify(TextView textViewWithLinks) {
		Linkify.addLinks(textViewWithLinks, Linkify.ALL);
	}

	private PackageInfo getPackageInfo() {
		PackageInfo result = null;
		try {
			result = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// do nothing
		}
		return result;
	}

	private boolean inNullOrEmpty(String login) {
		return login == null || login.length() == 0;
	}

	class FetchDataTask extends AsyncTask<Object, Integer, TimetableWeek> {

		private final TimeTableAPI api = new TimeTableAPI(TimeTableActivity.this);
		private Integer errorCode = 0;

		@Override
		protected TimetableWeek doInBackground(Object... params) {
			Date date = (Date) params[0];
			String login = (String) params[1];
			String password = (String) params[2];
			boolean forceRequest = (Boolean) params[3];

			TimetableWeek result = null;
			try {
				result = api.retrieve(date, login, password, forceRequest);

			} catch (ResponseParseException e) {
				e.printStackTrace();
				errorCode = DIALOG_ERROR_PARSE;
				result = new TimetableWeek();
			} catch (RequestException e) {
				e.printStackTrace();
				errorCode = DIALOG_ERROR_FETCH;
				result = new TimetableWeek();
			} catch (ServerConnectionException e) {
				e.printStackTrace();
				errorCode = DIALOG_ERROR_CONNECT;
				result = new TimetableWeek();
			}
			return result;
		}

		@Override
		protected void onPostExecute(TimetableWeek week) {
			if (errorCode == 0) {
				TimeTableActivity.this.week = week;
				for (DayFragment fragment : fragmentPageAdapter.getActiveFragments()) {
					fragment.updateDate(week);
				}
				datebox.setText(DateHelper.formatToUserFriendlyFormat(week.getLastUpdate()));
			} else {
				datebox.setText(getString(R.string.default_novalue));
			}

			progress.dismiss();

			if (errorCode != 0) {
				showDialog(errorCode);
			}

		}
	}

	public class MyAdapter extends FragmentStatePagerAdapter {

		private final HashMap<Integer, DayFragment> activeFragments = new HashMap<Integer, DayFragment>();

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

			Bundle args = new Bundle();
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_DATA, week);
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_WEEKDAY, weekDay);
			fragment.setArguments(args);

			activeFragments.put(position, fragment);

			return fragment;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			super.destroyItem(container, position, object);
			activeFragments.remove(position);
		}

		public Collection<DayFragment> getActiveFragments() {
			return activeFragments.values();
		}
	}

}
