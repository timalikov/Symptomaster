package com.example.symptomschecker;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SymptomDrawer extends HeaderActivity {

	private int COLOR_AREA_HEAD = -145783;
	private int COLOR_AREA_TORSO = -9090012;
	private int COLOR_AREA_PELVIS = -3874917;
	private int COLOR_AREA_ARMS = -3689830;
	private int COLOR_AREA_LEGS = -10910425;
	private int CODE_AREA_HEAD = 2;
	private int CODE_AREA_TORSO = 4;
	private int CODE_AREA_PELVIS = 5;
	private int CODE_AREA_ARMS = 18;
	private int CODE_AREA_LEGS = 19;
	private String SPINNERDEFVALUE = "Select";

	private int x;
	private int y;
	private ImageView iv;
	private Bitmap hotspots;
	private int symptomId;

	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		Spinner spinner = (Spinner) findViewById(R.id.drawer_simple_spinner);
		spinner.setSelection(0);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_symptom_drawer);
		// Show the Up button in the action bar.
		setupActionBar();

		Spinner spinner = (Spinner) findViewById(R.id.drawer_simple_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.symptomsArray,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				String selected = (String) parentView.getSelectedItem();

				if (!selected.equals(SPINNERDEFVALUE)) {

					// populate spinner
					DataSourceSymptoms dss = new DataSourceSymptoms(
							SymptomDrawer.this);
					ArrayList<DbSymptom> mainSymptoms = dss
							.selectSymptomById(1).getChilds();

					for (int i = 0; i < mainSymptoms.size(); i++) {
						DbSymptom s = mainSymptoms.get(i);
						if (s.getName().equals(selected)) {
							symptomId = s.getId();

							Intent LoginScreen = new Intent(
									getApplicationContext(),
									SelectSymptom.class);
							Bundle b = new Bundle();
							b.putInt("symptomId", symptomId);
							b.putString("path", "Start");

							LoginScreen.putExtras(b);
							startActivity(LoginScreen);
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {}

		});

		Toast.makeText(this, getString(R.string.notification_select_symptom),
				Toast.LENGTH_SHORT).show();

		iv = (ImageView) findViewById(R.id.drawer_male);

		// get all images
		final Drawable drawableOrig = iv.getDrawable();
		final Drawable drawableNone = getResources().getDrawable(
				R.drawable.male_none);
		final Drawable drawableHead = getResources().getDrawable(
				R.drawable.male_head);
		final Drawable drawableTorso = getResources().getDrawable(
				R.drawable.male_torso);
		final Drawable drawablePelvis = getResources().getDrawable(
				R.drawable.male_pelvis);
		final Drawable drawableLegs = getResources().getDrawable(
				R.drawable.male_legs);
		final Drawable drawableArms = getResources().getDrawable(
				R.drawable.male_arms);

		hotspots = ((BitmapDrawable) drawableOrig).getBitmap();

		iv.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int xRaw = (int) event.getX();
				int yRaw = (int) event.getY();
				
				int color = 0;
				if(xRaw > 0 && xRaw < hotspots.getWidth() &&
				   yRaw > 0 && yRaw < hotspots.getHeight()) {
					color = hotspots.getPixel(xRaw, yRaw);
				}
				
				TextView tv = (TextView) findViewById(R.id.drawer_message);
				
				Drawable drawableDisplay;

				if (color == COLOR_AREA_HEAD) {
					symptomId = CODE_AREA_HEAD;
					drawableDisplay = drawableHead;
				} else if (color == COLOR_AREA_TORSO) {
					symptomId = CODE_AREA_TORSO;
					drawableDisplay = drawableTorso;
				} else if (color == COLOR_AREA_PELVIS) {
					symptomId = CODE_AREA_PELVIS;
					drawableDisplay = drawablePelvis;
				} else if (color == COLOR_AREA_LEGS) {
					symptomId = CODE_AREA_LEGS;
					drawableDisplay = drawableLegs;
				} else if (color == COLOR_AREA_ARMS) {
					symptomId = CODE_AREA_ARMS;
					drawableDisplay = drawableArms;
				} else {
					symptomId = 0;
					drawableDisplay = drawableNone;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageDrawable(drawableDisplay);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					iv.setImageDrawable(drawableOrig);

					if (symptomId != 0) {

						Intent LoginScreen = new Intent(
								getApplicationContext(), SelectSymptom.class);
						Bundle b = new Bundle();
						b.putInt("symptomId", symptomId);
						b.putString("path", "Start");

						LoginScreen.putExtras(b);
						startActivity(LoginScreen);
					}
				}

				return true;
			}
		});

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
}
