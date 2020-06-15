package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Message;
import in.spoors.effort1.provider.EffortProvider.Messages;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class MessagesDao {

	public static final String TAG = "MessagesDao";
	private static MessagesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static MessagesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new MessagesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private MessagesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean messageWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Messages._ID
					+ ") AS count FROM " + Messages.TABLE + " WHERE "
					+ Messages._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(Message message) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (messageWithIdExists(message.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing message.");
			}

			message.setLocalModificationTime(new Date());
			message.setLocalCreationTime(getLocalCreationTimeWithId(message
					.getId()));

			ContentValues values = message.getContentValues(null);
			db.update(Messages.TABLE, values,
					Messages._ID + " = " + message.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new message.");
			}

			Date now = new Date();
			message.setLocalCreationTime(now);
			message.setLocalModificationTime(now);

			ContentValues values = message.getContentValues(null);
			db.insert(Messages.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(Messages.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " messages.");
		}
	}

	/**
	 * Delete the given message.
	 */
	public synchronized int deleteMessage(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(Messages.TABLE, Messages._ID + " = " + id,
				null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " messages.");
		}

		return affectedRows;
	}

	/**
	 * Delete the given messages.
	 */
	public synchronized int deleteMessages(List<Long> ids) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(Messages.TABLE, Messages._ID + " IN ("
				+ TextUtils.join(",", ids) + ")", null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " messages.");
		}

		return affectedRows;
	}

	public Message getMessage(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		Message message = null;

		try {
			cursor = db.query(Messages.TABLE, Messages.ALL_COLUMNS,
					Messages._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				message = new Message();
				message.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return message;
	}

	/**
	 * Returns the local creation time of the given message id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithId(Long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Messages.LOCAL_CREATION_TIME
					+ " FROM " + Messages.TABLE + " WHERE " + Messages._ID
					+ " = " + id, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getAddedCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Messages._ID + ") FROM "
					+ Messages.TABLE + " WHERE " + Messages.LOCAL_CREATION_TIME
					+ " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"addedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr });
			cursor.moveToNext();
			int count = cursor.getInt(0);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "addedCount: " + count);
			}

			return count;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Message getMostRecentMessage() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		Message message = null;

		try {
			// public Cursor query (String table, String[] columns, String
			// selection, String[] selectionArgs,
			// String groupBy, String having, String orderBy, String limit)

			cursor = db.query(Messages.TABLE, Messages.ALL_COLUMNS, null, null,
					null, null, Messages.REMOTE_CREATION_TIME + " DESC", "1");

			if (cursor.moveToNext()) {
				message = new Message();
				message.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return message;
	}

}
