package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSourceEmergency {

	// Table description
	public static final String TABLE_EMERG = "emergency_services";
	public static final String TABLE_EMERG_COL_ID = "_id";
	public static final String TABLE_EMERG_COL_NAME = "name";
	public static final String TABLE_EMERG_COL_LONGITUDE = "longitude";
	public static final String TABLE_EMERG_COL_LATITUDE = "latitude";
	public static final String TABLE_EMERG_COL_PHONE_NUMBER = "phone_number";
	public static final String TABLE_EMERG_COL_ENABLED = "enabled";
	
	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private String tableName = TABLE_EMERG;
	private String[] allColumns = { TABLE_EMERG_COL_ID, TABLE_EMERG_COL_NAME,
			TABLE_EMERG_COL_LONGITUDE, TABLE_EMERG_COL_LATITUDE,
			TABLE_EMERG_COL_PHONE_NUMBER };

	public DataSourceEmergency(Context context) {
		this.dbHelper = new DataBaseHelper(context);
		dbHelper.createDataBase();
		dbHelper.openDataBase();
		this.db = dbHelper.getDb();
	}

	public void close() {
		this.dbHelper.close();
	}


	// select user
	public ArrayList<DbEmergency> selectAllDbEmerg() {
		ArrayList<DbEmergency> allEmergs = new ArrayList<DbEmergency>();

		Cursor cursor = null;
		try {
			// defines query parameters
			String[] columns = allColumns;
			String selection = TABLE_EMERG_COL_ENABLED + " = ?";
			String[] selectionArgs = new String[] { "1" };
							
			cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);			
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DbEmergency emerg = cursorToEmerg(cursor);
				allEmergs.add(emerg);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}


		return allEmergs;
	}


	private DbEmergency cursorToEmerg(Cursor cursor) {
		DbEmergency dbe = new DbEmergency();

		dbe.setId(cursor.getInt(0));
		dbe.setName(cursor.getString(1));
		dbe.setLongitute(cursor.getDouble(2));
		dbe.setLatitude(cursor.getDouble(3));
		dbe.setPhoneNumber(cursor.getString(4));

		return dbe;
	}
}
