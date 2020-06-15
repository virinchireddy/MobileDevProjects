package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Invitation;
import in.spoors.effort1.provider.EffortProvider.Invitations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.BuildConfig;

public class InvitationsDao {

	public static final String TAG = "InvitationsDao";
	private static InvitationsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static InvitationsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new InvitationsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private InvitationsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean invitationWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Invitations._ID
					+ ") AS count FROM " + Invitations.TABLE + " WHERE "
					+ Invitations._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean invitationsAreDifferent(Invitation invitation1,
			Invitation invitation2) {
		if (invitation1 == null && invitation2 == null) {
			return false;
		}

		if (invitation1 == null || invitation2 == null) {
			return true;
		}

		// both the jobs are not null
		if (!Utils.datesEqual(invitation1.getStartTime(),
				invitation2.getStartTime())
				|| !Utils.datesEqual(invitation1.getEndTime(),
						invitation2.getEndTime())
				|| !TextUtils.equals(invitation1.getDescription(),
						invitation2.getDescription())
				|| !TextUtils.equals(invitation1.getTitle(),
						invitation2.getTitle())
				|| !Utils.longsEqual(invitation1.getLocalCustomerId(),
						invitation2.getLocalCustomerId())) {
			return true;
		}

		return false;
	}

	/**
	 * Saves the given invitation.
	 * 
	 * @param invitation
	 */
	public synchronized void save(Invitation invitation) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Date now = new Date();

		if (invitation.getLocalCustomerId() == null
				&& invitation.getRemoteCustomerId() != null) {
			CustomersDao customersDao = CustomersDao
					.getInstance(applicationContext);
			invitation.setLocalCustomerId(customersDao.getLocalId(invitation
					.getRemoteCustomerId()));
		}

		if (invitation.getId() != null
				&& invitationWithIdExists(invitation.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting invitation.");
			}

			Invitation oldInvitation = getInvitationWithId(invitation.getId());

			if (invitationsAreDifferent(invitation, oldInvitation)) {
				invitation.setLocalModificationTime(new Date());
			}

			if (invitation.getLocalCreationTime() == null) {
				invitation.setLocalCreationTime(oldInvitation
						.getLocalCreationTime());
			}

			ContentValues values = invitation.getContentValues(null);

			db.update(Invitations.TABLE, values, Invitations._ID + " = "
					+ invitation.getId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new invitation.");
		}

		invitation.setLocalCreationTime(now);
		invitation.setLocalModificationTime(now);

		ContentValues values = invitation.getContentValues(null);
		db.insert(Invitations.TABLE, null, values);
	}

	/**
	 * Completes the given job.
	 * 
	 * @param job
	 */
	public synchronized void acceptInvitation(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Invitation invitation = getInvitationWithId(id);

		if (invitation == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Cannot accept invitation with invitation ID " + id
						+ ". It doesn't exist!");
			}

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Accepting the invitation with ID " + id);
		}

		Date now = new Date();
		invitation.setLocalModificationTime(now);
		invitation.setAccepted(true);
		invitation.setDirty(true);

		ContentValues values = invitation.getContentValues(null);

		db.update(Invitations.TABLE, values, Invitations._ID + " = "
				+ invitation.getId(), null);
	}

	/**
	 * Returns the local creation time of the given local job id.
	 * 
	 * @param id
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithId(Long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Invitations.LOCAL_CREATION_TIME
					+ " FROM " + Invitations.TABLE + " WHERE "
					+ Invitations._ID + " = " + id, null);

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

	public synchronized int deleteOldInvitations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		Cursor cursor = db.query(Invitations.TABLE,
				new String[] { Invitations._ID }, Invitations.END_TIME
						+ " < ?  AND " + Invitations.DIRTY + " = ? AND "
						+ Invitations.LOCAL_MODIFICATION_TIME + " < ?",
				new String[] { today, "false", today }, null, null, null);

		while (cursor.moveToNext()) {
			deleteInvitation(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " old job invitations.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized void deleteAllInvitations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Invitations.TABLE,
				new String[] { Invitations._ID }, null, null, null, null, null);

		while (cursor.moveToNext()) {
			deleteInvitation(cursor.getLong(0));
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + cursor.getCount() + " invitations.");
		}

		cursor.close();
	}

	public synchronized void deleteInvitation(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Invitations.TABLE, Invitations._ID
				+ " = ?", new String[] { "" + id });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted invitation with id: " + id + ", affectedRows="
					+ affectedRows);
		}
	}

	public Invitation getInvitationWithId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Invitation invitation = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Invitations.TABLE, Invitations.ALL_COLUMNS,
					Invitations._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				invitation = new Invitation();
				invitation.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return invitation;
	}

	/**
	 * Get the invitations that are accepted on the device, and need to be
	 * synced.
	 * 
	 * @return
	 */
	public List<Long> getAcceptedInvitations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> invitations = null;

		try {
			cursor = db.query(Invitations.TABLE,
					new String[] { Invitations._ID }, Invitations.ACCEPTED
							+ " = 'true' AND " + Invitations.DIRTY
							+ " = 'true'", null, null, null,
					Invitations.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				invitations = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				invitations.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return invitations;
	}

	public int getAddedCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Invitations._ID + ") FROM "
					+ Invitations.TABLE + " WHERE "
					+ Invitations.LOCAL_CREATION_TIME + " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"addedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr });

			cursor.moveToNext();

			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getModifiedCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Invitations._ID + ") FROM "
					+ Invitations.TABLE + " WHERE "
					+ Invitations.LOCAL_CREATION_TIME + " <= ? AND "
					+ Invitations.LOCAL_MODIFICATION_TIME + " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"modifiedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr, afterStr });

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateDirtyFlag(long localJobId, boolean dirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Invitations.DIRTY, "" + dirty);

		db.update(Invitations.TABLE, values, Invitations._ID + " = "
				+ localJobId, null);
	}

	/**
	 * 
	 * @param remoteId
	 */
	public boolean getRead(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Invitations.READ + " FROM "
					+ Invitations.TABLE + " WHERE " + Invitations._ID + " = "
					+ localId, null);

			if (cursor.moveToNext()) {
				String read = cursor.getString(0);

				if (TextUtils.isEmpty(read)) {
					return false;
				} else {
					return Boolean.parseBoolean(read);
				}
			} else {
				return false;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateReadFlag(long localJobId, boolean read) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Invitations.READ, "" + read);

		db.update(Invitations.TABLE, values, Invitations._ID + " = "
				+ localJobId, null);
	}

	public synchronized void updateReadFlag(boolean read) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("UPDATE " + Invitations.TABLE + " SET " + Invitations.READ
				+ " = '" + read + "'");
	}
}
