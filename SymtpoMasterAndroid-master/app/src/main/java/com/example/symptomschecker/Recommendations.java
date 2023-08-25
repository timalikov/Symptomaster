package com.example.symptomschecker;

import java.text.SimpleDateFormat;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Recommendations extends HeaderActivity {
	private FragmentManager fm;
	EmergencyFragment emergFragment;
	private Button mEmergencyButton;
	private String timeFormat = "MM/dd/yyyy hh:mm";
	public String emergPhoneNumber;

	public void startEmergencyFragment(View v) {
		// Start fragment
		fm = getFragmentManager();
		emergFragment = new EmergencyFragment();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.fragment_container_e, emergFragment,
				"emerg_welcome");
		transaction.commit();
	}

	public void closeEmergencyFragment(View v) {
		getActionBar().setTitle(getString(R.string.recommendations));
		fm.beginTransaction().hide(emergFragment).commit();
	}
	
	
	/**
	 * Save state for this activity
	 */
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Fragment currentFragment = getFragmentManager().findFragmentById(
				R.id.fragment_container_e);

		if (currentFragment != null) {
			fm.beginTransaction().hide(emergFragment).commit();
			savedInstanceState.putBoolean("recreateFragment", true);
			savedInstanceState.putString("curFragment",
					currentFragment.getTag());
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendations);

		
		if(savedInstanceState != null && savedInstanceState.getBoolean("recreateFragment")) {
			fm = getFragmentManager();
			emergFragment = new EmergencyFragment();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.fragment_container_e, emergFragment,
					"emerg_welcome");
			transaction.commit();
		}

		
		
		
		Bundle b = getIntent().getExtras();
		String cause = b.getString("cause");
		String recommendation = b.getString("recommendation");
		String path = b.getString("path") + " > RESULT";

		// generate timestamp
		java.util.Date date = new java.util.Date();
		String time = new SimpleDateFormat(timeFormat).format(date.getTime());

		int userId = HeaderActivity.userId;
		DbHistory h = new DbHistory();
		h.setUserId(userId);
		h.setSequence(path);
		h.setResult(cause);
		h.setDate(time);
		DataSourceHistory dsh = new DataSourceHistory(this);
		dsh.insertNewHistory(h);

		// symptom result cause
		TextView symptomResultCauseTitleUi = (TextView) findViewById(R.id.symptom_result_cause_title);
		TextView symptomResultPathUi = (TextView) findViewById(R.id.symptom_result_path);
		TextView symptomResultCauseUi = (TextView) findViewById(R.id.symptom_result_cause);
		TextView symptomResultRecommendationTitleUi = (TextView) findViewById(R.id.symptom_result_recommendation_title);
		TextView symptomResultRecommendationUi = (TextView) findViewById(R.id.symptom_result_recommendation);

		// setup UI
		symptomResultPathUi.setText(path);
		symptomResultCauseTitleUi.setText(R.string.recommendation_title_cause);
		symptomResultCauseUi.setText(cause);
		symptomResultRecommendationTitleUi
				.setText(R.string.recommendation_title_tip);
		symptomResultRecommendationUi.setText(recommendation);

		// set back button to lead to the previous activity
		Button backButtonUi = (Button) findViewById(R.id.symptom_button_back);
		backButtonUi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

}
