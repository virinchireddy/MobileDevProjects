package in.spoors.effort1.provider;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.WorkFlowsActivity;
import in.spoors.effort1.dao.SettingsDao;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.BuildConfig;

public class EffortProvider extends ContentProvider {

	public static final String TAG = "EffortProvider";
	public static final String AUTHORITY = "in.spoors.effort1.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	private static final UriMatcher uriMatcher;
	private DBHelper dbHelper;
	private SettingsDao settingsDao;

	private static final int CUSTOMERS = 1;
	private static final int CUSTOMER_ID = 2;
	private static final int JOB_TYPES = 3;
	private static final int JOB_TYPE_ID = 4;
	private static final int JOBS = 5;
	private static final int JOB_ID = 6;
	private static final int NOTES = 7;
	private static final int NOTE_ID = 8;
	private static final int LEAVES = 9;
	private static final int LEAVE_ID = 10;
	private static final int AGENDA = 11;
	private static final int HOLIDAYS = 13;
	private static final int FORM_SPECS = 15;
	private static final int FORM_SPEC_ID = 16;
	private static final int FIELD_SPECS = 17;
	private static final int FIELD_SPEC_ID = 18;
	private static final int FIELD_VALUE_SPECS = 19;
	private static final int FIELD_VALUE_SPEC_ID = 20;
	private static final int FORMS = 21;
	private static final int FORM_ID = 22;
	private static final int FIELDS = 23;
	private static final int FIELD_ID = 24;
	private static final int FORMS_VIEW = 25;
	private static final int FIELDS_VIEW = 27;
	private static final int INVITATIONS = 29;
	private static final int INVITATION_ID = 30;
	private static final int JOB_STATES = 31;
	private static final int JOB_STATE_ID = 32;
	private static final int TYPE_STATE_MAPPINGS = 33;
	private static final int TYPE_STATE_MAPPING_ID = 34;
	private static final int ENTITIES = 35;
	private static final int ENTITY_ID = 36;
	private static final int ENTITIES_VIEW = 37;
	private static final int ARTICLES = 38;
	private static final int ARTICLE_ID = 39;
	private static final int NAMED_LOCATIONS = 40;
	private static final int NAMED_LOCATION_ID = 41;
	private static final int MESSAGES = 42;
	private static final int MESSAGE_ID = 43;
	private static final int EMPLOYEES = 44;
	private static final int EMPLOYEE_DETAIL = 45;
	private static final int ROUTES = 46;
	private static final int WORK_FLOW_SPECS = 47;
	private static final int WORK_FLOWS_VIEW = 48;
	private static final int WORK_FLOWS_HISTORIES = 49;
	private static final int WORK_FLOWS_SPECS_VIEW = 50;
	private static final int PENDING_WORK_FLOWS_VIEW = 51;

	// Used In Visibility dependecy

	// FormFieldSearchCritita condition Constants starts
	public static final int CON_TEXT_EQUALS = 1;
	public static final int CON_TEXT_CONTAINS = 2;
	public static final int CON_TEXT_DOES_NOT_CONTAINS = 3;
	public static final int CON_TEXT_START_WITH = 4;
	public static final int CON_TEXT_ENDS_WITH = 5;

	public static final int CON_NUMBER_LESS_THAN = 6;
	public static final int CON_NUMBER_GREATER_THAN_OR_EQUALS = 7;
	public static final int CON_NUMBER_LESS_THAN_OR_EQUALS = 8;
	public static final int CON_NUMBER_EQUALS = 9;
	public static final int CON_NUMBER_NOT_EQUAL = 10;
	public static final int CON_NUMBER_GREATER_THAN = 11;

	public static final int CON_DATE_AFTER = 12;
	public static final int CON_DATE_BEFORE = 13;
	public static final int CON_DATE_IN_BETWEEN = 14;
	public static final int CON_DATE_ON = 15;
	public static final int CON_DATE_NOT_ON = 16;

	// public static final int CON_CUSTOMER_IN = 17;
	// public static final int CON_SINGLE_SELECT_LIST_VALUES_IN = 18;

	public static final int CON_TYPE_YES = 19;
	public static final int CON_TYPE_NO = 20;

	// public static final int CON_TYPE_FIELD_CONTAINS_IMAGE=21;
	// public static final int CON_TYPE_FIELD_DOES_NOT_CONTAINS_IMAGE=22;

	public static final int CON_IN = 21;
	public static final int CON_NOT_IN = 22;

	// FormFieldSearchCritita condition Constants ends

	// FormFieldSearchCritita fieldType Constants ends

	public static final int FIELD_TYPE_FORM_FIELD = 1;
	public static final int FIELD_TYPE_SECTION_FIELD = 2;

	// FormFieldSearchCritita fieldType Constants ends

	// FormFieldSearchCritita viewType Constants ends
	public static final int VIS_TYPE_HIDE = 1;
	public static final int VIS_TYPE_DISABLE = 2;

	// FormFieldSearchCritita viewType Constants ends

	// Data type specific constants starts here
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_NUMBER = 2;
	public static final int TYPE_DATE = 3;
	public static final int TYPE_YES_OR_NO = 4;
	public static final int TYPE_SINGLE_SELECT_LIST = 5;
	public static final int TYPE_MULTI_SELECT_LIST = 6;
	public static final int TYPE_CUSTOMER = 7;
	public static final int TYPE_EMAIL = 8;
	public static final int TYPE_PHONE = 9;
	public static final int TYPE_URL = 10;
	public static final int TYPE_TIME = 11;
	public static final int TYPE_IMAGE = 12;
	public static final int TYPE_SIGNATURE = 13;
	public static final int TYPE_LIST = 14;
	public static final int TYPE_EMPLOYEE = 15;
	public static final int TYPE_CURRENCY = 16;
	public static final int TYPE_MULTI_LIST = 17;
	public static final int TYPE_LOCATION = 18;
	// Data type specific constants ends here

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "agenda", AGENDA);
		uriMatcher.addURI(AUTHORITY, "holidays", HOLIDAYS);
		uriMatcher.addURI(AUTHORITY, "customers", CUSTOMERS);
		uriMatcher.addURI(AUTHORITY, "customers/#", CUSTOMER_ID);
		uriMatcher.addURI(AUTHORITY, "job_types", JOB_TYPES);
		uriMatcher.addURI(AUTHORITY, "job_types/#", JOB_TYPE_ID);
		uriMatcher.addURI(AUTHORITY, "job_states", JOB_STATES);
		uriMatcher.addURI(AUTHORITY, "job_states/#", JOB_STATE_ID);
		uriMatcher
				.addURI(AUTHORITY, "type_state_mappings", TYPE_STATE_MAPPINGS);
		uriMatcher.addURI(AUTHORITY, "type_state_mappings/#",
				TYPE_STATE_MAPPING_ID);
		uriMatcher.addURI(AUTHORITY, "jobs", JOBS);
		uriMatcher.addURI(AUTHORITY, "jobs/#", JOB_ID);
		uriMatcher.addURI(AUTHORITY, "invitations", INVITATIONS);
		uriMatcher.addURI(AUTHORITY, "invitations/#", INVITATION_ID);
		uriMatcher.addURI(AUTHORITY, "notes", NOTES);
		uriMatcher.addURI(AUTHORITY, "notes/#", NOTE_ID);
		uriMatcher.addURI(AUTHORITY, "leaves", LEAVES);
		uriMatcher.addURI(AUTHORITY, "leaves/#", LEAVE_ID);
		uriMatcher.addURI(AUTHORITY, "form_specs", FORM_SPECS);
		uriMatcher.addURI(AUTHORITY, "form_specs/#", FORM_SPEC_ID);
		uriMatcher.addURI(AUTHORITY, "field_specs", FIELD_SPECS);
		uriMatcher.addURI(AUTHORITY, "field_specs/#", FIELD_SPEC_ID);
		uriMatcher.addURI(AUTHORITY, "field_value_specs", FIELD_VALUE_SPECS);
		uriMatcher
				.addURI(AUTHORITY, "field_value_specs/#", FIELD_VALUE_SPEC_ID);
		uriMatcher.addURI(AUTHORITY, "forms", FORMS);
		uriMatcher.addURI(AUTHORITY, "forms/#", FORM_ID);
		uriMatcher.addURI(AUTHORITY, "fields", FIELDS);
		uriMatcher.addURI(AUTHORITY, "fields/#", FIELD_ID);
		uriMatcher.addURI(AUTHORITY, "forms_view", FORMS_VIEW);
		uriMatcher.addURI(AUTHORITY, "fields_view", FIELDS_VIEW);
		uriMatcher.addURI(AUTHORITY, "entities", ENTITIES);
		uriMatcher.addURI(AUTHORITY, "entities/#", ENTITY_ID);
		uriMatcher.addURI(AUTHORITY, "entities_view", ENTITIES_VIEW);
		uriMatcher.addURI(AUTHORITY, "articles", ARTICLES);
		uriMatcher.addURI(AUTHORITY, "articles/#", ARTICLE_ID);
		uriMatcher.addURI(AUTHORITY, "named_locations", NAMED_LOCATIONS);
		uriMatcher.addURI(AUTHORITY, "named_locations/#", NAMED_LOCATION_ID);
		uriMatcher.addURI(AUTHORITY, "messages", MESSAGES);
		uriMatcher.addURI(AUTHORITY, "messages/#", MESSAGE_ID);
		uriMatcher.addURI(AUTHORITY, "employees", EMPLOYEES);
		uriMatcher.addURI(AUTHORITY, "employees/#", EMPLOYEE_DETAIL);
		uriMatcher.addURI(AUTHORITY, "assigned_routes", ROUTES);
		// WORK FLOWS
		uriMatcher.addURI(AUTHORITY, "work_flows_view", WORK_FLOWS_VIEW);
		uriMatcher.addURI(AUTHORITY, "pending_work_flows_view",
				PENDING_WORK_FLOWS_VIEW);
		uriMatcher.addURI(AUTHORITY, "work_flow_specs_view",
				WORK_FLOWS_SPECS_VIEW);
		uriMatcher.addURI(AUTHORITY, "work_flow_specs", WORK_FLOW_SPECS);
		uriMatcher.addURI(AUTHORITY, "work_flow_histories",
				WORK_FLOWS_HISTORIES);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.e(TAG, "Delete not implemented.");
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {

		case AGENDA:
			return Agenda.CONTENT_TYPE;

		case HOLIDAYS:
			return Holidays.CONTENT_TYPE;

		case CUSTOMERS:
			return Customers.CONTENT_TYPE;

		case CUSTOMER_ID:
			return Customers.CONTENT_ITEM_TYPE;

		case JOBS:
			return Jobs.CONTENT_TYPE;

		case JOB_ID:
			return Jobs.CONTENT_ITEM_TYPE;

		case JOB_TYPES:
			return JobTypes.CONTENT_TYPE;

		case JOB_TYPE_ID:
			return JobTypes.CONTENT_ITEM_TYPE;

		case JOB_STATES:
			return JobStates.CONTENT_TYPE;

		case JOB_STATE_ID:
			return JobStates.CONTENT_ITEM_TYPE;

		case TYPE_STATE_MAPPINGS:
			return TypeStateMappings.CONTENT_TYPE;

		case TYPE_STATE_MAPPING_ID:
			return TypeStateMappings.CONTENT_ITEM_TYPE;

		case INVITATIONS:
			return Invitations.CONTENT_TYPE;

		case INVITATION_ID:
			return Invitations.CONTENT_ITEM_TYPE;

		case NOTES:
			return Notes.CONTENT_TYPE;

		case NOTE_ID:
			return Notes.CONTENT_ITEM_TYPE;

		case LEAVES:
			return Leaves.CONTENT_TYPE;

		case FORM_SPECS:
			return FormSpecs.CONTENT_TYPE;

		case FORM_SPEC_ID:
			return FormSpecs.CONTENT_ITEM_TYPE;

		case FIELD_SPECS:
			return FieldSpecs.CONTENT_TYPE;

		case FIELD_SPEC_ID:
			return FieldSpecs.CONTENT_ITEM_TYPE;

		case FIELD_VALUE_SPECS:
			return FieldValueSpecs.CONTENT_TYPE;

		case FIELD_VALUE_SPEC_ID:
			return FieldValueSpecs.CONTENT_ITEM_TYPE;

		case FORMS:
			return Forms.CONTENT_TYPE;

		case FORM_ID:
			return Forms.CONTENT_ITEM_TYPE;

		case FIELDS:
			return Fields.CONTENT_TYPE;

		case FIELD_ID:
			return Fields.CONTENT_ITEM_TYPE;

		case FORMS_VIEW:
			return FormsView.CONTENT_ITEM_TYPE;

		case FIELDS_VIEW:
			return FieldsView.CONTENT_ITEM_TYPE;

		case ENTITIES:
			return Entities.CONTENT_TYPE;

		case ENTITY_ID:
			return Entities.CONTENT_ITEM_TYPE;

		case ENTITIES_VIEW:
			return EntitiesView.CONTENT_ITEM_TYPE;

		case ARTICLES:
			return Articles.CONTENT_TYPE;

		case ARTICLE_ID:
			return Articles.CONTENT_ITEM_TYPE;

		case NAMED_LOCATIONS:
			return NamedLocations.CONTENT_TYPE;

		case NAMED_LOCATION_ID:
			return NamedLocations.CONTENT_ITEM_TYPE;

		case MESSAGES:
			return Messages.CONTENT_TYPE;

		case MESSAGE_ID:
			return Messages.CONTENT_ITEM_TYPE;

		case WORK_FLOWS_VIEW:
			return WorkFlowsView.CONTENT_TYPE;

		case WORK_FLOWS_SPECS_VIEW:
			return WorkFlowSpecsView.CONTENT_TYPE;

		case WORK_FLOW_SPECS:
			return WorkFlowSpecs.CONTENT_TYPE;

		default:
			return null;

		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long insertedId = 0;

		switch (uriMatcher.match(uri)) {

		case JOBS:
			insertedId = db.insert(Jobs.TABLE, null, values);
			return Uri.withAppendedPath(Jobs.CONTENT_URI, "" + insertedId);

		case NOTES:
			insertedId = db.insert(Notes.TABLE, null, values);
			return Uri.withAppendedPath(Notes.CONTENT_URI, "" + insertedId);

		default:
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = DBHelper.getInstance(getContext().getApplicationContext());
		settingsDao = SettingsDao.getInstance(getContext()
				.getApplicationContext());

		// true indicates that provider was successfully loaded
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder builder = null;

		switch (uriMatcher.match(uri)) {

		case AGENDA:
			// TODO: dynamically frame the query honouring projection,
			// selection,
			// etc.
			boolean showCompletedJobs = settingsDao.getBoolean(
					Settings.KEY_SHOW_COMPLETED_JOBS, true);

			String completedFilter = showCompletedJobs ? "" : " AND ("
					+ Jobs.COMPLETED + " IS NULL OR " + Jobs.COMPLETED
					+ " = 'false')";

			// today in UTC
			String today = SQLiteDateTimeUtils
					.getSQLiteDateTimeForBeginningOfToday();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Beginning of today in UTC: " + today);
			}

			String query = "SELECT " + Jobs.TABLE + "." + Jobs._ID + " + "
					+ Agenda.JOB_ID_OFFSET + " AS " + Agenda._ID + ", "
					+ Agenda.TYPE_JOB + " AS " + Agenda.TYPE + ", "
					+ Jobs.TITLE + ", " + Jobs.DESCRIPTION + ", "
					+ JobTypes.NAME + " AS " + Agenda.EXTRA_INFO + ", "
					+ Jobs.START_TIME + ", " + Jobs.END_TIME + ", "
					+ Jobs.TREE_DIRTY + " FROM " + Jobs.TABLE + " JOIN "
					+ JobTypes.TABLE + " ON " + Jobs.JOB_TYPE_ID + " = "
					+ JobTypes.TABLE + "." + JobTypes._ID + " AND "
					+ JobTypes.CHECKED + " = 'true' WHERE " + Jobs.TEMPORARY
					+ " = 'false' AND " + Jobs.END_TIME + " >= '" + today + "'"
					+ completedFilter + " UNION SELECT " + Holidays._ID + " + "
					+ Agenda.HOLIDAY_ID_OFFSET + " AS " + Agenda._ID + ", "
					+ Agenda.TYPE_HOLIDAY + " AS " + Agenda.TYPE + ", "
					+ Holidays.TITLE + ", " + Holidays.DESCRIPTION + ", '' AS "
					+ Agenda.EXTRA_INFO + ", " + Holidays.START_TIME + ", "
					+ Holidays.END_TIME + ", 'false' AS " + Agenda.TREE_DIRTY
					+ " FROM " + Holidays.TABLE + " WHERE "
					+ Holidays.START_TIME + " >= '" + today + "' UNION SELECT "
					+ AssignedRoutes._ID + " + "
					+ Agenda.ASSIGNED_ROUTE_ID_OFFSET + " AS " + Agenda._ID
					+ ", " + Agenda.TYPE_ASSIGNED_ROUTE + " AS " + Agenda.TYPE
					+ ", " + AssignedRoutes.ROUTE_NAME + ", "
					+ AssignedRoutes.DURATION + ", '' AS " + Agenda.EXTRA_INFO
					+ ", " + AssignedRoutes.START_DATE + ", "
					+ AssignedRoutes.END_DATE + ", 'false' AS "
					+ Agenda.TREE_DIRTY + " FROM " + AssignedRoutes.TABLE
					+ " WHERE " + AssignedRoutes.END_DATE + " >= '" + today
					+ "' AND " + AssignedRoutes.DELETED + " != 'true' "
					+ " UNION SELECT " + SpecialWorkingDays._ID + " + "
					+ Agenda.SPECIAL_WORKING_DAY_ID_OFFSET + " AS "
					+ Agenda._ID + ", " + Agenda.TYPE_SPECIAL_WORKING_DAY
					+ " AS " + Agenda.TYPE + ", " + SpecialWorkingDays.TITLE
					+ ", " + SpecialWorkingDays.DESCRIPTION + ", '' AS "
					+ Agenda.EXTRA_INFO + ", " + SpecialWorkingDays.DATE
					+ " AS " + Agenda.START_TIME + ", "
					+ SpecialWorkingDays.DATE + " AS " + Agenda.END_TIME
					+ ", 'false' AS " + Agenda.TREE_DIRTY + " FROM "
					+ SpecialWorkingDays.TABLE + " WHERE "
					+ SpecialWorkingDays.DATE + " >= '" + today
					+ "' UNION SELECT " + Leaves.TABLE + "." + Leaves._ID
					+ " + " + Agenda.LEAVE_ID_OFFSET + " AS " + Agenda._ID
					+ ", " + Agenda.TYPE_LEAVE + " AS " + Agenda.TYPE + ", "
					+ LeaveStatus.TABLE + "." + LeaveStatus.NAME + " AS "
					+ Agenda.TITLE + ", " + Leaves.EMPLOYEE_REMARKS + " AS "
					+ Agenda.DESCRIPTION + ", '' AS " + Agenda.EXTRA_INFO
					+ ", " + Leaves.START_TIME + ", " + Leaves.END_TIME
					+ ", 'false' AS " + Agenda.TREE_DIRTY + " FROM "
					+ Leaves.TABLE + " JOIN " + LeaveStatus.TABLE + " ON "
					+ Leaves.TABLE + "." + Leaves.STATUS + " = "
					+ LeaveStatus.TABLE + "." + LeaveStatus._ID + " WHERE "
					+ Leaves.END_TIME + " >= '" + today + "' AND "
					+ Leaves.CANCELLED + " = 'false' ORDER BY "
					+ Agenda.START_TIME;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Agenda query: " + query);
			}

			return db.rawQuery(query, null);

		case JOBS:
			return db.query(Jobs.TABLE, projection, selection, selectionArgs,
					null, null, sortOrder);

		case JOB_ID:
			return db.query(Jobs.TABLE, projection,
					Jobs._ID + " = " + uri.getLastPathSegment(), null, null,
					null, null);

		case INVITATIONS:
			return db.query(Invitations.TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);

		case INVITATION_ID:
			return db.query(Invitations.TABLE, projection, Invitations._ID
					+ " = " + uri.getLastPathSegment(), null, null, null, null);

		case CUSTOMERS:
			return db.query(Customers.TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);

		case CUSTOMER_ID:
			return db.query(Customers.TABLE, projection, Customers._ID + " = "
					+ uri.getLastPathSegment(), null, null, null, null);

		case NOTES:
			return db.query(Notes.TABLE, Notes.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case NOTE_ID:
			return db.query(Notes.TABLE, Notes.ALL_COLUMNS, Notes._ID + " = "
					+ uri.getLastPathSegment(), null, null, null, null);

		case HOLIDAYS:
			return db.query(Holidays.TABLE, Holidays.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case LEAVES:
			return db.query(Leaves.TABLE, Leaves.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case LEAVE_ID:
			return db.query(Leaves.TABLE, Leaves.ALL_COLUMNS, Leaves._ID
					+ " = " + uri.getLastPathSegment(), null, null, null, null);

		case FORM_SPECS:
			builder = new SQLiteQueryBuilder();
			builder.setTables(FormSpecs.TABLE + " s1 LEFT JOIN "
					+ FormSpecs.TABLE + " s2 ON s1." + FormSpecs.UNIQUE_ID
					+ " = s2." + FormSpecs.UNIQUE_ID + " AND s1."
					+ FormSpecs._ID + " < s2." + FormSpecs._ID);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "FormSpecs query: " + builder.getTables()
						+ " WHERE " + "s2." + FormSpecs._ID + " IS NULL AND "
						+ selection);
			}

			return builder.query(db, projection, "s2." + FormSpecs._ID
					+ " IS NULL AND " + selection, selectionArgs, null, null,
					sortOrder);

		case FORM_SPEC_ID:
			return db.query(FormSpecs.TABLE, FormSpecs.ALL_COLUMNS,
					FormSpecs._ID + " = " + uri.getLastPathSegment(), null,
					null, null, null);

		case FIELD_SPECS:
			return db.query(FieldSpecs.TABLE, FieldSpecs.ALL_COLUMNS,
					selection, selectionArgs, null, null, sortOrder);

		case FIELD_SPEC_ID:
			return db.query(FieldSpecs.TABLE, FieldSpecs.ALL_COLUMNS,
					FieldSpecs._ID + " = " + uri.getLastPathSegment(), null,
					null, null, null);

		case FIELD_VALUE_SPECS:
			return db.query(FieldValueSpecs.TABLE, FieldValueSpecs.ALL_COLUMNS,
					selection, selectionArgs, null, null, sortOrder);

		case FIELD_VALUE_SPEC_ID:
			return db.query(FieldValueSpecs.TABLE, FieldValueSpecs.ALL_COLUMNS,
					FieldValueSpecs._ID + " = " + uri.getLastPathSegment(),
					null, null, null, null);

		case FORMS:
			// db.query(F)
			return db.query(Forms.TABLE, Forms.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case FORM_ID:
			return db.query(Forms.TABLE, Forms.ALL_COLUMNS, Forms._ID + " = "
					+ uri.getLastPathSegment(), null, null, null, null);

		case FIELDS:
			return db.query(Fields.TABLE, Fields.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case FIELD_ID:
			return db.query(Fields.TABLE, Fields.ALL_COLUMNS, Fields._ID
					+ " = " + uri.getLastPathSegment(), null, null, null, null);

		case FORMS_VIEW:
			builder = new SQLiteQueryBuilder();
			builder.setTables(Forms.TABLE + " LEFT JOIN " + FieldSpecs.TABLE
					+ " ON " + Forms.TABLE + "." + Forms.FORM_SPEC_ID + " = "
					+ FieldSpecs.TABLE + "." + FieldSpecs.FORM_SPEC_ID
					+ " AND " + FieldSpecs.IDENTIFIER + " = 'true' LEFT JOIN "
					+ Fields.TABLE + " ON " + FieldSpecs.TABLE + "."
					+ FieldSpecs._ID + " = " + Fields.TABLE + "."
					+ Fields.FIELD_SPEC_ID + " AND " + Forms.TABLE + "."
					+ Forms._ID + " = " + Fields.LOCAL_FORM_ID);

			return builder.query(db, FormsView.ALL_COLUMNS, selection,
					selectionArgs, null, null, FormsView.LOCAL_CREATION_TIME);

		case FIELDS_VIEW:
			builder = new SQLiteQueryBuilder();
			builder.setTables(Fields.TABLE + " JOIN " + FieldSpecs.TABLE
					+ " ON " + Fields.FIELD_SPEC_ID + " = " + FieldSpecs.TABLE
					+ "." + FieldSpecs._ID);

			return builder.query(db, FieldsView.ALL_COLUMNS, selection,
					selectionArgs, null, null, FieldsView.DISPLAY_ORDER);

		case ENTITIES:
			return db.query(Entities.TABLE, Entities.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case ENTITIES_VIEW:
			query = "SELECT " + EntityFields.LOCAL_ENTITY_ID + ", "
					+ EntitiesView.TYPE + " AS " + EntityFieldSpecs.TYPE + ", "
					+ EntityFields.LOCAL_VALUE + ", "
					+ EntityFields.REMOTE_VALUE + ", "
					+ EntityFieldValueSpecs.TABLE + "."
					+ EntityFieldValueSpecs.VALUE + " AS "
					+ EntitiesView.DISPLAY_VALUE + ", " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_ENTITY_ID + " AS "
					+ EntitiesView._ID + " FROM " + EntityFields.TABLE
					+ " JOIN " + EntityFieldSpecs.TABLE + " ON "
					+ EntityFields.TABLE + "." + EntityFields.FIELD_SPEC_ID
					+ " = " + EntityFieldSpecs.TABLE + "."
					+ EntityFieldSpecs._ID + " AND " + EntityFieldSpecs.TABLE
					+ "." + EntityFieldSpecs.IDENTIFIER + " = 'true' AND "
					+ EntityFieldSpecs.TYPE + " = "
					+ FieldSpecs.TYPE_SINGLE_SELECT_LIST + " JOIN "
					+ EntityFieldValueSpecs.TABLE + " ON " + EntityFields.TABLE
					+ "." + EntityFields.REMOTE_VALUE + " = "
					+ EntityFieldValueSpecs.TABLE + "."
					+ EntityFieldValueSpecs._ID + " JOIN " + Entities.TABLE
					+ " ON " + EntityFields.LOCAL_ENTITY_ID + " = "
					+ Entities.TABLE + "." + Entities._ID + " AND "
					+ Entities.TABLE + "." + Entities.DELETED + " = 'false'"
					+ " WHERE " + EntityFields.TABLE + "."
					+ EntityFields.ENTITY_SPEC_ID + " = " + selection
					+ " UNION SELECT " + EntityFields.LOCAL_ENTITY_ID + ", "
					+ EntitiesView.TYPE + " AS " + EntityFieldSpecs.TYPE + ", "
					+ EntityFields.LOCAL_VALUE + ", "
					+ EntityFields.REMOTE_VALUE + ", " + Customers.TABLE + "."
					+ Customers.NAME + " AS " + EntitiesView.DISPLAY_VALUE
					+ ", " + EntityFields.TABLE + "."
					+ EntityFields.LOCAL_ENTITY_ID + " AS " + EntitiesView._ID
					+ " FROM " + EntityFields.TABLE + " JOIN "
					+ EntityFieldSpecs.TABLE + " ON " + EntityFields.TABLE
					+ "." + EntityFields.FIELD_SPEC_ID + " = "
					+ EntityFieldSpecs.TABLE + "." + EntityFieldSpecs._ID
					+ " AND " + EntityFieldSpecs.TABLE + "."
					+ EntityFieldSpecs.IDENTIFIER + " = 'true' AND "
					+ EntityFieldSpecs.TYPE + " = " + FieldSpecs.TYPE_CUSTOMER
					+ " JOIN " + Customers.TABLE + " ON " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_VALUE + " = " + Customers.TABLE
					+ "." + Customers._ID + " JOIN " + Entities.TABLE + " ON "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + Entities.TABLE
					+ "." + Entities._ID + " AND " + Entities.TABLE + "."
					+ Entities.DELETED + " = 'false'" + " WHERE "
					+ EntityFields.TABLE + "." + EntityFields.ENTITY_SPEC_ID
					+ " = " + selection + " UNION SELECT "
					+ EntityFields.LOCAL_ENTITY_ID + ", " + EntitiesView.TYPE
					+ " AS " + EntityFieldSpecs.TYPE + ", "
					+ EntityFields.LOCAL_VALUE + ", "
					+ EntityFields.REMOTE_VALUE + ", " + Employees.TABLE + "."
					+ Employees.EMPLOYEE_FIRST_NAME + " AS "
					+ EntitiesView.DISPLAY_VALUE + ", " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_ENTITY_ID + " AS "
					+ EntitiesView._ID + " FROM " + EntityFields.TABLE
					+ " JOIN " + EntityFieldSpecs.TABLE + " ON "
					+ EntityFields.TABLE + "." + EntityFields.FIELD_SPEC_ID
					+ " = " + EntityFieldSpecs.TABLE + "."
					+ EntityFieldSpecs._ID + " AND " + EntityFieldSpecs.TABLE
					+ "." + EntityFieldSpecs.IDENTIFIER + " = 'true' AND "
					+ EntityFieldSpecs.TYPE + " = " + FieldSpecs.TYPE_EMPLOYEE
					+ " JOIN " + Employees.TABLE + " ON " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_VALUE + " = " + Employees.TABLE
					+ "." + Employees._ID + " JOIN " + Entities.TABLE + " ON "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + Entities.TABLE
					+ "." + Entities._ID + " AND " + Entities.TABLE + "."
					+ Entities.DELETED + " = 'false'" + " WHERE "
					+ EntityFields.TABLE + "." + EntityFields.ENTITY_SPEC_ID
					+ " = " + selection

					+ " UNION SELECT " + EntityFields.LOCAL_ENTITY_ID + ", "
					+ EntitiesView.TYPE + " AS " + EntityFieldSpecs.TYPE + ", "
					+ EntityFields.LOCAL_VALUE + ", "
					+ EntityFields.REMOTE_VALUE + ", " + EntityFields.TABLE + "."
					+ EntityFields.REMOTE_VALUE + " AS "
					+ EntitiesView.DISPLAY_VALUE + ", " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_ENTITY_ID + " AS "
					+ EntitiesView._ID + " FROM " + EntityFields.TABLE
					+ " JOIN " + EntityFieldSpecs.TABLE + " ON "
					+ EntityFields.TABLE + "." + EntityFields.FIELD_SPEC_ID
					+ " = " + EntityFieldSpecs.TABLE + "."
					+ EntityFieldSpecs._ID + " AND " + EntityFieldSpecs.TABLE
					+ "." + EntityFieldSpecs.IDENTIFIER + " = 'true' AND "
					+ EntityFieldSpecs.TYPE + " = " + FieldSpecs.TYPE_ENTITY
					+ " JOIN " + Entities.TABLE + " ON "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + Entities.TABLE
					+ "." + Entities._ID + " AND " + Entities.TABLE + "."
					+ Entities.DELETED + " = 'false'" + "WHERE "
					+ EntityFields.TABLE + "." + EntityFields.ENTITY_SPEC_ID
					+ " = " + selection

					+ " UNION SELECT " + EntityFields.LOCAL_ENTITY_ID + ", "
					+ EntitiesView.TYPE + " AS " + EntityFieldSpecs.TYPE + ", "
					+ EntityFields.LOCAL_VALUE + ", "
					+ EntityFields.REMOTE_VALUE + ", " + EntityFields.TABLE
					+ "." + EntityFields.REMOTE_VALUE + " AS "
					+ EntitiesView.DISPLAY_VALUE + ", " + EntityFields.TABLE
					+ "." + EntityFields.LOCAL_ENTITY_ID + " AS "
					+ EntitiesView._ID + " FROM " + EntityFields.TABLE
					+ " JOIN " + EntityFieldSpecs.TABLE + " ON "
					+ EntityFields.TABLE + "." + EntityFields.FIELD_SPEC_ID
					+ " = " + EntityFieldSpecs.TABLE + "."
					+ EntityFieldSpecs._ID + " AND " + EntityFieldSpecs.TABLE
					+ "." + EntityFieldSpecs.IDENTIFIER + " = 'true' AND "
					+ EntityFieldSpecs.TYPE + " NOT IN ("
					+ FieldSpecs.TYPE_CUSTOMER + ", "
					+ FieldSpecs.TYPE_SINGLE_SELECT_LIST + ", "
					+ FieldSpecs.TYPE_ENTITY + ", " + FieldSpecs.TYPE_EMPLOYEE
					+ ") " + " JOIN " + Entities.TABLE + " ON "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + Entities.TABLE
					+ "." + Entities._ID + " AND " + Entities.TABLE + "."
					+ Entities.DELETED + " = 'false'" + "WHERE "
					+ EntityFields.TABLE + "." + EntityFields.ENTITY_SPEC_ID
					+ " = " + selection + " ORDER BY " + sortOrder;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Query: " + query);
			}

			return db.rawQuery(query, null);

		case ARTICLES:
			Cursor cursorArticles = db.query(Articles.TABLE,
					Articles.ALL_COLUMNS, selection, selectionArgs, null, null,
					sortOrder);
			return cursorArticles;

		case ARTICLE_ID:
			return db.query(Articles.TABLE, Articles.ALL_COLUMNS, Articles._ID
					+ " = " + uri.getLastPathSegment(), null, null, null, null);

		case NAMED_LOCATIONS:
			return db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					selection, selectionArgs, null, null, sortOrder);

		case NAMED_LOCATION_ID:
			return db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations._ID + " = " + uri.getLastPathSegment(),
					null, null, null, null);

		case MESSAGES:
			return db.query(Messages.TABLE, Messages.ALL_COLUMNS, selection,
					selectionArgs, null, null, sortOrder);

		case MESSAGE_ID:
			return db.query(Messages.TABLE, Messages.ALL_COLUMNS, Messages._ID
					+ " = " + uri.getLastPathSegment(), null, null, null, null);

		case EMPLOYEES:
			String emploueeIds = settingsDao
					.getString(Settings.KEY_EMPLOYEE_UNDER_ID);
			return db.query(Employees.TABLE, Employees.ALL_COLUMNS,
					Employees.EMPLOYEE_ID + " IN(" + emploueeIds + ")", null,
					null, null, null);
		case ROUTES:
			Cursor cu = db.query(AssignedRoutes.TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cu;

		case EMPLOYEE_DETAIL:
			return db.query(Employees.TABLE, Employees.ALL_COLUMNS,
					Employees._ID + " = " + uri.getLastPathSegment(), null,
					null, null, null);
		case WORK_FLOWS_VIEW:

			builder = new SQLiteQueryBuilder();
			if (sortOrder != null
					&& sortOrder
							.equalsIgnoreCase(WorkFlowsActivity.FORM_CREATE)) {
				builder.setTables(WorkFlowStatus.TABLE + " LEFT JOIN "

				+ WorkFlowSpecs.TABLE + " ON " + WorkFlowStatus.TABLE + "."
						+ WorkFlowStatus.WORK_FLOW_ID + " = "
						+ WorkFlowSpecs.TABLE + "." + WorkFlowSpecs._ID
						+ " LEFT JOIN " + Forms.TABLE + " ON "
						+ WorkFlowStatus.TABLE + "."
						+ WorkFlowStatus.LOCAL_FORM_ID + " = " + Forms.TABLE
						+ "." + Forms._ID + " LEFT JOIN " + FormSpecs.TABLE
						+ " ON " + Forms.TABLE + "." + Forms.FORM_SPEC_ID
						+ " = " + FormSpecs.TABLE + "." + FormSpecs._ID
						+ " LEFT JOIN " + FieldSpecs.TABLE + " ON "
						+ Forms.TABLE + "." + Forms.FORM_SPEC_ID + " = "
						+ FieldSpecs.TABLE + "." + FieldSpecs.FORM_SPEC_ID
						+ " AND " + FieldSpecs.IDENTIFIER
						+ " = 'true' LEFT JOIN " + Fields.TABLE + " ON "
						+ FieldSpecs.TABLE + "." + FieldSpecs._ID + " = "
						+ Fields.TABLE + "." + Fields.FIELD_SPEC_ID + " AND "
						+ Forms.TABLE + "." + Forms._ID + " = " + Fields.TABLE
						+ "." + Fields.LOCAL_FORM_ID);

				Cursor cur = builder.query(db, WorkFlowsView.ALL_COLUMNS,
						selection, selectionArgs, null, null,
						WorkFlowsView.LOCAL_CREATION_TIME + " DESC");
				return cur;

			} else {
				builder.setTables(Forms.TABLE + " LEFT JOIN "

				+ WorkFlowFormSpecMappings.TABLE + " ON " + Forms.TABLE + "."
						+ Forms.FORM_SPEC_UNIQUE_ID + " = "
						+ WorkFlowFormSpecMappings.TABLE + "."
						+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
						+ " LEFT JOIN " + WorkFlowSpecs.TABLE + " ON "
						+ WorkFlowFormSpecMappings.TABLE + "."
						+ WorkFlowFormSpecMappings.WORK_FLOW_ID + " = "
						+ WorkFlowSpecs.TABLE + "." + WorkFlowSpecs._ID

						+ " LEFT JOIN " + WorkFlowStatus.TABLE + " ON "
						+ WorkFlowStatus.TABLE + "."
						+ WorkFlowStatus.LOCAL_FORM_ID + " = " + Forms.TABLE
						+ "." + Forms._ID + " LEFT JOIN " + FormSpecs.TABLE
						+ " ON " + Forms.TABLE + "." + Forms.FORM_SPEC_ID
						+ " = " + FormSpecs.TABLE + "." + FormSpecs._ID
						+ " LEFT JOIN " + FieldSpecs.TABLE + " ON "
						+ Forms.TABLE + "." + Forms.FORM_SPEC_ID + " = "
						+ FieldSpecs.TABLE + "." + FieldSpecs.FORM_SPEC_ID
						+ " AND " + FieldSpecs.IDENTIFIER
						+ " = 'true' LEFT JOIN " + Fields.TABLE + " ON "
						+ FieldSpecs.TABLE + "." + FieldSpecs._ID + " = "
						+ Fields.TABLE + "." + Fields.FIELD_SPEC_ID + " AND "
						+ Forms.TABLE + "." + Forms._ID + " = " + Fields.TABLE
						+ "." + Fields.LOCAL_FORM_ID);
				Cursor cursor = builder.query(db, WorkFlowsView.ALL_COLUMNS,
						selection, selectionArgs, null, null,
						WorkFlowsView.LOCAL_CREATION_TIME + " DESC");
				return cursor;
			}

		case WORK_FLOWS_SPECS_VIEW:

			builder = new SQLiteQueryBuilder();
			builder.setTables(WorkFlowSpecs.TABLE + " JOIN "
					+ WorkFlowFormSpecMappings.TABLE + " ON "
					+ WorkFlowSpecs.TABLE + "." + WorkFlowSpecs._ID + " = "
					+ WorkFlowFormSpecMappings.TABLE + "."
					+ WorkFlowFormSpecMappings.WORK_FLOW_ID + " JOIN "
					+ FormSpecs.TABLE + " ON " + WorkFlowFormSpecMappings.TABLE
					+ "." + WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = " + FormSpecs.TABLE + "." + FormSpecs.UNIQUE_ID
					+ " LEFT JOIN " + FormSpecs.TABLE + " fs2 ON "
					+ WorkFlowFormSpecMappings.TABLE + "."
					+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = fs2." + FormSpecs.UNIQUE_ID + " AND "
					+ FormSpecs.TABLE + "." + FormSpecs._ID + " < fs2."
					+ FormSpecs._ID);

			Cursor cursor11 = builder.query(
					db,
					WorkFlowSpecsView.ALL_COLUMNS,
					" fs2."
							+ FormSpecs._ID
							+ " IS NULL"
							+ (TextUtils.isEmpty(selection) ? "" : " AND "
									+ selection), selectionArgs, null, null,
					"UPPER(" + WorkFlowSpecsView.FORM_SPEC_TITLE + ")"
							+ " ASC ");

			return cursor11;

		case WORK_FLOWS_HISTORIES:
			return db.query(WorkFlowHistories.TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
		default:
			return null;

		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsAffected = 0;

		switch (uriMatcher.match(uri)) {

		case JOBS:
			rowsAffected = db.update(Jobs.TABLE, values, selection,
					selectionArgs);
			break;

		case NOTES:
			rowsAffected = db.update(Notes.TABLE, values, selection,
					selectionArgs);
			break;

		default:
			break;
		}

		return rowsAffected;
	}

	/**
	 * This is a virtual table (a view), which is a union of jobs, holidays, and
	 * special working days
	 * 
	 * @author tiru
	 * 
	 */
	public static final class Agenda implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Agenda() {
		}

		/** Table name */
		public static final String TABLE = "agenda";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.agenda";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Integer (1: Job, 2: Holiday, 3: Special Working day, 4: Leave,
		 * 5: AssignedRoutes
		 * </p>
		 */
		public static final String TYPE = "type";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String EXTRA_INFO = "extra_info";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		/**
		 * <p>
		 * Type: Boolean (Stored as string)
		 * </p>
		 */
		public static final String TREE_DIRTY = "tree_dirty";

		public static final String[] ALL_COLUMNS = { _ID, TYPE, TITLE,
				DESCRIPTION, EXTRA_INFO, START_TIME, END_TIME, TREE_DIRTY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TYPE_INDEX = 1;
		public static final int TITLE_INDEX = 2;
		public static final int DESCRIPTION_INDEX = 3;
		public static final int EXTRA_INFO_INDEX = 4;
		public static final int START_TIME_INDEX = 5;
		public static final int END_TIME_INDEX = 6;
		public static final int TREE_DIRTY_INDEX = 7;

		public static final int TYPE_JOB = 1;
		public static final int TYPE_HOLIDAY = 2;
		public static final int TYPE_SPECIAL_WORKING_DAY = 3;
		public static final int TYPE_LEAVE = 4;
		public static final int TYPE_ASSIGNED_ROUTE = 5;

		// These offsets are used to make sure that id's don't conflict with
		// each other
		private static final long OFFSET = 1000000000000000L; // thousand
		// trillion
		public static final long JOB_ID_OFFSET = TYPE_JOB * OFFSET;
		public static final long HOLIDAY_ID_OFFSET = TYPE_HOLIDAY * OFFSET;
		public static final long SPECIAL_WORKING_DAY_ID_OFFSET = TYPE_SPECIAL_WORKING_DAY
				* OFFSET;
		public static final long LEAVE_ID_OFFSET = TYPE_LEAVE * OFFSET;
		public static final long ASSIGNED_ROUTE_ID_OFFSET = TYPE_ASSIGNED_ROUTE
				* OFFSET;
	}

	public static final class Jobs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Jobs() {
		}

		/** Table name */
		public static final String TABLE = "jobs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.jobs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String JOB_TYPE_ID = "job_type_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String JOB_STATE_ID = "job_state_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_CUSTOMER_ID = "local_customer_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String EMPLOYEE_ID = "employee_id";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String COMPLETED = "completed";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String COMPLETION_TIME = "completion_time";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String APPROVED = "approved";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String ANDROID_EVENT_ID = "android_event_id";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Indicates whether there are any unsynced changes in the tree.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TREE_DIRTY = "tree_dirty";

		/**
		 * Indicates whether the job is read/unread. True if it is read. False
		 * if it is unread.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String READ = "read";

		/**
		 * Indicates whether the job has been saved or not.
		 * 
		 * There can be at most one temporary job, and the temporary job is not
		 * synced to the server.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String CACHED = "cached";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 * As on 2013-03-14, server doesn't return the job creation time
		 */
		public static final String REMOTE_MODIFICATION_TIME = "remote_modification_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LATITUDE = "latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LONGITUDE = "longitude";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STREET = "street";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String AREA = "area";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CITY = "city";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATE = "state";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String COUNTRY = "country";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PIN_CODE = "pin_code";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LANDMARK = "landmark";

		/**
		 * <p>
		 * Type: Boolean (Stored as a string)
		 * </p>
		 */
		public static final String SAME_AS_CUSTOMER_ADDRESS = "same_as_customer_address";

		/**
		 * <p>
		 * Type: Boolean (Stored as a string)
		 * </p>
		 */
		public static final String LATE_ALERT_DISMISSED = "late_alert_dismissed";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID, TITLE,
				DESCRIPTION, JOB_TYPE_ID, JOB_STATE_ID, LOCAL_CUSTOMER_ID,
				EMPLOYEE_ID, START_TIME, END_TIME, COMPLETED, COMPLETION_TIME,
				APPROVED, ANDROID_EVENT_ID, DIRTY, TREE_DIRTY, READ, TEMPORARY,
				CACHED, REMOTE_MODIFICATION_TIME, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, LATITUDE, LONGITUDE, STREET, AREA,
				CITY, STATE, COUNTRY, PIN_CODE, LANDMARK,
				SAME_AS_CUSTOMER_ADDRESS, LATE_ALERT_DISMISSED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int TITLE_INDEX = 2;
		public static final int DESCRIPTION_INDEX = 3;
		public static final int JOB_TYPE_ID_INDEX = 4;
		public static final int JOB_STATE_ID_INDEX = 5;
		public static final int LOCAL_CUSTOMER_ID_INDEX = 6;
		public static final int EMPLOYEE_ID_INDEX = 7;
		public static final int START_TIME_INDEX = 8;
		public static final int END_TIME_INDEX = 9;
		public static final int COMPLETED_INDEX = 10;
		public static final int COMPLETION_TIME_INDEX = 11;
		public static final int APPROVED_INDEX = 12;
		public static final int ANROID_EVENT_ID_INDEX = 13;
		public static final int DIRTY_INDEX = 14;
		public static final int TREE_DIRTY_INDEX = 15;
		public static final int READ_INDEX = 16;
		public static final int TEMPORARY_INDEX = 17;
		public static final int CACHED_INDEX = 18;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 19;
		public static final int LOCAL_CREATION_TIME_INDEX = 20;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 21;
		public static final int LATITUDE_INDEX = 22;
		public static final int LONGITUDE_INDEX = 23;
		public static final int STREET_INDEX = 24;
		public static final int AREA_INDEX = 25;
		public static final int CITY_INDEX = 26;
		public static final int STATE_INDEX = 27;
		public static final int COUNTRY_INDEX = 28;
		public static final int PIN_CODE_INDEX = 29;
		public static final int LANDMARK_INDEX = 30;
		public static final int SAME_AS_CUSTOMER_ADDRESS_INDEX = 31;
		public static final int LATE_ALERT_DISMISSED_INDEX = 32;
	}

	public static final class JobHistories implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private JobHistories() {
		}

		/** Table name */
		public static final String TABLE = "job_histories";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.job.histories";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_JOB_ID = "local_job_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String JOB_TYPE_ID = "job_type_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String JOB_STATE_ID = "job_state_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_CUSTOMER_ID = "local_customer_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String EMPLOYEE_ID = "employee_id";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String COMPLETED = "completed";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String COMPLETION_TIME = "completion_time";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String APPROVED = "approved";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";
		/**
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID,
				LOCAL_JOB_ID, LOCAL_FORM_ID, TITLE, DESCRIPTION, JOB_TYPE_ID,
				JOB_STATE_ID, LOCAL_CUSTOMER_ID, EMPLOYEE_ID, START_TIME,
				END_TIME, COMPLETED, COMPLETION_TIME, APPROVED, DIRTY,
				REMOTE_CREATION_TIME, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, TEMPORARY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int LOCAL_JOB_ID_INDEX = 2;
		public static final int LOCAL_FORM_ID_INDEX = 3;
		public static final int TITLE_INDEX = 4;
		public static final int DESCRIPTION_INDEX = 5;
		public static final int JOB_TYPE_ID_INDEX = 6;
		public static final int JOB_STATE_ID_INDEX = 7;
		public static final int LOCAL_CUSTOMER_ID_INDEX = 8;
		public static final int EMPLOYEE_ID_INDEX = 9;
		public static final int START_TIME_INDEX = 10;
		public static final int END_TIME_INDEX = 11;
		public static final int COMPLETED_INDEX = 12;
		public static final int COMPLETION_TIME_INDEX = 13;
		public static final int APPROVED_INDEX = 14;
		public static final int DIRTY_INDEX = 15;
		public static final int REMOTE_CREATION_TIME_INDEX = 16;
		public static final int LOCAL_CREATION_TIME_INDEX = 17;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 18;
		public static final int TEMPORARY_INDEX = 19;
	}

	public static final class Invitations implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Invitations() {
		}

		/** Table name */
		public static final String TABLE = "invitations";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.invitations";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String JOB_TYPE_ID = "job_type_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_CUSTOMER_ID = "local_customer_id";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String ACCEPTED = "accepted";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Indicates whether the job is read/unread. True if it is read. False
		 * if it is unread.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String READ = "read";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LATITUDE = "latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LONGITUDE = "longitude";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STREET = "street";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String AREA = "area";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CITY = "city";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATE = "state";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String COUNTRY = "country";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PIN_CODE = "pin_code";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LANDMARK = "landmark";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, DESCRIPTION,
				JOB_TYPE_ID, LOCAL_CUSTOMER_ID, START_TIME, END_TIME, READ,
				ACCEPTED, DIRTY, LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME,
				LATITUDE, LONGITUDE, STREET, AREA, CITY, STATE, COUNTRY,
				PIN_CODE, LANDMARK };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int DESCRIPTION_INDEX = 2;
		public static final int JOB_TYPE_ID_INDEX = 3;
		public static final int LOCAL_CUSTOMER_ID_INDEX = 4;
		public static final int START_TIME_INDEX = 5;
		public static final int END_TIME_INDEX = 6;
		public static final int READ_INDEX = 7;
		public static final int ACCEPTED_INDEX = 8;
		public static final int DIRTY_INDEX = 9;
		public static final int LOCAL_CREATION_TIME_INDEX = 10;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 11;
		public static final int LATITUDE_INDEX = 12;
		public static final int LONGITUDE_INDEX = 13;
		public static final int STREET_INDEX = 14;
		public static final int AREA_INDEX = 15;
		public static final int CITY_INDEX = 16;
		public static final int STATE_INDEX = 17;
		public static final int COUNTRY_INDEX = 18;
		public static final int PIN_CODE_INDEX = 19;
		public static final int LANDMARK_INDEX = 20;
	}

	public static final class CustomerTypes implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private CustomerTypes() {
		}

		/** Table name */
		public static final String TABLE = "customer_types";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.customer.types";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String CHECKED = "checked";

		public static final String[] ALL_COLUMNS = { _ID, NAME, CHECKED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int CHECKED_INDEX = 2;
	}

	public static final class JobTypes implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private JobTypes() {
		}

		/** Table name */
		public static final String TABLE = "job_types";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.job.types";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String CHECKED = "checked";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String DEFAULT_TYPE = "default_type";

		public static final String[] ALL_COLUMNS = { _ID, NAME, CHECKED,
				DEFAULT_TYPE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int CHECKED_INDEX = 2;
		public static final int DEFAULT_TYPE_INDEX = 3;
	}

	public static final class JobStates implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private JobStates() {
		}

		/** Table name */
		public static final String TABLE = "job_states";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.job.states";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String FORM_REQUIRED = "form_required";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String START_STATE = "start_state";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String END_STATE = "end_state";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String DEFAULT_STATE = "default_state";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String REVISITABLE = "revisitable";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MIN_SUBMISSIONS = "min_submissions";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MAX_SUBMISSIONS = "max_submissions";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String MANDATORY_FOR_COMPLETION = "mandatory_for_completion";

		public static final String[] ALL_COLUMNS = { _ID, NAME, FORM_SPEC_ID,
				FORM_REQUIRED, START_STATE, END_STATE, DEFAULT_STATE,
				REVISITABLE, MIN_SUBMISSIONS, MAX_SUBMISSIONS,
				MANDATORY_FOR_COMPLETION };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int FORM_SPEC_ID_INDEX = 2;
		public static final int FORM_REQUIRED_INDEX = 3;
		public static final int START_STATE_INDEX = 4;
		public static final int END_STATE_INDEX = 5;
		public static final int DEFAULT_STATE_INDEX = 6;
		public static final int REVISITABLE_INDEX = 7;
		public static final int MIN_SUBMISSIONS_INDEX = 8;
		public static final int MAX_SUBMISSIONS_INDEX = 9;
		public static final int MANDATORY_FOR_COMPLETION_INDEX = 10;
	}

	public static final class JobStageStatuses implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private JobStageStatuses() {
		}

		/** Table name */
		public static final String TABLE = "job_stage_statuses";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.job.stage.statuses";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_JOB_ID = "local_job_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String STATE_ID = "state_id";

		/**
		 * <p>
		 * Type: Boolean (String)
		 * </p>
		 */
		public static final String DONE = "done";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";
		/**
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";

		public static final String[] ALL_COLUMNS = { _ID, LOCAL_JOB_ID,
				STATE_ID, DONE, DIRTY, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, TEMPORARY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int LOCAL_JOB_ID_INDEX = 1;
		public static final int STATE_ID_INDEX = 2;
		public static final int DONE_INDEX = 3;
		public static final int DIRTY_INDEX = 4;
		public static final int LOCAL_CREATION_TIME_INDEX = 5;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 6;
		public static final int TEMPORARY_INDEX = 7;
	}

	public static final class TypeStateMappings implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private TypeStateMappings() {
		}

		/** Table name */
		public static final String TABLE = "type_state_mappings";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.type.state.mappings";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String TYPE_ID = "type_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String STATE_ID = "state_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = "display_order";

		public static final String[] ALL_COLUMNS = { _ID, TYPE_ID, STATE_ID,
				DISPLAY_ORDER };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TYPE_ID_INDEX = 1;
		public static final int STATE_ID_INDEX = 2;
		public static final int DISPLAY_ORDER_INDEX = 3;
	}

	public static final class LeaveStatus implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private LeaveStatus() {
		}

		/** Table name */
		public static final String TABLE = "leave_status";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.job.types";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		public static final String[] ALL_COLUMNS = { _ID, NAME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
	}

	public static final class Holidays implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Holidays() {
		}

		/** Table name */
		public static final String TABLE = "holidays";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.holidays";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, DESCRIPTION,
				START_TIME, END_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int DESCRIPTION_INDEX = 2;
		public static final int START_TIME_INDEX = 3;
		public static final int END_TIME_INDEX = 4;
	}

	public static final class SpecialWorkingDays implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SpecialWorkingDays() {
		}

		/** Table name */
		public static final String TABLE = "special_working_days";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.special.working.days";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: String (SQLite Date)
		 * </p>
		 */
		public static final String DATE = "date";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, DESCRIPTION,
				DATE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int DESCRIPTION_INDEX = 2;
		public static final int DATE_INDEX = 3;
	}

	public static final class SpecialWorkingHours implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SpecialWorkingHours() {
		}

		/** Table name */
		public static final String TABLE = "special_working_hours";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.special.working.hours";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * This is a foreign-key that references SpecialWorkingDays._ID.
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String SPECIAL_WORKING_DAY_ID = "special_working_day_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		public static final String[] ALL_COLUMNS = { _ID,
				SPECIAL_WORKING_DAY_ID, TITLE, START_TIME, END_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int SPECIAL_WORKING_DAY_ID_INDEX = 1;
		public static final int TITLE_INDEX = 2;
		public static final int START_TIME_INDEX = 3;
		public static final int END_TIME_INDEX = 4;
	}

	public static final class WorkingHours implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkingHours() {
		}

		/** Table name */
		public static final String TABLE = "working_hours";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.working.hours";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * This must be set to 1-7. Sunday is 1, and Saturday is 7.
		 * 
		 * You can use the day of week constants defined in java.util.Calendar
		 * class.
		 * 
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DAY_OF_WEEK = "day_of_week";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String (SQLite Time) in IST. This is one of the very few
		 * exceptions, usually times are stored in GMT.
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Time) in IST. This is one of the very few
		 * exceptions, usually times are stored in GMT.
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		public static final String[] ALL_COLUMNS = { _ID, DAY_OF_WEEK, TITLE,
				START_TIME, END_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int DAY_OF_WEEK_INDEX = 1;
		public static final int TITLE_INDEX = 2;
		public static final int START_TIME_INDEX = 3;
		public static final int END_TIME_INDEX = 4;
	}

	public static final class FormSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FormSpecs() {
		}

		/** Table name */
		public static final String TABLE = "form_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.form.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String WITHDRAWN = "withdrawn";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String VISIBLE = "visible";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String UNIQUE_ID = "unique_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PRINT_TEMPLATE = "print_template";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, WITHDRAWN,
				VISIBLE, UNIQUE_ID, PRINT_TEMPLATE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int WITHDRAWN_INDEX = 2;
		public static final int VISIBLE_INDEX = 3;
		public static final int UNIQUE_ID_INDEX = 4;
		public static final int PRINT_TEMPLATE_INDEX = 5;
	}

	/**
	 * @author susmitha
	 * 
	 */
	public static final class FieldSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FieldSpecs() {
		}

		/** Table name */
		public static final String TABLE = "field_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.field.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";
		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String VISIBLE = "visible";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String EDITABLE = "editable";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MIN_VALUE = "min_value";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MAX_VALUE = "max_value";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LABEL = "label";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = "type";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String REQUIRED = "required";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = "display_order";

		/**
		 * Used for storing entity spec id when type is entity.
		 * 
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TYPE_EXTRA = "type_extra";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SELECTOR = "selector";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String COMPUTED = "computed";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String BARCODE = "barcode";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORMULA = "formula";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String PAGE_ID = "page_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String UNIQUE_ID = "unique_id";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String DEFAULT_FIELD = "default_field";

		public static final String[] ALL_COLUMNS = { _ID, FORM_SPEC_ID, LABEL,
				TYPE, IDENTIFIER, REQUIRED, DISPLAY_ORDER, TYPE_EXTRA,
				SELECTOR, COMPUTED, FORMULA, PAGE_ID, UNIQUE_ID, BARCODE,
				VISIBLE, EDITABLE, MIN_VALUE, MAX_VALUE, DEFAULT_FIELD };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int LABEL_INDEX = 2;
		public static final int TYPE_INDEX = 3;
		public static final int IDENTIFIER_INDEX = 4;
		public static final int REQUIRED_INDEX = 5;
		public static final int DISPLAY_ORDER_INDEX = 6;
		public static final int TYPE_EXTRA_INDEX = 7;
		public static final int SELECTOR_INDEX = 8;
		public static final int COMPUTED_INDEX = 9;
		public static final int FORMULA_INDEX = 10;
		public static final int PAGE_ID_INDEX = 11;
		public static final int UNIQUE_ID_INDEX = 12;
		public static final int ENABLED_BARCODE_INDEX = 13;
		public static final int VISIBLE_INDEX = 14;
		public static final int EDITABLE_INDEX = 15;
		public static final int MIN_VALUE_INDEX = 16;
		public static final int MAX_VALUE_INDEX = 17;
		public static final int DEFAULT_FIELD_INDEX = 18;
		/**
		 * The following constant values are defined on the server-side Thus,
		 * they may not be in sequential order. E.g. TYPE_CUSTOMER is 7 (not 6)
		 */
		public static final int TYPE_TEXT = 1;
		public static final int TYPE_NUMBER = 2;
		public static final int TYPE_DATE = 3;
		public static final int TYPE_YES_OR_NO = 4;
		public static final int TYPE_SINGLE_SELECT_LIST = 5;
		public static final int TYPE_MULTI_SELECT_LIST = 6;
		public static final int TYPE_CUSTOMER = 7;
		public static final int TYPE_EMAIL = 8;
		public static final int TYPE_PHONE = 9;
		public static final int TYPE_URL = 10;
		public static final int TYPE_TIME = 11;
		public static final int TYPE_IMAGE = 12;
		public static final int TYPE_SIGNATURE = 13;
		public static final int TYPE_ENTITY = 14;
		public static final int TYPE_EMPLOYEE = 15;
		public static final int TYPE_CURRENCY = 16;
		public static final int TYPE_MULTI_LIST = 17;
		public static final int TYPE_LOCATION = 18;
	}

	public static final class FieldValueSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FieldValueSpecs() {
		}

		/** Table name */
		public static final String TABLE = "field_value_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.field.value.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE = "value";

		public static final String[] ALL_COLUMNS = { _ID, FIELD_SPEC_ID, VALUE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FIELD_SPEC_ID_INDEX = 1;
		public static final int VALUE_INDEX = 2;
	}

	public static final class Forms implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Forms() {
		}

		/** Table name */
		public static final String TABLE = "forms";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.forms";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FILLED_BY_ID = "filled_by_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FILLED_BY_NAME = "filled_by_name";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String MODIFIED_BY_ID = "modified_by_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY_NAME = "modified_by_name";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String ASSIGNED_TO_ID = "assigned_to_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ASSIGNED_TO_NAME = "assigned_to_name";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String DELETED = "deleted";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 * This is not currently being used.
		 */
		public static final String STATUS = "status";

		/**
		 * Retrieved using previous forms, and can be deleted during cleanup.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String CACHED = "cached";

		/**
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Indicates whether there are any unsynced changes in the tree.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TREE_DIRTY = "tree_dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_MODIFICATION_TIME = "remote_modification_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String REMOTE_LOCATION_ID = "remote_location_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORM_SPEC_UNIQUE_ID = "form_spec_unique_id";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID,
				FORM_SPEC_ID, FILLED_BY_ID, FILLED_BY_NAME, MODIFIED_BY_ID,
				MODIFIED_BY_NAME, ASSIGNED_TO_ID, ASSIGNED_TO_NAME, DELETED,
				STATUS, CACHED, TEMPORARY, DIRTY, TREE_DIRTY,
				REMOTE_CREATION_TIME, REMOTE_MODIFICATION_TIME,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME,
				REMOTE_LOCATION_ID, FORM_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int FORM_SPEC_ID_INDEX = 2;
		public static final int FILLED_BY_ID_INDEX = 3;
		public static final int FILLED_BY_NAME_INDEX = 4;
		public static final int MODIFIED_BY_ID_INDEX = 5;
		public static final int MODIFIED_BY_NAME_INDEX = 6;
		public static final int ASSIGNED_TO_ID_INDEX = 7;
		public static final int ASSIGNED_TO_NAME_INDEX = 8;
		public static final int DELETED_INDEX = 9;
		public static final int STATUS_INDEX = 10;
		public static final int CACHED_INDEX = 11;
		public static final int TEMPORARY_INDEX = 12;
		public static final int DIRTY_INDEX = 13;
		public static final int TREE_DIRTY_INDEX = 14;
		public static final int REMOTE_CREATION_TIME_INDEX = 15;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 16;
		public static final int LOCAL_CREATION_TIME_INDEX = 17;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 18;
		public static final int REMOTE_LOCATION_ID_INDEX = 19;
		public static final int FORM_SPEC_UNIQUE_ID_INDEX = 20;
	}

	public static final class Fields implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Fields() {
		}

		/** Table name */
		public static final String TABLE = "fields";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.fields";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = "local_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = "remote_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FIELD_SPEC_UNIQUE_ID = "field_spec_unique_id";

		public static final String[] ALL_COLUMNS = { _ID, FORM_SPEC_ID,
				FIELD_SPEC_ID, LOCAL_FORM_ID, LOCAL_VALUE, REMOTE_VALUE,
				FIELD_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int FIELD_SPEC_ID_INDEX = 2;
		public static final int LOCAL_FORM_ID_INDEX = 3;
		public static final int LOCAL_VALUE_INDEX = 4;
		public static final int REMOTE_VALUE_INDEX = 5;
		public static final int FIELD_SPEC_UNIQUE_ID_INDEX = 6;
	}

	public static final class FormsView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FormsView() {
		}

		/** Table name */
		public static final String TABLE = "forms_view";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.forms.view";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_ID = Forms.TABLE + "." + _ID;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = Forms.TABLE + "."
				+ Forms.FORM_SPEC_ID;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = FieldSpecs.TABLE + "."
				+ FieldSpecs.TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = Fields.TABLE + "."
				+ Fields.LOCAL_VALUE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = Fields.TABLE + "."
				+ Fields.REMOTE_VALUE;

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = Forms.TABLE + "."
				+ "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_MODIFICATION_TIME = Forms.TABLE + "."
				+ "remote_modification_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = Forms.TABLE + "."
				+ "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = Forms.TABLE + "."
				+ "local_modification_time";

		/**
		 * <p>
		 * Type: Boolean (Stored as a string)
		 * </p>
		 */
		public static final String TREE_DIRTY = Forms.TABLE + "."
				+ Forms.TREE_DIRTY;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORM_SPEC_UNIQUE_ID = Forms.TABLE + "."
				+ Forms.FORM_SPEC_UNIQUE_ID;

		public static final String[] ALL_COLUMNS = { FORM_ID, FORM_SPEC_ID,
				TYPE, LOCAL_VALUE, REMOTE_VALUE, REMOTE_CREATION_TIME,
				REMOTE_MODIFICATION_TIME, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, TREE_DIRTY, FORM_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int FORM_ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int TYPE_INDEX = 2;
		public static final int LOCAL_VALUE_INDEX = 3;
		public static final int REMOTE_VALUE_INDEX = 4;
		public static final int REMOTE_CREATION_TIME_INDEX = 5;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 6;
		public static final int LOCAL_CREATION_TIME_INDEX = 7;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 8;
		public static final int TREE_DIRTY_INDEX = 9;
		public static final int FORM_SPEC_UNIQUE_ID_INDEX = 10;
	}

	public static final class FieldsView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FieldsView() {
		}

		/** Table name */
		public static final String TABLE = "fields_view";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.fields.view";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_ID = Fields.TABLE + "." + Fields._ID;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = Fields.TABLE + "."
				+ Fields.FORM_SPEC_ID;

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = "local_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = "remote_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LABEL = FieldSpecs.LABEL;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = FieldSpecs.TYPE;

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String IDENTIFIER = FieldSpecs.IDENTIFIER;

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String REQUIRED = FieldSpecs.REQUIRED;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = FieldSpecs.DISPLAY_ORDER;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TYPE_EXTRA = FieldSpecs.TYPE_EXTRA;

		/**
		 * <p>
		 * Type: Boolean (stored as a String)
		 * </p>
		 */
		public static final String COMPUTED = FieldSpecs.COMPUTED;

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String BARCODE = "barcode";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORMULA = FieldSpecs.FORMULA;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String SECTION_SPEC_ID = SectionFieldSpecs.SECTION_SPEC_ID;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String SECTION_INSTANCE_ID = SectionFields.SECTION_INSTANCE_ID;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SELECTOR = FieldSpecs.SELECTOR;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String PAGE_ID = FieldSpecs.PAGE_ID;

		public static final String[] ALL_COLUMNS = { FIELD_ID, FORM_SPEC_ID,
				FIELD_SPEC_ID, LOCAL_FORM_ID, LOCAL_VALUE, REMOTE_VALUE, LABEL,
				TYPE, IDENTIFIER, REQUIRED, DISPLAY_ORDER, TYPE_EXTRA,
				COMPUTED, FORMULA, SECTION_SPEC_ID, SECTION_INSTANCE_ID,
				SELECTOR, PAGE_ID, BARCODE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int FIELD_ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int FIELD_SPEC_ID_INDEX = 2;
		public static final int LOCAL_FORM_ID_INDEX = 3;
		public static final int LOCAL_VALUE_INDEX = 4;
		public static final int REMOTE_VALUE_INDEX = 5;
		public static final int LABEL_INDEX = 6;
		public static final int TYPE_INDEX = 7;
		public static final int IDENTIFIER_INDEX = 8;
		public static final int REQUIRED_INDEX = 9;
		public static final int DISPLAY_ORDER_INDEX = 10;
		public static final int TYPE_EXTRA_INDEX = 11;
		public static final int COMPUTED_INDEX = 12;
		public static final int FORMULA_INDEX = 13;
		public static final int SECTION_SPEC_ID_INDEX = 14;
		public static final int SECTION_INSTANCE_ID_INDEX = 15;
		public static final int SELECTOR_INDEX = 16;
		public static final int PAGE_ID_INDEX = 17;
	}

	public static final class Customers implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Customers() {
		}

		/** Table name */
		public static final String TABLE = "customers";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.customers";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CUSTOMER_TYPE_ID = "customer_type_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PHONE = "phone";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LATITUDE = "latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LONGITUDE = "longitude";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STREET = "street";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String AREA = "area";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CITY = "city";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATE = "state";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String COUNTRY = "country";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PIN_CODE = "pin_code";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LANDMARK = "landmark";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PC_FIRST_NAME = "pc_first_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PC_LAST_NAME = "pc_last_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PC_TITLE = "pc_title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PC_PHONE = "pc_phone";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PC_EMAIL = "pc_email";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SC_FIRST_NAME = "sc_first_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SC_LAST_NAME = "sc_last_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SC_TITLE = "sc_title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SC_PHONE = "sc_phone";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SC_EMAIL = "sc_email";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Indicate whether this is a partial record inserted due to a customer
		 * search.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String PARTIAL = "partial";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: Float
		 * </p>
		 */
		public static final String DISTANCE = "distance";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String IN_USE = "in_use";

		/**
		 * Indicates whether this customer is deleted or not.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DELETED = "deleted";

		/**
		 * Indicates the external customer ID given to that customer
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String CUSTOMER_NUM = "customerNo";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID, NAME,
				CUSTOMER_TYPE_ID, PHONE, LATITUDE, LONGITUDE, STREET, AREA,
				CITY, STATE, COUNTRY, PIN_CODE, LANDMARK, PC_FIRST_NAME,
				PC_LAST_NAME, PC_TITLE, PC_PHONE, PC_EMAIL, SC_FIRST_NAME,
				SC_LAST_NAME, SC_TITLE, SC_PHONE, SC_EMAIL, DIRTY, PARTIAL,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME, DISTANCE, IN_USE,
				DELETED, CUSTOMER_NUM };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int ALL_ID_INDEX = 0;
		public static final int ALL_REMOTE_ID_INDEX = 1;
		public static final int ALL_NAME_INDEX = 2;
		public static final int ALL_CUSTOMER_TYPE_ID_INDEX = 3;
		public static final int ALL_PHONE_INDEX = 4;
		public static final int ALL_LATITUDE_INDEX = 5;
		public static final int ALL_LONGITUDE_INDEX = 6;
		public static final int ALL_STREET_INDEX = 7;
		public static final int ALL_AREA_INDEX = 8;
		public static final int ALL_CITY_INDEX = 9;
		public static final int ALL_STATE_INDEX = 10;
		public static final int ALL_COUNTRY_INDEX = 11;
		public static final int ALL_PIN_CODE_INDEX = 12;
		public static final int ALL_LANDMARK_INDEX = 13;
		public static final int ALL_PC_FIRST_NAME_INDEX = 14;
		public static final int ALL_PC_LAST_NAME_INDEX = 15;
		public static final int ALL_PC_TITLE_INDEX = 16;
		public static final int ALL_PC_PHONE_INDEX = 17;
		public static final int ALL_PC_EMAIL_INDEX = 18;
		public static final int ALL_SC_FIRST_NAME_INDEX = 19;
		public static final int ALL_SC_LAST_NAME_INDEX = 20;
		public static final int ALL_SC_TITLE_INDEX = 21;
		public static final int ALL_SC_PHONE_INDEX = 22;
		public static final int ALL_SC_EMAIL_INDEX = 23;
		public static final int ALL_DIRTY_INDEX = 24;
		public static final int ALL_PARTIAL_INDEX = 25;
		public static final int ALL_LOCAL_CREATION_TIME_INDEX = 26;
		public static final int ALL_LOCAL_MODIFICATION_TIME_INDEX = 27;
		public static final int ALL_DISTANCE_INDEX = 28;
		public static final int ALL_IN_USE_INDEX = 29;
		public static final int ALL_DELETED_INDEX = 30;
		public static final int ALL_CUSTOMER_NUM_INDEX = 31;

		/**
		 * Summary columns
		 */
		public static final String[] SUMMARY_COLUMNS = { _ID, NAME,
				CUSTOMER_TYPE_ID, PC_FIRST_NAME, PC_LAST_NAME, DISTANCE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int SUMMARY_ID_INDEX = 0;
		public static final int SUMMARY_NAME_INDEX = 1;
		public static final int SUMMARY_CUSTOMER_TYPE_ID_INDEX = 2;
		public static final int SUMMARY_PC_FIRST_NAME_INDEX = 3;
		public static final int SUMMARY_PC_LAST_NAME_INDEX = 4;
		public static final int SUMMARY_DISTANCE_INDEX = 5;
	}

	public static final class Leaves implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Leaves() {
		}

		/** Table name */
		public static final String TABLE = "leaves";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.leaves";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String STATUS = "status";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String START_TIME = "start_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String END_TIME = "end_time";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String EMPLOYEE_REMARKS = "employee_remarks";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MANAGER_REMARKS = "manager_remarks";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String STATUS_CHANGED_BY_ID = "status_changed_by_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS_CHANGED_BY_NAME = "status_changed_by_name";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String CANCELLED = "deleted";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID, STATUS,
				START_TIME, END_TIME, EMPLOYEE_REMARKS, MANAGER_REMARKS,
				STATUS_CHANGED_BY_ID, STATUS_CHANGED_BY_NAME, CANCELLED, DIRTY,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int STATUS_INDEX = 2;
		public static final int START_TIME_INDEX = 3;
		public static final int END_TIME_INDEX = 4;
		public static final int EMPLOYEE_REMARKS_INDEX = 5;
		public static final int MANAGER_REMARKS_INDEX = 6;
		public static final int STATUS_CHANGED_BY_ID_INDEX = 7;
		public static final int STATUS_CHANGED_BY_NAME_INDEX = 8;
		public static final int CANCELLED_INDEX = 9;
		public static final int DIRTY_INDEX = 10;
		public static final int LOCAL_CREATION_TIME_INDEX = 11;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 12;

		/*
		 * Constants that indicate leave status.
		 * 
		 * Note that there is no cancelled state. A cancelled leave is marked as
		 * deleted on the server side. Cancelled leaves should be deleted from
		 * the device.
		 */
		public static final int STATUS_APPLIED = 0;
		public static final int STATUS_APPROVED = 1;
		public static final int STATUS_REJECTED = 2;
	}

	public static final class Locations implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Locations() {
		}

		/** Table name */
		public static final String TABLE = "locations";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.locations";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Integer (PURPOSE_TRACKING = 1, PURPOSE_LOCATION = 2)
		 * </p>
		 */
		public static final String PURPOSE = "purpose";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String LOCATION_FINALIZED = "location_finalized";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String GPS_ON = "gps_on";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String GPS_FINALIZED = "gps_finalized";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String GPS_CACHED = "gps_cached";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String GPS_FIX_TIME = "gps_fix_time";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String GPS_ACCURACY = "gps_accuracy";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String GPS_LATITUDE = "gps_latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String GPS_LONGITUDE = "gps_longitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String GPS_ALTITUDE = "gps_altitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String GPS_SPEED = "gps_speed";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String GPS_BEARING = "gps_bearing";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String NETWORK_ON = "network_on";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String NETWORK_FINALIZED = "network_finalized";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String NETWORK_CACHED = "network_cached";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String NETWORK_FIX_TIME = "network_fix_time";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String NETWORK_ACCURACY = "network_accuracy";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String NETWORK_LATITUDE = "network_latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String NETWORK_LONGITUDE = "network_longitude";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_ID = "cell_id";

		/**
		 * location area code
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_LAC = "cell_lac";

		/**
		 * primary scrambling code (for UMTS networks)
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_PSC = "cell_psc";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_MCC = "cell_mcc";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_MNC = "cell_mnc";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String SIGNAL_STRENGTH = "signal_strength";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String BATTERY_LEVEL = "battery_level";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String CONNECTED = "connected";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String WIFI_CONNECTED = "wifi_connected";

		/**
		 * Foreign key back into Notes._ID (when purpose is PURPOSE_NOTE) or
		 * Histories._ID (when purpose is PURPOSE_HISTORY). This will be null
		 * when purpose is PURPOSE_TRACKING.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FOR_ID = "for_id";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String SMS_PROCESS_STATE = "sms_process_state";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String FRESH_FIX = "fresh_fix";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String REMOTE_LOCATION_ID = "remote_location_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String REASON_FOR_TRACKING = "reason_for_tracking";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String FUSED_ON = "fused_on";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String FUSED_FINALIZED = "fused_finalized";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String FUSED_CACHED = "fused_cached";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String FUSED_FIX_TIME = "fused_fix_time";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String FUSED_ACCURACY = "fused_accuracy";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String FUSED_LATITUDE = "fused_latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String FUSED_LONGITUDE = "fused_longitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String FUSED_ALTITUDE = "fused_altitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String FUSED_SPEED = "fused_speed";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String FUSED_BEARING = "fused_bearing";

		public static final String[] ALL_COLUMNS = { _ID, PURPOSE,
				LOCATION_FINALIZED, GPS_ON, GPS_FINALIZED, GPS_CACHED,
				GPS_FIX_TIME, GPS_ACCURACY, GPS_LATITUDE, GPS_LONGITUDE,
				GPS_ALTITUDE, GPS_SPEED, GPS_BEARING, NETWORK_ON,
				NETWORK_FINALIZED, NETWORK_CACHED, NETWORK_FIX_TIME,
				NETWORK_ACCURACY, NETWORK_LATITUDE, NETWORK_LONGITUDE, CELL_ID,
				CELL_LAC, CELL_PSC, CELL_MCC, CELL_MNC, SIGNAL_STRENGTH,
				BATTERY_LEVEL, CONNECTED, WIFI_CONNECTED, FOR_ID, DIRTY,
				SMS_PROCESS_STATE, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, FRESH_FIX, REMOTE_LOCATION_ID,
				REASON_FOR_TRACKING, FUSED_ON, FUSED_FINALIZED, FUSED_CACHED,
				FUSED_FIX_TIME, FUSED_ACCURACY, FUSED_LATITUDE,
				FUSED_LONGITUDE, FUSED_ALTITUDE, FUSED_SPEED, FUSED_BEARING };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int PURPOSE_INDEX = 1;
		public static final int LOCATION_FINALIZED_INDEX = 2;
		public static final int GPS_ON_INDEX = 3;
		public static final int GPS_FINALIZED_INDEX = 4;
		public static final int GPS_CACHED_INDEX = 5;
		public static final int GPS_FIX_TIME_INDEX = 6;
		public static final int GPS_ACCURACY_INDEX = 7;
		public static final int GPS_LATITUDE_INDEX = 8;
		public static final int GPS_LONGITUDE_INDEX = 9;
		public static final int GPS_ALTITUDE_INDEX = 10;
		public static final int GPS_SPEED_INDEX = 11;
		public static final int GPS_BEARING_INDEX = 12;
		public static final int NETWORK_ON_INDEX = 13;
		public static final int NETWORK_FINALIZED_INDEX = 14;
		public static final int NETWORK_CACHED_INDEX = 15;
		public static final int NETWORK_FIX_TIME_INDEX = 16;
		public static final int NETWORK_ACCURACY_INDEX = 17;
		public static final int NETWORK_LATITUDE_INDEX = 18;
		public static final int NETWORK_LONGITUDE_INDEX = 19;
		public static final int CELL_ID_INDEX = 20;
		public static final int CELL_LAC_INDEX = 21;
		public static final int CELL_PSC_INDEX = 22;
		public static final int CELL_MCC_INDEX = 23;
		public static final int CELL_MNC_INDEX = 24;
		public static final int SIGNAL_STRENGTH_INDEX = 25;
		public static final int BATTERY_LEVEL_INDEX = 26;
		public static final int CONNECTED_INDEX = 27;
		public static final int WIFI_CONNECTED_INDEX = 28;
		public static final int FOR_ID_INDEX = 29;
		public static final int DIRTY_INDEX = 30;
		public static final int SMS_PROCESS_STATE_INDEX = 31;
		public static final int LOCAL_CREATION_TIME_INDEX = 32;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 33;
		public static final int FRESH_FIX_INDEX = 34;
		public static final int REMOTE_LOCATION_ID_INDEX = 35;
		public static final int REASON_FOR_TRACKING_INDEX = 36;
		public static final int FUSED_ON_INDEX = 37;
		public static final int FUSED_FINALIZED_INDEX = 38;
		public static final int FUSED_CACHED_INDEX = 39;
		public static final int FUSED_FIX_TIME_INDEX = 40;
		public static final int FUSED_ACCURACY_INDEX = 41;
		public static final int FUSED_LATITUDE_INDEX = 42;
		public static final int FUSED_LONGITUDE_INDEX = 43;
		public static final int FUSED_ALTITUDE_INDEX = 44;
		public static final int FUSED_SPEED_INDEX = 45;
		public static final int FUSED_BEARING_INDEX = 46;

		/*
		 * Constants that indicate provider on/off state.
		 */
		public static final int PROVIDER_ON = 1;
		public static final int PROVIDER_OFF = 0;

		/**
		 * 
		 */
		public static final int PURPOSE_TRACKING = 1;
		public static final int PURPOSE_NOTE = 2;
		public static final int PURPOSE_HISTORY = 3;
		public static final int PURPOSE_FORM = 4;
		public static final int PURPOSE_START_WORK = 5;
		public static final int PURPOSE_STOP_WORK = 6;
		public static final int PURPOSE_COMPLETE_ACTIVITY = 7;
		public static final int PURPOSE_CUSTOMER_STATUS_ACTIVITY = 8;
		public static final int PURPOSE_NEARBY_CUSTOMERS = 9;
		public static final int PURPOSE_COMPLETE_ROUTE_PLAN = 10;
		public static final int PURPOSE_FORM_FILE = 11;
		public static final int PURPOSE_SECTION_FILE = 12;

	}

	public static final class NeighboringCellInfos implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private NeighboringCellInfos() {
		}

		/** Table name */
		public static final String TABLE = "neighboring_cell_infos";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.neighboring.cell.infos";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_ID = "cell_id";

		/**
		 * location area code
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_LAC = "cell_lac";

		/**
		 * primary scrambling code (for UMTS networks)
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_PSC = "cell_psc";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_RSSI = "cell_rssi";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_MCC = "cell_mcc";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CELL_MNC = "cell_mnc";

		/**
		 * Foreign key that references Locations._ID
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCATION_ID = "location_id";

		public static final String[] ALL_COLUMNS = { _ID, CELL_ID, CELL_LAC,
				CELL_PSC, CELL_RSSI, CELL_MCC, CELL_MNC, LOCATION_ID };

		public static final int _ID_INDEX = 0;
		public static final int CELL_ID_INDEX = 1;
		public static final int CELL_LAC_INDEX = 2;
		public static final int CELL_PSC_INDEX = 3;
		public static final int CELL_RSSI_INDEX = 4;
		public static final int CELL_MCC_INDEX = 5;
		public static final int CELL_MNC_INDEX = 6;
		public static final int LOCATION_ID_INDEX = 7;
	}

	public static final class Notes implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Notes() {
		}

		/** Table name */
		public static final String TABLE = "notes";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.notes";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NOTE = "note";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String NOTE_TYPE = "note_type";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MIME_TYPE = "mime_type";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String MEDIA_ID = "media_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MEDIA_TYPE = "media_type";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_MEDIA_PATH = "local_media_path";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_JOB_ID = "local_job_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String STATE = "state";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String NOTE_TIME = "note_time";

		/**
		 * ID of the employee who added this note.
		 * 
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String BY_ID = "by_id";

		/**
		 * Name of the employee who added this note.
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String BY_NAME = "by_name";

		/**
		 * This flag is set to true to request download.
		 * 
		 * This flag will be considered only when localMediaPath is not set or
		 * the file is missing.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DOWNLOAD_REQUESTED = "download_requested";

		/**
		 * This flag is set to true to request forced upload.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String UPLOAD_REQUESTED = "upload_requested";

		/**
		 * Greater values indicate higher priority
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String UPLOAD_PRIORITY = "upload_priority";

		/**
		 * As both upload and download of the same file is not allowed, transfer
		 * percentage is used for uploads as well as downloads.
		 * 
		 * Valid media ID indicates that the file has been uploaded
		 * successfully.
		 * 
		 * Any file with out media ID and a non-zero transfer percentage should
		 * be considered as upload in progress.
		 * 
		 * <b>Type: Int</b>
		 */
		public static final String TRANSFER_PERCENTAGE = "transfer_percentage";

		/**
		 * File size in bytes.
		 * 
		 * <b>Type: Long</b>
		 */
		public static final String FILE_SIZE = "file_size";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID, NOTE,
				NOTE_TYPE, MIME_TYPE, MEDIA_ID, MEDIA_TYPE, LOCAL_MEDIA_PATH,
				LOCAL_JOB_ID, STATE, NOTE_TIME, BY_ID, BY_NAME,
				DOWNLOAD_REQUESTED, UPLOAD_REQUESTED, UPLOAD_PRIORITY,
				TRANSFER_PERCENTAGE, FILE_SIZE, DIRTY, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int NOTE_INDEX = 2;
		public static final int NOTE_TYPE_INDEX = 3;
		public static final int MIME_TYPE_INDEX = 4;
		public static final int MEDIA_ID_INDEX = 5;
		public static final int MEDIA_TYPE_INDEX = 6;
		public static final int LOCAL_MEDIA_PATH_INDEX = 7;
		public static final int LOCAL_JOB_ID_INDEX = 8;
		public static final int STATE_INDEX = 9;
		public static final int NOTE_TIME_INDEX = 10;
		public static final int BY_ID_INDEX = 11;
		public static final int BY_NAME_INDEX = 12;
		public static final int DOWNLOAD_REQUESTED_INDEX = 13;
		public static final int UPLOAD_REQUESTED_INDEX = 14;
		public static final int UPLOAD_PRIORITY_INDEX = 15;
		public static final int TRANSFER_PERCENTAGE_INDEX = 16;
		public static final int FILE_SIZE_INDEX = 17;
		public static final int DIRTY_INDEX = 18;
		public static final int LOCAL_CREATION_TIME_INDEX = 19;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 20;

		/*
		 * Constants that indicate note type.
		 */
		public static final int NOTE_TYPE_NOTE = 0;
		public static final int NOTE_TYPE_PROOF = 1;
		public static final int NOTE_TYPE_EXTRA = 2;

		/*
		 * Constants that indicate media type.
		 */
		public static final int MEDIA_TYPE_NOTE = -1;
		public static final int MEDIA_TYPE_FILE = 0;
		public static final int MEDIA_TYPE_IMAGE = 1;
		public static final int MEDIA_TYPE_AUDIO = 2;
		public static final int MEDIA_TYPE_VIDEO = 3;
		public static final int MEDIA_TYPE_SIGNATURE = 4;

		/*
		 * Constants that indicate note state.
		 */
		public static final int STATE_PRE_COMPLETE = 0;
		public static final int STATE_ON_COMPLETE = 1;
		public static final int STATE_POST_COMPLETE = 2;
	}

	public static final class FormFiles implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private FormFiles() {
		}

		/** Table name */
		public static final String TABLE = "form_files";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.form_files";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MIME_TYPE = "mime_type";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String MEDIA_ID = "media_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_MEDIA_PATH = "local_media_path";

		/**
		 * This flag is set to true to request download.
		 * 
		 * This flag will be considered only when localMediaPath is not set or
		 * the file is missing.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DOWNLOAD_REQUESTED = "download_requested";

		/**
		 * This flag is set to true to request forced upload.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String UPLOAD_REQUESTED = "upload_requested";

		/**
		 * Greater values indicate higher priority
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String UPLOAD_PRIORITY = "upload_priority";

		/**
		 * As both upload and download of the same file is not allowed, transfer
		 * percentage is used for uploads as well as downloads.
		 * 
		 * Valid media ID indicates that the file has been uploaded
		 * successfully.
		 * 
		 * Any file with out media ID and a non-zero transfer percentage should
		 * be considered as upload in progress.
		 * 
		 * <b>Type: Int</b>
		 */
		public static final String TRANSFER_PERCENTAGE = "transfer_percentage";

		/**
		 * File size in bytes.
		 * 
		 * <b>Type: Long</b>
		 */
		public static final String FILE_SIZE = "file_size";

		/**
		 * 
		 * <b>Type: String</b>
		 */
		public static final String FIELD_SPEC_UNIQUE_ID = "field_spec_unique_id";

		public static final String[] ALL_COLUMNS = { _ID, FIELD_SPEC_ID,
				LOCAL_FORM_ID, MIME_TYPE, MEDIA_ID, LOCAL_MEDIA_PATH,
				DOWNLOAD_REQUESTED, UPLOAD_REQUESTED, UPLOAD_PRIORITY,
				TRANSFER_PERCENTAGE, FILE_SIZE, FIELD_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FIELD_SPEC_ID_INDEX = 1;
		public static final int LOCAL_FORM_ID_INDEX = 2;
		public static final int MIME_TYPE_INDEX = 3;
		public static final int MEDIA_ID_INDEX = 4;
		public static final int LOCAL_MEDIA_PATH_INDEX = 5;
		public static final int DOWNLOAD_REQUESTED_INDEX = 6;
		public static final int UPLOAD_REQUESTED_INDEX = 7;
		public static final int UPLOAD_PRIORITY_INDEX = 8;
		public static final int TRANSFER_PERCENTAGE_INDEX = 9;
		public static final int FILE_SIZE_INDEX = 10;
		public static final int FIELD_SPEC_UNIQUE_ID_INDEX = 11;
	}

	public static class Settings implements BaseColumns {
		public static final String TABLE = "settings";
		public static final String KEY = "key";
		public static final String VALUE = "value";

		// Tracking type constants

		public static final int TRACKING_TYPE_IN_ACTIVITIES = 1;
		public static final int TRACKING_TYPE_IN_WORK_HOURS = 2;
		public static final int TRACKING_TYPE_NEVER = 3;

		public static final String KEY_COMMENT_LOCATION_TIMEOUT = "commentLocationTimeout";
		public static final String KEY_LOCATION_SHELF_LIFE = "locationShelfLife";
		public static final String KEY_CAN_UPDATE_HOME_LOCATION = "canUpdateHomeLocation";
		public static final String KEY_CAN_EDIT_CUSTOMER_INFO = "canEditCustomerInfo";
		public static final String KEY_APP_TITLE = "appTitle";
		public static final String KEY_SMSC = "smsc";
		public static final String KEY_APP_LOGO_URL = "appLogoUrl";
		public static final String KEY_SHOW_COMPLETED_JOBS = "showCompletedJobs";
		public static final String KEY_HIDE_CUSTOMER_INFO = "hideCustomerInfo";
		public static final String KEY_LOCAL_SYNC_TIME = "localSyncTime";
		public static final String KEY_REMOTE_SYNC_TIME = "remoteSyncTime";
		public static final String KEY_CODE = "code";
		public static final String KEY_EMPLOYEE_ID = "employeeId";
		public static final String KEY_EMPLOYEE_NAME = "employeeName";
		public static final String KEY_EMPLOYEE_RANK = "employeeRank";
		public static final String KEY_SORT_ORDER = "sortOrder";
		public static final String KEY_REMINDER_MINUTES = "reminderMinutes";
		public static final String KEY_TRACKING_FREQUENCY = "trackFrequency";
		public static final String KEY_SMS_TRACK_FREQUENCY = "smsTrackFreq";
		public static final String KEY_LAST_UPDATE_TO_CLOUD = "lastUpdateToCloud";
		public static final String KEY_LAST_GPS_LATITUDE = "lastGpsLatitude";
		public static final String KEY_LAST_GPS_LONGITUDE = "lastGpsLongitude";
		public static final String KEY_LAST_GPS_ALTITUDE = "lastGpsAltitude";
		public static final String KEY_LAST_GPS_ACCURACY = "lastGpsAccuracy";
		public static final String KEY_LAST_GPS_SPEED = "lastGpsSpeed";
		public static final String KEY_LAST_GPS_BEARING = "lastGpsBearing";
		public static final String KEY_LAST_GPS_FIX_TIME = "lastGpsFixTime";
		public static final String KEY_LAST_FUSED_LATITUDE = "lastFusedLatitude";
		public static final String KEY_LAST_FUSED_LONGITUDE = "lastFusedLongitude";
		public static final String KEY_LAST_FUSED_ALTITUDE = "lastFusedAltitude";
		public static final String KEY_LAST_FUSED_ACCURACY = "lastFusedAccuracy";
		public static final String KEY_LAST_FUSED_SPEED = "lastFusedSpeed";
		public static final String KEY_LAST_FUSED_BEARING = "lastFusedBearing";
		public static final String KEY_LAST_FUSED_FIX_TIME = "lastFusedFixTime";
		public static final String KEY_LAST_NETWORK_LATITUDE = "lastNetworkLatitude";
		public static final String KEY_LAST_NETWORK_LONGITUDE = "lastNetworkLongitude";
		public static final String KEY_LAST_NETWORK_ACCURACY = "lastNetworkAccuracy";
		public static final String KEY_LAST_NETWORK_FIX_TIME = "lastNetworkFixTime";
		public static final String KEY_UPLOAD = "upload";
		public static final String KEY_OLDEST_JOB_FETCHED = "oldestJobFetched";
		public static final String KEY_OLDEST_FORM_FETCHED = "oldestFormFetched";
		public static final String KEY_PREVIOUS_START_JOB_ID = "previousstart";
		public static final String KEY_PREVIOUS_END_JOB_ID = "previousend";
		public static final String KEY_LOCAL_SYNC_START_TIME_IN_MILLIS = "localSyncStartTimeInMillis";

		// TRACK SCENARIOS
		public static final String KEY_TRACKING_TYPE = "empTrackTypeId";

		public static final String KEY_PERMISSION_CUSTOMER_ADD = "addCustomer";
		public static final String KEY_PERMISSION_CUSTOMER_VIEW = "viewCustomer";
		public static final String KEY_PERMISSION_CUSTOMER_MODIFY = "modifyCustomer";
		public static final String KEY_PERMISSION_CUSTOMER_DELETE = "deleteCustomer";
		// public static final String KEY_DOWNLOAD_STARTED_AT =
		// "downloadStartedAt";

		public static final String LABEL_CUSTOMER_SINGULAR_KEY = "label_customer_singular";
		public static final String LABEL_CUSTOMER_PLURAL_KEY = "label_customer_plural";
		public static final String LABEL_EMPLOYEE_SINGULAR_KEY = "label_employee_singular";
		public static final String LABEL_EMPLOYEE_PLURAL_KEY = "label_employee_plural";
		public static final String LABEL_AGENDA_SINGULAR_KEY = "label_agenda_singular";
		public static final String LABEL_AGENDA_PLURAL_KEY = "label_agenda_plural";
		public static final String LABEL_NAMEDLOCATION_SINGULAR_KEY = "label_namedLocation_singular";
		public static final String LABEL_NAMEDLOCATION_PLURAL_KEY = "label_namedLocation_plural";
		public static final String LABEL_KNOWLEDGEBASE_SINGULAR_KEY = "label_knowledgeBase_singular";
		public static final String LABEL_KNOWLEDGEBASE_PLURAL_KEY = "label_knowledgeBase_plural";
		public static final String LABEL_FORM_SINGULAR_KEY = "label_form_singular";
		public static final String LABEL_FORM_PLURAL_KEY = "label_form_plural";
		public static final String LABEL_JOB_SINGULAR_KEY = "label_job_singular";
		public static final String LABEL_JOB_PLURAL_KEY = "label_job_plural";
		public static final String LABEL_LIST_SINGULAR_KEY = "label_list_singular";
		public static final String LABEL_LIST_PLURAL_KEY = "label_list_plural";
		public static final String LABEL_MESSAGE_SINGULAR_KEY = "label_message_singular";
		public static final String LABEL_MESSAGE_PLURAL_KEY = "label_message_plural";

		// VISISBILITY KEYS
		public static final String LABEL_VISIBILITY_CUSTOMER_KEY = "label_customer_visibility";
		public static final String LABEL_VISIBILITY_EMPLOYEE_KEY = "label_employee_visibility";
		public static final String LABEL_VISIBILITY_NAMED_LOCATION_KEY = "label_namedLocation_visibility";
		public static final String LABEL_VISIBILITY_KNOWLEDGEBASE_KEY = "label_knowledgeBase_visibility";
		public static final String LABEL_VISIBILITY_FORM_KEY = "label_form_visibility";
		public static final String LABEL_VISIBILITY_JOB_KEY = "label_job_visibility";
		public static final String LABEL_VISIBILITY_AGENDA_KEY = "label_agenda_visibility";
		public static final String LABEL_VISIBILITY_LEAVE_KEY = "label_leave_visibility";
		public static final String LABEL_VISIBILITY_HOLIDAYS_KEY = "label_holiday_visibility";
		public static final String LABEL_VISIBILITY_ABOUT_KEY = "label_about_visibility";
		public static final String LABEL_VISIBILITY_START_WORK_KEY = "label_startstop_visibility";
		public static final String LABEL_VISIBILITY_SETTINGS_KEY = "label_setting_visibility";
		public static final String LABEL_VISIBILITY_HELP_KEY = "label_help_visibility";
		public static final String LABEL_VISIBILITY_APPROVAL_STATUS_KEY = "label_workflow_visibility";
		public static final String LABEL_VISIBILITY_ROUTE_KEY = "label_route_visibility";
		public static final String LABEL_VISIBILITY_MESSAGES_KEY = "label_message_visibility";

		public static final String LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE = "Customer";
		public static final String LABEL_CUSTOMER_PLURAL_DEFAULT_VLAUE = "Customers";
		public static final String LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE = "Employee";
		public static final String LABEL_EMPLOYEE_PLURAL_DEFAULT_VLAUE = "Employees";
		public static final String LABEL_NAMEDLOCATION_SINGULAR_DEFAULT_VLAUE = "NamedLocation";
		public static final String LABEL_NAMEDLOCATION_PLURAL_DEFAULT_VLAUE = "NamedLocations";
		public static final String LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE = "KnowledgeBase";
		public static final String LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE = "KnowledgeBases";
		public static final String LABEL_FORM_SINGULAR_DEFAULT_VLAUE = "Form";
		public static final String LABEL_FORM_PLURAL_DEFAULT_VLAUE = "Forms";
		public static final String LABEL_JOB_SINGULAR_DEFAULT_VLAUE = "Job";
		public static final String LABEL_JOB_PLURAL_DEFAULT_VLAUE = "Jobs";
		public static final String LABEL_LIST_SINGULAR_DEFAULT_VLAUE = "List";
		public static final String LABEL_LIST_PLURAL_DEFAULT_VLAUE = "Lists";
		public static final String LABEL_MESSAGE_SINGULAR_DEFAULT_VLAUE = "Message";
		public static final String LABEL_MESSAGE_PLURAL_DEFAULT_VLAUE = "Messages";
		public static final String LABEL_AGENDA_SINGULAR_DEFAULT_VALUE = "Agenda";
		public static final String LABEL_AGENDA_PLURAL_DEFAULT_VALUE = "Agenda";
		public static final String LABEL_LOCATION_PLURAL_DEFAULT_VLAUE = "Locations";
		public static final String LABEL_LEAVES_PLURAL_DEFAULT_VLAUE = "Leaves";
		public static final String LABEL_SETTING_PLURAL_DEFAULT_VLAUE = "Settings";
		public static final String LABEL_HELP_DEFAULT_VLAUE = "Help";
		public static final String LABEL_STARTWORK_DEFAULT_VLAUE = "Start Work";
		public static final String LABEL_HOME_DEFAULT_VALUE = "Home";

		public static final String KEY_PERMISSION_JOB_ADD = "addJob";
		public static final String KEY_PERMISSION_JOB_VIEW = "viewJob";
		public static final String KEY_PERMISSION_JOB_MODIFY = "modifyJob";
		public static final String KEY_PERMISSION_JOB_DELETE = "deleteJob";

		public static final String KEY_PERMISSION_INVITATION_ADD = "addJobInvitation";
		public static final String KEY_PERMISSION_INVITATION_VIEW = "viewJobInvitation";
		public static final String KEY_PERMISSION_INVITATION_MODIFY = "modifyJobInvitation";
		public static final String KEY_PERMISSION_INVITATION_DELETE = "deleteJobInvitation";

		public static final String KEY_PERMISSION_FORM_ADD = "addForm";
		public static final String KEY_PERMISSION_FORM_VIEW = "viewForm";
		public static final String KEY_PERMISSION_FORM_MODIFY = "modifyForm";
		public static final String KEY_PERMISSION_FORM_DELETE = "deleteForm";

		public static final String KEY_PERMISSION_HOLIDAY_ADD = "addHoliday";
		public static final String KEY_PERMISSION_HOLIDAY_VIEW = "viewHoliday";
		public static final String KEY_PERMISSION_HOLIDAY_MODIFY = "modifyHoliday";
		public static final String KEY_PERMISSION_HOLIDAY_DELETE = "deleteHoliday";

		public static final String KEY_PERMISSION_LEAVE_ADD = "addLeave";
		public static final String KEY_PERMISSION_LEAVE_VIEW = "viewLeave";
		public static final String KEY_PERMISSION_LEAVE_MODIFY = "modifyLeave";
		public static final String KEY_PERMISSION_LEAVE_DELETE = "deleteLeave";

		public static final String KEY_PERMISSION_HOME_LOCATION_ADD = "addHomeLocation";
		public static final String KEY_PERMISSION_HOME_LOCATION_VIEW = "viewHomeLocation";
		public static final String KEY_PERMISSION_HOME_LOCATION_MODIFY = "modifyHomeLocation";
		public static final String KEY_PERMISSION_MANAGE_NAMED_LOCATIONS = "manageNamedLocations";

		public static final String KEY_PERMISSION_ROUTE_ADD = "addRoute";
		public static final String KEY_PERMISSION_ROUTE_VIEW = "viewRoute";
		public static final String KEY_PERMISSION_ROUTE_PLAN_ADD = "addRoutePlan";
		public static final String KEY_PERMISSION_ROUTE_DELETE = "deleteRoute";
		public static final String KEY_KEEP_LISTENING_FOR_GPS_UPDATES = "keepListeningForGpsUpdates";
		public static final String KEY_APP_VERSION_ON_SERVER = "appVersion";
		public static final String KEY_CHANGE_LOG = "changeLog";
		public static final String KEY_COMPRESS_MEDIA = "compressMedias";
		public static final String KEY_TRACK_BY = "trackBy";
		public static final String KEY_LAST_UPDATE_TO_CLOUD_VIA_SMS = "lastUpdateToCloudViaSms";
		public static final String KEY_LAST_UPDATE_AT_SMS_SENT = "lastUpdateAtSmsSent";
		public static final String KEY_CAPTURE_LOCATION_ON_EVERY_FORM_SAVE = "captureLocationInFormUpdate";
		public static final String KEY_CAPTURE_LOCATION_ON_EVERY_FORM_FIELD_MEDIA = "captureLocationInFormMedia";
		public static final String KEY_PREFETCH_NEARBY_CUSTOMERS = "nearByCustomer";
		public static final String KEY_DEFAULT_NBC_RADIUS = "defaultNbcRadius";
		public static final String KEY_DEFAULT_NBC_LIMIT = "defaultNbcLimit";
		public static final String KEY_LOCATION_PROVIDER = "locationProvider";
		public static final String KEY_PRINTER_NAME = "printerName";
		public static final String KEY_PRINTER_ADDRESS = "printerAddress";
		public static final String KEY_PRINTER_SERVICE_RECORD_UUID = "printerServiceRecordUuid";
		public static final String KEY_PRINTER_COLUMN_WIDTH = "printerColumnWidth";
		public static final String KEY_EMPLOYEE_UNDER_ID = "employeeUnderId";
		public static final String KEY_IMSI = "imsi";
		public static final String KEY_IS_IT_IMSI_FIRST_CONFIGURATION = "imsifirst";
		public static final String KEY_PICK_FROM_GALLARY = "pickFromGallary";

		// there is no home location deletion permission
		// public static final String KEY_PERMISSION_HOME_LOCATION_DELETE =
		// "deleteHomeLocation";

		public static final int DEFAULT_COMMENT_LOCATION_TIMEOUT = 180000;
		public static final int DEFAULT_LOCATION_SHELF_LIFE = 600000;
		public static final int DEFAULT_TRACKING_FREQUENCY = 600000;
		public static final int DEFAULT_FALL_BACK_INTERVAL = DEFAULT_TRACKING_FREQUENCY + 120000;
		public static final int DEFAULT_REMINDER_MINUTES = 60;
		public static final String SORT_ORDER_COMPANY = "company";
		public static final String SORT_ORDER_CONTACT = "contact";
		public static final String DEFAULT_SORT_ORDER = SORT_ORDER_COMPANY;
		public static final String DEFAULT_PRINTER_SERVICE_RECORD_UUID = "00001101-0000-1000-8000-00805F9B34FB";
		public static final int DEFAULT_PRINTER_COLUMN_WIDTH = 12;
		public static final int DEFAULT_JOB_ID = 0;

		public static final String UPLOAD_WIFI = "wifi";
		public static final String UPLOAD_ANY = "any";
		public static final String DEFAULT_UPLOAD = UPLOAD_ANY;
		public static final boolean DEFAULT_COMPRESS_MEDIA = false;
		public static final String LOCATION_PROVIDER_ANDROID = "1";
		public static final String LOCATION_PROVIDER_FUSED = "2";
	}

	public static class Countries implements BaseColumns {
		public static final String TABLE = "countries";
		public static final String NAME = "name";
		public static final String DISPLAY_ORDER = "display_order";
		public static final String[] ALL_COLUMNS = { _ID, NAME, DISPLAY_ORDER };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int DISPLAY_ORDER_INDEX = 2;

		public static final int INDIA = 1;
	}

	public static final class SectionSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SectionSpecs() {
		}

		/** Table name */
		public static final String TABLE = "section_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.section.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MIN_ENTRIES = "min_entries";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MAX_ENTRIES = "max_entries";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = "display_order";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String PAGE_ID = "page_id";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, FORM_SPEC_ID,
				MIN_ENTRIES, MAX_ENTRIES, DISPLAY_ORDER, PAGE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int FORM_SPEC_ID_INDEX = 2;
		public static final int MIN_ENTRIES_INDEX = 3;
		public static final int MAX_ENTRIES_INDEX = 4;
		public static final int DISPLAY_ORDER_INDEX = 5;
		public static final int PAGE_ID_INDEX = 6;
	}

	public static final class SectionFieldSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SectionFieldSpecs() {
		}

		/** Table name */
		public static final String TABLE = "section_field_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.section.field.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LABEL = "label";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = "type";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String REQUIRED = "required";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = "display_order";

		/**
		 * Used for storing entity spec id when type is entity.
		 * 
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TYPE_EXTRA = "type_extra";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SELECTOR = "selector";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String COMPUTED = "computed";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String BARCODE = "barcode";
		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String VISIBLE = "visible";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String EDITABLE = "editable";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MIN_VALUE = "min_value";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MAX_VALUE = "max_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORMULA = "formula";

		/**
		 * Foreign key that reference SectionSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String SECTION_SPEC_ID = "section_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String UNIQUE_ID = "unique_id";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String DEFAULT_FIELD = "default_field";

		public static final String[] ALL_COLUMNS = { _ID, FORM_SPEC_ID, LABEL,
				TYPE, IDENTIFIER, REQUIRED, DISPLAY_ORDER, TYPE_EXTRA,
				SELECTOR, COMPUTED, FORMULA, SECTION_SPEC_ID, UNIQUE_ID,
				BARCODE, VISIBLE, EDITABLE, MIN_VALUE, MAX_VALUE, DEFAULT_FIELD };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int LABEL_INDEX = 2;
		public static final int TYPE_INDEX = 3;
		public static final int IDENTIFIER_INDEX = 4;
		public static final int REQUIRED_INDEX = 5;
		public static final int DISPLAY_ORDER_INDEX = 6;
		public static final int TYPE_EXTRA_INDEX = 7;
		public static final int SELECTOR_INDEX = 8;
		public static final int COMPUTED_INDEX = 9;
		public static final int FORMULA_INDEX = 10;
		public static final int SECTION_SPEC_ID_INDEX = 11;
		public static final int UNIQUE_ID_INDEX = 12;
		public static final int BARCODE_INDEX = 13;
		public static final int VISIBLE_INDEX = 14;
		public static final int EDITABLE_INDEX = 15;
		public static final int MIN_VALUE_INDEX = 16;
		public static final int MAX_VALUE_INDEX = 17;
		public static final int DEFAULT_FIELD_INDEX = 18;

		// NOTE: Use type constants from FieldSpecs
	}

	public static final class SectionFieldValueSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SectionFieldValueSpecs() {
		}

		/** Table name */
		public static final String TABLE = "section_field_value_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.section.field.value.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference SectionFieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE = "value";

		public static final String[] ALL_COLUMNS = { _ID, FIELD_SPEC_ID, VALUE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FIELD_SPEC_ID_INDEX = 1;
		public static final int VALUE_INDEX = 2;
	}

	public static final class SectionFields implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SectionFields() {
		}

		/** Table name */
		public static final String TABLE = "section_fields";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.section.fields";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * Foreign key that reference SectionSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String SECTION_SPEC_ID = "section_spec_id";

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String SECTION_INSTANCE_ID = "section_instance_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = "local_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = "remote_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FIELD_SPEC_UNIQUE_ID = "field_spec_unique_id";

		public static final String[] ALL_COLUMNS = { _ID, FORM_SPEC_ID,
				SECTION_SPEC_ID, FIELD_SPEC_ID, LOCAL_FORM_ID,
				SECTION_INSTANCE_ID, LOCAL_VALUE, REMOTE_VALUE,
				FIELD_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FORM_SPEC_ID_INDEX = 1;
		public static final int SECTION_SPEC_ID_INDEX = 2;
		public static final int FIELD_SPEC_ID_INDEX = 3;
		public static final int LOCAL_FORM_ID_INDEX = 4;
		public static final int SECTION_INSTANCE_ID_INDEX = 5;
		public static final int LOCAL_VALUE_INDEX = 6;
		public static final int REMOTE_VALUE_INDEX = 7;
		public static final int FIELD_SPEC_UNIQUE_ID_INDEX = 8;
	}

	public static final class SectionFiles implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SectionFiles() {
		}

		/** Table name */
		public static final String TABLE = "section_files";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.section.files";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * 
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String SECTION_INSTANCE_ID = "section_instance_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MIME_TYPE = "mime_type";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String MEDIA_ID = "media_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_MEDIA_PATH = "local_media_path";

		/**
		 * This flag is set to true to request download.
		 * 
		 * This flag will be considered only when localMediaPath is not set or
		 * the file is missing.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DOWNLOAD_REQUESTED = "download_requested";

		/**
		 * This flag is set to true to request forced upload.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String UPLOAD_REQUESTED = "upload_requested";

		/**
		 * Greater values indicate higher priority
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String UPLOAD_PRIORITY = "upload_priority";

		/**
		 * As both upload and download of the same file is not allowed, transfer
		 * percentage is used for uploads as well as downloads.
		 * 
		 * Valid media ID indicates that the file has been uploaded
		 * successfully.
		 * 
		 * Any file with out media ID and a non-zero transfer percentage should
		 * be considered as upload in progress.
		 * 
		 * <b>Type: Int</b>
		 */
		public static final String TRANSFER_PERCENTAGE = "transfer_percentage";

		/**
		 * File size in bytes.
		 * 
		 * <b>Type: Long</b>
		 */
		public static final String FILE_SIZE = "file_size";

		/**
		 * 
		 * <b>Type: String</b>
		 */
		public static final String FIELD_SPEC_UNIQUE_ID = "field_spec_unique_id";

		public static final String[] ALL_COLUMNS = { _ID, FIELD_SPEC_ID,
				LOCAL_FORM_ID, SECTION_INSTANCE_ID, MIME_TYPE, MEDIA_ID,
				LOCAL_MEDIA_PATH, DOWNLOAD_REQUESTED, UPLOAD_REQUESTED,
				UPLOAD_PRIORITY, TRANSFER_PERCENTAGE, FILE_SIZE,
				FIELD_SPEC_UNIQUE_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FIELD_SPEC_ID_INDEX = 1;
		public static final int LOCAL_FORM_ID_INDEX = 2;
		public static final int SECTION_INSTANCE_ID_INDEX = 3;
		public static final int MIME_TYPE_INDEX = 4;
		public static final int MEDIA_ID_INDEX = 5;
		public static final int LOCAL_MEDIA_PATH_INDEX = 6;
		public static final int DOWNLOAD_REQUESTED_INDEX = 7;
		public static final int UPLOAD_REQUESTED_INDEX = 8;
		public static final int UPLOAD_PRIORITY_INDEX = 9;
		public static final int TRANSFER_PERCENTAGE_INDEX = 10;
		public static final int FILE_SIZE_INDEX = 11;
		public static final int FIELD_SPEC_UNIQUE_ID_INDEX = 12;
	}

	public static final class PageSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private PageSpecs() {
		}

		/** Table name */
		public static final String TABLE = "page_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.page.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String PAGE_ID = "page_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		public static final String[] ALL_COLUMNS = { _ID, PAGE_ID, TITLE,
				FORM_SPEC_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int PAGE_ID_INDEX = 1;
		public static final int TITLE_INDEX = 2;
		public static final int FORM_SPEC_ID_INDEX = 3;
	}

	// public static class States implements BaseColumns {
	// public static final String TABLE = "states";
	// public static final String NAME = "name";
	// public static final String COUNTRY_ID = "country_id";
	//
	// public static final String[] ALL_COLUMNS = { _ID, NAME, COUNTRY_ID };
	//
	// /**
	// * The following INDEX constants can be used for performance reasons,
	// * Instead of looking them up by column name.
	// */
	// public static final int _ID_INDEX = 0;
	// public static final int NAME_INDEX = 1;
	// public static final int COUNTRY_ID_INDEX = 2;
	// }

	public static final class EntitySpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private EntitySpecs() {
		}

		/** Table name */
		public static final String TABLE = "entity_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entity.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String WITHDRAWN = "withdrawn";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, WITHDRAWN };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int WITHDRAWN_INDEX = 2;
	}

	public static final class EntityFieldSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private EntityFieldSpecs() {
		}

		/** Table name */
		public static final String TABLE = "entity_field_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entity.field.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String ENTITY_SPEC_ID = "entity_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LABEL = "label";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = "type";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String REQUIRED = "required";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String DISPLAY_ORDER = "display_order";

		/**
		 * Used for storing entity spec id when type is entity.
		 * 
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TYPE_EXTRA = "type_extra";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SELECTOR = "selector";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String COMPUTED = "computed";

		/**
		 * <p>
		 * Type: Boolean (stored as a string)
		 * </p>
		 */
		public static final String BARCODE = "barcode";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORMULA = "formula";

		public static final String[] ALL_COLUMNS = { _ID, ENTITY_SPEC_ID,
				LABEL, TYPE, IDENTIFIER, REQUIRED, DISPLAY_ORDER, TYPE_EXTRA,
				SELECTOR, COMPUTED, FORMULA, BARCODE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int ENTITY_SPEC_ID_INDEX = 1;
		public static final int LABEL_INDEX = 2;
		public static final int TYPE_INDEX = 3;
		public static final int IDENTIFIER_INDEX = 4;
		public static final int REQUIRED_INDEX = 5;
		public static final int DISPLAY_ORDER_INDEX = 6;
		public static final int TYPE_EXTRA_INDEX = 7;
		public static final int SELECTOR_INDEX = 8;
		public static final int COMPUTED_INDEX = 9;
		public static final int FORMULA_INDEX = 10;
		public static final int BARCODE_INDEX = 11;
	}

	public static final class EntityFieldValueSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private EntityFieldValueSpecs() {
		}

		/** Table name */
		public static final String TABLE = "entity_field_value_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entity.field.value.specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE = "value";

		public static final String[] ALL_COLUMNS = { _ID, FIELD_SPEC_ID, VALUE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int FIELD_SPEC_ID_INDEX = 1;
		public static final int VALUE_INDEX = 2;
	}

	public static final class Entities implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Entities() {
		}

		/** Table name */
		public static final String TABLE = "entities";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entities";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String ENTITY_SPEC_ID = "entity_spec_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FILLED_BY_ID = "filled_by_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FILLED_BY_NAME = "filled_by_name";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String MODIFIED_BY_ID = "modified_by_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY_NAME = "modified_by_name";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String ASSIGNED_TO_ID = "assigned_to_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ASSIGNED_TO_NAME = "assigned_to_name";

		/**
		 * <p>
		 * Type: String (Boolean)
		 * </p>
		 */
		public static final String DELETED = "deleted";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 * This is not currently being used.
		 */
		public static final String STATUS = "status";

		/**
		 * Retrieved using previous forms, and can be deleted during cleanup.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String CACHED = "cached";

		/**
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * Indicates whether there are any unsynced changes in the tree.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String TREE_DIRTY = "tree_dirty";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_MODIFICATION_TIME = "remote_modification_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID,
				ENTITY_SPEC_ID, FILLED_BY_ID, FILLED_BY_NAME, MODIFIED_BY_ID,
				MODIFIED_BY_NAME, ASSIGNED_TO_ID, ASSIGNED_TO_NAME, DELETED,
				STATUS, CACHED, TEMPORARY, DIRTY, TREE_DIRTY,
				REMOTE_CREATION_TIME, REMOTE_MODIFICATION_TIME,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int ENTITY_SPEC_ID_INDEX = 2;
		public static final int FILLED_BY_ID_INDEX = 3;
		public static final int FILLED_BY_NAME_INDEX = 4;
		public static final int MODIFIED_BY_ID_INDEX = 5;
		public static final int MODIFIED_BY_NAME_INDEX = 6;
		public static final int ASSIGNED_TO_ID_INDEX = 7;
		public static final int ASSIGNED_TO_NAME_INDEX = 8;
		public static final int DELETED_INDEX = 9;
		public static final int STATUS_INDEX = 10;
		public static final int CACHED_INDEX = 11;
		public static final int TEMPORARY_INDEX = 12;
		public static final int DIRTY_INDEX = 13;
		public static final int TREE_DIRTY_INDEX = 14;
		public static final int REMOTE_CREATION_TIME_INDEX = 15;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 16;
		public static final int LOCAL_CREATION_TIME_INDEX = 17;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 18;
	}

	public static final class EntityFields implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private EntityFields() {
		}

		/** Table name */
		public static final String TABLE = "entity_fields";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entity.fields";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * Foreign key that reference FormSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String ENTITY_SPEC_ID = "entity_spec_id";

		/**
		 * Foreign key that reference FieldSpecs._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * Foreign key that reference Forms._ID.
		 * 
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_ENTITY_ID = "local_entity_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = "local_value";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = "remote_value";

		public static final String[] ALL_COLUMNS = { _ID, ENTITY_SPEC_ID,
				FIELD_SPEC_ID, LOCAL_ENTITY_ID, LOCAL_VALUE, REMOTE_VALUE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int ENTITY_SPEC_ID_INDEX = 1;
		public static final int FIELD_SPEC_ID_INDEX = 2;
		public static final int LOCAL_ENTITY_ID_INDEX = 3;
		public static final int LOCAL_VALUE_INDEX = 4;
		public static final int REMOTE_VALUE_INDEX = 5;
	}

	public static final class EntitiesView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private EntitiesView() {
		}

		/** Table name */
		public static final String TABLE = "entities_view";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.entities.view";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String LOCAL_ENTITY_ID = EntityFields.TABLE + "."
				+ EntityFields.LOCAL_ENTITY_ID;

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String TYPE = EntityFieldSpecs.TABLE + "."
				+ EntityFieldSpecs.TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_VALUE = EntityFields.TABLE + "."
				+ EntityFields.LOCAL_VALUE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_VALUE = EntityFields.TABLE + "."
				+ EntityFields.REMOTE_VALUE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DISPLAY_VALUE = "display_value";

		public static final String[] ALL_COLUMNS = { LOCAL_ENTITY_ID, TYPE,
				LOCAL_VALUE, REMOTE_VALUE, DISPLAY_VALUE, _ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int LOCAL_ENTITY_ID_INDEX = 0;
		public static final int TYPE_INDEX = 1;
		public static final int LOCAL_VALUE_INDEX = 2;
		public static final int REMOTE_VALUE_INDEX = 3;
		public static final int DISPLAY_VALUE_INDEX = 4;
		public static final int _ID_INDEX = 5;
	}

	public static final class Articles implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Articles() {
		}

		/** Table name */
		public static final String TABLE = "articles";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.articles";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TITLE = "title";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MEDIA_ID = "media_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MIME_TYPE = "mime_type";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_MEDIA_PATH = "local_media_path";

		/**
		 * This flag is set to true to request download.
		 * 
		 * This flag will be considered only when localMediaPath is not set or
		 * the file is missing.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DOWNLOAD_REQUESTED = "download_requested";

		/**
		 * As both upload and download of the same file is not allowed, transfer
		 * percentage is used for uploads as well as downloads.
		 * 
		 * Valid media ID indicates that the file has been uploaded
		 * successfully.
		 * 
		 * Any file with out media ID and a non-zero transfer percentage should
		 * be considered as upload in progress.
		 * 
		 * <b>Type: Int</b>
		 */
		public static final String TRANSFER_PERCENTAGE = "transfer_percentage";

		/**
		 * File size in bytes.
		 * 
		 * <b>Type: Long</b>
		 */
		public static final String FILE_SIZE = "file_size";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CREATED_BY_NAME = "created_by_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY_NAME = "modified_by_name";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_MODIFICATION_TIME = "remote_modification_time";

		/**
		 * This flag is set to true to request download.
		 * 
		 * This flag will be considered only when localMediaPath is not set or
		 * the file is missing.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */

		public static final String GOT_VIA_SEARCH = "got_via_search";

		public static final String FILE_TYPE = "file_type";
		public static final String PARENT_PATH = "parent_path";
		public static final String PARENT_ID = "parent_id";
		public static final String IS_SECURED = "is_secured";
		public static final String DELETED = "deleted";
		// For Notification purpose added these times
		public static final String LOCAL_CREATION_TIME = "local_creation_time";
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, TITLE, DESCRIPTION,
				MEDIA_ID, MIME_TYPE, LOCAL_MEDIA_PATH, DOWNLOAD_REQUESTED,
				TRANSFER_PERCENTAGE, FILE_SIZE, CREATED_BY_NAME,
				MODIFIED_BY_NAME, REMOTE_CREATION_TIME,
				REMOTE_MODIFICATION_TIME, GOT_VIA_SEARCH, FILE_TYPE,
				PARENT_PATH, PARENT_ID, IS_SECURED, DELETED,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int DESCRIPTION_INDEX = 2;
		public static final int MEDIA_ID_INDEX = 3;
		public static final int MIME_TYPE_INDEX = 4;
		public static final int LOCAL_MEDIA_PATH_INDEX = 5;
		public static final int DOWNLOAD_REQUESTED_INDEX = 6;
		public static final int TRANSFER_PERCENTAGE_INDEX = 7;
		public static final int FILE_SIZE_INDEX = 8;
		public static final int CREATED_BY_NAME_INDEX = 9;
		public static final int MODIFIED_BY_NAME_INDEX = 10;
		public static final int REMOTE_CREATION_TIME_INDEX = 11;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 12;
		public static final int GOT_VIA_SEARCH_INDEX = 13;
		public static final int FILE_TYPE_INDEX = 14;
		public static final int PARENT_PATH_INDEX = 15;
		public static final int PARENT_ID_INDEX = 16;
		public static final int IS_SECURED_INDEX = 17;
		public static final int DELETED_INDEX = 18;
		public static final int LOCAL_CREATION_TIME_INDEX = 19;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 20;

	}

	public static final class NamedLocations implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private NamedLocations() {
		}

		/** Table name */
		public static final String TABLE = "named_locations";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.named_locations";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LATITUDE = "latitude";

		/**
		 * <p>
		 * Type: Double
		 * </p>
		 */
		public static final String LONGITUDE = "longitude";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STREET = "street";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String AREA = "area";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CITY = "city";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATE = "state";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String COUNTRY = "country";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PIN_CODE = "pin_code";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LANDMARK = "landmark";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String PARTIAL = "partial";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID, NAME,
				DESCRIPTION, LATITUDE, LONGITUDE, STREET, AREA, CITY, STATE,
				COUNTRY, PIN_CODE, LANDMARK, DIRTY, PARTIAL,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int NAME_INDEX = 2;
		public static final int DESCRIPTION_INDEX = 3;
		public static final int LATITUDE_INDEX = 4;
		public static final int LONGITUDE_INDEX = 5;
		public static final int STREET_INDEX = 6;
		public static final int AREA_INDEX = 7;
		public static final int CITY_INDEX = 8;
		public static final int STATE_INDEX = 9;
		public static final int COUNTRY_INDEX = 10;
		public static final int PIN_CODE_INDEX = 11;
		public static final int LANDMARK_INDEX = 12;
		public static final int DIRTY_INDEX = 13;
		public static final int PARTIAL_INDEX = 14;
		public static final int LOCAL_CREATION_TIME_INDEX = 15;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 16;
	}

	public static final class Messages implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Messages() {
		}

		/** Table name */
		public static final String TABLE = "messages";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.messages";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String SUBJECT = "subject";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String BODY = "body";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String QUALITY_TYPE = "quality_type";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String REMOTE_CREATION_TIME = "remote_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		public static final String[] ALL_COLUMNS = { _ID, SUBJECT, BODY,
				REMOTE_CREATION_TIME, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, QUALITY_TYPE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int SUBJECT_INDEX = 1;
		public static final int BODY_INDEX = 2;
		public static final int REMOTE_CREATION_TIME_INDEX = 3;
		public static final int LOCAL_CREATION_TIME_INDEX = 4;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 5;
		public static final int QUALITY_TYPE_INDEX = 6;
	}

	public static final class VisibilityCriterias implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private VisibilityCriterias() {
		}

		/** Table name */
		public static final String TABLE = "visibility_criterias";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.visibility_criterias";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE = "value";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String FIELD_TYPE = "field_type";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CONDITION = "condition";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String TARGET_FIELD_EXPRESSION = "target_field_expression";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String FIELD_DATA_TYPE = "field_data_type";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE_IDS = "value_ids";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String VISIBILITY_TYPE = "visibility_type";

		public static final String[] ALL_COLUMNS = { _ID, VALUE, REMOTE_ID,
				FIELD_TYPE, CONDITION, FORM_SPEC_ID, FIELD_SPEC_ID,
				TARGET_FIELD_EXPRESSION, FIELD_DATA_TYPE, VALUE_IDS,
				VISIBILITY_TYPE };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int VALUE_INDEX = 1;
		public static final int REMOTE_ID_INDEX = 2;
		public static final int FIELD_TYPE_INDEX = 3;
		public static final int CONDITION_INDEX = 4;
		public static final int FORM_SPEC_ID_INDEX = 5;
		public static final int FIELD_SPEC_ID_INDEX = 6;
		public static final int TARGET_FIELD_EXPRESSION_INDEX = 7;
		public static final int FIELD_DATA_TYPE_INDEX = 8;
		public static final int VALUE_IDS_INDEX = 9;
		public static final int VISIBILITY_TYPE_INDEX = 10;
	}

	public static final class ListFilteringCriterias implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private ListFilteringCriterias() {
		}

		/** Table name */
		public static final String TABLE = "list_filtering_criterias";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.list_filtering_criterias";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String VALUE = "value";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String TYPE = "type";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String CONDITION = "condition";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";

		/**
		 * <p>
		 * Type: Long
		 * </p>
		 */
		public static final String FIELD_SPEC_ID = "field_spec_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REFERENCE_FIELD_EXPRESSION_ID = "reference_field_expression_id";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String LIST_FIELD_SPEC_ID = "list_field_spec_id";

		public static final String[] ALL_COLUMNS = { _ID, VALUE, TYPE,
				CONDITION, FORM_SPEC_ID, FIELD_SPEC_ID,
				REFERENCE_FIELD_EXPRESSION_ID, LIST_FIELD_SPEC_ID };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int VALUE_INDEX = 1;
		public static final int TYPE_INDEX = 2;
		public static final int CONDITION_INDEX = 3;
		public static final int FORM_SPEC_ID_INDEX = 4;
		public static final int FIELD_SPEC_ID_INDEX = 5;
		public static final int REFERENCE_FIELD_EXPRESSION_ID_INDEX = 6;
		public static final int LIST_FIELD_SPEC_ID_INDEX = 7;
	}

	public static final class Employees implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private Employees() {
		}

		/** Table name */
		public static final String TABLE = "employees";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.employees";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String EMPLOYEE_ID = "emp_id";
		public static final String EMPLOYEE_FIRST_NAME = "emp_first_name";
		public static final String EMPLOYEE_LAST_NAME = "emp_last_name";
		public static final String EMPLOYEE_EMAIL = "emp_email";
		public static final String MANAGER_ID = "manager_id";
		public static final String EMPLOYEE_PHONE = "emp_phone";
		public static final String IMEI = "imei";
		public static final String COMPANY_ID = "company_id";
		public static final String MANAGER = "manager";
		public static final String EMPLOYEE_TYPE_ID = "emp_type_id";
		public static final String CALENDAR_ID = "calendar_id";
		public static final String EMPLOYEE_NO = "emp_no";
		public static final String HOME_LAT = "home_lat";
		public static final String HOME_LONG = "home_long";
		public static final String CLIENT_EMPLOYEE_ID = "client_emp_id";
		public static final String EMPLOYEE_ADDRESS_STREET = "emp_address_street";
		public static final String EMPLOYEE_ADDRESS_AREA = "emp_address_area";
		public static final String EMPLOYEE_ADDRESS_CITY = "emp_address_city";
		public static final String EMPLOYEE_ADDRESS_PINCODE = "emp_address_pincode";
		public static final String EMPLOYEE_ADDRESS_LANDMARK = "emp_address_landmark";
		public static final String EMPLOYEE_ADDRESS_STATE = "emp_address_state";
		public static final String EMPLOYEE_ADDRESS_COUNTRY = "emp_address_country";
		public static final String WORK_LAT = "work_lat";
		public static final String WORK_LONG = "work_long";
		public static final String PROVISIONING = "provisioning";
		public static final String RANK = "rank";

		public static final String[] ALL_COLUMNS = { _ID, EMPLOYEE_ID,
				EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME, EMPLOYEE_EMAIL,
				MANAGER_ID, EMPLOYEE_PHONE, IMEI, COMPANY_ID, MANAGER,
				EMPLOYEE_TYPE_ID, CALENDAR_ID, EMPLOYEE_NO, HOME_LAT,
				HOME_LONG, CLIENT_EMPLOYEE_ID, EMPLOYEE_ADDRESS_STREET,
				EMPLOYEE_ADDRESS_AREA, EMPLOYEE_ADDRESS_CITY,
				EMPLOYEE_ADDRESS_PINCODE, EMPLOYEE_ADDRESS_LANDMARK,
				EMPLOYEE_ADDRESS_STATE, EMPLOYEE_ADDRESS_COUNTRY, WORK_LAT,
				WORK_LONG, PROVISIONING, RANK };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int EMPLOYEE_ID_INDEX = 1;
		public static final int EMPLOYEE_FIRST_NAME_INDEX = 2;
		public static final int EMPLOYEE_LAST_NAME_INDEX = 3;
		public static final int EMPLOYEE_EMAIL_INDEX = 4;
		public static final int MANAGER_ID_INDEX = 5;
		public static final int EMPLOYEE_PHONE_INDEX = 6;
		public static final int IMEI_INDEX = 7;
		public static final int COMPANY_ID_INDEX = 8;
		public static final int MANAGER_INDEX = 9;
		public static final int EMPLOYEE_TYPE_ID_INDEX = 10;
		public static final int CALENDAR_ID_INDEX = 11;
		public static final int EMPLOYEE_NO_INDEX = 12;
		public static final int HOME_LAT_INDEX = 13;
		public static final int HOME_LONG_INDEX = 14;
		public static final int CLIENT_EMPLOYEE_ID_INDEX = 15;
		public static final int EMPLOYEE_ADDRESS_STREET_INDEX = 16;
		public static final int EMPLOYEE_ADDRESS_AREA_INDEX = 17;
		public static final int EMPLOYEE_ADDRESS_CITY_INDEX = 18;
		public static final int EMPLOYEE_ADDRESS_PINCODE_INDEX = 19;
		public static final int EMPLOYEE_ADDRESS_LANDMARK_INDEX = 20;
		public static final int EMPLOYEE_ADDRESS_STATE_INDEX = 21;
		public static final int EMPLOYEE_ADDRESS_COUNTRY_INDEX = 22;
		public static final int WORK_LAT_INDEX = 23;
		public static final int WORK_LONG_INDEX = 24;
		public static final int PROVISIONING_INDEX = 25;
		public static final int RANK_INDEX = 26;
	}

	public static final class CompletedActivities implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private CompletedActivities() {
		}

		/** Table name */
		public static final String TABLE = "completed_activities";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.completed_activities";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ASSIGNED_ROUTE_ID = "assigned_route_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_activity_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ACTIVITY_ID = "activity_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CLIENT_ACTIVITY_ID = "client_activity_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CUSTOMER_ID = "customer_id";
		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String ACTIVITY_COMPLETED_TIME = "activity_completed_time";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CLIENT_FORM_ID = "client_form_id";

		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID,
				ASSIGNED_ROUTE_ID, ACTIVITY_ID, ACTIVITY_COMPLETED_TIME,
				CUSTOMER_ID, CLIENT_FORM_ID, DIRTY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int ASSIGNED_ROUTE_ID_INDEX = 2;
		public static final int ACTIVITY_ID_INDEX = 3;
		public static final int ACTIVITY_COMPLETED_TIME_INDEX = 4;
		public static final int CUSTOMER_ID_INDEX = 5;
		public static final int CLIENT_FORM_ID_INDEX = 6;
		// public static final int FORM_ID_INDEX = 7;
		public static final int DIRTY_INDEX = 7;
	}

	public static final class ActivitySpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private ActivitySpecs() {
		}

		/** Table name */
		public static final String TABLE = "activity_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.activity_specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String NAME = "name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String FORM_SPEC_ID = "form_spec_id";
		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DELETED = "deleted";

		public static final String[] ALL_COLUMNS = { _ID, NAME, FORM_SPEC_ID,
				DELETED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int FORM_SPEC_ID_INDEX = 2;
		public static final int DELETED_INDEX = 3;

	}

	public static final class CustomerStatus implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private CustomerStatus() {
		}

		/** Table name */
		public static final String TABLE = "customer_status";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.customer_status";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ASSIGN_ROUTE_ID = "assign_route_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CUSTOMER_ID = "customer_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS_CHANGE_TIME = "status_change_time";
		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String STATUS = "status";
		/**
		 * Indicates whether there are any unsynced changes.
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		public static final String[] ALL_COLUMNS = { _ID, ASSIGN_ROUTE_ID,
				CUSTOMER_ID, STATUS_CHANGE_TIME, STATUS, DIRTY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int ASSIGN_ROUTE_ID_INDEX = 1;
		public static final int CUSTOMER_ID_INDEX = 2;
		public static final int STATUS_CHANGE_TIME_INDEX = 3;
		public static final int STATUS_INDEX = 4;
		public static final int DIRTY_INDEX = 5;

	}

	public static final class AssignedRoutes implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private AssignedRoutes() {
		}

		/** Table name */
		public static final String TABLE = "assigned_routes";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.assigned_routes";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		// public static final String ASSIGN_ROUTE_ID = "assign_route_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ROUTE_NAME = "route_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String DURATION = "duration";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String START_DATE = "start_date";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String END_DATE = "end_date";

		/**
		 * Retrieved using previous assignedRoute, and can be deleted during
		 * cleanup.
		 * 
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String CACHED = "cached";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DELETED = "deleted";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String STATUS = "status";
		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";
		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String COMPLETION_TIME = "completion_time";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_CUSTOMER_IDS = "remote_cutomer_ids";

		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String MIN_CUSTOMERS_TO_COMPLETE = "min_customers_to_complete";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";

		public static final String[] ALL_COLUMNS = { _ID, ROUTE_NAME, DURATION,
				START_DATE, END_DATE, CACHED, DELETED, STATUS,
				LOCAL_MODIFICATION_TIME, COMPLETION_TIME, REMOTE_CUSTOMER_IDS,
				MIN_CUSTOMERS_TO_COMPLETE, DIRTY };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int ROUTE_NAME_INDEX = 1;
		public static final int DURATION_INDEX = 2;
		public static final int START_DATE_INDEX = 3;
		public static final int END_DATE_INDEX = 4;
		public static final int CACHED_INDEX = 5;
		public static final int DELETED_INDEX = 6;
		public static final int STATUS_INDEX = 7;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 8;
		public static final int COMPLETION_TIME_INDEX = 9;
		public static final int REMOTE_CUSTOMER_IDS_INDEX = 10;
		public static final int MIN_CUSTOMERS_TO_COMPLETE_INDEX = 11;
		public static final int DIRTY_INDEX = 12;

	}

	public static final class WorkFlowSpecs implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowSpecs() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_specs";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_specs";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String WORK_FLOW_NAME = "work_flow_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		// public static final String FORM_SPE_UNIQUE_ID =
		// "form_spec_unique_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CREATED_TIME = "created_time";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CREATED_BY = "created_by";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String CHECKED = "checked";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */

		public static final String DELETED = "deleted";

		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */

		public static final String EDITABLE = "editable";
		public static final String HAS_ROLE_BASED_STAGES = "has_role_based_stages";

		public static final String[] ALL_COLUMNS = { _ID, WORK_FLOW_NAME,
				CREATED_TIME, CREATED_BY, DELETED, EDITABLE, CHECKED,
				HAS_ROLE_BASED_STAGES };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int WORK_FLOW_NAME_INDEX = 1;
		public static final int CREATED_TIME_INDEX = 2;
		public static final int CREATED_BY_INDEX = 3;
		public static final int DELETED_INDEX = 4;
		public static final int EDITABLE_INDEX = 5;
		public static final int CHECKED_INDEX = 6;
		public static final int HAS_ROLE_BASED_STAGES_INDEX = 7;

	}

	public static final class WorkFlowFormSpecMappings implements BaseColumns {
		/**
		 * Can not be instantiated
		 */

		private WorkFlowFormSpecMappings() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_entity_mappings";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_entity_mappings";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String WORK_FLOW_ID = "work_flow_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String WORKFLOW_MAP_ENTITY_ID = "mapping_entity_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String ENTITY_TYPE = "entity_type";
		public static final String CHECKED = "checked";

		public static final String[] ALL_COLUMNS = { _ID, WORK_FLOW_ID,
				WORKFLOW_MAP_ENTITY_ID, ENTITY_TYPE, CHECKED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int WORK_FLOW_ID_INDEX = 1;
		public static final int WORKFLOW_MAP_ENTITY_ID_INDEX = 2;
		public static final int ENTITY_TYPE_INDEX = 3;
		public static final int CHECKED_INDEX = 4;

	}

	public static final class WorkFlowFormSpecFilterView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */

		private WorkFlowFormSpecFilterView() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_form_spec_filter";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_form_spec_filter";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;
		public static final String _ID = WorkFlowFormSpecMappings.TABLE + "."
				+ WorkFlowFormSpecMappings._ID;

		public static final String WORK_FLOW_ID = WorkFlowFormSpecMappings.TABLE
				+ "." + WorkFlowFormSpecMappings.WORK_FLOW_ID;

		public static final String WORKFLOW_MAP_ENTITY_ID = WorkFlowFormSpecMappings.TABLE
				+ "." + WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID;

		public static final String FORM_TEMPLATE_NAME = FormSpecs.TABLE + "."
				+ FormSpecs.TITLE;

		public static final String ENTITY_TYPE = WorkFlowFormSpecMappings.TABLE
				+ "." + WorkFlowFormSpecMappings.ENTITY_TYPE;
		public static final String CHECKED = WorkFlowFormSpecMappings.TABLE
				+ "." + WorkFlowFormSpecMappings.CHECKED;

		public static final String[] ALL_COLUMNS = { _ID, WORK_FLOW_ID,
				WORKFLOW_MAP_ENTITY_ID, FORM_TEMPLATE_NAME, ENTITY_TYPE,
				CHECKED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int WORK_FLOW_ID_INDEX = 1;
		public static final int WORKFLOW_MAP_ENTITY_ID_INDEX = 2;
		public static final int FORM_TEMPLATE_NAME_INDEX = 3;
		public static final int ENTITY_TYPE_INDEX = 4;
		public static final int CHECKED_INDEX = 5;
	}

	public static final class WorkFlowStages implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowStages() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_stage";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_stage";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String WORK_FLOW_ID = "work_flow_id";
		public static final String STAGE_NAME = "stage_name";
		public static final String DELETED = "deleted";

		public static final String[] ALL_COLUMNS = { _ID, WORK_FLOW_ID,
				STAGE_NAME, DELETED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int WORK_FLOW_ID_INDEX = 1;
		public static final int STAGE_NAME_INDEX = 2;
		public static final int DELETED_INDEX = 5;

	}

	public static final class WorkFlowStatus implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowStatus() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_status";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_status";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMOTE_ID = "remote_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String WORK_FLOW_ID = "work_flow_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CREATED_BY = "created_by";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY = "modified_by";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String APPROVED_BY = "approved_by";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String CREATED_BY_NAME = "created_by_name";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY_NAME = "modified_by_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String APPROVED_BY_NAME = "approved_by_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String CREATED_TIME = "created_time";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_TIME = "modified_time";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String APPROVED_TIME = "approved_time";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PREVIOUS_RANK = "previous_rank";
		public static final String CURRENT_RANK = "current_rank";
		public static final String NEXT_RANK = "next_rank";
		/**
		 * <p>
		 * Type: Integer
		 * </p>
		 */
		public static final String STAGE_TYPE = "stage_type";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STAGE_NAME = "stage_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS = "status";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS_MESSAGE = "status_message";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMARKS = "remarks";
		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String DIRTY = "dirty";
		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_CREATION_TIME = "local_creation_time";

		/**
		 * <p>
		 * Type: String (SQLite Date Time)
		 * </p>
		 */
		public static final String LOCAL_MODIFICATION_TIME = "local_modification_time";

		/**
		 * <p>
		 * Type: String (We can use this for pending submisstion workflow
		 * recognizization)
		 * </p>
		 */
		public static final String TEMPORARY = "temporary";
		public static final String EMP_GROUP_ID = "emp_group_id";
		public static final String MANAGER_RANK = "manager_rank";

		public static final String[] ALL_COLUMNS = { _ID, REMOTE_ID,
				LOCAL_FORM_ID, WORK_FLOW_ID, CREATED_BY, MODIFIED_BY,
				APPROVED_BY, CREATED_BY_NAME, MODIFIED_BY_NAME,
				APPROVED_BY_NAME, CREATED_TIME, MODIFIED_TIME, APPROVED_TIME,
				STAGE_TYPE, STAGE_NAME, PREVIOUS_RANK, CURRENT_RANK, NEXT_RANK,
				STATUS, STATUS_MESSAGE, REMARKS, DIRTY, LOCAL_CREATION_TIME,
				LOCAL_MODIFICATION_TIME, TEMPORARY, EMP_GROUP_ID, MANAGER_RANK };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int REMOTE_ID_INDEX = 1;
		public static final int LOCAL_FORM_ID_INDEX = 2;
		public static final int WORK_FLOW_ID_INDEX = 3;
		public static final int CREATED_BY_INDEX = 4;
		public static final int MODIFIED_BY_INDEX = 5;
		public static final int APPROVED_BY_INDEX = 6;
		public static final int CREATED_BY_NAME_INDEX = 7;
		public static final int MODIFIED_BY_NAME_INDEX = 8;
		public static final int APPROVED_BY_NAME_INDEX = 9;
		public static final int CREATED_TIME_INDEX = 10;
		public static final int MODIFIED_TIME_INDEX = 11;
		public static final int APPROVED_TIME_INDEX = 12;
		public static final int STAGE_TYPE_INDEX = 13;
		public static final int STAGE_NAME_INDEX = 14;
		public static final int PREVIOUS_RANK_INDEX = 15;
		public static final int CURRENT_RANK_INDEX = 16;
		public static final int NEXT_RANK_INDEX = 17;
		public static final int STATUS_INDEX = 18;
		public static final int STATUS_MESSAGE_INDEX = 19;
		public static final int REMARKS_INDEX = 20;
		public static final int DIRTY_INDEX = 21;
		public static final int LOCAL_CREATION_TIME_INDEX = 22;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 23;
		public static final int TEMPARORY_INDEX = 24;
		public static final int EMP_GROUP_ID_INDEX = 25;
		public static final int MANAGER_RANK_INDEX = 26;
	}

	public static final class WorkFlowHistories implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowHistories() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_histories";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_histories";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String WORK_FLOW_ID = "work_flow_id";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String LOCAL_FORM_ID = "local_form_id";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String CREATED_BY = "created_by";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String MODIFIED_BY = "modified_by";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String APPROVED_BY = "approved_by";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String CREATED_BY_NAME = "created_by_name";

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_BY_NAME = "modified_by_name";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String APPROVED_BY_NAME = "approved_by_name";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */

		public static final String CREATED_TIME = "created_time";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MODIFIED_TIME = "modified_time";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String APPROVED_TIME = "approved_time";
		/**
		 * <p>
		 * Type: Boolean
		 * </p>
		 */
		public static final String STAGE_NAME = "stage_name";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String PREVIOUS_RANK = "previous_rank";
		public static final String CURRENT_RANK = "current_rank";
		public static final String NEXT_RANK = "next_rank";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS = "status";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS_MESSAGE = "status_message";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String REMARKS = "remarks";

		public static final String[] ALL_COLUMNS = { _ID, WORK_FLOW_ID,
				LOCAL_FORM_ID, CREATED_BY, MODIFIED_BY, APPROVED_BY,
				CREATED_BY_NAME, MODIFIED_BY_NAME, APPROVED_BY_NAME,
				CREATED_TIME, MODIFIED_TIME, APPROVED_TIME, PREVIOUS_RANK,
				CURRENT_RANK, NEXT_RANK, STAGE_NAME, STATUS, STATUS_MESSAGE,
				REMARKS };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */

		public static final int _ID_INDEX = 0;
		public static final int WORK_FLOW_ID_INDEX = 1;
		public static final int LOCAL_FORM_ID_INDEX = 2;
		public static final int CREATED_BY_INDEX = 3;
		public static final int MODIFIED_BY_INDEX = 4;
		public static final int APPROVED_BY_INDEX = 5;
		public static final int CREATED_BY_NAME_INDEX = 6;
		public static final int MODIFIED_BY_NAME_INDEX = 7;
		public static final int APPROVED_BY_NAME_INDEX = 8;
		public static final int CREATED_TIME_INDEX = 9;
		public static final int MODIFIED_TIME_INDEX = 10;
		public static final int APPROVED_TIME_INDEX = 11;
		public static final int PREVIOUS_RANK_INDEX = 12;
		public static final int CURRENT_RANK_INDEX = 13;
		public static final int NEXT_RANK_INDEX = 14;
		public static final int STAGE_NAME_INDEX = 15;
		public static final int STATUS_INDEX = 16;
		public static final int STATUS_MESSAGE_INDEX = 17;
		public static final int REMARKS_INDEX = 18;
	}

	/**
	 * This is a virtual table (a view), which is a join of WorkFlows, Forms,
	 * FormFields and WorkFlowStatus
	 * 
	 * @author Kanakachary
	 * 
	 */
	public static final class WorkFlowsView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowsView() {
		}

		/** Table name */
		public static final String TABLE = "work_flows_view";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flows_view";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String WORK_FLOW_ID = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs._ID;

		public static final String WORK_FLOW_NAME = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.WORK_FLOW_NAME;

		public static final String FORM_SPEC_NAME = FormSpecs.TABLE + "."
				+ FormSpecs.TITLE;

		public static final String WORK_FLOW_STAGE_NAME = WorkFlowStatus.TABLE
				+ "." + WorkFlowStatus.STAGE_NAME;

		public static final String CURRENT_RANK = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.CURRENT_RANK;

		public static final String PREVIOUS_RANK = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.PREVIOUS_RANK;

		public static final String NEXT_RANK = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.NEXT_RANK;

		public static final String STATUS = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.STATUS;
		public static final String STAGE_TYPE = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.STAGE_TYPE;
		public static final String STATUS_MESSAGE = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.STATUS_MESSAGE;
		public static final String EMP_GROUP_ID = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.EMP_GROUP_ID;
		public static final String MANAGER_RANK = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.MANAGER_RANK;

		public static final String LOCAL_FORM_ID = Forms.TABLE + "."
				+ Forms._ID;

		public static final String TYPE = FieldSpecs.TABLE + "."
				+ FieldSpecs.TYPE;

		public static final String LOCAL_VALUE = Fields.TABLE + "."
				+ Fields.LOCAL_VALUE;

		public static final String REMOTE_VALUE = Fields.TABLE + "."
				+ Fields.REMOTE_VALUE;

		public static final String REMOTE_CREATION_TIME = Forms.TABLE + "."
				+ "remote_creation_time";

		public static final String REMOTE_MODIFICATION_TIME = Forms.TABLE + "."
				+ "remote_modification_time";

		public static final String LOCAL_CREATION_TIME = Forms.TABLE + "."
				+ "local_creation_time";

		public static final String LOCAL_MODIFICATION_TIME = Forms.TABLE + "."
				+ "local_modification_time";

		// public static final String TREE_DIRTY = Forms.TABLE + "."
		// + Forms.TREE_DIRTY;
		public static final String TREE_DIRTY = WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.DIRTY;

		public static final String[] ALL_COLUMNS = { WORK_FLOW_ID,
				WORK_FLOW_NAME, FORM_SPEC_NAME, WORK_FLOW_STAGE_NAME,
				PREVIOUS_RANK, CURRENT_RANK, NEXT_RANK, STATUS, STATUS_MESSAGE,
				LOCAL_FORM_ID, TYPE, LOCAL_VALUE, REMOTE_VALUE,
				REMOTE_CREATION_TIME, REMOTE_MODIFICATION_TIME,
				LOCAL_CREATION_TIME, LOCAL_MODIFICATION_TIME, TREE_DIRTY,
				STAGE_TYPE, EMP_GROUP_ID, MANAGER_RANK };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */

		public static final int WORK_FLOW_ID_INDEX = 0;
		public static final int WORK_FLOW_NAME_INDEX = 1;
		public static final int FORM_SPEC_NAME_INDEX = 2;
		public static final int WORK_FLOW_STAGE_NAME_INDEX = 3;
		public static final int PREVIOUS_RANK_INDEX = 4;
		public static final int CURRENT_RANK_INDEX = 5;
		public static final int NEXT_RANK_INDEX = 6;
		public static final int STATUS_INDEX = 7;
		public static final int STATUS_MESSAGE_INDEX = 8;
		public static final int LOCAL_FORM_ID_INDEX = 9;
		public static final int TYPE_INDEX = 10;
		public static final int LOCAL_VALUE_INDEX = 11;
		public static final int REMOTE_VALUE_INDEX = 12;
		public static final int REMOTE_CREATION_TIME_INDEX = 13;
		public static final int REMOTE_MODIFICATION_TIME_INDEX = 14;
		public static final int LOCAL_CREATION_TIME_INDEX = 15;
		public static final int LOCAL_MODIFICATION_TIME_INDEX = 16;
		public static final int TREE_DIRTY_INDEX = 17;
		public static final int STAGE_TYPE_INDEX = 18;
		public static final int EMP_GROUP_ID_INDEX = 19;
		public static final int MANAGER_RANK_INDEX = 20;

	}

	/**
	 * This is a virtual table (a view), which is a join of WorkFlows, Forms,
	 * FormFields and WorkFlowStatus
	 * 
	 * @author Kanakachary
	 * 
	 */

	public static final class WorkFlowSpecsView implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private WorkFlowSpecsView() {
		}

		/** Table name */
		public static final String TABLE = "work_flow_specs_view";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.work_flow_specs_view";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		public static final String WORK_FLOW_ID = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs._ID;

		public static final String WORK_FLOW_NAME = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.WORK_FLOW_NAME;// "work_flow_name";
		public static final String FORM_SPEC_TITLE = FormSpecs.TABLE + "."
				+ FormSpecs.TITLE;// "work_flow_name";

		/*
		 * public static final String FORM_SPEC_UNIQUE_ID = WorkFlowSpecs.TABLE
		 * + "." + WorkFlowSpecs.FORM_SPE_UNIQUE_ID;
		 */// "form_specs_unique_id";
		public static final String WORKFLOW_MAP_ENTITY_ID = WorkFlowFormSpecMappings.TABLE
				+ "." + WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID;

		public static final String CREATED_TIME = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.CREATED_TIME;// "created_time";

		public static final String CREATED_BY = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.CREATED_BY;// "created_by";

		public static final String CHECKED = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.CHECKED;// "checked";

		public static final String DELETED = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.DELETED;// "deleted";

		public static final String EDITABLE = WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.EDITABLE;// "editable";

		public static final String[] ALL_COLUMNS = { WORK_FLOW_ID,
				WORK_FLOW_NAME, FORM_SPEC_TITLE, WORKFLOW_MAP_ENTITY_ID,
				CREATED_TIME, CREATED_BY, DELETED, EDITABLE, CHECKED };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int WORK_FLOW_ID_INDEX = 0;
		public static final int WORK_FLOW_NAME_INDEX = 1;
		public static final int FORM_SPEC_TITLE_INDEX = 2;
		public static final int WORKFLOW_MAP_ENTITY_ID_INDEX = 3;
		public static final int CREATED_TIME_INDEX = 4;
		public static final int CREATED_BY_INDEX = 5;
		public static final int DELETED_INDEX = 6;
		public static final int EDITABLE_INDEX = 7;
		public static final int CHECKED_INDEX = 8;

	}

	public static final class SimCardChangeMessages implements BaseColumns {
		/**
		 * Can not be instantiated
		 */
		private SimCardChangeMessages() {
		}

		/** Table name */
		public static final String TABLE = "sim_card_change_messages";

		/**
		 * The content:// style URI for this table. Requests to this URI can be
		 * performed on the UI thread because they are always unblocking.
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, TABLE);

		private static final String BASE_CONTENT_TYPE = "in.spoors.effort1.provider.sim_card_change_messages";

		/**
		 * The MIME-type of {@link #CONTENT_URI} providing a directory of
		 * contact directories.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
				+ BASE_CONTENT_TYPE;

		/**
		 * The MIME type of a {@link #CONTENT_URI} item.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
				+ BASE_CONTENT_TYPE;

		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String RECEIVER_NUMBER = "receiver_number";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String MESSAGE_BODY = "message_body";
		/**
		 * <p>
		 * Type: String
		 * </p>
		 */
		public static final String STATUS = "status";

		public static final String[] ALL_COLUMNS = { _ID, RECEIVER_NUMBER,
				MESSAGE_BODY, STATUS };

		/**
		 * The following INDEX constants can be used for performance reasons,
		 * Instead of looking them up by column name.
		 */
		public static final int _ID_INDEX = 0;
		public static final int RECEIVER_NUMBER_INDEX = 1;
		public static final int MESSAGE_BODY_INDEX = 2;
		public static final int STATUS_INDEX = 3;
	}
}