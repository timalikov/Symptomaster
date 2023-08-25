package com.example.symptomschecker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Profile extends HeaderActivity {
	private Button mEditProfileButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		mEditProfileButton = (Button) findViewById(R.id.profile_edit_button);

		mEditProfileButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent A = new Intent(getApplicationContext(),
						ProfileEdit.class);
				startActivity(A);
			}
		});

		// get information for the logged in user
		int uId = HeaderActivity.userId;
		DataSourceUsers dsu = new DataSourceUsers(Profile.this);
		DbUser u = dsu.selectUser(uId);
		String gender = (u.getGender() == 0) ? "Male" : "Female";

		TextView firstnameUi = (TextView) findViewById(R.id.profile_firstname);
		TextView lastnameUi = (TextView) findViewById(R.id.profile_lastname);
		TextView emailUi = (TextView) findViewById(R.id.profile_email);
		TextView genderUi = (TextView) findViewById(R.id.profile_gender);
		TextView dobUi = (TextView) findViewById(R.id.profile_dob);
		TextView heightUi = (TextView) findViewById(R.id.profile_height);
		TextView weightUi = (TextView) findViewById(R.id.profile_weight);

		// parse input
		firstnameUi.setText(u.getFirstname());
		lastnameUi.setText(u.getLastname());
		emailUi.setText(u.getEmail());
		genderUi.setText(gender);
		dobUi.setText(u.getDob());
		heightUi.setText(""+u.getHeight());
		weightUi.setText(""+u.getWeight());
	}

}
