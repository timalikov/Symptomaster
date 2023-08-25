package com.example.symptomschecker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSourceHistory {

	// Table description
	public static final String TABLE_HIST = "history";
	public static final String TABLE_HIST_COL_ID = "_id";
	public static final String TABLE_HIST_COL_USER_ID = "user_id";
	public static final String TABLE_HIST_COL_SEQUENCE = "sequence";
	public static final String TABLE_HIST_COL_RESULT = "result";
	public static final String TABLE_HIST_COL_DATE = "date";

	private String iv = "ABAFACAFAA5ABBAA";
	private String key = "0123456789abcdef";

	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private String[] allColumns = { TABLE_HIST_COL_ID, TABLE_HIST_COL_USER_ID,
			TABLE_HIST_COL_SEQUENCE, TABLE_HIST_COL_RESULT, TABLE_HIST_COL_DATE };

	public DataSourceHistory(Context context) {
		this.dbHelper = new DataBaseHelper(context);
		dbHelper.createDataBase();
		dbHelper.openDataBase();
		this.db = dbHelper.getDb();
	}

	public void close() {
		this.dbHelper.close();
	}

	/**
	 * insert new history
	 */
	public int insertNewHistory(DbHistory h) {
		int out = 0;

		// Sensitive data encryption
		DataEncryptor de = new DataEncryptor();
		de.setIV(iv); // set initialization vector
		byte[] sequenceCipher = null, resultCipher = null;
		try {
			sequenceCipher = de.encrypt(h.getSequence(), key);
			resultCipher = de.encrypt(h.getResult(), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// end encryption

		// store to db
		try {
			ContentValues insertValues = new ContentValues();
			insertValues.put(TABLE_HIST_COL_USER_ID, h.getUserId());
			insertValues.put(TABLE_HIST_COL_SEQUENCE, sequenceCipher);
			insertValues.put(TABLE_HIST_COL_RESULT, resultCipher);
			insertValues.put(TABLE_HIST_COL_DATE, h.getDate());

			out = (int) this.db.insert(TABLE_HIST, null, insertValues);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * Deletes a record from db
	 */
	public void deleteHistoryRecord(int historyId) {
		try {
			String selection = TABLE_HIST_COL_ID + " = ?";
			String[] selectionArgs = new String[] { "" + historyId };
			db.delete(TABLE_HIST, selection, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Selects all records
	 * 
	 * @param uId
	 * @return
	 */
	public ArrayList<DbHistory> selectAllHistoryForUser(int uId) {
		ArrayList<DbHistory> allHistory = new ArrayList<DbHistory>();

		Cursor cursor = null;
		try {
			// defines query parameters
			String[] columns = allColumns;
			String selection = TABLE_HIST_COL_USER_ID + " = ?";
			String[] selectionArgs = new String[] { "" + uId };
			String orderBy = TABLE_HIST_COL_ID + " DESC";
			cursor = db.query(TABLE_HIST, columns, selection, selectionArgs,
					null, null, orderBy);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DbHistory history = cursorToHist(cursor);
				allHistory.add(history);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return allHistory;
	}

	private DbHistory cursorToHist(Cursor cursor) {
		DbHistory h = new DbHistory();

		h.setId(cursor.getInt(0));
		h.setUserId(cursor.getInt(1));

		// Decrypt sensitive data
		DataEncryptor de = new DataEncryptor();
		de.setIV(iv); // set initialization vector

		String decrSequence = "", decrResult = "";
		try {
			decrSequence = de.decrypt(cursor.getBlob(2), key);
			decrResult = de.decrypt(cursor.getBlob(3), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// End sensitive data decryption

		h.setSequence(decrSequence);
		h.setResult(decrResult);
		h.setDate(cursor.getString(4));

		return h;
	}
}
