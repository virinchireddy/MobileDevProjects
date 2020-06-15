package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SimChangeMessage;
import in.spoors.effort1.provider.EffortProvider.SimCardChangeMessages;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SimCardChangeSmsDao {

	public static final String TAG = "SimCardChangeSmsDao";
	private static SimCardChangeSmsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimCardChangeSmsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SimCardChangeSmsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SimCardChangeSmsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	/*
	 * insert into db, before trying to send
	 */
	public synchronized long save(SimChangeMessage simChangeMessage) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		long insertid = 0;
		if (simChangeMessage != null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving the sms.");
			}
			ContentValues values = simChangeMessage.getContentValues(null);
			insertid = db.insert(SimCardChangeMessages.TABLE, null, values);
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "inserted with id " + insertid);
			}
			simChangeMessage.setId(insertid);
		}
		return insertid;
	}

	/*
	 * delete the processed sms
	 */
	public synchronized void deleteSentSmsRecord(
			SimChangeMessage simChangeMessage) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(SimCardChangeMessages.TABLE,
				SimCardChangeMessages._ID + " = ?", new String[] { ""
						+ simChangeMessage.getId() });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted message with id: " + simChangeMessage.getId()
					+ ", affectedRows=" + affectedRows);
		}
	}

	/*
	 * get all unsent messages
	 */
	public synchronized List<SimChangeMessage> getAllUnsentMessages() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = null;
		List<SimChangeMessage> pendingMessages = new ArrayList<SimChangeMessage>();

		try {
			cursor = db.query(SimCardChangeMessages.TABLE,
					SimCardChangeMessages.ALL_COLUMNS,
					SimCardChangeMessages.STATUS + " = ?",
					new String[] { "false" }, null, null,
					SimCardChangeMessages._ID);

			if (cursor.getCount() > 0) {
				pendingMessages = new ArrayList<SimChangeMessage>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SimChangeMessage pendingMessage = new SimChangeMessage();
				pendingMessage.load(cursor);
				pendingMessages.add(pendingMessage);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return pendingMessages;
	}

}
