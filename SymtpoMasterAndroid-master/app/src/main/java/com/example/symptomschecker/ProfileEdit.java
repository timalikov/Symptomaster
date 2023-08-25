package com.example.symptomschecker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileEdit extends FragmentActivity {

	private Button mEditProfileDoneButton;
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
		settings.edit().putInt("genderProfile", gender).commit();
		settings.edit().putString("dobProfile", dob).commit();

		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_edit);
		
		final int uId = HeaderActivity.userId;


		Spinner spinner = (Spinner) findViewById(R.id.profile_edit_gender);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.gendersArray,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);


		
		mEditProfileDoneButton = (Button) findViewById(R.id.profile_edit_done_button);

		mEditProfileDoneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Validator validator = new Validator();

				// get user input
				EditText firstnameUi = (EditText) findViewById(R.id.profile_edit_firstname);
				EditText lastnameUi = (EditText) findViewById(R.id.profile_edit_lastname);
				EditText emailUi = (EditText) findViewById(R.id.profile_edit_email);
				genderUi = (Spinner) findViewById(R.id.profile_edit_gender);
				dobUi = (Button) findViewById(R.id.signup_dob);
				EditText heightUi = (EditText) findViewById(R.id.profile_edit_height);
				EditText weightUi = (EditText) findViewById(R.id.profile_edit_weight);

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


					DbUser u = new DbUser();
					u.setId(uId);
					u.setFirstname(firstname);
					u.setLastname(lastname);
					u.setEmail(email);
					u.setGender(gender);
					u.setDob(dob);
					u.setHeight(height);
					u.setWeight(weight);
					u.setPassword("dummy");

					// validate input
					validator = u.validate();

					// if verified
					if (validator.isVerified()) {
						// store to db
						DataSourceUsers dsu = new DataSourceUsers(
								ProfileEdit.this);
						dsu.updateUser(u);
						dsu.close();

						validator
								.setStatus(getString(R.string.v_record_updated));
					}


				Toast.makeText(ProfileEdit.this, validator.getStatus(),
						Toast.LENGTH_SHORT).show();

				if(validator.isVerified()) {
					Intent A = new Intent(getApplicationContext(), Profile.class);
					startActivity(A);
				}
				
			}
		});

		// get information for the logged in user
		DataSourceUsers dsu = new DataSourceUsers(ProfileEdit.this);
		DbUser u = dsu.selectUser(uId);

		// preset fields from db
		EditText firstnameUi = (EditText) findViewById(R.id.profile_edit_firstname);
		EditText lastnameUi = (EditText) findViewById(R.id.profile_edit_lastname);
		EditText emailUi = (EditText) findViewById(R.id.profile_edit_email);
		genderUi = (Spinner) findViewById(R.id.profile_edit_gender);
		dobUi = (Button) findViewById(R.id.signup_dob);
		EditText heightUi = (EditText) findViewById(R.id.profile_edit_height);
		EditText weightUi = (EditText) findViewById(R.id.profile_edit_weight);

		firstnameUi.setText(u.getFirstname());
		lastnameUi.setText(u.getLastname());
		emailUi.setText(u.getEmail());
		genderUi.setSelection(u.getGender());
		dobUi.setText(u.getDob());
		heightUi.setText("" + u.getHeight());
		weightUi.setText("" + u.getWeight());

		settings = getSharedPreferences("Prefs", this.MODE_PRIVATE);
		// handle change of orientation
		int genderPicked = settings.getInt("genderProfile", u.getGender());
		String dobPicked = settings.getString("dobProfile", u.getDob());
		genderUi = (Spinner) findViewById(R.id.profile_edit_gender);
		dobUi = (Button) findViewById(R.id.signup_dob);
		genderUi.setSelection(genderPicked);
		dobUi.setText(dobPicked);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.header, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent Intent;

		// Handle item selection
		switch (item.getItemId()) {

		case R.id.action_home:
			Intent = new Intent(getApplicationContext(), SelectSymptom.class);
			Bundle b = new Bundle();
			b.putInt("symptomId", 1);
			b.putString("path", "Start");
			Intent.putExtras(b);
			startActivity(Intent);
			return true;
		case R.id.action_account:
			Intent = new Intent(getApplicationContext(), Profile.class);
			startActivity(Intent);
			return true;
		case R.id.action_history:
			Intent = new Intent(getApplicationContext(), SymptomsHistory.class);
			startActivity(Intent);
			return true;
		case R.id.action_logout:
			Intent = new Intent(getApplicationContext(), Welcome.class);
			startActivity(Intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
