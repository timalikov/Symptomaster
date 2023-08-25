package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSourceUsers {

	// Table description
	public static final String TABLE_USERS = "users";
	public static final String TABLE_USERS_COL_ID = "_id";
	public static final String TABLE_USERS_COL_PASSWORD = "password";
	public static final String TABLE_USERS_COL_SALT = "salt";
	public static final String TABLE_USERS_COL_FIRSTNAME = "firstname";
	public static final String TABLE_USERS_COL_LASTNAME = "lastname";
	public static final String TABLE_USERS_COL_EMAIL = "email";
	public static final String TABLE_USERS_COL_GENDER = "gender";
	public static final String TABLE_USERS_COL_DOB = "dob";
	public static final String TABLE_USERS_COL_HEIGHT = "height";
	public static final String TABLE_USERS_COL_WEIGHT = "weight";

	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private String tableName = TABLE_USERS;
	private String[] allColumns = { TABLE_USERS_COL_ID, TABLE_USERS_COL_EMAIL,
			TABLE_USERS_COL_PASSWORD, TABLE_USERS_COL_SALT,
			TABLE_USERS_COL_FIRSTNAME, TABLE_USERS_COL_LASTNAME,
			TABLE_USERS_COL_GENDER, TABLE_USERS_COL_DOB,
			TABLE_USERS_COL_HEIGHT, TABLE_USERS_COL_WEIGHT };

	public DataSourceUsers(Context context) {
		this.dbHelper = new DataBaseHelper(context);
		dbHelper.createDataBase();
		dbHelper.openDataBase();
		this.db = dbHelper.getDb();
	}

	public void close() {
		this.dbHelper.close();
	}

	/**
	 * insert new user
	 */
	public int insertNewUser(DbUser u) {
		int out = 0;

		try {
			ContentValues insertValues = new ContentValues();
			insertValues.put(TABLE_USERS_COL_EMAIL, u.getEmail());
			insertValues.put(TABLE_USERS_COL_PASSWORD, u.getPassword());
			insertValues.put(TABLE_USERS_COL_SALT, u.getSalt());
			insertValues.put(TABLE_USERS_COL_FIRSTNAME, u.getFirstname());
			insertValues.put(TABLE_USERS_COL_LASTNAME, u.getLastname());
			insertValues.put(TABLE_USERS_COL_GENDER, u.getGender());
			insertValues.put(TABLE_USERS_COL_DOB, u.getDob());
			insertValues.put(TABLE_USERS_COL_HEIGHT, u.getHeight());
			insertValues.put(TABLE_USERS_COL_WEIGHT, u.getWeight());

			out = (int) this.db.insert(TABLE_USERS, null, insertValues);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * Update user
	 * 
	 * @param user
	 */
	public void updateUser(DbUser u) {

		try {
			ContentValues updateValues = new ContentValues();

			String strFilter = "_id=" + u.getId();

			updateValues.put(TABLE_USERS_COL_EMAIL, u.getEmail());
			updateValues.put(TABLE_USERS_COL_FIRSTNAME, u.getFirstname());
			updateValues.put(TABLE_USERS_COL_LASTNAME, u.getLastname());
			updateValues.put(TABLE_USERS_COL_GENDER, u.getGender());
			updateValues.put(TABLE_USERS_COL_DOB, u.getDob());
			updateValues.put(TABLE_USERS_COL_HEIGHT, u.getHeight());
			updateValues.put(TABLE_USERS_COL_WEIGHT, u.getWeight());

			this.db.update("users", updateValues, strFilter, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// select user
	public DbUser selectUser(int uId) {
		DbUser u = new DbUser();

		Cursor cursor = null;
		try {
			// defines query parameters
			String[] columns = allColumns;
			String selection = TABLE_USERS_COL_ID + " = ?";
			String[] selectionArgs = new String[] { "" + uId };

			cursor = db.query(tableName, columns, selection, selectionArgs,
					null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cursor.moveToFirst();
		u = cursorToUser(cursor);

		cursor.close();

		return u;
	}

	// select user
	public Validator isValidEmail(String email) {
		Validator res = new Validator();
		res.setVerified(false);

		DbUser u = new DbUser();

		Cursor cursor = null;
		try {
			// defines query parameters
			String[] columns = allColumns;
			String selection = TABLE_USERS_COL_EMAIL + " = ?";
			String[] selectionArgs = new String[] { email };

			cursor = db.query(tableName, columns, selection, selectionArgs,
					null, null, null);

			cursor.moveToFirst();
			u = cursorToUser(cursor);
			cursor.close();

			res.setVerified(true);
			return res;

		} catch (Exception e) {
			return res;

		}

	}

	/**
	 * Selects user by email
	 * 
	 * @param email
	 * @return DbUser
	 */
	public DbUser selectUserByEmail(String email) {
		DbUser u = new DbUser();

		Cursor cursor = null;
		try {
			// defines query parameters
			String[] columns = allColumns;
			String selection = TABLE_USERS_COL_EMAIL + " = ?";
			String[] selectionArgs = new String[] { email };

			cursor = db.query(tableName, columns, selection, selectionArgs,
					null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cursor.moveToFirst();
		u = cursorToUser(cursor);

		cursor.close();

		return u;
	}

	// update user

	private DbUser cursorToUser(Cursor cursor) {
		DbUser u = new DbUser();

		u.setId(cursor.getInt(0));
		u.setEmail(cursor.getString(1));
		u.setPassword(cursor.getString(2));
		u.setSalt(cursor.getString(3));
		u.setFirstname(cursor.getString(4));
		u.setLastname(cursor.getString(5));
		u.setGender(cursor.getInt(6));
		u.setDob(cursor.getString(7));
		u.setHeight(cursor.getInt(8));
		u.setWeight(cursor.getInt(9));

		return u;
	}

}
