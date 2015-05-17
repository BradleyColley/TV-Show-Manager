package com.project.tv_manager.database;

/*
 * This class is responsible for generating the database I've created using 
 * SQLiteBrowser application to create the tables and input data. The methods 
 * below will perform SQL operations to operate the database.
 * 
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.project.tv_manager.Global.*;

public class SQLOperations extends SQLiteOpenHelper {
	private boolean isAvailable;
	private final Context c;
	private SQLiteDatabase tvShow_DB;

	public SQLOperations(Context context) {
		super(context, DATABASE_NAME, null, 1);
		this.c = context;
		Log.d("Database operations", "Database created");
	}

	/*
	 * This method is responsible for loading the created database file
	 * within assets folder.
	 */
	public void generateDB() {
		if(tvShow_DB == null) { //If the database is null then try loading the path file.
			try {
				tvShow_DB = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
				isAvailable = true;
			} catch (SQLException e) {
				Log.e(this.getClass().toString(), "Error while checking db");
			}
		}
		else if (tvShow_DB != null) { //If it isn't null then update Boolean variable to false.
			isAvailable = false;
		}

		if(isAvailable == false) { //If the database isn't available then copy file.
			try {
				this.getReadableDatabase(); 
				copyDB(); //Runs method.
				Log.d("Database operations", "Database copied");
			} catch (IOException e) {
				throw new Error("Cannot add TV show database");
			}
		}
	}

	/*
	 * This method will copy over the database file
	 * replacing the old version currently installed by using 
	 * input and output streams.
	 */
	private void copyDB() throws IOException {
		try {
			InputStream inputS = c.getAssets().open(DATABASE_NAME);
			OutputStream ouputS = new FileOutputStream(PATH);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputS.read(buffer)) > 0) {
				ouputS.write(buffer, 0, length);
			}

			ouputS.flush(); //Save
			ouputS.close(); //Close output stream.
			inputS.close(); //Close input stream.

		} catch (IOException e) {
			throw new Error("Cannot copy database");
		}
	}

	/*
	 * This method will open the database path.
	 */
	public SQLiteDatabase openDataBase() throws SQLException {   
		tvShow_DB = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);     
		return tvShow_DB;
	}  


	/*
	 * This method will input information into table 4 within the database once
	 * called upon.
	 */
	public long putInformation(String name, String season, String number, String ep_name, int pos, String related) {
		ContentValues add = new ContentValues();
		add.put(TV_NAME, name);
		add.put(SEASON, season);
		add.put(EP_NUMBER, number);
		add.put(EP_NAME, ep_name);
		add.put(POSITION, pos);
		add.put(RELATED, related);
		Log.d("Database operations", "One raw inserted");
		return tvShow_DB.insert(TABLE_NAME_04, null, add);
	}

	/*
	 * This method will delete a specific row in table 4 once its been called upon.
	 */
	public void delete_byID(int id){
		tvShow_DB.delete(TABLE_NAME_04, COLUMN_ID+"="+id, null);
	}

	/*
	 * This method will add notification data into table 5 once called upon.
	 */
	public long addNotification(int id, String name, String season, String number, String time, String date) {
		ContentValues add = new ContentValues();
		add.put(POSITION, id);
		add.put(TV_NAME, name);
		add.put(SEASON, season);
		add.put(EP_NUMBER, number);
		add.put(TIME, time);
		add.put(DATE, date);
		return tvShow_DB.insert(TABLE_NAME_05, null, add);
	}
	
	/*
	 * This method will delete a specific row in table 5 once its been called upon.
	 */
	public void deleteRow(int id) {
		tvShow_DB.delete(TABLE_NAME_05, COLUMN_ID+"="+id, null);
	}
	
	/*
	 * This method will delete a specific row in table 5 once its been called upon.
	 */
	public void deleteRow2(int id) {
		tvShow_DB.delete(TABLE_NAME_05, POSITION+"="+id, null);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
