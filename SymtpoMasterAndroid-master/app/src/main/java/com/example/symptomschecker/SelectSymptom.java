package com.example.symptomschecker;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectSymptom extends ListActivity {

	private Button mSelectSymptomNext;
	private String path;

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		if (HeaderActivity.userId == 0) {
			Intent lock = new Intent(getApplicationContext(), Welcome.class);
			startActivity(lock);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_symptom);

		int a = HeaderActivity.userId;
		// Toast.makeText(SelectSymptom.this, "" + a,
		// Toast.LENGTH_SHORT).show();

		// get symptomId to run in this activity
		Bundle b = getIntent().getExtras();
		int symptomId = b.getInt("symptomId");
		this.path = b.getString("path");

		DataSourceSymptoms dss = new DataSourceSymptoms(SelectSymptom.this);
		DbSymptom s = dss.selectSymptomById(symptomId);

		// display name to the user
		TextView symptomNameUi = (TextView) findViewById(R.id.symptom_name);
		// symptomNameUi.setText(s.getName());
		symptomNameUi.setText(path);

		// display description to the user
		TextView symptomDescriptionUi = (TextView) findViewById(R.id.symptom_description);
		if (s.getDescription() != null) {
			symptomDescriptionUi.setVisibility(View.VISIBLE);
			symptomDescriptionUi.setText(s.getDescription());
		} else {
			symptomDescriptionUi.setVisibility(View.GONE);
		}

		// display question to the user
		TextView symptomQuestionUi = (TextView) findViewById(R.id.symptom_question);
		if (s.getQuestion() != null) {
			symptomQuestionUi.setVisibility(View.VISIBLE);
			symptomQuestionUi.setText(s.getQuestion());
		} else {
			symptomQuestionUi.setVisibility(View.GONE);
		}

		// display the list of symptom childs
		DbSymptomArrayAdapter adapter = new DbSymptomArrayAdapter(
				getBaseContext(), s.getChilds());
		setListAdapter(adapter);

		// set back button to lead to the previous activity
		Button backButtonUi = (Button) findViewById(R.id.symptom_button_back);
		backButtonUi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// DbResult r = dss.selectAllResultsForSymptom(s.getId());
		// Toast.makeText(SelectSymptom.this, r.toString(),
		// Toast.LENGTH_SHORT).show();
		// Toast.makeText(SelectSymptom.this, s.getChilds().toString(),
		// Toast.LENGTH_SHORT).show();

		// RESULTS HANDLING
		// if this symptom does not have any children then check for results
		if (s.getChilds() == null || s.getChilds().size() == 0) {

			DbResult res = dss.selectAllResultsForSymptom(s.getId());

			Intent ResultsScreen = new Intent(getApplicationContext(),
					Recommendations.class);
			Bundle bb = new Bundle();
			bb.putString("cause", res.getCause());
			bb.putString("recommendation", res.getRecommendation());
			bb.putString("path", this.path);

			ResultsScreen.putExtras(bb);
			startActivity(ResultsScreen);
			finish();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DbSymptom item = (DbSymptom) getListAdapter().getItem(position);

		// redirect to next symptom
		Intent newSymptomIntent = new Intent(getApplicationContext(),
				SelectSymptom.class);
		Bundle b = new Bundle();
		b.putInt("symptomId", item.getId());
		b.putString("path", this.path + " > " + item.getName());
		newSymptomIntent.putExtras(b);
		startActivity(newSymptomIntent);

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
			Intent symptomDrawer = new Intent(getApplicationContext(),
					SymptomDrawer.class);
			startActivity(symptomDrawer);
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
			HeaderActivity.userId = 0;
			Intent = new Intent(getApplicationContext(), Welcome.class);
			startActivity(Intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
