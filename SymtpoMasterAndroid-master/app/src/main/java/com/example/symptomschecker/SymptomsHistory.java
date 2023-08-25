package com.example.symptomschecker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SymptomsHistory extends ListActivity {
	private Button mSymptomsHistoryEditButton;
	TextView message;
	private int userId;

	public SymptomsHistory() {
		userId = HeaderActivity.userId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_symptoms_history);
		message = (TextView) findViewById(R.id.history_message);
		
		
		// get all history
		DataSourceHistory dsh = new DataSourceHistory(this);
		ArrayList<DbHistory> allHist = dsh.selectAllHistoryForUser(userId);
		if(allHist == null || allHist.isEmpty()) {
			message.setText(getString(R.string.history_empty));
		}
				
		// display the list of symptom childs
		DbHistoryArrayAdapter adapter = new DbHistoryArrayAdapter(
				getBaseContext(), allHist);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Button bt = (Button) v.findViewWithTag("button_remove");

		if (!bt.isEnabled()) {
			bt.setEnabled(true);
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(150);
			bt.setAnimation(animation);

			bt.setVisibility(View.VISIBLE);
		} else {
			bt.setEnabled(false);
			bt.setVisibility(View.GONE);
		}
	}

	public void removeItem(View view) {
		int id = view.getId();
		DataSourceHistory dsh = new DataSourceHistory(this);
		dsh.deleteHistoryRecord(id);
		ArrayList<DbHistory> allHist = dsh.selectAllHistoryForUser(1);
		dsh.close();

		DbHistoryArrayAdapter adapter = new DbHistoryArrayAdapter(
				getBaseContext(), allHist);
		setListAdapter(adapter);

		if(allHist == null || allHist.isEmpty()) {
			message.setText(getString(R.string.history_empty));
		}
		
		Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
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
