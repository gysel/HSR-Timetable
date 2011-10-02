package ch.scythe.hsr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_LOGIN = "login";
	// _UI
	private EditText field_login;
	private EditText field_password;
	private Button go;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		field_login = (EditText) findViewById(R.id.login);
		field_password = (EditText) findViewById(R.id.password);
		go = (Button) findViewById(R.id.go);

		go.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						TimeTableDayActivity.class);

				Bundle params = new Bundle();
				params.putString(PARAM_LOGIN, field_login.getText().toString());
				params.putString(PARAM_PASSWORD, field_password.getText()
						.toString());
				myIntent.putExtras(params);

				startActivityForResult(myIntent, 0);

			}

		});

	}

}