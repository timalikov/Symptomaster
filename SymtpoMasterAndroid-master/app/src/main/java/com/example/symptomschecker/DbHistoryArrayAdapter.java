package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class DbHistoryArrayAdapter extends ArrayAdapter<DbHistory> {

	private final Context context;
	private final ArrayList<DbHistory> values;

	public DbHistoryArrayAdapter(Context context, ArrayList<DbHistory> values) {
		super(context, R.layout.history_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_item, parent, false);

		int id = values.get(position).getId();
		rowView.setId(id);


		// set date
		TextView tv2 = (TextView) rowView.findViewById(R.id.item_hist_date);
		tv2.setText(values.get(position).getDate());

		// set result
		TextView tv3 = (TextView) rowView.findViewById(R.id.item_hist_result);
		tv3.setText(values.get(position).getResult());

		// set sequence
		TextView tv4 = (TextView) rowView.findViewById(R.id.item_hist_sequence);
		tv4.setText(values.get(position).getSequence());

		// button
		Button bt1 = (Button) rowView.findViewById(R.id.item_hist_remove);
		bt1.setId(id);
		bt1.setTag("button_remove");
		
		return rowView;
	}

}
