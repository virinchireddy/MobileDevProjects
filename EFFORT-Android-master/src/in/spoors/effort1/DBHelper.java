package in.spoors.effort1;

import in.spoors.effort1.provider.EffortProvider.ActivitySpecs;
import in.spoors.effort1.provider.EffortProvider.Articles;
import in.spoors.effort1.provider.EffortProvider.AssignedRoutes;
import in.spoors.effort1.provider.EffortProvider.CompletedActivities;
import in.spoors.effort1.provider.EffortProvider.Countries;
import in.spoors.effort1.provider.EffortProvider.CustomerStatus;
import in.spoors.effort1.provider.EffortProvider.CustomerTypes;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Entities;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.EntityFieldValueSpecs;
import in.spoors.effort1.provider.EffortProvider.EntityFields;
import in.spoors.effort1.provider.EffortProvider.EntitySpecs;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.FieldValueSpecs;
import in.spoors.effort1.provider.EffortProvider.Fields;
import in.spoors.effort1.provider.EffortProvider.FormFiles;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.Holidays;
import in.spoors.effort1.provider.EffortProvider.Invitations;
import in.spoors.effort1.provider.EffortProvider.JobHistories;
import in.spoors.effort1.provider.EffortProvider.JobStageStatuses;
import in.spoors.effort1.provider.EffortProvider.JobStates;
import in.spoors.effort1.provider.EffortProvider.JobTypes;
import in.spoors.effort1.provider.EffortProvider.Jobs;
import in.spoors.effort1.provider.EffortProvider.LeaveStatus;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.ListFilteringCriterias;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Messages;
import in.spoors.effort1.provider.EffortProvider.NamedLocations;
import in.spoors.effort1.provider.EffortProvider.NeighboringCellInfos;
import in.spoors.effort1.provider.EffortProvider.Notes;
import in.spoors.effort1.provider.EffortProvider.PageSpecs;
import in.spoors.effort1.provider.EffortProvider.SectionFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.SectionFieldValueSpecs;
import in.spoors.effort1.provider.EffortProvider.SectionFields;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;
import in.spoors.effort1.provider.EffortProvider.SectionSpecs;
import in.spoors.effort1.provider.EffortProvider.Settings;
import in.spoors.effort1.provider.EffortProvider.SimCardChangeMessages;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingDays;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingHours;
import in.spoors.effort1.provider.EffortProvider.TypeStateMappings;
import in.spoors.effort1.provider.EffortProvider.VisibilityCriterias;
import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecMappings;
import in.spoors.effort1.provider.EffortProvider.WorkFlowHistories;
import in.spoors.effort1.provider.EffortProvider.WorkFlowSpecs;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStages;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;
import in.spoors.effort1.provider.EffortProvider.WorkingHours;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TAG = "DBHelper";
	private Context context;
	private static DBHelper instance;
	public static final boolean storeDBOnSDCard = false;

	private static final String SQLITE_SEQUENCE_TABLE = "sqlite_sequence";
	private static final String SEQUENCE_COLUMN = "seq";
	private static final String TABLE_NAME_COLUMN = "name";

	/**
	 * Returns the DBHelper singleton instance.
	 * 
	 * @param context
	 *            This is used for constructing SQLiteOpenHelper and accessing
	 *            values stored in resources.
	 * @return
	 */
	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}

		return instance;
	}

	/**
	 * DBHelper is a singleton. Use the static getInstance(context) method.
	 * 
	 * @param context
	 */
	private DBHelper(Context context) {
		super(context, (storeDBOnSDCard ? Environment
				.getExternalStorageDirectory()
				+ "/"
				+ context.getString(R.string.app_name) + "/" : "")
				+ context.getString(R.string.db_name), null, Integer
				.parseInt(context.getString(R.string.db_version)));
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		long now = System.currentTimeMillis();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: log");
		}

		db.execSQL("DROP TABLE IF EXISTS log");
		db.execSQL("CREATE TABLE log(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "tag TEXT NOT NULL, "
				+ "message TEXT NOT NULL, "
				+ "time TEXT NOT NULL)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + CustomerTypes.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + CustomerTypes.TABLE);
		db.execSQL("CREATE TABLE " + CustomerTypes.TABLE + "("
				+ CustomerTypes._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ CustomerTypes.NAME + " TEXT NOT NULL, "
				+ CustomerTypes.CHECKED + " TEXT NOT NULL)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + JobTypes.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + JobTypes.TABLE);
		db.execSQL("CREATE TABLE " + JobTypes.TABLE + "(" + JobTypes._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ JobTypes.NAME + " TEXT NOT NULL, " + JobTypes.CHECKED
				+ " TEXT NOT NULL, " + JobTypes.DEFAULT_TYPE
				+ " TEXT NOT NULL)");
		db.execSQL("INSERT INTO " + JobTypes.TABLE + "(" + JobTypes._ID + ", "
				+ JobTypes.NAME + ", " + JobTypes.CHECKED + ", "
				+ JobTypes.DEFAULT_TYPE + ") VALUES (0, 'Job', 'true', 'true')");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + JobStates.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + JobStates.TABLE);
		db.execSQL("CREATE TABLE " + JobStates.TABLE + "(" + JobStates._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ JobStates.NAME + " TEXT NOT NULL, " + JobStates.FORM_SPEC_ID
				+ " INTEGER, " + JobStates.FORM_REQUIRED + " TEXT NOT NULL, "
				+ JobStates.START_STATE + " TEXT NOT NULL, "
				+ JobStates.END_STATE + " TEXT NOT NULL, "
				+ JobStates.DEFAULT_STATE + " TEXT NOT NULL, "
				+ JobStates.REVISITABLE + " TEXT NOT NULL, "
				+ JobStates.MIN_SUBMISSIONS + " INTEGER NOT NULL, "
				+ JobStates.MAX_SUBMISSIONS + " INTEGER NOT NULL, "
				+ JobStates.MANDATORY_FOR_COMPLETION + " TEXT NOT NULL)");
		db.execSQL("INSERT INTO "
				+ JobStates.TABLE
				+ "("
				+ JobStates._ID
				+ ", "
				+ JobStates.NAME
				+ ", "
				+ JobStates.FORM_SPEC_ID
				+ ", "
				+ JobStates.FORM_REQUIRED
				+ ", "
				+ JobStates.START_STATE
				+ ", "
				+ JobStates.END_STATE
				+ ", "
				+ JobStates.DEFAULT_STATE
				+ ", "
				+ JobStates.REVISITABLE
				+ ", "
				+ JobStates.MIN_SUBMISSIONS
				+ ", "
				+ JobStates.MAX_SUBMISSIONS
				+ ", "
				+ JobStates.MANDATORY_FOR_COMPLETION
				+ ") VALUES (0, 'Not completed', null, 'false', 'true', 'false', 'true', 'false', 0, 0, 'false')");
		db.execSQL("INSERT INTO "
				+ JobStates.TABLE
				+ "("
				+ JobStates._ID
				+ ", "
				+ JobStates.NAME
				+ ", "
				+ JobStates.FORM_SPEC_ID
				+ ", "
				+ JobStates.FORM_REQUIRED
				+ ", "
				+ JobStates.START_STATE
				+ ", "
				+ JobStates.END_STATE
				+ ", "
				+ JobStates.DEFAULT_STATE
				+ ", "
				+ JobStates.REVISITABLE
				+ ", "
				+ JobStates.MIN_SUBMISSIONS
				+ ", "
				+ JobStates.MAX_SUBMISSIONS
				+ ", "
				+ JobStates.MANDATORY_FOR_COMPLETION
				+ ") VALUES (-1, 'Completed', null, 'false', 'false', 'true', 'true', 'false', 0, 0, 'false')");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + TypeStateMappings.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + TypeStateMappings.TABLE);
		db.execSQL("CREATE TABLE " + TypeStateMappings.TABLE + "("
				+ TypeStateMappings._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ TypeStateMappings.TYPE_ID + " INTEGER NOT NULL, "
				+ TypeStateMappings.STATE_ID + " INTEGER NOT NULL, "
				+ TypeStateMappings.DISPLAY_ORDER + " INTEGER NOT NULL, "
				+ "FOREIGN KEY (" + TypeStateMappings.TYPE_ID + ") REFERENCES "
				+ JobTypes.TABLE + " (" + JobTypes._ID
				+ ") ON DELETE CASCADE ON UPDATE CASCADE, " + "FOREIGN KEY ("
				+ TypeStateMappings.STATE_ID + ") REFERENCES "
				+ JobStates.TABLE + " (" + JobStates._ID
				+ ")  ON DELETE CASCADE ON UPDATE CASCADE)");
		db.execSQL("INSERT INTO " + TypeStateMappings.TABLE + "("
				+ TypeStateMappings._ID + ", " + TypeStateMappings.TYPE_ID
				+ ", " + TypeStateMappings.STATE_ID + ", "
				+ TypeStateMappings.DISPLAY_ORDER + ") VALUES (1, 0, 0, 1)");
		db.execSQL("INSERT INTO " + TypeStateMappings.TABLE + "("
				+ TypeStateMappings._ID + ", " + TypeStateMappings.TYPE_ID
				+ ", " + TypeStateMappings.STATE_ID + ", "
				+ TypeStateMappings.DISPLAY_ORDER + ") VALUES (2, 0, -1, 2)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Jobs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Jobs.TABLE);
		db.execSQL("CREATE TABLE " + Jobs.TABLE + "(" + Jobs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Jobs.REMOTE_ID + " INTEGER, " + Jobs.JOB_TYPE_ID
				+ " INTEGER, " + Jobs.JOB_STATE_ID + " INTEGER, "
				+ Jobs.EMPLOYEE_ID + " INTEGER, " + Jobs.LOCAL_CUSTOMER_ID
				+ " INTEGER, " + Jobs.TITLE + " TEXT, " + Jobs.DESCRIPTION
				+ " TEXT, " + Jobs.START_TIME + " TEXT, " + Jobs.END_TIME
				+ " TEXT, " + Jobs.COMPLETED + " TEXT, " + Jobs.COMPLETION_TIME
				+ " TEXT, " + Jobs.APPROVED + " TEXT, " + Jobs.ANDROID_EVENT_ID
				+ " INTEGER, " + Jobs.DIRTY + " TEXT, " + Jobs.TREE_DIRTY
				+ " TEXT, " + Jobs.READ + " TEXT, " + Jobs.TEMPORARY
				+ " TEXT, " + Jobs.CACHED + " TEXT, "
				+ Jobs.REMOTE_MODIFICATION_TIME + " TEXT, "
				+ Jobs.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ Jobs.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ Jobs.LATITUDE + " REAL, " + Jobs.LONGITUDE + " REAL, "
				+ Jobs.STREET + " TEXT, " + Jobs.AREA + " TEXT, " + Jobs.CITY
				+ " TEXT, " + Jobs.STATE + " TEXT, " + Jobs.COUNTRY + " TEXT, "
				+ Jobs.LANDMARK + " TEXT, " + Jobs.PIN_CODE + " TEXT, "
				+ Jobs.SAME_AS_CUSTOMER_ADDRESS + " TEXT, "
				+ Jobs.LATE_ALERT_DISMISSED + " TEXT)");
		insertSequence(db, Jobs.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + JobStageStatuses.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + JobStageStatuses.TABLE);
		db.execSQL("CREATE TABLE " + JobStageStatuses.TABLE + "("
				+ JobStageStatuses._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ JobStageStatuses.LOCAL_JOB_ID + " INTEGER NOT NULL, "
				+ JobStageStatuses.STATE_ID + " INTEGER NOT NULL, "
				+ JobStageStatuses.DONE + " TEXT NOT NULL, "
				+ JobStageStatuses.DIRTY + " TEXT NOT NULL, "
				+ JobStageStatuses.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ JobStageStatuses.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ JobStageStatuses.TEMPORARY + " TEXT, " + "FOREIGN KEY ("
				+ JobStageStatuses.LOCAL_JOB_ID + ") REFERENCES " + Jobs.TABLE
				+ " (" + Jobs._ID + ") ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "FOREIGN KEY (" + JobStageStatuses.STATE_ID + ") REFERENCES "
				+ JobStates.TABLE + " (" + JobStates._ID
				+ ") ON DELETE CASCADE ON UPDATE CASCADE)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + JobHistories.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + JobHistories.TABLE);
		db.execSQL("CREATE TABLE " + JobHistories.TABLE + "("
				+ JobHistories._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ JobHistories.REMOTE_ID + " INTEGER, "
				+ JobHistories.LOCAL_JOB_ID + " INTEGER, "
				+ JobHistories.LOCAL_FORM_ID + " INTEGER, "
				+ JobHistories.JOB_TYPE_ID + " INTEGER, "
				+ JobHistories.JOB_STATE_ID + " INTEGER, "
				+ JobHistories.EMPLOYEE_ID + " INTEGER, "
				+ JobHistories.LOCAL_CUSTOMER_ID + " INTEGER, "
				+ JobHistories.TITLE + " TEXT, " + JobHistories.DESCRIPTION
				+ " TEXT, " + JobHistories.START_TIME + " TEXT, "
				+ JobHistories.END_TIME + " TEXT, " + JobHistories.COMPLETED
				+ " TEXT, " + JobHistories.COMPLETION_TIME + " TEXT, "
				+ JobHistories.APPROVED + " TEXT, " + JobHistories.DIRTY
				+ " TEXT, " + JobHistories.REMOTE_CREATION_TIME + " TEXT, "
				+ JobHistories.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ JobHistories.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ JobHistories.TEMPORARY + " TEXT)");
		insertSequence(db, JobHistories.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Invitations.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Invitations.TABLE);
		db.execSQL("CREATE TABLE " + Invitations.TABLE + "(" + Invitations._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Invitations.JOB_TYPE_ID + " INTEGER, "
				+ Invitations.LOCAL_CUSTOMER_ID + " INTEGER, "
				+ Invitations.TITLE + " TEXT, " + Invitations.DESCRIPTION
				+ " TEXT, " + Invitations.START_TIME + " TEXT, "
				+ Invitations.END_TIME + " TEXT, " + Invitations.ACCEPTED
				+ " TEXT, " + Invitations.READ + " TEXT, " + Invitations.DIRTY
				+ " TEXT, " + Invitations.LOCAL_CREATION_TIME
				+ " TEXT NOT NULL, " + Invitations.LOCAL_MODIFICATION_TIME
				+ " TEXT NOT NULL, " + Invitations.LATITUDE + " REAL, "
				+ Invitations.LONGITUDE + " REAL, " + Invitations.STREET
				+ " TEXT, " + Invitations.AREA + " TEXT, " + Invitations.CITY
				+ " TEXT, " + Invitations.STATE + " TEXT, "
				+ Invitations.COUNTRY + " TEXT, " + Invitations.LANDMARK
				+ " TEXT, " + Invitations.PIN_CODE + " TEXT)");
		insertSequence(db, Invitations.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Customers.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Customers.TABLE);
		db.execSQL("CREATE TABLE " + Customers.TABLE + "(" + Customers._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Customers.REMOTE_ID + " INTEGER, " + Customers.NAME
				+ " TEXT, " + Customers.CUSTOMER_TYPE_ID + " INTEGER, "
				+ Customers.PHONE + " TEXT, " + Customers.LATITUDE + " REAL, "
				+ Customers.LONGITUDE + " REAL, " + Customers.STREET
				+ " TEXT, " + Customers.AREA + " TEXT, " + Customers.CITY
				+ " TEXT, " + Customers.STATE + " TEXT, " + Customers.COUNTRY
				+ " TEXT, " + Customers.LANDMARK + " TEXT, "
				+ Customers.PIN_CODE + " TEXT, " + Customers.PC_FIRST_NAME
				+ " TEXT, " + Customers.PC_LAST_NAME + " TEXT, "
				+ Customers.PC_TITLE + " TEXT, " + Customers.PC_PHONE
				+ " TEXT, " + Customers.PC_EMAIL + " TEXT, "
				+ Customers.SC_FIRST_NAME + " TEXT, " + Customers.SC_LAST_NAME
				+ " TEXT, " + Customers.SC_TITLE + " TEXT, "
				+ Customers.SC_PHONE + " TEXT, " + Customers.SC_EMAIL
				+ " TEXT, " + Customers.DIRTY + " TEXT, " + Customers.PARTIAL
				+ " TEXT, " + Customers.LOCAL_CREATION_TIME
				+ " TEXT NOT NULL, " + Customers.LOCAL_MODIFICATION_TIME
				+ " TEXT NOT NULL, " + Customers.DISTANCE + " FLOAT, "
				+ Customers.IN_USE + " TEXT, " + Customers.DELETED + " TEXT, "
				+ Customers.CUSTOMER_NUM + " INTEGER)");
		insertSequence(db, Customers.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Notes.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Notes.TABLE);
		db.execSQL("CREATE TABLE " + Notes.TABLE + "(" + Notes._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Notes.REMOTE_ID + " INTEGER, " + Notes.NOTE + " TEXT, "
				+ Notes.NOTE_TYPE + " INTEGER, " + Notes.MIME_TYPE + " TEXT, "
				+ Notes.MEDIA_ID + " INTEGER, " + Notes.MEDIA_TYPE
				+ " INTEGER, " + Notes.LOCAL_MEDIA_PATH + " TEXT, "
				+ Notes.LOCAL_JOB_ID + " INTEGER, " + Notes.STATE
				+ " INTEGER, " + Notes.NOTE_TIME + " TEXT, " + Notes.BY_ID
				+ " INTEGER, " + Notes.BY_NAME + " TEXT, "
				+ Notes.DOWNLOAD_REQUESTED + " TEXT, " + Notes.UPLOAD_REQUESTED
				+ " TEXT, " + Notes.UPLOAD_PRIORITY + " INTEGER, "
				+ Notes.TRANSFER_PERCENTAGE + " INTEGER, " + Notes.FILE_SIZE
				+ " INTEGER, " + Notes.DIRTY + " TEXT, "
				+ Notes.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ Notes.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL)");
		insertSequence(db, Notes.TABLE, now);

		createFormFilesTable(db);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Locations.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Locations.TABLE);
		db.execSQL("CREATE TABLE " + Locations.TABLE + "(" + Locations._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Locations.PURPOSE + " INTEGER NOT NULL, "
				+ Locations.LOCATION_FINALIZED + " TEXT, " + Locations.GPS_ON
				+ " TEXT, " + Locations.GPS_FINALIZED + " TEXT, "
				+ Locations.GPS_CACHED + " TEXT, " + Locations.GPS_FIX_TIME
				+ " TEXT, " + Locations.GPS_ACCURACY + " REAL, "
				+ Locations.GPS_LATITUDE + " REAL, " + Locations.GPS_LONGITUDE
				+ " REAL, " + Locations.GPS_ALTITUDE + " REAL, "
				+ Locations.GPS_SPEED + " REAL, " + Locations.GPS_BEARING
				+ " REAL, " + Locations.NETWORK_ON + " TEXT, "
				+ Locations.NETWORK_FINALIZED + " TEXT, "
				+ Locations.NETWORK_CACHED + " TEXT, "
				+ Locations.NETWORK_FIX_TIME + " TEXT, "
				+ Locations.NETWORK_ACCURACY + " REAL, "
				+ Locations.NETWORK_LATITUDE + " REAL, "
				+ Locations.NETWORK_LONGITUDE + " REAL, " + Locations.CELL_ID
				+ " INTEGER, " + Locations.CELL_LAC + " INTEGER, "
				+ Locations.CELL_PSC + " INTEGER, " + Locations.CELL_MCC
				+ " INTEGER, " + Locations.CELL_MNC + " INTEGER, "
				+ Locations.SIGNAL_STRENGTH + " INTEGER, "
				+ Locations.BATTERY_LEVEL + " INTEGER, " + Locations.CONNECTED
				+ " TEXT, " + Locations.WIFI_CONNECTED + " TEXT, "
				+ Locations.FOR_ID + " INTEGER, " + Locations.DIRTY + " TEXT, "
				+ Locations.SMS_PROCESS_STATE + " TEXT, "
				+ Locations.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ Locations.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ Locations.FRESH_FIX + " TEXT, "
				+ Locations.REMOTE_LOCATION_ID + " INTEGER, "
				+ Locations.REASON_FOR_TRACKING + " INTEGER, "
				+ Locations.FUSED_ON + " TEXT, " + Locations.FUSED_FINALIZED
				+ " TEXT, " + Locations.FUSED_CACHED + " TEXT, "
				+ Locations.FUSED_FIX_TIME + " TEXT, "
				+ Locations.FUSED_ACCURACY + " REAL, "
				+ Locations.FUSED_LATITUDE + " REAL, "
				+ Locations.FUSED_LONGITUDE + " REAL, "
				+ Locations.FUSED_ALTITUDE + " REAL, " + Locations.FUSED_SPEED
				+ " REAL, " + Locations.FUSED_BEARING + " REAL)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + NeighboringCellInfos.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + NeighboringCellInfos.TABLE);
		db.execSQL("CREATE TABLE " + NeighboringCellInfos.TABLE + "("
				+ NeighboringCellInfos._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ NeighboringCellInfos.CELL_ID + " INTEGER, "
				+ NeighboringCellInfos.CELL_LAC + " INTEGER, "
				+ NeighboringCellInfos.CELL_PSC + " INTEGER, "
				+ NeighboringCellInfos.CELL_RSSI + " INTEGER, "
				+ NeighboringCellInfos.CELL_MCC + " INTEGER, "
				+ NeighboringCellInfos.CELL_MNC + " INTEGER, " + " TEXT, "
				+ NeighboringCellInfos.LOCATION_ID + " INTEGER)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Settings.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE);
		db.execSQL("CREATE TABLE " + Settings.TABLE + "(" + Settings._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Settings.KEY + " TEXT NOT NULL, " + Settings.VALUE + " TEXT)");

		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_APP_TITLE + "', '"
				+ context.getString(R.string.app_name) + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_COMMENT_LOCATION_TIMEOUT + "', '"
				+ Settings.DEFAULT_COMMENT_LOCATION_TIMEOUT + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_LOCATION_SHELF_LIFE + "', '"
				+ Settings.DEFAULT_LOCATION_SHELF_LIFE + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_CAN_UPDATE_HOME_LOCATION + "', 'true')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_CAN_EDIT_CUSTOMER_INFO + "', 'true')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_TRACKING_FREQUENCY + "', '"
				+ Settings.DEFAULT_TRACKING_FREQUENCY + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_SMS_TRACK_FREQUENCY + "', '"
				+ Settings.DEFAULT_FALL_BACK_INTERVAL + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_SHOW_COMPLETED_JOBS + "', 'true')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_SORT_ORDER + "', '"
				+ Settings.DEFAULT_SORT_ORDER + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_UPLOAD + "', '" + Settings.DEFAULT_UPLOAD + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_REMINDER_MINUTES + "', '"
				+ Settings.DEFAULT_REMINDER_MINUTES + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_KEEP_LISTENING_FOR_GPS_UPDATES + "', 'false')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_LAST_UPDATE_AT_SMS_SENT
				+ "', '1971-01-01 11:52:29')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_SMSC + "', '7207639067')");// 917207639067
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_LOCATION_PROVIDER + "', '1')"); // Native Android
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_PRINTER_SERVICE_RECORD_UUID + "', '"
				+ Settings.DEFAULT_PRINTER_SERVICE_RECORD_UUID + "')");
		db.execSQL("INSERT INTO Settings(key, value) VALUES('"
				+ Settings.KEY_PRINTER_COLUMN_WIDTH + "', '"
				+ Settings.DEFAULT_PRINTER_COLUMN_WIDTH + "')");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Leaves.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Leaves.TABLE);
		db.execSQL("CREATE TABLE " + Leaves.TABLE + "(" + Leaves._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Leaves.REMOTE_ID + " INTEGER, " + Leaves.STATUS
				+ " INTEGER, " + Leaves.START_TIME + " TEXT, "
				+ Leaves.END_TIME + " TEXT, " + Leaves.EMPLOYEE_REMARKS
				+ " TEXT, " + Leaves.MANAGER_REMARKS + " TEXT, "
				+ Leaves.STATUS_CHANGED_BY_ID + " INTEGER, "
				+ Leaves.STATUS_CHANGED_BY_NAME + " TEXT, " + Leaves.CANCELLED
				+ " TEXT, " + Leaves.DIRTY + " TEXT, "
				+ Leaves.LOCAL_CREATION_TIME + " TEXT, "
				+ Leaves.LOCAL_MODIFICATION_TIME + " TEXT)");
		insertSequence(db, Leaves.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + LeaveStatus.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + LeaveStatus.TABLE);
		db.execSQL("CREATE TABLE " + LeaveStatus.TABLE + " (" + LeaveStatus._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ LeaveStatus.NAME + " TEXT)");
		db.execSQL("INSERT INTO " + LeaveStatus.TABLE + " (" + LeaveStatus._ID
				+ ", " + LeaveStatus.NAME + ") VALUES(0, 'Applied for leave')");
		db.execSQL("INSERT INTO " + LeaveStatus.TABLE + " (" + LeaveStatus._ID
				+ ", " + LeaveStatus.NAME + ") VALUES(1, 'Leave approved')");
		db.execSQL("INSERT INTO " + LeaveStatus.TABLE + " (" + LeaveStatus._ID
				+ ", " + LeaveStatus.NAME + ") VALUES(2, 'Leave rejected')");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Holidays.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Holidays.TABLE);
		db.execSQL("CREATE TABLE " + Holidays.TABLE + "(" + Holidays._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Holidays.TITLE + " TEXT, " + Holidays.DESCRIPTION + " TEXT, "
				+ Holidays.START_TIME + " TEXT, " + Holidays.END_TIME
				+ " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SpecialWorkingDays.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SpecialWorkingDays.TABLE);
		db.execSQL("CREATE TABLE " + SpecialWorkingDays.TABLE + "("
				+ SpecialWorkingDays._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SpecialWorkingDays.TITLE + " TEXT, "
				+ SpecialWorkingDays.DESCRIPTION + " TEXT, "
				+ SpecialWorkingDays.DATE + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SpecialWorkingHours.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SpecialWorkingHours.TABLE);
		db.execSQL("CREATE TABLE " + SpecialWorkingHours.TABLE + "("
				+ SpecialWorkingHours._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SpecialWorkingHours.SPECIAL_WORKING_DAY_ID
				+ " INTEGER NOT NULL, " + SpecialWorkingHours.TITLE + " TEXT, "
				+ SpecialWorkingHours.START_TIME + " TEXT, "
				+ SpecialWorkingHours.END_TIME + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkingHours.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkingHours.TABLE);
		db.execSQL("CREATE TABLE " + WorkingHours.TABLE + "("
				+ WorkingHours._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ WorkingHours.DAY_OF_WEEK + " INTEGER NOT NULL, "
				+ WorkingHours.TITLE + " TEXT, " + WorkingHours.START_TIME
				+ " TEXT, " + WorkingHours.END_TIME + " TEXT)");

		// 1 Sunday is a holiday, 2-7 Monday-Saturday
		for (int dayOfWeek = 2; dayOfWeek <= 7; ++dayOfWeek) {
			insertWorkingHour(db, dayOfWeek);
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + FormSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + FormSpecs.TABLE);
		db.execSQL("CREATE TABLE " + FormSpecs.TABLE + "(" + FormSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ FormSpecs.TITLE + " TEXT, " + FormSpecs.WITHDRAWN + " TEXT, "
				+ FormSpecs.VISIBLE + " TEXT, " + FormSpecs.UNIQUE_ID
				+ " TEXT, " + FormSpecs.PRINT_TEMPLATE + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + FieldSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + FieldSpecs.TABLE);
		db.execSQL("CREATE TABLE " + FieldSpecs.TABLE + "(" + FieldSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ FieldSpecs.FORM_SPEC_ID + " INTEGER, " + FieldSpecs.LABEL
				+ " TEXT, " + FieldSpecs.TYPE + " INTEGER, "
				+ FieldSpecs.IDENTIFIER + " TEXT, " + FieldSpecs.REQUIRED
				+ " TEXT, " + FieldSpecs.DISPLAY_ORDER + " INTEGER, "
				+ FieldSpecs.TYPE_EXTRA + " TEXT, " + FieldSpecs.SELECTOR
				+ " TEXT, " + FieldSpecs.COMPUTED + " TEXT, "
				+ FieldSpecs.FORMULA + " TEXT, " + FieldSpecs.PAGE_ID
				+ " INTEGER, " + FieldSpecs.UNIQUE_ID + " TEXT,"
				+ FieldSpecs.BARCODE + " TEXT," + FieldSpecs.VISIBLE
				+ " INTEGER," + FieldSpecs.EDITABLE + " INTEGER,"
				+ FieldSpecs.MIN_VALUE + " INTEGER," + FieldSpecs.MAX_VALUE
				+ " INTEGER," + FieldSpecs.DEFAULT_FIELD + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + FieldValueSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + FieldValueSpecs.TABLE);
		db.execSQL("CREATE TABLE " + FieldValueSpecs.TABLE + "("
				+ FieldValueSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ FieldValueSpecs.FIELD_SPEC_ID + " INTEGER, "
				+ FieldValueSpecs.VALUE + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Forms.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Forms.TABLE);
		db.execSQL("CREATE TABLE " + Forms.TABLE + "(" + Forms._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Forms.REMOTE_ID + " INTEGER, " + Forms.FORM_SPEC_ID
				+ " INTEGER, " + Forms.FILLED_BY_ID + " INTEGER, "
				+ Forms.FILLED_BY_NAME + " TEXT, " + Forms.MODIFIED_BY_ID
				+ " INTEGER, " + Forms.MODIFIED_BY_NAME + " TEXT, "
				+ Forms.ASSIGNED_TO_ID + " INTEGER, " + Forms.ASSIGNED_TO_NAME
				+ " TEXT, " + Forms.DELETED + " TEXT, " + Forms.STATUS
				+ " INTEGER, " + Forms.CACHED + " TEXT, " + Forms.TEMPORARY
				+ " TEXT, " + Forms.DIRTY + " TEXT, " + Forms.TREE_DIRTY
				+ " TEXT, " + Forms.REMOTE_CREATION_TIME + " TEXT, "
				+ Forms.REMOTE_MODIFICATION_TIME + " TEXT, "
				+ Forms.LOCAL_CREATION_TIME + " TEXT, "
				+ Forms.LOCAL_MODIFICATION_TIME + " TEXT, "
				+ Forms.REMOTE_LOCATION_ID + " INTEGER, "
				+ Forms.FORM_SPEC_UNIQUE_ID + " TEXT)");
		insertSequence(db, Forms.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Fields.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Fields.TABLE);
		db.execSQL("CREATE TABLE " + Fields.TABLE + "(" + Fields._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Fields.FORM_SPEC_ID + " INTEGER, " + Fields.FIELD_SPEC_ID
				+ " INTEGER, " + Fields.LOCAL_FORM_ID + " INTEGER, "
				+ Fields.LOCAL_VALUE + " TEXT, " + Fields.REMOTE_VALUE
				+ " TEXT, " + Fields.FIELD_SPEC_UNIQUE_ID + " TEXT)");

		createAndLoadCountries(db);
		// createAndLoadStates(db);

		createSectionTables(db);
		createPageSpecs(db);
		createEntityTables(db, now);
		createArticles(db);
		createNamedLocations(db, now);
		createMessages(db);
		createEmployees(db);
		createVisibilityCriteria(db);
		createListFilteringCriteria(db);

		// Route Plan related tables creation
		createCompletedActivities(db);
		createActivitySpecs(db);
		createCustomerStatus(db);
		createAssignedRoutes(db);

		// sim card change sms related
		createSimCardChangeMessagesTable(db);

		// WorkFlow related tables creation
		createWorkFlowFormSpecMappings(db);
		createWorkFlows(db);
		createWorkFlowStages(db);
		createWorkFlowStatus(db);
		createWorkFlowHistory(db);
	}

	private void createWorkFlowStages(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkFlowStages.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkFlowStages.TABLE);
		db.execSQL("CREATE TABLE " + WorkFlowStages.TABLE + "("
				+ WorkFlowStages._ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ WorkFlowStages.WORK_FLOW_ID + " TEXT, "
				+ WorkFlowStages.STAGE_NAME + " INTEGER, "
				+ WorkFlowStages.DELETED + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, WorkFlowStages.TABLE, now);

	}

	private void createWorkFlows(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkFlowSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkFlowSpecs.TABLE);
		db.execSQL("CREATE TABLE " + WorkFlowSpecs.TABLE
				+ "("
				+ WorkFlowSpecs._ID
				+ " INTEGER PRIMARY KEY NOT NULL, "
				+ WorkFlowSpecs.WORK_FLOW_NAME
				+ " TEXT, "
				// + WorkFlowSpecs.FORM_SPE_UNIQUE_ID + " TEXT, "
				+ WorkFlowSpecs.CREATED_TIME + " TEXT, "
				+ WorkFlowSpecs.CREATED_BY + " TEXT, " + WorkFlowSpecs.DELETED
				+ " TEXT, " + WorkFlowSpecs.EDITABLE + " TEXT, "
				+ WorkFlowSpecs.CHECKED + " TEXT, "
				+ WorkFlowSpecs.HAS_ROLE_BASED_STAGES + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, WorkFlowSpecs.TABLE, now);

	}

	private void createWorkFlowFormSpecMappings(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkFlowFormSpecMappings.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkFlowFormSpecMappings.TABLE);
		db.execSQL("CREATE TABLE " + WorkFlowFormSpecMappings.TABLE + "("
				+ WorkFlowFormSpecMappings._ID
				+ " INTEGER PRIMARY KEY NOT NULL, "
				+ WorkFlowFormSpecMappings.WORK_FLOW_ID + " INTEGER, "
				+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID + " TEXT, "
				+ WorkFlowFormSpecMappings.ENTITY_TYPE + " INTEGER, "
				+ WorkFlowFormSpecMappings.CHECKED + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, WorkFlowFormSpecMappings.TABLE, now);

	}

	private void createWorkFlowStatus(SQLiteDatabase db) {

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkFlowStatus.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkFlowStatus.TABLE);
		db.execSQL("CREATE TABLE " + WorkFlowStatus.TABLE + "("
				+ WorkFlowStatus._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ WorkFlowStatus.REMOTE_ID + " INTEGER, "
				+ WorkFlowStatus.LOCAL_FORM_ID + " INTEGER, "
				+ WorkFlowStatus.WORK_FLOW_ID + " INTEGER, "
				+ WorkFlowStatus.CREATED_BY + " INTEGER, "
				+ WorkFlowStatus.MODIFIED_BY + " INTEGER, "
				+ WorkFlowStatus.APPROVED_BY + " INTEGER, "
				+ WorkFlowStatus.CREATED_BY_NAME + " TEXT, "
				+ WorkFlowStatus.MODIFIED_BY_NAME + " TEXT, "
				+ WorkFlowStatus.APPROVED_BY_NAME + " TEXT, "
				+ WorkFlowStatus.CREATED_TIME + " TEXT, "
				+ WorkFlowStatus.MODIFIED_TIME + " TEXT, "
				+ WorkFlowStatus.APPROVED_TIME + " TEXT, "
				+ WorkFlowStatus.STAGE_TYPE + " INTEGER, "
				+ WorkFlowStatus.STAGE_NAME + " TEXT, "
				+ WorkFlowStatus.PREVIOUS_RANK + " INTEGER, "
				+ WorkFlowStatus.CURRENT_RANK + " INTEGER, "
				+ WorkFlowStatus.NEXT_RANK + " INTEGER, "
				+ WorkFlowStatus.STATUS + " INTEGER, "
				+ WorkFlowStatus.STATUS_MESSAGE + " TEXT, "
				+ WorkFlowStatus.REMARKS + " TEXT, " + WorkFlowStatus.DIRTY
				+ " TEXT, " + WorkFlowStatus.LOCAL_CREATION_TIME + " TEXT, "
				+ WorkFlowStatus.LOCAL_MODIFICATION_TIME + " TEXT, "
				+ WorkFlowStatus.TEMPORARY + " TEXT, "
				+ WorkFlowStatus.EMP_GROUP_ID + " INTEGER, "
				+ WorkFlowStatus.MANAGER_RANK + " INTEGER)");
		long now = System.currentTimeMillis();
		insertSequence(db, WorkFlowStatus.TABLE, now);

	}

	private void createWorkFlowHistory(SQLiteDatabase db) {

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + WorkFlowHistories.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + WorkFlowHistories.TABLE);
		db.execSQL("CREATE TABLE " + WorkFlowHistories.TABLE + "("
				+ WorkFlowHistories._ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ WorkFlowHistories.WORK_FLOW_ID + " INTEGER, "
				+ WorkFlowHistories.LOCAL_FORM_ID + " INTEGER, "
				+ WorkFlowHistories.CREATED_BY + " INTEGER, "
				+ WorkFlowHistories.MODIFIED_BY + " INTEGER, "
				+ WorkFlowHistories.APPROVED_BY + " INTEGER, "
				+ WorkFlowHistories.CREATED_BY_NAME + " TEXT, "
				+ WorkFlowHistories.MODIFIED_BY_NAME + " TEXT, "
				+ WorkFlowHistories.APPROVED_BY_NAME + " TEXT, "
				+ WorkFlowHistories.CREATED_TIME + " TEXT, "
				+ WorkFlowHistories.MODIFIED_TIME + " TEXT, "
				+ WorkFlowHistories.APPROVED_TIME + " TEXT, "
				+ WorkFlowHistories.PREVIOUS_RANK + " INTEGER, "
				+ WorkFlowHistories.CURRENT_RANK + " INTEGER, "
				+ WorkFlowHistories.NEXT_RANK + " INTEGER, "
				+ WorkFlowHistories.STAGE_NAME + " TEXT, "
				+ WorkFlowHistories.STATUS + " INTEGER, "
				+ WorkFlowHistories.STATUS_MESSAGE + " TEXT, "
				+ WorkFlowHistories.REMARKS + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, WorkFlowHistories.TABLE, now);

	}

	private void createSectionTables(SQLiteDatabase db) {
		createSectionSpecs(db);
		createSectionFieldSpecs(db);
		createSectionFieldValueSpecs(db);
		createSectionFields(db);
		createSectionFilesTable(db);
	}

	private void createFormFilesTable(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + FormFiles.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + FormFiles.TABLE);
		db.execSQL("CREATE TABLE " + FormFiles.TABLE + "(" + FormFiles._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ FormFiles.FIELD_SPEC_ID + " INTEGER, "
				+ FormFiles.LOCAL_FORM_ID + " INTEGER, " + FormFiles.MIME_TYPE
				+ " TEXT, " + FormFiles.MEDIA_ID + " INTEGER, "
				+ FormFiles.LOCAL_MEDIA_PATH + " TEXT, "
				+ FormFiles.DOWNLOAD_REQUESTED + " TEXT, "
				+ FormFiles.UPLOAD_REQUESTED + " TEXT, "
				+ FormFiles.UPLOAD_PRIORITY + " INTEGER, "
				+ FormFiles.TRANSFER_PERCENTAGE + " INTEGER, "
				+ FormFiles.FILE_SIZE + " INTEGER, "
				+ FormFiles.FIELD_SPEC_UNIQUE_ID + " TEXT)");

	}

	private void createSectionFilesTable(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SectionFiles.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SectionFiles.TABLE);
		db.execSQL("CREATE TABLE " + SectionFiles.TABLE + "("
				+ SectionFiles._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SectionFiles.FIELD_SPEC_ID + " INTEGER, "
				+ SectionFiles.LOCAL_FORM_ID + " INTEGER, "
				+ SectionFiles.SECTION_INSTANCE_ID + " INTEGER, "
				+ SectionFiles.MIME_TYPE + " TEXT, " + FormFiles.MEDIA_ID
				+ " INTEGER, " + SectionFiles.LOCAL_MEDIA_PATH + " TEXT, "
				+ SectionFiles.DOWNLOAD_REQUESTED + " TEXT, "
				+ SectionFiles.UPLOAD_REQUESTED + " TEXT, "
				+ SectionFiles.UPLOAD_PRIORITY + " INTEGER, "
				+ SectionFiles.TRANSFER_PERCENTAGE + " INTEGER, "
				+ SectionFiles.FILE_SIZE + " INTEGER, "
				+ SectionFiles.FIELD_SPEC_UNIQUE_ID + " TEXT)");

	}

	private void createAndLoadCountries(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Countries.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Countries.TABLE);
		db.execSQL("CREATE TABLE " + Countries.TABLE + "(" + Countries._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Countries.NAME + " TEXT, " + Countries.DISPLAY_ORDER
				+ " INTEGER)");

		String[] countries = { "India", "Afghanistan", "Albania", "Algeria",
				"American Samoa", "Angola", "Anguilla", "Antartica",
				"Antigua and Barbuda", "Argentina", "Armenia", "Aruba",
				"Ashmore and Cartier Island", "Australia", "Austria",
				"Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
				"Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan",
				"Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil",
				"British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso",
				"Burma", "Burundi", "Cambodia", "Cameroon", "Canada",
				"Cape Verde", "Cayman Islands", "Central African Republic",
				"Chad", "Chile", "China", "Christmas Island",
				"Clipperton Island", "Cocos (Keeling) Islands", "Colombia",
				"Comoros", "Congo, Democratic Republic of the",
				"Congo, Republic of the", "Cook Islands", "Costa Rica",
				"Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czeck Republic",
				"Denmark", "Djibouti", "Dominica", "Dominican Republic",
				"Ecuador", "Egypt", "El Salvador", "Equatorial Guinea",
				"Eritrea", "Estonia", "Ethiopia", "Europa Island",
				"Falkland Islands (Islas Malvinas)", "Faroe Islands", "Fiji",
				"Finland", "France", "French Guiana", "French Polynesia",
				"French Southern and Antarctic Lands", "Gabon", "Gambia, The",
				"Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar",
				"Glorioso Islands", "Greece", "Greenland", "Grenada",
				"Guadeloupe", "Guam", "Guatemala", "Guernsey", "Guinea",
				"Guinea-Bissau", "Guyana", "Haiti",
				"Heard Island and McDonald Islands", "Holy See (Vatican City)",
				"Honduras", "Hong Kong", "Howland Island", "Hungary",
				"Iceland", "Indonesia", "Iran", "Iraq", "Ireland",
				"Ireland, Northern", "Israel", "Italy", "Jamaica", "Jan Mayen",
				"Japan", "Jarvis Island", "Jersey", "Johnston Atoll", "Jordan",
				"Juan de Nova Island", "Kazakhstan", "Kenya", "Kiribati",
				"Korea, North", "Korea, South", "Kuwait", "Kyrgyzstan", "Laos",
				"Latvia", "Lebanon", "Lesotho", "Liberia", "Libya",
				"Liechtenstein", "Lithuania", "Luxembourg", "Macau",
				"Macedonia, Former Yugoslav Republic of", "Madagascar",
				"Malawi", "Malaysia", "Maldives", "Mali", "Malta",
				"Man, Isle of", "Marshall Islands", "Martinique", "Mauritania",
				"Mauritius", "Mayotte", "Mexico",
				"Micronesia, Federated States of", "Midway Islands", "Moldova",
				"Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique",
				"Namibia", "Nauru", "Nepal", "Netherlands",
				"Netherlands Antilles", "New Caledonia", "New Zealand",
				"Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island",
				"Northern Mariana Islands", "Norway", "Oman", "Pakistan",
				"Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
				"Philippines", "Pitcaim Islands", "Poland", "Portugal",
				"Puerto Rico", "Qatar", "Reunion", "Romainia", "Russia",
				"Rwanda", "Saint Helena", "Saint Kitts and Nevis",
				"Saint Lucia", "Saint Pierre and Miquelon",
				"Saint Vincent and the Grenadines", "Samoa", "San Marino",
				"Sao Tome and Principe", "Saudi Arabia", "Scotland", "Senegal",
				"Seychelles", "Sierra Leone", "Singapore", "Slovakia",
				"Slovenia", "Solomon Islands", "Somalia", "South Africa",
				"South Georgia and South Sandwich Islands", "Spain",
				"Spratly Islands", "Sri Lanka", "Sudan", "Suriname",
				"Svalbard", "Swaziland", "Sweden", "Switzerland", "Syria",
				"Taiwan", "Tajikistan", "Tanzania", "Thailand", "Tobago",
				"Toga", "Tokelau", "Tonga", "Trinidad", "Tunisia", "Turkey",
				"Turkmenistan", "Tuvalu", "Uganda", "Ukraine",
				"United Arab Emirates", "United Kingdom", "Uruguay", "USA",
				"Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",
				"Virgin Islands", "Wales", "Wallis and Futuna", "West Bank",
				"Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe" };

		for (String country : countries) {
			int displayOrder = "India".equals(country) ? 1 : 2;

			db.execSQL("INSERT INTO " + Countries.TABLE + "(" + Countries.NAME
					+ ", " + Countries.DISPLAY_ORDER + ") VALUES(?, ?)",
					new String[] { country, "" + displayOrder });
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		long now = System.currentTimeMillis();

		if (oldVersion <= 2) {
			if (!checkColumnExists(db, Notes.TABLE, Notes.UPLOAD_REQUESTED)) {
				db.execSQL("ALTER TABLE " + Notes.TABLE + " ADD COLUMN "
						+ Notes.UPLOAD_REQUESTED + " TEXT");
			}

			if (!checkColumnExists(db, Notes.TABLE, Notes.UPLOAD_PRIORITY)) {
				db.execSQL("ALTER TABLE " + Notes.TABLE + " ADD COLUMN "
						+ Notes.UPLOAD_PRIORITY + " INTEGER");
			}
		}

		if (oldVersion <= 3) {
			if (!checkColumnExists(db, Locations.TABLE,
					Locations.SMS_PROCESS_STATE)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.SMS_PROCESS_STATE + " TEXT");
			}
		}

		if (oldVersion <= 4) {
			createFormFilesTable(db);
		}

		if (oldVersion <= 5) {
			if (!checkColumnExists(db, Jobs.TABLE, Jobs.TREE_DIRTY)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.TREE_DIRTY + " TEXT");
			}

			if (!checkColumnExists(db, Forms.TABLE, Forms.TREE_DIRTY)) {
				db.execSQL("ALTER TABLE " + Forms.TABLE + " ADD COLUMN "
						+ Forms.TREE_DIRTY + " TEXT");
			}
		}

		if (oldVersion <= 6) {
			// customer table changes
			if (!checkColumnExists(db, Customers.TABLE, Customers.STATE)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.STATE + " TEXT");
			}

			if (!checkColumnExists(db, Customers.TABLE, Customers.COUNTRY)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.COUNTRY + " TEXT");
			}

			if (!checkColumnExists(db, Customers.TABLE, Customers.LANDMARK)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.LANDMARK + " TEXT");
			}

			// jobs table changes
			if (!checkColumnExists(db, Jobs.TABLE, Jobs.LATITUDE)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.LATITUDE + " REAL");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.LONGITUDE)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.LONGITUDE + " REAL");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.STREET)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.STREET + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.AREA)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.AREA + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.CITY)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.CITY + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.STATE)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.STATE + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.COUNTRY)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.COUNTRY + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.PIN_CODE)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.PIN_CODE + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE, Jobs.LANDMARK)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.LANDMARK + " TEXT");
			}

			if (!checkColumnExists(db, Jobs.TABLE,
					Jobs.SAME_AS_CUSTOMER_ADDRESS)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.SAME_AS_CUSTOMER_ADDRESS + " TEXT");
			}

			// invitations table changes
			if (!checkColumnExists(db, Invitations.TABLE, Invitations.LATITUDE)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.LATITUDE + " REAL");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.LONGITUDE)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.LONGITUDE + " REAL");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.STREET)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.STREET + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.AREA)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.AREA + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.CITY)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.CITY + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.STATE)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.STATE + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.COUNTRY)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.COUNTRY + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.PIN_CODE)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.PIN_CODE + " TEXT");
			}

			if (!checkColumnExists(db, Invitations.TABLE, Invitations.LANDMARK)) {
				db.execSQL("ALTER TABLE " + Invitations.TABLE + " ADD COLUMN "
						+ Invitations.LANDMARK + " TEXT");
			}

			createAndLoadCountries(db);
			// createAndLoadStates(db);
		}

		if (oldVersion <= 7) {
			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.TYPE_EXTRA)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.TYPE_EXTRA + " TEXT");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.SELECTOR)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.SELECTOR + " TEXT");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.COMPUTED)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.COMPUTED + " TEXT");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.FORMULA)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.FORMULA + " TEXT");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.PAGE_ID)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.PAGE_ID + " INTEGER");
			}
			createSectionTables(db);
			createPageSpecs(db);
			createEntityTables(db, now);

			if (!checkColumnExists(db, Locations.TABLE, Locations.FRESH_FIX)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FRESH_FIX + " TEXT");
			}
		}

		if (oldVersion <= 8) {
			// trigger complete sync
			db.execSQL("UPDATE " + Settings.TABLE + " SET " + Settings.VALUE
					+ " = ? WHERE " + Settings.KEY + " = ?", new String[] { "",
					Settings.KEY_REMOTE_SYNC_TIME });
		}

		if (oldVersion <= 9) {
			createArticles(db);
		}

		if (oldVersion <= 10) {
			if (!checkColumnExists(db, Forms.TABLE, Forms.REMOTE_LOCATION_ID)) {
				db.execSQL("ALTER TABLE " + Forms.TABLE + " ADD COLUMN "
						+ Forms.REMOTE_LOCATION_ID + " INTEGER");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.REMOTE_LOCATION_ID)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.REMOTE_LOCATION_ID + " INTEGER");
			}
		}

		if (oldVersion <= 11) {
			createNamedLocations(db, now);
			createMessages(db);
		}

		if (oldVersion <= 12) {
			try {
				db.execSQL("ALTER TABLE notifications RENAME TO "
						+ Messages.TABLE);
			} catch (SQLiteException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Could not rename notifications table. Probably it doesn't exist.");
				}
			}
		}

		if (oldVersion <= 13) {
			if (!checkColumnExists(db, Customers.TABLE, Customers.DISTANCE)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.DISTANCE + " FLOAT");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.REASON_FOR_TRACKING)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.REASON_FOR_TRACKING + " INTEGER");
			}
		}

		if (oldVersion <= 14) {
			if (!checkColumnExists(db, FormSpecs.TABLE, FormSpecs.UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + FormSpecs.TABLE + " ADD COLUMN "
						+ FormSpecs.UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.UNIQUE_ID
						+ " TEXT");
			}

			if (!checkColumnExists(db, Forms.TABLE, Forms.FORM_SPEC_UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + Forms.TABLE + " ADD COLUMN "
						+ Forms.FORM_SPEC_UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, Fields.TABLE,
					Fields.FIELD_SPEC_UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + Fields.TABLE + " ADD COLUMN "
						+ Fields.FIELD_SPEC_UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, SectionFields.TABLE,
					SectionFields.FIELD_SPEC_UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + SectionFields.TABLE
						+ " ADD COLUMN " + SectionFields.FIELD_SPEC_UNIQUE_ID
						+ " TEXT");
			}

			if (!checkColumnExists(db, FormFiles.TABLE,
					FormFiles.FIELD_SPEC_UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + FormFiles.TABLE + " ADD COLUMN "
						+ FormFiles.FIELD_SPEC_UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, SectionFiles.TABLE,
					SectionFiles.FIELD_SPEC_UNIQUE_ID)) {
				db.execSQL("ALTER TABLE " + SectionFiles.TABLE + " ADD COLUMN "
						+ SectionFiles.FIELD_SPEC_UNIQUE_ID + " TEXT");
			}

			if (!checkColumnExists(db, Customers.TABLE, Customers.IN_USE)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.IN_USE + " TEXT");
			}

			// trigger complete sync
			db.execSQL("UPDATE " + Settings.TABLE + " SET " + Settings.VALUE
					+ " = ? WHERE " + Settings.KEY + " = ?", new String[] { "",
					Settings.KEY_REMOTE_SYNC_TIME });
		}
		if (oldVersion <= 15) {
			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.BARCODE)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.BARCODE + " TEXT");
			}

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.BARCODE)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.BARCODE + " TEXT");
			}

			if (!checkColumnExists(db, EntityFieldSpecs.TABLE,
					EntityFieldSpecs.BARCODE)) {
				db.execSQL("ALTER TABLE " + EntityFieldSpecs.TABLE
						+ " ADD COLUMN " + EntityFieldSpecs.BARCODE + " TEXT");
			}
		}

		if (oldVersion <= 16) {
			if (!checkColumnExists(db, Locations.TABLE, Locations.FUSED_ON)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_ON + " TEXT");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_FINALIZED)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_FINALIZED + " TEXT");
			}

			if (!checkColumnExists(db, Locations.TABLE, Locations.FUSED_CACHED)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_CACHED + " TEXT");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_FIX_TIME)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_FIX_TIME + " TEXT");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_ACCURACY)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_ACCURACY + " REAL");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_LATITUDE)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_LATITUDE + " REAL");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_LONGITUDE)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_LONGITUDE + " REAL");
			}

			if (!checkColumnExists(db, Locations.TABLE,
					Locations.FUSED_ALTITUDE)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_ALTITUDE + " REAL");
			}

			if (!checkColumnExists(db, Locations.TABLE, Locations.FUSED_SPEED)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_SPEED + " REAL");
			}

			if (!checkColumnExists(db, Locations.TABLE, Locations.FUSED_BEARING)) {
				db.execSQL("ALTER TABLE " + Locations.TABLE + " ADD COLUMN "
						+ Locations.FUSED_BEARING + " REAL");
			}

			db.execSQL("INSERT INTO Settings(key, value) VALUES('"
					+ Settings.KEY_LOCATION_PROVIDER + "', '1')");
		}

		if (oldVersion <= 17) {
			if (!checkColumnExists(db, FormSpecs.TABLE,
					FormSpecs.PRINT_TEMPLATE)) {
				db.execSQL("ALTER TABLE " + FormSpecs.TABLE + " ADD COLUMN "
						+ FormSpecs.PRINT_TEMPLATE + " TEXT");
			}
		}

		if (oldVersion <= 18) {
			if (!checkColumnExists(db, Jobs.TABLE, Jobs.LATE_ALERT_DISMISSED)) {
				db.execSQL("ALTER TABLE " + Jobs.TABLE + " ADD COLUMN "
						+ Jobs.LATE_ALERT_DISMISSED + " TEXT");
			}

			db.execSQL("UPDATE " + Jobs.TABLE + " SET "
					+ Jobs.LATE_ALERT_DISMISSED + " = 'false'");
		}

		if (oldVersion <= 19) {
			if (!checkColumnExists(db, Messages.TABLE, Messages.QUALITY_TYPE)) {
				db.execSQL("ALTER TABLE " + Messages.TABLE + " ADD COLUMN "
						+ Messages.QUALITY_TYPE + " INTEGER");
			}
		}

		if (oldVersion <= 20) {
			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.VISIBLE)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.VISIBLE + " INTEGER");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.EDITABLE)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.EDITABLE + " INTEGER");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.MIN_VALUE)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.MIN_VALUE + " INTEGER");
			}

			if (!checkColumnExists(db, FieldSpecs.TABLE, FieldSpecs.MAX_VALUE)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.MAX_VALUE + " INTEGER");
			}

			// make all the old specs visible and editable by default
			db.execSQL("UPDATE " + FieldSpecs.TABLE + " SET "
					+ FieldSpecs.VISIBLE + " = 1");
			db.execSQL("UPDATE " + FieldSpecs.TABLE + " SET "
					+ FieldSpecs.EDITABLE + " = 1");

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.VISIBLE)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.VISIBLE
						+ " INTEGER");
			}

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.EDITABLE)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.EDITABLE
						+ " INTEGER");
			}

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.MIN_VALUE)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.MIN_VALUE
						+ " INTEGER");
			}

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.MAX_VALUE)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.MAX_VALUE
						+ " INTEGER");
			}

			// make all the old specs visible and editable by default
			db.execSQL("UPDATE " + SectionFieldSpecs.TABLE + " SET "
					+ SectionFieldSpecs.VISIBLE + " = 1");
			db.execSQL("UPDATE " + SectionFieldSpecs.TABLE + " SET "
					+ SectionFieldSpecs.EDITABLE + " = 1");

			createEmployees(db);
			createVisibilityCriteria(db);
			createListFilteringCriteria(db);
			createCompletedActivities(db);
			createActivitySpecs(db);
			createCustomerStatus(db);
			createAssignedRoutes(db);

			// trigger complete sync
			db.execSQL("UPDATE " + Settings.TABLE + " SET " + Settings.VALUE
					+ " = ? WHERE " + Settings.KEY + " = ?", new String[] { "",
					Settings.KEY_REMOTE_SYNC_TIME });
		}

		if (oldVersion <= 21) {
			createWorkFlowFormSpecMappings(db);
			createWorkFlows(db);
			createWorkFlowStages(db);
			createWorkFlowStatus(db);
			createWorkFlowHistory(db);
			createSimCardChangeMessagesTable(db);

			if (!checkColumnExists(db, Employees.TABLE, Employees.RANK)) {
				db.execSQL("ALTER TABLE " + Employees.TABLE + " ADD COLUMN "
						+ Employees.RANK + " INTEGER");
			}
		}
		if (oldVersion <= 22) {
			createWorkFlowStages(db);

			if (!checkColumnExists(db, WorkFlowFormSpecMappings.TABLE,
					WorkFlowFormSpecMappings.CHECKED)) {
				db.execSQL("ALTER TABLE " + WorkFlowFormSpecMappings.TABLE
						+ " ADD COLUMN " + WorkFlowFormSpecMappings.CHECKED
						+ " TEXT");
			}
			db.execSQL("UPDATE " + WorkFlowFormSpecMappings.TABLE + " SET "
					+ WorkFlowFormSpecMappings.CHECKED + " = 'true' WHERE "
					+ WorkFlowFormSpecMappings.CHECKED + " IS NULL");
		}

		if (oldVersion <= 23) {
			db.execSQL("UPDATE " + WorkFlowFormSpecMappings.TABLE + " SET "
					+ WorkFlowFormSpecMappings.CHECKED + " = 'true' WHERE "
					+ WorkFlowFormSpecMappings.CHECKED + " IS NULL");
		}

		if (oldVersion <= 24) {
			if (!checkColumnExists(db, FieldSpecs.TABLE,
					FieldSpecs.DEFAULT_FIELD)) {
				db.execSQL("ALTER TABLE " + FieldSpecs.TABLE + " ADD COLUMN "
						+ FieldSpecs.DEFAULT_FIELD + " TEXT");
			}

			// make all the old form fields is_default=false by default
			db.execSQL("UPDATE " + FieldSpecs.TABLE + " SET "
					+ FieldSpecs.DEFAULT_FIELD + " = 'false'");

			if (!checkColumnExists(db, SectionFieldSpecs.TABLE,
					SectionFieldSpecs.DEFAULT_FIELD)) {
				db.execSQL("ALTER TABLE " + SectionFieldSpecs.TABLE
						+ " ADD COLUMN " + SectionFieldSpecs.DEFAULT_FIELD
						+ " TEXT");
			}

			// make all the old section fields is_default=false by default
			db.execSQL("UPDATE " + SectionFieldSpecs.TABLE + " SET "
					+ SectionFieldSpecs.DEFAULT_FIELD + " = 'false'");

			if (!checkColumnExists(db, Customers.TABLE, Customers.DELETED)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.DELETED + " TEXT");
			}

			// make all the old customers deleted=false by default
			db.execSQL("UPDATE " + Customers.TABLE + " SET "
					+ Customers.DELETED + " = 'false'");

			// we changed
			// PURPOSE_START_WORK = 5;
			// PURPOSE_STOP_WORK = 6;
			// PURPOSE_NEARBY_CUSTOMERS = 9;
			// PURPOSE_COMPLETE_ROUTE_PLAN = 10;
			// in the previous versions it was like
			// PURPOSE_NEARBY_CUSTOMERS = 5;
			// PURPOSE_COMPLETE_ROUTE_PLAN = 6;
			// changed because , to sync with server side constants
			// make all the old customers deleted=false by default
			// make all previous 5 to 9 and 6 to 10
			db.execSQL("UPDATE " + Locations.TABLE + " SET "
					+ Locations.PURPOSE + " = "
					+ Locations.PURPOSE_NEARBY_CUSTOMERS + " where "
					+ Locations.PURPOSE + " = " + Locations.PURPOSE_START_WORK);

			db.execSQL("UPDATE " + Locations.TABLE + " SET "
					+ Locations.PURPOSE + " = "
					+ Locations.PURPOSE_COMPLETE_ROUTE_PLAN + " where "
					+ Locations.PURPOSE + " = " + Locations.PURPOSE_STOP_WORK);

			// trigger complete sync
			db.execSQL("UPDATE " + Settings.TABLE + " SET " + Settings.VALUE
					+ " = ? WHERE " + Settings.KEY + " = ?", new String[] { "",
					Settings.KEY_REMOTE_SYNC_TIME });
		}

		if (oldVersion <= 25) {
			if (!checkColumnExists(db, Customers.TABLE, Customers.CUSTOMER_NUM)) {
				db.execSQL("ALTER TABLE " + Customers.TABLE + " ADD COLUMN "
						+ Customers.CUSTOMER_NUM + " INTEGER");
			}
		}
		if (oldVersion <= 26) {
			if (!checkColumnExists(db, JobHistories.TABLE,
					JobHistories.TEMPORARY)) {
				db.execSQL("ALTER TABLE " + JobHistories.TABLE + " ADD COLUMN "
						+ JobHistories.TEMPORARY + " TEXT");
			}
			if (!checkColumnExists(db, JobStageStatuses.TABLE,
					JobStageStatuses.TEMPORARY)) {
				db.execSQL("ALTER TABLE " + JobStageStatuses.TABLE
						+ " ADD COLUMN " + JobStageStatuses.TEMPORARY + " TEXT");
			}
			db.execSQL("UPDATE " + JobStageStatuses.TABLE + " SET "
					+ JobStageStatuses.TEMPORARY + " = 'false'");
			db.execSQL("UPDATE " + JobHistories.TABLE + " SET "
					+ JobHistories.TEMPORARY + " = 'false'");
		}
		if (oldVersion <= 27) {
			// trigger complete sync
			db.execSQL("UPDATE " + Settings.TABLE + " SET " + Settings.VALUE
					+ " = ? WHERE " + Settings.KEY + " = ?", new String[] { "",
					Settings.KEY_REMOTE_SYNC_TIME });

			if (!checkColumnExists(db, Articles.TABLE, Articles.FILE_TYPE)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.FILE_TYPE + " INTEGER");
			}
			if (!checkColumnExists(db, Articles.TABLE, Articles.PARENT_PATH)) {

				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.PARENT_PATH + " TEXT");
			}
			if (!checkColumnExists(db, Articles.TABLE, Articles.PARENT_ID)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.PARENT_ID + " INTEGER");
			}

			if (!checkColumnExists(db, Articles.TABLE, Articles.IS_SECURED)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.IS_SECURED + " TEXT");
			}

			if (!checkColumnExists(db, Articles.TABLE, Articles.DELETED)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.DELETED + " TEXT");
			}
			if (!checkColumnExists(db, Articles.TABLE,
					Articles.LOCAL_CREATION_TIME)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.LOCAL_CREATION_TIME + " TEXT");
			}

			if (!checkColumnExists(db, Articles.TABLE,
					Articles.LOCAL_MODIFICATION_TIME)) {
				db.execSQL("ALTER TABLE " + Articles.TABLE + " ADD COLUMN "
						+ Articles.LOCAL_MODIFICATION_TIME + " TEXT");
			}
		}
	}

	private boolean checkColumnExists(SQLiteDatabase db, String tableName,
			String columnName) {

		String sql = "pragma table_info(" + tableName + ")";

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String existedColumnName = cursor.getString(1);
					if (columnName != null && existedColumnName != null
							&& columnName.equalsIgnoreCase(existedColumnName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void insertSequence(SQLiteDatabase db, String tableName,
			long sequence) {
		db.execSQL("INSERT INTO " + SQLITE_SEQUENCE_TABLE + "("
				+ TABLE_NAME_COLUMN + ", " + SEQUENCE_COLUMN + ") VALUES('"
				+ tableName + "', " + sequence + ")");
	}

	private void insertWorkingHour(SQLiteDatabase db, int dayOfWeek) {
		db.execSQL("INSERT INTO " + WorkingHours.TABLE + "("
				+ WorkingHours.TITLE + ", " + WorkingHours.DAY_OF_WEEK + ", "
				+ WorkingHours.START_TIME + ", " + WorkingHours.END_TIME
				+ ") VALUES('Full Day', " + dayOfWeek
				+ ", '1970-01-01 04:30:00', '1970-01-01 12:30:00')");

	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		SQLiteDatabase db = super.getReadableDatabase();
		db.execSQL("PRAGMA foreign_keys = ON");
		return db;
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase db = super.getWritableDatabase();
		db.execSQL("PRAGMA foreign_keys = ON");
		return db;
	}

	private void createSectionSpecs(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SectionSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SectionSpecs.TABLE);
		db.execSQL("CREATE TABLE " + SectionSpecs.TABLE + "("
				+ SectionSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SectionSpecs.TITLE + " TEXT, " + SectionSpecs.FORM_SPEC_ID
				+ " INTEGER, " + SectionSpecs.MIN_ENTRIES + " INTEGER, "
				+ SectionSpecs.MAX_ENTRIES + " INTEGER, "
				+ SectionSpecs.DISPLAY_ORDER + " INTEGER, "
				+ SectionSpecs.PAGE_ID + " INTEGER)");
	}

	private void createSectionFieldSpecs(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SectionFieldSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SectionFieldSpecs.TABLE);
		db.execSQL("CREATE TABLE " + SectionFieldSpecs.TABLE + "("
				+ SectionFieldSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SectionFieldSpecs.FORM_SPEC_ID + " INTEGER, "
				+ SectionFieldSpecs.LABEL + " TEXT, " + SectionFieldSpecs.TYPE
				+ " INTEGER, " + SectionFieldSpecs.IDENTIFIER + " TEXT, "
				+ SectionFieldSpecs.REQUIRED + " TEXT, "
				+ SectionFieldSpecs.DISPLAY_ORDER + " INTEGER, "
				+ SectionFieldSpecs.TYPE_EXTRA + " TEXT, "
				+ SectionFieldSpecs.SELECTOR + " TEXT, "
				+ SectionFieldSpecs.COMPUTED + " TEXT, "
				+ SectionFieldSpecs.FORMULA + " TEXT, "
				+ SectionFieldSpecs.SECTION_SPEC_ID + " INTEGER, "
				+ SectionFieldSpecs.UNIQUE_ID + " TEXT,"
				+ SectionFieldSpecs.BARCODE + " TEXT,"
				+ SectionFieldSpecs.VISIBLE + " INTEGER,"
				+ SectionFieldSpecs.EDITABLE + " INTEGER,"
				+ SectionFieldSpecs.MIN_VALUE + " INTEGER,"
				+ SectionFieldSpecs.MAX_VALUE + " INTEGER,"
				+ SectionFieldSpecs.DEFAULT_FIELD + " TEXT)");
	}

	private void createSectionFieldValueSpecs(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SectionFieldValueSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SectionFieldValueSpecs.TABLE);
		db.execSQL("CREATE TABLE " + SectionFieldValueSpecs.TABLE + "("
				+ SectionFieldValueSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SectionFieldValueSpecs.FIELD_SPEC_ID + " INTEGER, "
				+ SectionFieldValueSpecs.VALUE + " TEXT)");
	}

	private void createSectionFields(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SectionFields.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SectionFields.TABLE);
		db.execSQL("CREATE TABLE " + SectionFields.TABLE + "("
				+ SectionFields._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SectionFields.FORM_SPEC_ID + " INTEGER, "
				+ SectionFields.SECTION_SPEC_ID + " INTEGER, "
				+ SectionFields.FIELD_SPEC_ID + " INTEGER, "
				+ SectionFields.LOCAL_FORM_ID + " INTEGER, "
				+ SectionFields.SECTION_INSTANCE_ID + " INTEGER, "
				+ SectionFields.LOCAL_VALUE + " TEXT, "
				+ SectionFields.REMOTE_VALUE + " TEXT, "
				+ SectionFields.FIELD_SPEC_UNIQUE_ID + " TEXT)");
	}

	private void createPageSpecs(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + PageSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + PageSpecs.TABLE);
		db.execSQL("CREATE TABLE " + PageSpecs.TABLE + "(" + PageSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ PageSpecs.PAGE_ID + " INTEGER, " + PageSpecs.TITLE
				+ " TEXT, " + PageSpecs.FORM_SPEC_ID + " INTEGER)");
	}

	private void createEntityTables(SQLiteDatabase db, long now) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + EntitySpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + EntitySpecs.TABLE);
		db.execSQL("CREATE TABLE " + EntitySpecs.TABLE + "(" + EntitySpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ EntitySpecs.TITLE + " TEXT, " + EntitySpecs.WITHDRAWN
				+ " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + EntityFieldSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + EntityFieldSpecs.TABLE);
		db.execSQL("CREATE TABLE " + EntityFieldSpecs.TABLE + "("
				+ EntityFieldSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ EntityFieldSpecs.ENTITY_SPEC_ID + " INTEGER, "
				+ EntityFieldSpecs.LABEL + " TEXT, " + EntityFieldSpecs.TYPE
				+ " INTEGER, " + EntityFieldSpecs.IDENTIFIER + " TEXT, "
				+ EntityFieldSpecs.REQUIRED + " TEXT, "
				+ EntityFieldSpecs.DISPLAY_ORDER + " INTEGER, "
				+ EntityFieldSpecs.TYPE_EXTRA + " TEXT, "
				+ EntityFieldSpecs.SELECTOR + " TEXT, "
				+ EntityFieldSpecs.COMPUTED + " TEXT, "
				+ EntityFieldSpecs.FORMULA + " TEXT,"
				+ SectionFieldSpecs.BARCODE + " TEXT)");
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + EntityFieldValueSpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + EntityFieldValueSpecs.TABLE);
		db.execSQL("CREATE TABLE " + EntityFieldValueSpecs.TABLE + "("
				+ EntityFieldValueSpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ EntityFieldValueSpecs.FIELD_SPEC_ID + " INTEGER, "
				+ EntityFieldValueSpecs.VALUE + " TEXT)");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Entities.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Entities.TABLE);
		db.execSQL("CREATE TABLE " + Entities.TABLE + "(" + Entities._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Entities.REMOTE_ID + " INTEGER, " + Entities.ENTITY_SPEC_ID
				+ " INTEGER, " + Entities.FILLED_BY_ID + " INTEGER, "
				+ Entities.FILLED_BY_NAME + " TEXT, " + Entities.MODIFIED_BY_ID
				+ " INTEGER, " + Entities.MODIFIED_BY_NAME + " TEXT, "
				+ Entities.ASSIGNED_TO_ID + " INTEGER, "
				+ Entities.ASSIGNED_TO_NAME + " TEXT, " + Entities.DELETED
				+ " TEXT, " + Entities.STATUS + " INTEGER, " + Entities.CACHED
				+ " TEXT, " + Entities.TEMPORARY + " TEXT, " + Entities.DIRTY
				+ " TEXT, " + Entities.TREE_DIRTY + " TEXT, "
				+ Entities.REMOTE_CREATION_TIME + " TEXT, "
				+ Entities.REMOTE_MODIFICATION_TIME + " TEXT, "
				+ Entities.LOCAL_CREATION_TIME + " TEXT, "
				+ Entities.LOCAL_MODIFICATION_TIME + " TEXT)");
		insertSequence(db, Entities.TABLE, now);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + EntityFields.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + EntityFields.TABLE);
		db.execSQL("CREATE TABLE " + EntityFields.TABLE + "("
				+ EntityFields._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ EntityFields.ENTITY_SPEC_ID + " INTEGER, "
				+ EntityFields.FIELD_SPEC_ID + " INTEGER, "
				+ EntityFields.LOCAL_ENTITY_ID + " INTEGER, "
				+ EntityFields.LOCAL_VALUE + " TEXT, "
				+ EntityFields.REMOTE_VALUE + " TEXT)");
	}

	// public void createArticles(SQLiteDatabase db) {
	// if (BuildConfig.DEBUG) {
	// Log.d(TAG, "Creating table: " + Articles.TABLE);
	// }
	//
	// db.execSQL("DROP TABLE IF EXISTS " + Articles.TABLE);
	// db.execSQL("CREATE TABLE " + Articles.TABLE + "(" + Articles._ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
	// + Articles.TITLE + " TEXT, " + Articles.DESCRIPTION + " TEXT, "
	// + Articles.MEDIA_ID + " INTEGER, " + Articles.MIME_TYPE
	// + " TEXT, " + " INTEGER, " + Articles.LOCAL_MEDIA_PATH
	// + " TEXT, " + Articles.DOWNLOAD_REQUESTED + " TEXT, "
	// + Articles.TRANSFER_PERCENTAGE + " INTEGER, "
	// + Articles.FILE_SIZE + " INTEGER, " + Articles.CREATED_BY_NAME
	// + " TEXT, " + Articles.MODIFIED_BY_NAME + " TEXT, "
	// + Articles.REMOTE_CREATION_TIME + " TEXT NOT NULL, "
	// + Articles.REMOTE_MODIFICATION_TIME + " TEXT NOT NULL, "
	// + Articles.GOT_VIA_SEARCH + " TEXT," + Articles.FILE_TYPE
	// + " INTEGER," + Articles.PARENT_PATH + " TEXT,"
	// + Articles.PARENT_ID + " INTEGER," + Articles.IS_SECURED
	// + " TEXT," + Articles.DELETED + " TEXT)");
	// }
	public void createArticles(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Articles.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Articles.TABLE);
		db.execSQL("CREATE TABLE " + Articles.TABLE + "(" + Articles._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Articles.TITLE + " TEXT, " + Articles.DESCRIPTION + " TEXT, "
				+ Articles.MEDIA_ID + " INTEGER, " + Articles.MIME_TYPE
				+ " TEXT, " + " INTEGER, " + Articles.LOCAL_MEDIA_PATH
				+ " TEXT, " + Articles.DOWNLOAD_REQUESTED + " TEXT, "
				+ Articles.TRANSFER_PERCENTAGE + " INTEGER, "
				+ Articles.FILE_SIZE + " INTEGER, " + Articles.CREATED_BY_NAME
				+ " TEXT, " + Articles.MODIFIED_BY_NAME + " TEXT, "
				+ Articles.REMOTE_CREATION_TIME + " TEXT NOT NULL, "
				+ Articles.REMOTE_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ Articles.GOT_VIA_SEARCH + " TEXT," + Articles.FILE_TYPE
				+ " INTEGER," + Articles.PARENT_PATH + " TEXT,"
				+ Articles.PARENT_ID + " INTEGER," + Articles.IS_SECURED
				+ " TEXT," + Articles.DELETED + " TEXT,"
				+ Articles.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ Articles.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL)");
	}

	private void createNamedLocations(SQLiteDatabase db, long now) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + NamedLocations.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + NamedLocations.TABLE);
		db.execSQL("CREATE TABLE " + NamedLocations.TABLE + "("
				+ NamedLocations._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ NamedLocations.REMOTE_ID + " INTEGER, " + NamedLocations.NAME
				+ " TEXT, " + NamedLocations.DESCRIPTION + " TEXT, "
				+ NamedLocations.LATITUDE + " REAL, "
				+ NamedLocations.LONGITUDE + " REAL, " + NamedLocations.STREET
				+ " TEXT, " + NamedLocations.AREA + " TEXT, "
				+ NamedLocations.CITY + " TEXT, " + NamedLocations.STATE
				+ " TEXT, " + NamedLocations.COUNTRY + " TEXT, "
				+ NamedLocations.LANDMARK + " TEXT, " + NamedLocations.PIN_CODE
				+ " TEXT, " + NamedLocations.DIRTY + " TEXT, "
				+ NamedLocations.PARTIAL + " TEXT, "
				+ NamedLocations.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ NamedLocations.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL)");
		insertSequence(db, NamedLocations.TABLE, now);
	}

	private void createMessages(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Messages.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Messages.TABLE);
		db.execSQL("CREATE TABLE " + Messages.TABLE + "(" + Messages._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Messages.SUBJECT + " TEXT, " + Messages.BODY + " TEXT, "
				+ Messages.REMOTE_CREATION_TIME + " TEXT NOT NULL, "
				+ Messages.LOCAL_CREATION_TIME + " TEXT NOT NULL, "
				+ Messages.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL, "
				+ Messages.QUALITY_TYPE + " INTEGER)");

	}

	private void createEmployees(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + Employees.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + Employees.TABLE);
		db.execSQL("CREATE TABLE " + Employees.TABLE + "(" + Employees._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ Employees.EMPLOYEE_ID + " INTEGER, "
				+ Employees.EMPLOYEE_FIRST_NAME + " TEXT, "
				+ Employees.EMPLOYEE_LAST_NAME + " TEXT, "
				+ Employees.EMPLOYEE_EMAIL + " TEXT, " + Employees.MANAGER_ID
				+ " INTEGER, " + Employees.EMPLOYEE_PHONE + " TEXT, "
				+ Employees.IMEI + " TEXT, " + Employees.COMPANY_ID
				+ " INTEGER, " + Employees.MANAGER + " TEXT, "
				+ Employees.EMPLOYEE_TYPE_ID + " INTEGER, "
				+ Employees.CALENDAR_ID + " INTEGER, " + Employees.EMPLOYEE_NO
				+ " TEXT, " + Employees.HOME_LAT + " REAL, "
				+ Employees.HOME_LONG + " REAL, "
				+ Employees.CLIENT_EMPLOYEE_ID + " INTEGER, "
				+ Employees.EMPLOYEE_ADDRESS_STREET + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_AREA + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_CITY + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_PINCODE + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_LANDMARK + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_STATE + " TEXT, "
				+ Employees.EMPLOYEE_ADDRESS_COUNTRY + " TEXT, "
				+ Employees.WORK_LAT + " REAL, " + Employees.WORK_LONG
				+ " REAL, " + Employees.PROVISIONING + " TEXT, "
				+ Employees.RANK + " INTEGER)");
	}

	private void createVisibilityCriteria(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + VisibilityCriterias.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + VisibilityCriterias.TABLE);

		db.execSQL("CREATE TABLE " + VisibilityCriterias.TABLE + "("
				+ VisibilityCriterias._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ VisibilityCriterias.VALUE + " TEXT, "
				+ VisibilityCriterias.REMOTE_ID + " INTEGER, "
				+ VisibilityCriterias.FIELD_TYPE + " INTEGER, "
				+ VisibilityCriterias.CONDITION + " INTEGER, "
				+ VisibilityCriterias.FORM_SPEC_ID + " INTEGER, "
				+ VisibilityCriterias.FIELD_SPEC_ID + " INTEGER, "
				+ VisibilityCriterias.TARGET_FIELD_EXPRESSION + " TEXT, "
				+ VisibilityCriterias.FIELD_DATA_TYPE + " INTEGER, "
				+ VisibilityCriterias.VALUE_IDS + " TEXT, "
				+ VisibilityCriterias.VISIBILITY_TYPE + " INTEGER)");
	}

	private void createListFilteringCriteria(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + ListFilteringCriterias.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + ListFilteringCriterias.TABLE);

		db.execSQL("CREATE TABLE " + ListFilteringCriterias.TABLE + "("
				+ ListFilteringCriterias._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ ListFilteringCriterias.VALUE + " TEXT, "
				+ ListFilteringCriterias.TYPE + " INTEGER, "
				+ ListFilteringCriterias.CONDITION + " INTEGER, "
				+ ListFilteringCriterias.FORM_SPEC_ID + " INTEGER, "
				+ ListFilteringCriterias.FIELD_SPEC_ID + " INTEGER, "
				+ ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID
				+ " TEXT, " + ListFilteringCriterias.LIST_FIELD_SPEC_ID
				+ " INTEGER)");
	}

	private void createAssignedRoutes(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + AssignedRoutes.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + AssignedRoutes.TABLE);
		db.execSQL("CREATE TABLE " + AssignedRoutes.TABLE + "("
				+ AssignedRoutes._ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ AssignedRoutes.ROUTE_NAME + " TEXT, "
				+ AssignedRoutes.DURATION + " INTEGER, "
				+ AssignedRoutes.START_DATE + " INTEGER, "
				+ AssignedRoutes.END_DATE + " TEXT, " + AssignedRoutes.CACHED
				+ " TEXT, " + AssignedRoutes.DELETED + " TEXT, "
				+ AssignedRoutes.STATUS + " INTEGER, "
				+ AssignedRoutes.LOCAL_MODIFICATION_TIME + " TEXT NOT NULL,"
				+ AssignedRoutes.COMPLETION_TIME + " TEXT, "
				+ AssignedRoutes.REMOTE_CUSTOMER_IDS + " TEXT, "
				+ AssignedRoutes.MIN_CUSTOMERS_TO_COMPLETE + " INTEGER, "
				+ AssignedRoutes.DIRTY + " TEXT )");
		long now = System.currentTimeMillis();
		insertSequence(db, AssignedRoutes.TABLE, now);

	}

	private void createCustomerStatus(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + CustomerStatus.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + CustomerStatus.TABLE);
		db.execSQL("CREATE TABLE " + CustomerStatus.TABLE + "("
				+ CustomerStatus._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ CustomerStatus.ASSIGN_ROUTE_ID + " INTEGER, "
				+ CustomerStatus.CUSTOMER_ID + " INTEGER, "
				+ CustomerStatus.STATUS_CHANGE_TIME + " TEXT, "
				+ CustomerStatus.STATUS + " TEXT, " + CustomerStatus.DIRTY
				+ " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, CustomerStatus.TABLE, now);

	}

	private void createActivitySpecs(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + ActivitySpecs.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + ActivitySpecs.TABLE);
		db.execSQL("CREATE TABLE " + ActivitySpecs.TABLE + "("
				+ ActivitySpecs._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ ActivitySpecs.NAME + " TEXT, " + ActivitySpecs.FORM_SPEC_ID
				+ " INTEGER, " + ActivitySpecs.DELETED + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, ActivitySpecs.TABLE, now);
	}

	private void createCompletedActivities(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + CompletedActivities.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + CompletedActivities.TABLE);
		db.execSQL("CREATE TABLE " + CompletedActivities.TABLE + "("
				+ CompletedActivities._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ CompletedActivities.REMOTE_ID + " INTEGER, "
				+ CompletedActivities.ASSIGNED_ROUTE_ID + " INTEGER, "
				+ CompletedActivities.ACTIVITY_ID + " INTEGER, "
				+ CompletedActivities.ACTIVITY_COMPLETED_TIME + " TEXT, "
				+ CompletedActivities.CLIENT_FORM_ID + " INTEGER, "
				+ CompletedActivities.CUSTOMER_ID + " INTEGER, "
				+ CompletedActivities.DIRTY + " TEXT)");
		long now = System.currentTimeMillis();
		insertSequence(db, CompletedActivities.TABLE, now);
	}

	private void createSimCardChangeMessagesTable(SQLiteDatabase db) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + SimCardChangeMessages.TABLE);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SimCardChangeMessages.TABLE);

		db.execSQL("CREATE TABLE " + SimCardChangeMessages.TABLE + "("
				+ SimCardChangeMessages._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ SimCardChangeMessages.RECEIVER_NUMBER + " TEXT, "
				+ SimCardChangeMessages.MESSAGE_BODY + " TEXT, "
				+ SimCardChangeMessages.STATUS + " TEXT )");
	}
}