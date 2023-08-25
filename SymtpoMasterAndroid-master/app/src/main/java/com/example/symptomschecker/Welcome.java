package com.example.symptomschecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Welcome extends Activity {

	// private static Context mContext;
	//
	// public static Context getContext() {
	// return mContext;
	// }
	//
	// public static void setContext(Context Context) {
	// mContext = Context;
	// }
	private Button mSignInButton;
	private Button mSignUpButton;
	private Button mAboutButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		mSignInButton = (Button) findViewById(R.id.welcome_signin_button);
		mSignUpButton = (Button) findViewById(R.id.welcome_signup_button);
		mAboutButton = (Button) findViewById(R.id.welcome_about_button);

		mSignInButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String debug = "debug: ";
				EditText emailUi = (EditText) findViewById(R.id.signin_email);
				EditText passwordUi = (EditText) findViewById(R.id.signin_password);

//				String email = emailUi.getText().toString();
//				String password = passwordUi.getText().toString();
				
				String email = "dkarmazi@iu.edu";
				String password = "qwer";

				
				Authenticator auth = new Authenticator();
				Validator validator = auth.checkIfUsernamePasswordMatch(email,
						password);

				if (validator.isVerified()) {

					DataSourceUsers dsu = new DataSourceUsers(Welcome.this);
					DbUser u = dsu.selectUserByEmail(email);
					HeaderActivity.userId = u.getId();


					// Start symptom drawer
					Intent symptomDrawer = new Intent(getApplicationContext(),
							SymptomDrawer.class);
					startActivity(symptomDrawer);

				} else {
					// show status of login
					Toast.makeText(Welcome.this, validator.getStatus(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mSignUpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent LoginScreen = new Intent(getApplicationContext(),
						SignUp.class);
				startActivity(LoginScreen);
			}
		});

		mAboutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent AboutScreen = new Intent(getApplicationContext(),
						About.class);
				startActivity(AboutScreen);

			}
		});

	}

	public class Authenticator {

		public Validator checkIfUsernamePasswordMatch(String email,
				String plainPass) {
			// validate input
			Validator result = validateEmailAndPlainInput(email, plainPass);
			if (!result.isVerified()) {
				return result;
			}

			DataSourceUsers dsu = new DataSourceUsers(Welcome.this);

			// check email
			result = dsu.isValidEmail(email);
			if (!result.isVerified()) {
				result.setStatus(getString(R.string.v_auth_failed));
				return result;
			}

			// get user by email
			DbUser u = dsu.selectUserByEmail(email);

			String dbSalt = u.getSalt();
			String dbPass = u.getPassword();
			String plainHash = PasswordHasher.sha512(plainPass, dbSalt);

			if (dbPass.equals(plainHash)) {
				result.setVerified(true);
				result.setStatus(getString(R.string.v_auth_success));
				return result;
			} else {
				result.setVerified(false);
				result.setStatus(getString(R.string.v_auth_failed));
				return result;
			}
		}

		public Validator validateEmailAndPlainInput(String email,
				String plainPass) {
			Validator vResult = new Validator();
			Validator vEmail, vPlain;
			Boolean verified = true;
			String status = "";

			// 1. email
			vEmail = InputValidation.validateEmail(email,
					getString(R.string.email));
			verified &= vEmail.isVerified();
			status += vEmail.getStatus();

			// 2. plain
			vPlain = InputValidation.validateString(plainPass,
					getString(R.string.password));
			verified &= vPlain.isVerified();
			status += vPlain.getStatus();

			vResult.setVerified(verified);
			vResult.setStatus(status);

			return vResult;
		}

	}

}
