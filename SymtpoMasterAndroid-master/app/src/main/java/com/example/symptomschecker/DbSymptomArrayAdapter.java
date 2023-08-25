package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DbSymptomArrayAdapter extends ArrayAdapter<DbSymptom> {

	private final Context context;
	private final ArrayList<DbSymptom> values;

	public DbSymptomArrayAdapter(Context context, ArrayList<DbSymptom> values) {
		super(context, R.layout.symptom_child_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.symptom_child_item, parent,
				false);

		// set name
		TextView viewSymptomName = (TextView) rowView
				.findViewById(R.id.symptom_child_item_name);
		viewSymptomName.setText(values.get(position).getName());

		
		
		
		
		return rowView;
	}

}
