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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import ch.scythe.hsr.authenticator.AuthenticatorActivity;
import ch.scythe.hsr.helper.AndroidHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class UserPreferencesActivity extends SherlockPreferenceActivity {

	private AccountManager accountManager;
	private Preference login;
	private Preference removeAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountManager = AccountManager.get(getApplicationContext());

		addPreferencesFromResource(R.xml.user_preferences);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Preference removeAccount = findPreference(getString(R.string.key_removeaccount));

		login = findPreference(getString(R.string.key_login));
		removeAccount = findPreference(getString(R.string.key_removeaccount));

		login.setOnPreferenceClickListener(new AddAccountListener());
		removeAccount.setOnPreferenceClickListener(new RemoveAccountListener());

		// updateViewState(account);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Account account = AndroidHelper.getAccount(AccountManager.get(getApplicationContext()));
		updateViewState(account);
	}

	private void updateViewState(Account account) {
		if (account == null) {
			login.setSummary(getString(R.string.preferences_account_not_there));
			login.setEnabled(true);
			removeAccount.setEnabled(false);
		} else {
			login.setSummary(account.name);
			removeAccount.setEnabled(true);
			login.setEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, TimeTableActivity.class));
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private final class AddAccountListener implements OnPreferenceClickListener {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			startActivity(new Intent(UserPreferencesActivity.this, AuthenticatorActivity.class));
			return true;
		}
	}

	private final class RemoveAccountListener implements OnPreferenceClickListener {
		public boolean onPreferenceClick(Preference preference) {
			Account account = AndroidHelper.getAccount(accountManager);
			accountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {

				@Override
				public void run(AccountManagerFuture<Boolean> future) {
					Toast toast = Toast.makeText(getApplicationContext(), "Login sucessfully removed.", Toast.LENGTH_SHORT);
					toast.show();
					updateViewState(null);
				}
			}, null);

			return true;
		}
	}

}
