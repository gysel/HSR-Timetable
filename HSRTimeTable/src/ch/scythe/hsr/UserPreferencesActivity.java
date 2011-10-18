package ch.scythe.hsr;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UserPreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_preferences);
	}

}
