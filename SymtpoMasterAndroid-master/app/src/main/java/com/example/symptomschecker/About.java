package com.example.symptomschecker;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.action_share:
			
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			// The intent does not have a URI, so declare the "text/plain" MIME type
			emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {}); // recipients
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.share_subject));
			emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.activity_about));

			//			Resources resources = getResources();
//			Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.screenshot) + '/' + resources.getResourceTypeName(R.drawable.screenshot) + '/' + resources.getResourceEntryName(R.drawable.screenshot) + ".png" );		
//			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
			
			startActivity(Intent.createChooser(emailIntent, getResources().getText(R.string.share)));
						
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
}
