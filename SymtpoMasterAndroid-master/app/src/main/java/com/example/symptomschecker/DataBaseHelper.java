package com.example.symptomschecker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/com.example.symptomschecker/databases/";
	private static String DB_NAME = "symptoms.sqlite";
	private SQLiteDatabase db;
	private final Context myContext;

	/**
	 * Default constructor
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() {

		if (!checkIfDbExists()) {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();
				System.out.println("asda");

			} catch (Exception e) {

				System.out.println("asd");
			}
		} else {

		}

	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkIfDbExists() {

		SQLiteDatabase dbExists = null;

		try {
			String myPath = DB_PATH + DB_NAME;

			System.out.println("sss");

			dbExists = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
			System.out.println("asdasd");

		} catch (SQLiteException e) {
			System.out.println("fail");
			// database does't exist yet.

		}

		if (dbExists != null) {
			dbExists.close();
		}

		return dbExists != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() {

		try {
			// Open your local db as the input stream
			InputStream myInput = myContext.getAssets().open(DB_NAME);

			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
			
			System.out.println("copied");
			
		} catch (Exception e) {
			System.out.println("fail");
			// database does't exist yet.

		}

	}

	public void openDataBase() {
		try {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		} catch(Exception e){
    		System.out.println("fail");
    		//database does't exist yet.
 
    	}
 
	}

	@Override
	public synchronized void close() {

		if (db != null)
			db.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
