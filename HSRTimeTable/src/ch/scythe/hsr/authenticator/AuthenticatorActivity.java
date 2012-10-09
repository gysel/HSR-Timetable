/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ch.scythe.hsr.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ch.scythe.hsr.Constants;
import ch.scythe.hsr.R;
import ch.scythe.hsr.api.TimeTableAPI;
import ch.scythe.hsr.error.ServerConnectionException;

/** Activity which displays login screen to the user. */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	private static final String TAG = "AuthenticatorActivity";
	private boolean firstLoginAttempt = true;
	private AccountManager accountManager;
	private Thread mAuthThread;
	private String mAuthtoken;
	private String mAuthtokenType;

	/** If set we are just checking that the user knows their credentials; this doesn't cause the user's password to be changed on the device. */
	// private Boolean mConfirmCredentials = false;

	/** for posting authentication attempts back to UI thread */
	// private final Handler mHandler = new Handler();
	private TextView mMessage;
	private String mPassword;
	private EditText mPasswordEdit;

	private String mUsername;
	private EditText mUsernameEdit;
	private AsyncTask<String, Object, Boolean> authTask;

	/** {@inheritDoc} */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		accountManager = AccountManager.get(this);

		final Intent intent = getIntent();

		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		firstLoginAttempt = TextUtils.isEmpty(mUsername) && TextUtils.isEmpty(mAuthtoken);

		setContentView(R.layout.login_activity);

		mMessage = (TextView) findViewById(R.id.message_bottom);
		mUsernameEdit = (EditText) findViewById(R.id.username_edit);
		mPasswordEdit = (EditText) findViewById(R.id.password_edit);

		mUsernameEdit.setText(mUsername);
		mMessage.setText(getMessage());
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getText(R.string.ui_activity_authenticating));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.i(TAG, "dialog cancel has been invoked");
				if (authTask != null) {
					authTask.cancel(true);
				}
			}
		});
		return dialog;
	}

	/** Handles onClick event on the Submit button. Sends username/password to the server for authentication.
	 * 
	 * @param view
	 *            The Submit button for which this method is invoked */
	public void handleLogin(View view) {
		mUsername = mUsernameEdit.getText().toString();
		mPassword = mPasswordEdit.getText().toString();
		if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
			mMessage.setText(getMessage());
		} else {
			showProgress();
			authTask = new AttemptAuthTask(new TimeTableAPI(getApplicationContext()));
			authTask.execute(mUsername, mPassword);
		}
	}

	private class AttemptAuthTask extends AsyncTask<String, Object, Boolean> {

		private final TimeTableAPI api;

		public AttemptAuthTask(TimeTableAPI api) {
			this.api = api;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			Boolean result = null;
			try {
				result = api.validateCredentials(username, password);
			} catch (ServerConnectionException e) {
				// TODO handle error message
				// for now: result = null means error
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			onAuthenticationResult(success);
		}

	}

	/** Called when the authentication process completes. */
	public void onAuthenticationResult(Boolean result) {
		Log.i(TAG, "Authentication " + (result ? "succeeded" : "failed") + ".");
		hideProgress();

		if (Boolean.TRUE == result) {
			finishLogin();
		} else {
			if (Boolean.FALSE == result) {
				mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
			} else {
				mMessage.setText(getText(R.string.message_error_while_connecting));
			}
		}
	}

	/** Called when response is received from the server for authentication request. See onAuthenticationResult(). Sets the AccountAuthenticatorResult which is
	 * sent back to the caller. Also sets the authToken in AccountManager for this account.
	 * 
	 * @param the
	 *            confirmCredentials result. */

	protected void finishLogin() {
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);

		if (firstLoginAttempt) {

			accountManager.addAccountExplicitly(account, mPassword, null);
			// Set contacts sync for this account.
			// ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
		} else {
			accountManager.setPassword(account, mPassword);
		}
		final Intent intent = new Intent();
		mAuthtoken = mPassword;
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		if (mAuthtokenType != null && mAuthtokenType.equals(Constants.AUTHTOKEN_TYPE)) {
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken);
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/** Hides the progress UI for a lengthy operation. */
	protected void hideProgress() {
		dismissDialog(0);
	}

	/** Shows the progress UI for a lengthy operation. */
	protected void showProgress() {
		showDialog(0);
	}

	/** Returns the message to be displayed at the top of the login dialog box. */
	private CharSequence getMessage() {
		getString(R.string.app_name);
		if (TextUtils.isEmpty(mUsername) && TextUtils.isEmpty(mPassword)) {
			return "";
		} else if (TextUtils.isEmpty(mUsername)) {
			return getText(R.string.login_activity_login_missing);
		} else if (TextUtils.isEmpty(mPassword)) {
			return getText(R.string.login_activity_pw_missing);
		}
		return null;
	}
}
