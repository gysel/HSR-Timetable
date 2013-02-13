/* 
 * Copyright (C) 2011 - 2013 Michi Gysel <michael.gysel@gmail.com>
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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ch.scythe.hsr.api.RequestException;
import ch.scythe.hsr.api.TimeTableAPI;
import ch.scythe.hsr.api.ui.UiWeek;
import ch.scythe.hsr.authenticator.AuthenticatorActivity;
import ch.scythe.hsr.enumeration.Weekday;
import ch.scythe.hsr.error.AccessDeniedException;
import ch.scythe.hsr.error.ResponseParseException;
import ch.scythe.hsr.error.ServerConnectionException;
import ch.scythe.hsr.helper.AndroidHelper;
import ch.scythe.hsr.helper.DateHelper;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class TimeTableActivity extends SherlockFragmentActivity {
	// _ViewPager
	public static final int NUM_ITEMS = 6;
	private ViewPager dayPager;
	private MyAdapter fragmentPageAdapter;
	// _UI
	private TextView datebox;
	private TextView weekbox;
	private ProgressDialog progress;
	// _Android
	private AccountManager accountManager;
	private SharedPreferences preferences;
	// _State
	public UiWeek week = new UiWeek();
	public Date lastAcessed;
	private AsyncTask<Object, Integer, UiWeek> task;
	// _Helpers
	private TimeTableAPI api;
	// _Keys
	private static final int DIALOG_NO_USER_PASS = 0;
	private static final int DIALOG_ERROR_FETCH = 1;
	private static final int DIALOG_ERROR_CONNECT = 2;
	private static final int DIALOG_ERROR_PARSE = 3;
	private static final int DIALOG_USER_PASS_FETCH = 4;
	private static final int DIALOG_ERROR_ACCESS_DENIED = 5;
	private static final String PREFERENCE_ACTIVATED_TAB_TIMESTAMP = "ActivatedTabTimestamp";
	private static final String PREFERENCE_ACTIVATED_TAB = "ActivatedTab";
	private static final String LOGGING_TAG = "TimeTableActivity";
	private static final String SAVED_INSTANCE_TIMETABLE_WEEK = "TimetableWeek";
	private static final int THRESHOLD_IN_MINUTES = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new TimeTableAPI(TimeTableActivity.this);

		setContentView(R.layout.timetable_main);

		fragmentPageAdapter = new MyAdapter(getSupportFragmentManager());
		accountManager = AccountManager.get(getApplicationContext());
		preferences = getPreferences(MODE_PRIVATE);

		dayPager = (ViewPager) findViewById(R.id.day_pager);
		dayPager.setAdapter(fragmentPageAdapter);

		final float density = getResources().getDisplayMetrics().density;

		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(dayPager);

		titleIndicator.setBackgroundColor(getResources().getColor(android.R.color.background_dark)); // 0x330065A3
		titleIndicator.setFooterColor(0xFF0065A3);
		titleIndicator.setFooterLineHeight(4 * density); // 1dp
		titleIndicator.setFooterIndicatorHeight(6 * density); // 3dp
		titleIndicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
		titleIndicator.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
		titleIndicator.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));

		datebox = (TextView) findViewById(R.id.date_value);
		weekbox = (TextView) findViewById(R.id.week_value);

		Date date = new Date();
		weekbox.setText(DateHelper.formatToWeekNumber(date));

		UiWeek lastInstance = (UiWeek) getLastCustomNonConfigurationInstance();
		if (lastInstance != null) {
			Log.i(LOGGING_TAG, "Creating Activity from lastInstance.");
			// there was a screen orientation change.
			// we can don't have to create the ui...
			week = lastInstance;
			datebox.setText(DateHelper.formatToUserFriendlyFormat(week.getLastUpdate()));
		} else if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_TIMETABLE_WEEK)) {
			Log.i(LOGGING_TAG, "Creating Activity from savedInstanceState.");
			// the state of the app was saved, so we can just update the ui
			week = (UiWeek) savedInstanceState.get(SAVED_INSTANCE_TIMETABLE_WEEK);
			datebox.setText(DateHelper.formatToUserFriendlyFormat(week.getLastUpdate()));
		} else {
			Log.i(LOGGING_TAG, "Creating Activity from scratch.");
			// no data available, read it!
			reloadCurrentTab();
			startRequest(date, false);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadCurrentTab();
	}

	@Override
	protected void onPause() {
		super.onPause();
		persistCurrentTab();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(progress != null && progress.isShowing())
			progress.cancel();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(SAVED_INSTANCE_TIMETABLE_WEEK, week);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return week;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
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
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case R.id.today:
			scrollToToday();
			break;
		}
		return true;
	}

	private void persistCurrentTab() {
		SharedPreferences.Editor editor = this.preferences.edit();
		int currentTab = dayPager.getCurrentItem();
		editor.putInt(PREFERENCE_ACTIVATED_TAB, currentTab);
		editor.putLong(PREFERENCE_ACTIVATED_TAB_TIMESTAMP, new Date().getTime());
		Log.d(LOGGING_TAG, "Persisting current tab: " + Weekday.getById(currentTab + 1));
		editor.commit();
	}

	private void reloadCurrentTab() {
		int activatedTab = preferences.getInt(PREFERENCE_ACTIVATED_TAB, -1);
		long activatedTabTimestamp = preferences.getLong(PREFERENCE_ACTIVATED_TAB_TIMESTAMP, -1);

		if (activatedTab == -1 || activatedTabTimestamp == -1) {
			scrollToToday();
		} else {
			long timeDiff = new Date().getTime() - activatedTabTimestamp;

			Log.d(LOGGING_TAG, "Timediff:" + timeDiff + "/" + 1000 * 60 * THRESHOLD_IN_MINUTES);

			if (timeDiff > 1000 * 60 * THRESHOLD_IN_MINUTES) {
				scrollToToday();
			} else {
				dayPager.setCurrentItem(activatedTab);
				Log.d(LOGGING_TAG, "Reloading current tab: " + Weekday.getById(activatedTab + 1));
			}

		}
	}

	private void scrollToToday() {
		Log.d(LOGGING_TAG, "Activating todays tab.");
		Weekday today = Weekday.getByDate(new Date());
		if (today == Weekday.SUNDAY) {
			today = Weekday.MONDAY;
		}
		dayPager.setCurrentItem(today.getId() - 1, true);
		persistCurrentTab();
	}

	private synchronized void startRequest(Date date, boolean forceRequest) {

		Account account = AndroidHelper.getAccount(accountManager);

		persistCurrentTab();

		// try to migrate settings
		if (account == null) {
			SharedPreferences oldPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			String login = oldPrefs.getString(getString(R.string.key_login), null);
			String password = oldPrefs.getString(getString(R.string.key_password), null);

			if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
				account = new Account(login.toLowerCase(), Constants.ACCOUNT_TYPE);
				accountManager.addAccountExplicitly(account, password, null);
				Toast.makeText(getApplicationContext(), "Automatically migrated your HSR Login into the Android AccountManager.", Toast.LENGTH_LONG).show();
				Editor editor = oldPrefs.edit();
				editor.clear();
				editor.commit();
			}
		}

		if (account == null) {

			showDialog(DIALOG_NO_USER_PASS);
		} else if (api.retrieveRequiresBlockingCall(forceRequest)) { // TODO implement this
			progress = new ProgressDialog(this);
			progress.setMessage(getText(R.string.message_loading_data));
			progress.setIndeterminate(true);
			progress.setCancelable(true);
			progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Log.i(LOGGING_TAG, "dialog cancel has been invoked");
					if (task != null) {
						task.cancel(true);
					}
				}
			});
			progress.show();

			// progress = ProgressDialog.show(this, "", getString(R.string.message_loading_data));
			executeTask(date, forceRequest, account);
		} else {
			executeTask(date, forceRequest, account);
		}
	}

	private void executeTask(Date date, boolean forceRequest, Account account) {
		task = new FetchDataTask();
		task.execute(date, account, forceRequest);
	}

	private void updateFragemetsWithData(UiWeek week) {
		for (DayFragment fragment : fragmentPageAdapter.getActiveFragments()) {
			Log.d("TimeTableActivity", "Update Fragment " + fragment.getWeekDay() + " with new data.");
			fragment.updateDate(week);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_NO_USER_PASS:
			builder.setMessage(getString(R.string.message_configure_credentials)).setCancelable(true)
					.setPositiveButton(getString(R.string.button_add_login), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(TimeTableActivity.this, AuthenticatorActivity.class));
						}
					}).setNegativeButton(getString(R.string.button_cancel), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_CONNECT:
			// TODO add option to retry?
			builder.setMessage(getString(R.string.message_error_while_connecting)).setPositiveButton(getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_FETCH:
			builder.setMessage(getString(R.string.message_error_while_fetching)).setPositiveButton(getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_PARSE:
			builder.setMessage(getString(R.string.message_error_while_parsing)).setPositiveButton(getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_USER_PASS_FETCH:
			builder.setMessage(getString(R.string.message_error_while_fetching_user)).setPositiveButton(getString(R.string.button_ok), null);
			result = builder.create();
			break;
		case DIALOG_ERROR_ACCESS_DENIED:
			builder.setMessage(getString(R.string.message_error_access_denied)).setPositiveButton(getString(R.string.button_ok), null);
			result = builder.create();
			break;
		default:
			result = null;
		}
		return result;
	}

	class FetchDataTask extends AsyncTask<Object, Integer, UiWeek> {

		private Integer errorCode = 0;

		@Override
		protected UiWeek doInBackground(Object... params) {
			Date date = (Date) params[0];
			Account account = (Account) params[1];
			boolean forceRequest = (Boolean) params[2];

			UiWeek result = new UiWeek();

			String password = null;
			try {

				password = accountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true);

				result = api.retrieve(date, account.name, password, forceRequest);

			} catch (OperationCanceledException e) {
				Log.e(LOGGING_TAG, "Exception while fetching the user from the AccountManager.", e);
				errorCode = DIALOG_USER_PASS_FETCH;
			} catch (AuthenticatorException e) {
				Log.e(LOGGING_TAG, "Exception while fetching the user from the AccountManager.", e);
				errorCode = DIALOG_USER_PASS_FETCH;
			} catch (IOException e) {
				Log.e(LOGGING_TAG, "Exception while fetching the user from the AccountManager.", e);
				errorCode = DIALOG_USER_PASS_FETCH;
			} catch (ResponseParseException e) {
				Log.e(LOGGING_TAG, "Exception while parsing the server response.", e);
				errorCode = DIALOG_ERROR_PARSE;
			} catch (RequestException e) {
				Log.e(LOGGING_TAG, "Exception while fetching data from the server.", e);
				errorCode = DIALOG_ERROR_FETCH;
			} catch (ServerConnectionException e) {
				Log.e(LOGGING_TAG, "Exception while fetching data from the server.", e);
				errorCode = DIALOG_ERROR_CONNECT;
			} catch (AccessDeniedException e) {
				Log.e(LOGGING_TAG, "Exception while fetching data from the server. (AccessDenied!)", e);
				errorCode = DIALOG_ERROR_ACCESS_DENIED;
			}

			return result;
		}

		@Override
		protected void onPostExecute(UiWeek week) {

			if (errorCode == 0) {
				TimeTableActivity.this.week = week;
				updateFragemetsWithData(week);
				datebox.setText(DateHelper.formatToUserFriendlyFormat(week.getLastUpdate()));
			}

			if (progress != null && progress.isShowing())
				progress.dismiss();

			if (errorCode != 0) {
				showDialog(errorCode);
			} else {
				reloadCurrentTab();
			}

		}

	}

	public class MyAdapter extends FragmentStatePagerAdapter {

		@SuppressLint("UseSparseArrays")
		private final HashMap<Integer, DayFragment> activeFragments = new HashMap<Integer, DayFragment>();

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(getWeekday(position).getResourceReference());
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			DayFragment fragment = new DayFragment();

			Weekday weekDay = getWeekday(position);

			Bundle args = new Bundle();
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_DATA, week);
			args.putSerializable(DayFragment.FRAGMENT_PARAMETER_WEEKDAY, weekDay);
			fragment.setArguments(args);

			activeFragments.put(position, fragment);

			return fragment;
		}

		private Weekday getWeekday(int position) {
			return Weekday.getById(position + 1);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			activeFragments.remove(position);
			super.destroyItem(container, position, object);
		}

		public Collection<DayFragment> getActiveFragments() {
			return activeFragments.values();
		}
	}

}
