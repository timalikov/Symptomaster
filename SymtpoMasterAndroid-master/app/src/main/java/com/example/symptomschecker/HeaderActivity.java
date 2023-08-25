package com.example.symptomschecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HeaderActivity extends Activity {

	public static int userId;

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
