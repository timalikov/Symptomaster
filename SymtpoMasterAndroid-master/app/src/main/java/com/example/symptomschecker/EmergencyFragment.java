package com.example.symptomschecker;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyFragment extends Fragment implements SensorEventListener {
	private LocationManager locationManager;
	private String provider = LocationManager.NETWORK_PROVIDER;
	private Button emergCallUi, emergNoUi;
	private TextView emergNameUi;
	DbEmergency targetEmerg;
	private SensorManager sensorManager;
	private long lastUpdate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_emergency, container,
				false);

		DataSourceEmergency dse = new DataSourceEmergency(getActivity());
		ArrayList<DbEmergency> allEmergencies = dse.selectAllDbEmerg();

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		String provider = LocationManager.NETWORK_PROVIDER;
		Location l = locationManager.getLastKnownLocation(provider);

		double latitude, longitude;
		String locationFailed = "";
		if(l == null) {
			// no location, provide mock one
			latitude = 38.897857;
			longitude = -77.049347;	
			//locationFailed = " no GPS";
		} else {		
			latitude = l.getLatitude();
			longitude = l.getLongitude();
		}

		targetEmerg = getClosestEmergency(latitude, longitude, allEmergencies);
		emergNameUi = (TextView) view.findViewById(R.id.emergency_name);
		emergCallUi = (Button) view.findViewById(R.id.emergency_number);

		String distInMiles = String.format("%.2f", targetEmerg.getDistanceTo());

		emergNameUi.setText(targetEmerg.getName() + " (" + distInMiles
				+ " miles)" + locationFailed);
		emergCallUi.setText(targetEmerg.getPhoneNumber());

		getActivity().getActionBar().setTitle(getString(R.string.emerg_call));

		SensorManager sensorMgr = (SensorManager) getActivity()
				.getSystemService(getActivity().SENSOR_SERVICE);
		sensorManager = (SensorManager) getActivity().getSystemService(
				getActivity().SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();

		emergCallUi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				callEmergencyNumber(targetEmerg.getPhoneNumber());
			}
		});

		return view;
	}

	private void callEmergencyNumber(String number) {
		Intent callImplicitIntent = new Intent(Intent.ACTION_CALL);
		callImplicitIntent.setData(Uri.parse("tel:" + number));
		startActivity(callImplicitIntent);
	}

	private DbEmergency getClosestEmergency(double curLatitude,
			double curLongitude, ArrayList<DbEmergency> allEmerg) {
		DbEmergency emerg = new DbEmergency();
		double targetLatitude = 0, targetLongitude = 0, hypotenuse = 0, minSoFar = 999999999;

		for (DbEmergency dbe : allEmerg) {
			targetLatitude = dbe.getLatitude();
			targetLongitude = dbe.getLongitute();

			hypotenuse = differnceInMiles(targetLatitude, targetLongitude,
					curLatitude, curLongitude);

			if (hypotenuse < minSoFar) {
				minSoFar = hypotenuse;
				emerg = dbe;
				emerg.setDistanceTo(hypotenuse);
			}
		}

		return emerg;
	}

	/**
	 * This function computes distance between 2 points in miles Implements
	 * Haversine formula
	 */
	public static double differnceInMiles(double targetLat, double targetLong,
			double sourseLat, double sourceLong) {
		double earthRadius = 3959;
		double dLat = Math.toRadians(sourseLat - targetLat);
		double dLng = Math.toRadians(sourceLong - targetLong);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(targetLat))
				* Math.cos(Math.toRadians(sourseLat));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;

		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 5) //
		{
			if (actualTime - lastUpdate < 200) {
				return;
			}
			lastUpdate = actualTime;
			callEmergencyNumber(targetEmerg.getPhoneNumber());
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

}
