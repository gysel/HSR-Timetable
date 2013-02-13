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
package ch.scythe.hsr.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import ch.scythe.hsr.Constants;

public class AndroidHelper {

	public static String getAppVersionName(Context context) {
		PackageInfo packageInfo = getPackageInfo(context.getPackageManager(), context.getPackageName());
		return packageInfo != null ? packageInfo.versionName : "";
	}

	private static PackageInfo getPackageInfo(PackageManager packageManager, String packageName) {
		PackageInfo result = null;
		try {
			result = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// do nothing
		}
		return result;
	}

	public static Account getAccount(AccountManager accountManager) {
		Account result = null;
		Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
		if (accounts.length == 1) {
			result = accounts[0];
		}
		return result;
	}

}
