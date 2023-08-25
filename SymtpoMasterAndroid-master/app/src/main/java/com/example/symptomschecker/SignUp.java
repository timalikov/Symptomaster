package com.example.symptomschecker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp extends FragmentActivity {

	private Button mSignUpDoneButton;
	private SharedPreferences settings;
	private Spinner genderUi;
	private Button dobUi;

	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	
	/**
	 * Save state for this activity
	 */
	public void onSaveInstanceState(Bundle savedInstanceState) {

		// save picked choices
		int gender = (int) genderUi.getSelectedItemId();
		String dob = dobUi.getText().toString();		
		settings.edit().putInt("gender", gender).commit();
		settings.edit().putString("dob", dob).commit();

		super.onSaveInstanceState(savedInstanceState);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		settings = getSharedPreferences("Prefs", this.MODE_PRIVATE);

		
		Spinner spinner = (Spinner) findViewById(R.id.signup_gender);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.gendersArray,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);


		// handle change of orientation
		int genderPicked = settings.getInt("gender", 1);
		String dobPicked = settings.getString("dob", getString(R.string.dob_select));
		genderUi = (Spinner) findViewById(R.id.signup_gender);
		dobUi = (Button) findViewById(R.id.signup_dob);
		genderUi.setSelection(genderPicked);
		dobUi.setText(dobPicked);
		
		
		mSignUpDoneButton = (Button) findViewById(R.id.signup_done_button);

		mSignUpDoneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Validator validator = new Validator();

				// get user input
				EditText firstnameUi = (EditText) findViewById(R.id.signup_firstname);
				EditText lastnameUi = (EditText) findViewById(R.id.signup_lastname);
				EditText emailUi = (EditText) findViewById(R.id.signup_email);
				Spinner genderUi = (Spinner) findViewById(R.id.signup_gender);
				Button dobUi = (Button) findViewById(R.id.signup_dob);
				EditText heightUi = (EditText) findViewById(R.id.signup_height);
				EditText weightUi = (EditText) findViewById(R.id.signup_weight);
				EditText passwordUi = (EditText) findViewById(R.id.signup_password);
				EditText confirmPasswordUi = (EditText) findViewById(R.id.signup_confirm_password);

				// parse input
				String firstname = firstnameUi.getText().toString();
				String lastname = lastnameUi.getText().toString();
				String email = emailUi.getText().toString();
				int gender = (int) genderUi.getSelectedItemId();
				String dob = dobUi.getText().toString();
				String heightStr = heightUi.getText().toString();
				int height = heightStr.isEmpty() || (heightStr == null) ? 0
						: Integer.parseInt(heightStr);
				String weightStr = weightUi.getText().toString();
				int weight = weightStr.isEmpty() || (weightStr == null) ? 0
						: Integer.parseInt(weightStr);
				String password = passwordUi.getText().toString();
				String confirmPassword = confirmPasswordUi.getText().toString();

				// check if passwords match
				if (!password.equals(confirmPassword)) {
					validator.setVerified(false);
					validator.setStatus(getString(R.string.v_pass_not_match));
				} else {

					DbUser u = new DbUser();
					u.setFirstname(firstname);
					u.setLastname(lastname);
					u.setEmail(email);
					u.setGender(gender);
					u.setDob(dob);
					u.setHeight(height);
					u.setWeight(weight);
					u.setPassword(password);

					// validate input
					validator = u.validate();

					// if verified
					if (validator.isVerified()) {
						// hash the password
						u.setPasswordHashed(password);
						// store to db
						DataSourceUsers dsu = new DataSourceUsers(SignUp.this);
						int res = dsu.insertNewUser(u);
						dsu.close();

						validator
								.setStatus(getString(R.string.v_record_created));
					}

				}

				Toast.makeText(SignUp.this, validator.getStatus(),
						Toast.LENGTH_SHORT).show();

				if (validator.isVerified()) {
					Intent A = new Intent(getApplicationContext(),
							Welcome.class);
					startActivity(A);
				}
			}
		});

	}

}
