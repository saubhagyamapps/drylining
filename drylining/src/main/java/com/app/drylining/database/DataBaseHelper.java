package com.app.drylining.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

	@SuppressLint("SdCardPath")
	private static String DB_PATH;
	private static String DB_NAME = "data.sqlite";
	public SQLiteDatabase myDataBase;
	private final Context myContext;
	private SharedPreferences preferences;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */

	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		DB_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/";
		AppDebugLog.println("DB_PATH : " + DB_PATH);
		this.myContext = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(myContext);

		this.createDatabse();
		this.openDataBase();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a empty database on the system and rewrites it with our own
	 * database.
	 * */
	public boolean createDatabse() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {

			// By calling this method and empty database will be created into
			// the default system path
			// of application so we are able to overwrite that database with our
			// database.
			try {
				AppDebugLog.println("DB Path : " + DB_PATH);
				this.getReadableDatabase();

				copyDataBase();
				dbExist = true;
			} catch (IOException e) {
				dbExist = false;
				AppDebugLog.println("Error copying data" + e.toString() + e.getMessage());
			}
		}
		return dbExist;
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */

	public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			File file = new File(myPath);
			if (file.exists()) {
				if (checkDB == null || !checkDB.isOpen()) {
					checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY
							+ SQLiteDatabase.CREATE_IF_NECESSARY);
				}
			}

		} catch (SQLiteException e) {

			// database does't exist yet.
			AppDebugLog.println("Exception " + e.getMessage());

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies database from your local assets-folder to the just created empty
	 * database in the system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */

	public void copyDataBase() throws IOException {

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
		preferences.edit().putInt("dbVersion", AppConstant.DB_VERSION).commit();

	}

	public void deleteOldDB() {
		String oldDBPath = DB_PATH + DB_NAME;
		File oldDB = new File(oldDBPath);
		if (oldDB.exists()) {
			close();
			oldDB.delete();
		}
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (newVersion > oldVersion) {
			try {
				copyDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

	public void emptyTable(String tableName) {
		myDataBase.delete(tableName, "1", null);
	}

	public long insertContent(String tableName, ContentValues colonValues) {
		return myDataBase.insert(tableName, null, colonValues);
	}

}
