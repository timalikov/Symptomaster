package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSourceSymptoms {
	// Symptoms table description
	public static final String TABLE_SYMPTOMS = "symptoms_list";
	public static final String TABLE_SYMPTOMS_COL_ID = "_id";
	public static final String TABLE_SYMPTOMS_COL_NAME = "name";
	public static final String TABLE_SYMPTOMS_COL_DESCRIPTION = "description";
	public static final String TABLE_SYMPTOMS_COL_QUESTION = "question";
	public static final String TABLE_SYMPTOMS_COL_ENABLED = "enabled";

	// Symptoms table description
	public static final String TABLE_RESULTS = "results_list";
	public static final String TABLE_RESULTS_COL_ID = "_id";
	public static final String TABLE_RESULTS_COL_CAUSE = "cause";
	public static final String TABLE_RESULTS_COL_RECOMMENDATION = "recommendation";
	public static final String TABLE_RESULTS_COL_ENABLED = "enabled";
	
	// Symptoms tree table description
	public static final String TABLE_SYMPTOMS_TREE = "symptoms_tree";
	public static final String TABLE_SYMPTOMS_TREE_COL_PARENT_ID = "parent_symptom_id";
	public static final String TABLE_SYMPTOMS_TREE_COL_CHILD_ID = "child_symptom_id";
	public static final String TABLE_SYMPTOMS_TREE_COL_ENABLED = "enabled";
	public static final String TABLE_SYMPTOMS_TREE_COL_PRIORITY = "priority";

	// Symptoms Results tree table description
	public static final String TABLE_SYMPTOMS_RESULTS_TREE = "symptoms_results_tree";
	public static final String TABLE_SYMPTOMS_RESULTS_TREE_COL_SYMPTOM_ID = "symptom_id";
	public static final String TABLE_SYMPTOMS_RESULTS_TREE_COL_RESULT_ID = "result_id";
	public static final String TABLE_SYMPTOMS_RESULTS_TREE_COL_ENABLED = "enabled";
	public static final String TABLE_SYMPTOMS_RESULTS_TREE_COL_PRIORITY = "priority";

	
	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private String tableName = TABLE_SYMPTOMS;
	private String[] allColumns = { TABLE_SYMPTOMS_COL_ID,
			TABLE_SYMPTOMS_COL_NAME, TABLE_SYMPTOMS_COL_DESCRIPTION,
			TABLE_SYMPTOMS_COL_QUESTION, TABLE_SYMPTOMS_COL_ENABLED };

	
	public DataSourceSymptoms(Context context) {
		this.dbHelper = new DataBaseHelper(context);
		dbHelper.createDataBase();
		dbHelper.openDataBase();
		this.db = dbHelper.getDb();
	}

	public void close() {
		this.dbHelper.close();
	}

	public ArrayList<DbSymptom> selectAllSymptoms() {

		ArrayList<DbSymptom> allSymptoms = new ArrayList<DbSymptom>();

		Cursor cursor = null;
		try {

			// defines query parameters
			String selection = TABLE_SYMPTOMS_COL_ENABLED + " = ?";
			String[] selectionArgs = new String[] { "1" };

			cursor = db.query(tableName, allColumns, selection, selectionArgs,
					null, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DbSymptom masterSymptom = cursorToSymptom(cursor);
			allSymptoms.add(masterSymptom);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return allSymptoms;
	}

	public DbSymptom selectSymptomById(int id) {
		DbSymptom s = new DbSymptom();
		Cursor cursor = null;

		try {
			// 1. get information about the symptom
			String selection = TABLE_SYMPTOMS_COL_ID + " = ?";
			String[] selectionArgs = new String[] { "" + id };

			cursor = db.query(tableName, allColumns, selection, selectionArgs,
					null, null, null);

			cursor.moveToFirst();
			s = cursorToSymptom(cursor);
			cursor.close();

			// 2. set childs for this symptom
			s.setChilds(selectAllSymptomsForParent(s.getId()));
						
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return s;
	}
	
	
	
	
	
	
	
	public ArrayList<DbSymptom> selectAllSymptomsForParent(int parentId) {
		ArrayList<DbSymptom> allSymptoms = new ArrayList<DbSymptom>();

		Cursor cursor = null;
		try {

			String query = "SELECT " + TABLE_SYMPTOMS + ".*" +
		                    " FROM " + TABLE_SYMPTOMS + ", " + TABLE_SYMPTOMS_TREE +
		                   " WHERE " + TABLE_SYMPTOMS + "." + TABLE_SYMPTOMS_COL_ID + " = " + TABLE_SYMPTOMS_TREE + "." + TABLE_SYMPTOMS_TREE_COL_CHILD_ID + 
		                     " AND " + TABLE_SYMPTOMS_TREE + "." + TABLE_SYMPTOMS_TREE_COL_PARENT_ID + " = ?" +
		                     " AND " + TABLE_SYMPTOMS_TREE + "." + TABLE_SYMPTOMS_TREE_COL_ENABLED + " = 1" +
		                     " AND " + TABLE_SYMPTOMS + "." + TABLE_SYMPTOMS_COL_ENABLED + " = 1" +
		                   " ORDER BY " + TABLE_SYMPTOMS_TREE + "." + TABLE_SYMPTOMS_TREE_COL_PRIORITY;	
			String[] selectionArgs = new String[] { "" + parentId };
	
			cursor = db.rawQuery(query, selectionArgs);
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DbSymptom masterSymptom = cursorToSymptom(cursor);
				allSymptoms.add(masterSymptom);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();


		} catch (Exception e) {
			//e.printStackTrace();
		}

		return allSymptoms;
	}

	
	public DbResult selectAllResultsForSymptom(int symptomId) {
		DbResult result = new DbResult();
		
		Cursor cursor = null;
		try {

			String query = "SELECT " + TABLE_RESULTS + ".*" +
		                    " FROM " + TABLE_RESULTS + ", " + TABLE_SYMPTOMS_RESULTS_TREE +
		                   " WHERE " + TABLE_RESULTS + "." + TABLE_RESULTS_COL_ID + " = " + TABLE_SYMPTOMS_RESULTS_TREE + "." + TABLE_SYMPTOMS_RESULTS_TREE_COL_RESULT_ID + 
		                     " AND " + TABLE_SYMPTOMS_RESULTS_TREE + "." + TABLE_SYMPTOMS_RESULTS_TREE_COL_SYMPTOM_ID + " = ?" +
		                     " AND " + TABLE_SYMPTOMS_RESULTS_TREE + "." + TABLE_SYMPTOMS_RESULTS_TREE_COL_ENABLED + " = 1" +
		                     " AND " + TABLE_RESULTS + "." + TABLE_RESULTS_COL_ENABLED + " = 1" +
		                   " ORDER BY " + TABLE_SYMPTOMS_RESULTS_TREE + "." + TABLE_SYMPTOMS_RESULTS_TREE_COL_PRIORITY;			
			String[] selectionArgs = new String[] { "" + symptomId };
	
			cursor = db.rawQuery(query, selectionArgs);
			
			cursor.moveToFirst();
			result = cursorToResult(cursor);

			// make sure to close the cursor
			cursor.close();

		} catch (Exception e) {
			//e.printStackTrace();
		}

		return result;
	}
	

	private DbSymptom cursorToSymptom(Cursor cursor) {
		DbSymptom symptom = new DbSymptom();
		symptom.setId(cursor.getInt(0));
		symptom.setName(cursor.getString(1));
		symptom.setDescription(cursor.getString(2));
		symptom.setQuestion(cursor.getString(3));
		symptom.setEnabled(cursor.getInt(4));

		return symptom;
	}
	
	
	private DbResult cursorToResult(Cursor cursor) {
		DbResult result = new DbResult();

		result.setId(cursor.getInt(0));
		result.setCause(cursor.getString(1));
		result.setRecommendation(cursor.getString(2));
		result.setEnable(cursor.getInt(3));
		
		return result;
	}	
}
