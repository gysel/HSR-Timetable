package ch.scythe.hsr.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

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

}
