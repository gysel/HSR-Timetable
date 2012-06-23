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
