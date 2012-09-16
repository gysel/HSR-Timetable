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
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import ch.scythe.hsr.helper.AndroidHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class UserPreferencesActivity extends SherlockPreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_preferences);
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		// Preference removeAccount = findPreference(getString(R.string.key_removeaccount));

		Preference login = findPreference(getString(R.string.key_login));

		Account account = AndroidHelper.getAccount(AccountManager.get(getApplicationContext()));
		if (account != null) {
			login.setSummary(account.name);
			// removeAccount.setEnabled(true);
		} else {
			login.setSummary(getString(R.string.preferences_account_not_there));
			// removeAccount.setEnabled(false);
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

}
