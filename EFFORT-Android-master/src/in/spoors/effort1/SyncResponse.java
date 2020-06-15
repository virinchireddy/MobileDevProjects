package in.spoors.effort1;

import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.ActivitySpec;
import in.spoors.effort1.dto.AppVersion;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.dto.CompletedActivity;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.dto.CustomerType;
import in.spoors.effort1.dto.Employee;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.dto.EntityFieldSpec;
import in.spoors.effort1.dto.EntityFieldValueSpec;
import in.spoors.effort1.dto.EntitySpec;
import in.spoors.effort1.dto.Field;
import in.spoors.effort1.dto.FieldSpec;
import in.spoors.effort1.dto.FieldValueSpec;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.FormSpec;
import in.spoors.effort1.dto.Holiday;
import in.spoors.effort1.dto.Invitation;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.JobHistory;
import in.spoors.effort1.dto.JobStageStatus;
import in.spoors.effort1.dto.JobState;
import in.spoors.effort1.dto.JobType;
import in.spoors.effort1.dto.Leave;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.dto.Message;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.dto.PageSpec;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.SectionFieldSpec;
import in.spoors.effort1.dto.SectionFieldValueSpec;
import in.spoors.effort1.dto.SectionSpec;
import in.spoors.effort1.dto.SpecialWorkingDay;
import in.spoors.effort1.dto.TypeStateMapping;
import in.spoors.effort1.dto.VisibilityCriteria;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.dto.WorkFlowHistory;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.dto.WorkingHour;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Sync response acts as a JSON parser and container that holds the results of a
 * successful sync.
 * 
 * @author tiru
 * 
 */
public class SyncResponse {

	public static final String TAG = "SyncResponse";
	public static final String JSON_PATH_FORM_SPECS = "forms/specs/formSpecs";
	public static final String JSON_PATH_LIST_FILTERING_CRITERIAS = "forms/specs/listFilteringCriterias";
	public static final String JSON_PATH_VISIBILITY_DEPENDENCY_CRITERIAS = "forms/specs/visibilityDependencyCriterias";
	public static final String JSON_PATH_FORM_SPECS_VISIBILITY_MAP = "forms/specs/formSpecsVisibilityMap";
	public static final String JSON_PATH_FIELD_SPECS = "forms/specs/fields";
	public static final String JSON_PATH_FIELD_VALUE_SPECS = "forms/specs/fieldValidValues";
	public static final String JSON_PATH_ADDED_FORMS = "forms/data/added";
	public static final String JSON_PATH_MODIFIED_FORMS = "forms/data/modified";
	public static final String JSON_PATH_DELETED_FORMS = "forms/data/deleted";
	public static final String JSON_PATH_FIELDS = "forms/data/fields";
	public static final String JSON_PATH_APP_VERSION = "appVersion";
	public static final String JSON_PATH_ADDED_CUSTOMERS = "addedCustomers";
	public static final String JSON_PATH_MODIFIED_CUSTOMERS = "modifiedCustomers";
	public static final String JSON_PATH_ADDED_EMPLOYEES = "employees/added";
	public static final String JSON_PATH_MODIFIED_EMPLOYEES = "employees/modified";
	public static final String JSON_PATH_DELETED_EMPLOYEES = "employees/deleted";
	public static final String JSON_PATH_EMPLOYEES_UNDER = "employees/employeesUnder";
	/** Note that it is interestedCustomer, not interestedCustomers! */
	public static final String JSON_PATH_INTERESTED_CUSTOMERS = "interestedCustomer";
	public static final String JSON_PATH_INTERESTED_ENTITIES = "interestedEntities";
	public static final String JSON_PATH_MAPPED_CUSTOMERS = "mappedCustomers";
	public static final String JSON_PATH_JOB_TYPES = "visits/types";
	public static final String JSON_PATH_JOB_STATES = "visits/states";
	public static final String JSON_PATH_JOB_HISTORIES = "visits/histories";
	public static final String JSON_PATH_JOB_STAGE_STATUSES = "visits/visitStates";
	public static final String JSON_PATH_TYPE_STATE_MAPPINGS = "visits/stateTypeMap";
	public static final String JSON_PATH_ADDED_JOBS = "visits/added";
	public static final String JSON_PATH_MODIFIED_JOBS = "visits/modified";
	public static final String JSON_PATH_DELETED_JOBS = "visits/deleted";
	public static final String JSON_PATH_ADDED_NOTES = "visits/comments/added";
	public static final String JSON_PATH_MODIFIED_NOTES = "visits/comments/modified";
	public static final String JSON_PATH_DELETED_NOTES = "visits/comments/deleted";
	public static final String JSON_PATH_ADDED_LEAVES = "leaves/added";
	public static final String JSON_PATH_MODIFIED_LEAVES = "leaves/modified";
	public static final String JSON_PATH_DELETED_LEAVES = "leaves/deleted";
	public static final String JSON_PATH_CALENDAR = "calendar";
	public static final String JSON_NEW_CALENDAR = "newCalendar";
	public static final String JSON_PATH_WORKING_HOURS = "calendar/workingHours";
	public static final String JSON_PATH_ADDED_EXCEPTIONS = "calendar/exceptions/added";
	public static final String JSON_PATH_MODIFIED_EXCEPTIONS = "calendar/exceptions/modified";
	public static final String JSON_PATH_DELETED_EXCEPTIONS = "calendar/exceptions/deleted";
	public static final String JSON_PATH_ADDED_INVITATIONS = "visitInvitations/added";
	public static final String JSON_PATH_MODIFIED_INVITATIONS = "visitInvitations/modified";
	public static final String JSON_PATH_DELETED_INVITATIONS = "visitInvitations/deleted";
	public static final String JSON_PATH_SECTION_SPECS = "forms/specs/sections";
	public static final String JSON_PATH_SECTION_FIELD_SPECS = "forms/specs/sectionFields";
	public static final String JSON_PATH_SECTION_FIELD_VALUE_SPECS = "forms/specs/sectionFieldValidValues";
	public static final String JSON_PATH_SECTION_FIELDS = "forms/data/sectionFields";
	public static final String JSON_PATH_ENTITY_SPECS = "entities/specs/entitySpecs";
	public static final String JSON_PATH_ENTITY_FIELD_SPECS = "entities/specs/fields";
	public static final String JSON_PATH_ENTITY_FIELD_VALUE_SPECS = "entities/specs/fieldValidValues";
	public static final String JSON_PATH_ADDED_ENTITIES = "entities/data/added";
	public static final String JSON_PATH_MODIFIED_ENTITIES = "entities/data/modified";
	public static final String JSON_PATH_DELETED_ENTITIES = "entities/data/deleted";
	public static final String JSON_PATH_ENTITY_FIELDS = "entities/data/fields";
	public static final String JSON_PATH_PAGE_SPECS = "forms/specs/pageSpecs";

	public static final String JSON_PATH_WORK_FLOW_HISTORY = "workflow/data/workflowFormStatusHistory";
	public static final String JSON_PATH_WORK_FLOWS = "workflow/specs/workflows";
	public static final String JSON_PATH_WORK_FLOW_FORM_STATUS = "workflow/data/workflowFormStatus";
	public static final String JSON_PATH_WORK_FLOW_STAGES = "workflow/specs/workflowStages";
	public static final String JSON_PATH_WORK_FLOW_FORM_SPECS_MAP = "workflow/specs/workflowEntityMap";

	public static final String JSON_INVITATION_ID = "visitInvitationId";
	public static final String JSON_REMOTE_SYNC_TIME = "syncTime";
	public static final String JSON_PATH_ADDED_ARTICLES = "articles/added";
	public static final String JSON_PATH_MODIFIED_ARTICLES = "articles/modified";
	public static final String JSON_PATH_DELETED_ARTICLES = "articles/deleted";
	public static final String JSON_PATH_MAPPED_ARTICLES = "articles/mappedArticleIds";

	public static final String JSON_PATH_ADDED_LOCATIONS = "namedLocations/added";
	public static final String JSON_PATH_MODIFIED_LOCATIONS = "namedLocations/modified";
	public static final String JSON_PATH_DELETED_LOCATIONS = "namedLocations/deleted";
	public static final String JSON_PATH_INTERESTED_LOCATIONS = "namedLocations/interestedNamedLocations";
	public static final String JSON_PATH_MESSAGES = "notifications";

	// ROUTE PLAN
	public static final String JSON_PATH_ACTIVITIES = "activities";
	public static final String JSON_PATH_COMPLETED_ACTIVITIES = "completedActivities";
	public static final String JSON_PATH_COMPLETED_ASSIGNED_ACTIVITIES = "completedAssignedActivities";
	public static final String JSON_PATH_ASSIGNED_ROUTES = "assignedRoutes";
	public static final String JSON_PATH_CUSTOMERS_STATUS = "customersStatus";
	public static final String JSON_PATH_ROUTES = "routes";
	// MEDIA IDS THAT ARE SENT as MEDIA LOCATIONS
	public static final String JSON_MEDIA_IDS = "updatedMediaIds";

	/**
	 * Identifies whether an exception is a holiday or a special working day.
	 */
	public static final String JSON_EXCEPTION_TYPE = "expType";

	public static final int EXCEPTION_TYPE_HOLIDAY = 0;
	public static final int EXCEPTION_TYPE_SPECIAL_WORKING_DAY = 1;

	private List<Customer> addedCustomers = new ArrayList<Customer>();
	private List<Job> addedJobs = new ArrayList<Job>();
	private List<Long> deletedJobs = new ArrayList<Long>();
	private List<Long> deletedInvitations = new ArrayList<Long>();
	private List<Note> addedNotes = new ArrayList<Note>();
	private List<Leave> addedLeaves = new ArrayList<Leave>();
	private List<Long> deletedLeaves = new ArrayList<Long>();
	private List<Employee> addedEmployees = new ArrayList<Employee>();
	private List<Long> deletedEmployees = new ArrayList<Long>();
	private List<Long> employeesUnder = new ArrayList<Long>();
	private List<FormSpec> formSpecs = new ArrayList<FormSpec>();
	private List<VisibilityCriteria> visibilityDependencyCriterias = new ArrayList<VisibilityCriteria>();
	private List<ListFilteringCriteria> listFilteringCriterias = new ArrayList<ListFilteringCriteria>();
	private List<FieldSpec> fieldSpecs = new ArrayList<FieldSpec>();
	private List<FieldValueSpec> fieldValueSpecs = new ArrayList<FieldValueSpec>();
	private List<Form> addedForms = new ArrayList<Form>();
	private List<Long> deletedForms = new ArrayList<Long>();
	private List<Long> activeFormSpecIds = new ArrayList<Long>();
	private List<Long> inactiveFormSpecIds = new ArrayList<Long>();
	private List<Field> fields = new ArrayList<Field>();
	private List<WorkingHour> workingHours = new ArrayList<WorkingHour>();
	private List<Holiday> addedHolidays = new ArrayList<Holiday>();
	private List<SpecialWorkingDay> addedSpecialWorkingDays = new ArrayList<SpecialWorkingDay>();
	private List<Long> deletedExceptions = new ArrayList<Long>();
	private Boolean isNewCalendar;
	private boolean hasWorkingHours;
	private List<Invitation> addedInvitations = new ArrayList<Invitation>();
	private List<CustomerType> addedCustomerTypes = new ArrayList<CustomerType>();
	private List<JobType> addedTypes = new ArrayList<JobType>();
	private List<JobState> addedStates = new ArrayList<JobState>();
	private List<TypeStateMapping> addedTypeStateMappings = new ArrayList<TypeStateMapping>();
	private List<JobHistory> addedHistories = new ArrayList<JobHistory>();
	private List<JobStageStatus> addedStatuses = new ArrayList<JobStageStatus>();
	private List<SectionSpec> sectionSpecs = new ArrayList<SectionSpec>();
	private List<SectionFieldSpec> sectionFieldSpecs = new ArrayList<SectionFieldSpec>();
	private List<SectionFieldValueSpec> sectionFieldValueSpecs = new ArrayList<SectionFieldValueSpec>();
	private List<SectionField> sectionFields = new ArrayList<SectionField>();
	private List<EntitySpec> entitySpecs = new ArrayList<EntitySpec>();
	private List<EntityFieldSpec> entityFieldSpecs = new ArrayList<EntityFieldSpec>();
	private List<EntityFieldValueSpec> entityFieldValueSpecs = new ArrayList<EntityFieldValueSpec>();
	private List<Entity> addedEntities = new ArrayList<Entity>();
	private List<Long> deletedEntities = new ArrayList<Long>();
	private List<EntityField> entityFields = new ArrayList<EntityField>();
	private List<PageSpec> pageSpecs = new ArrayList<PageSpec>();
	private List<Article> addedArticles = new ArrayList<Article>();
	private List<Long> deletedArticles = new ArrayList<Long>();
	private List<Long> mappedArticlesList = new ArrayList<Long>();

	private List<NamedLocation> addedLocations = new ArrayList<NamedLocation>();
	private List<Message> addedMessages = new ArrayList<Message>();
	private List<Long> deletedLocations = new ArrayList<Long>();

	// ROUTE PLAN
	private List<ActivitySpec> activities = new ArrayList<ActivitySpec>();
	private List<CompletedActivity> completedActivities = new ArrayList<CompletedActivity>();
	private List<CustomerStatusDto> customerStatus = new ArrayList<CustomerStatusDto>();
	private List<AssignedRoute> assignedRoutes = new ArrayList<AssignedRoute>();
	private List<AssignedRoute> completedAssignedRoutes = new ArrayList<AssignedRoute>();

	// WORK FLOW
	private List<WorkFlowSpec> workFlowSpecs = new ArrayList<WorkFlowSpec>();
	private List<WorkFlowStatusDto> workFlowStatusDtos = new ArrayList<WorkFlowStatusDto>();
	private List<WorkFlowHistory> workFlowHistory = new ArrayList<WorkFlowHistory>();
	private List<WorkFlowStage> workFlowStages = new ArrayList<WorkFlowStage>();
	private List<WorkFlowFormSpecMapping> workFlowFormSpecMappings = new ArrayList<WorkFlowFormSpecMapping>();

	// MEDIA IDS
	private List<Long> mediaIds = new ArrayList<Long>();

	private Date remoteSyncTime;
	private Date localSyncTime;

	private Context applicationContext;
	private SettingsDao settingsDao;

	public SyncResponse(Context applicationContext) {
		this.applicationContext = applicationContext;
		settingsDao = SettingsDao.getInstance(applicationContext);
	}

	/**
	 * 
	 * @param jsonString
	 * @param applicationContext
	 *            Required for accessing resource strings, and creating DAO
	 *            objects.
	 * @return true if parsing succeeded
	 */
	public boolean parse(String jsonString) {
		try {
			JSONTokener tokener = new JSONTokener(jsonString);
			Object obj = tokener.nextValue();

			if (!(obj instanceof JSONObject)) {
				Log.e(TAG,
						"Invalid sync response. Expected a JSON object but did not get it.");
				return false;
			}

			JSONObject json = (JSONObject) obj;

			if (!json.has(JSON_REMOTE_SYNC_TIME)) {
				return false;
			}

			remoteSyncTime = XsdDateTimeUtils.getLocalTime(json
					.getString(JSON_REMOTE_SYNC_TIME));
			localSyncTime = new Date();

			// PROCESS APP VERSION
			if (!json.isNull(JSON_PATH_APP_VERSION)) {
				JSONObject appVersionJson = Utils.getJsonObject(json,
						JSON_PATH_APP_VERSION);

				if (appVersionJson != null) {
					AppVersion appVersion = AppVersion.parse(appVersionJson);

					settingsDao.saveSetting("appVersion",
							appVersion.getVersion());
					settingsDao.saveSetting("appUrl", appVersion.getUrl());
					settingsDao.saveSetting("appChangeLog",
							appVersion.getChangeLog());
				}
			} else {
				settingsDao.saveSetting("appVersion", "");
				settingsDao.saveSetting("appUrl", "");
				settingsDao.saveSetting("appChangeLog", "");
			}

			// PROCESS SETTINGS
			processSettings(json);

			// send empty string, instead of null
			// as we cannot store nulls in concurrent hash map
			settingsDao.saveSetting("employeeHomeLatitude",
					json.isNull("homeLat") ? "" : json.getString("homeLat"));
			settingsDao.saveSetting("employeeHomeLongitude",
					json.isNull("homeLong") ? "" : json.getString("homeLong"));

			// PROCESS CUSTOMERS
			if (json.has("customerTypes") && !json.isNull("customerTypes")) {
				addCustomerTypes(json.getJSONArray("customerTypes"),
						addedCustomerTypes, applicationContext);
			}

			// Note addedCustomers is not a JSON array. It is a JSON object,
			// where
			// key is localCustomerId and value is remoteCustomerId
			CustomersDao customersDao = CustomersDao
					.getInstance(applicationContext);

			JSONObject addedCustomersJson = json
					.getJSONObject(JSON_PATH_ADDED_CUSTOMERS);

			@SuppressWarnings("unchecked")
			Iterator<String> keysIterator = addedCustomersJson.keys();

			while (keysIterator.hasNext()) {
				String key = (String) keysIterator.next();
				long localCustomerId = Long.parseLong(key);
				long remoteCustomerId = addedCustomersJson.getLong(key);

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Saving customer id mapping local: "
							+ localCustomerId + " remote: " + remoteCustomerId);
				}

				customersDao.updateCustomerIdMapping(localCustomerId,
						remoteCustomerId);
			}

			// modifiedCustomers is a JSON array consisting of remote
			// customer ID (long) values.
			JSONArray modifiedCustomers = json
					.getJSONArray(JSON_PATH_MODIFIED_CUSTOMERS);

			for (int i = 0; i < modifiedCustomers.length(); ++i) {
				long remoteCustomerId = modifiedCustomers.getLong(i);
				customersDao.updateDirtyFlag(false, remoteCustomerId);
			}

			JSONArray mappedCustomers = json
					.getJSONArray(JSON_PATH_MAPPED_CUSTOMERS);

			ArrayList<Long> mappedCustomerList = new ArrayList<Long>();

			for (int i = 0; i < mappedCustomers.length(); ++i) {
				long remoteCustomerId = mappedCustomers.getLong(i);
				mappedCustomerList.add(remoteCustomerId);
			}

			if (mappedCustomerList.size() > 0) {
				settingsDao.saveSetting("mappedCustomers",
						TextUtils.join(",", mappedCustomerList));
			} else {
				settingsDao.saveSetting("mappedCustomers", "");
			}

			Utils.addCustomers(
					Utils.getJsonArray(json, JSON_PATH_INTERESTED_CUSTOMERS),
					addedCustomers, applicationContext, false);

			// PROCESS JOB TYPES
			addTypes(Utils.getJsonArray(json, JSON_PATH_JOB_TYPES), addedTypes,
					applicationContext);

			// PROCESS JOB STATES
			addStates(Utils.getJsonArray(json, JSON_PATH_JOB_STATES),
					addedStates, applicationContext);

			addTypeStateMappings(
					Utils.getJsonArray(json, JSON_PATH_TYPE_STATE_MAPPINGS),
					addedTypeStateMappings, applicationContext);

			// PROCESS JOBS
			Utils.addJobs(Utils.getJsonArray(json, JSON_PATH_ADDED_JOBS),
					addedJobs, addedCustomers, applicationContext);

			Utils.addJobs(Utils.getJsonArray(json, JSON_PATH_MODIFIED_JOBS),
					addedJobs, addedCustomers, applicationContext);

			addDeletedItems(Utils.getJsonArray(json, JSON_PATH_DELETED_JOBS),
					deletedJobs);

			Utils.addHistories(
					Utils.getJsonArray(json, JSON_PATH_JOB_HISTORIES),
					addedHistories, applicationContext);

			Utils.addStatuses(
					Utils.getJsonArray(json, JSON_PATH_JOB_STAGE_STATUSES),
					addedStatuses, applicationContext);

			// PROCESS INVITATIONS
			addInvitations(
					Utils.getJsonArray(json, JSON_PATH_ADDED_INVITATIONS),
					addedInvitations, applicationContext);

			addInvitations(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_INVITATIONS),
					addedInvitations, applicationContext);

			addDeletedInvitations(
					Utils.getJsonArray(json, JSON_PATH_DELETED_INVITATIONS),
					deletedInvitations);

			// PROCESS NOTES
			Utils.addNotes(Utils.getJsonArray(json, JSON_PATH_ADDED_NOTES),
					addedNotes, applicationContext);

			Utils.addNotes(Utils.getJsonArray(json, JSON_PATH_MODIFIED_NOTES),
					addedNotes, applicationContext);

			// PROCESS LEAVES
			addLeaves(Utils.getJsonArray(json, JSON_PATH_ADDED_LEAVES),
					addedLeaves);
			addLeaves(Utils.getJsonArray(json, JSON_PATH_MODIFIED_LEAVES),
					addedLeaves);
			addDeletedItems(Utils.getJsonArray(json, JSON_PATH_DELETED_LEAVES),
					deletedLeaves);
			// PROCESS EMPLOYEES

			Utils.addEmployees(
					Utils.getJsonArray(json, JSON_PATH_ADDED_EMPLOYEES),
					addedEmployees, applicationContext);
			Utils.addEmployees(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_EMPLOYEES),
					addedEmployees, applicationContext);
			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_DELETED_EMPLOYEES),
					deletedEmployees);
			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_EMPLOYEES_UNDER),
					employeesUnder);

			// PROCESS FORM SPECS
			addListFilteringCriterias(Utils.getJsonArray(json,
					JSON_PATH_LIST_FILTERING_CRITERIAS), listFilteringCriterias);
			addVisibilityDependencyCriterias(Utils.getJsonArray(json,
					JSON_PATH_VISIBILITY_DEPENDENCY_CRITERIAS),
					visibilityDependencyCriterias);
			addFormSpecs(Utils.getJsonArray(json, JSON_PATH_FORM_SPECS),
					formSpecs);
			addFieldSpecs(Utils.getJsonArray(json, JSON_PATH_FIELD_SPECS),
					fieldSpecs);
			addFieldValueSpecs(
					Utils.getJsonArray(json, JSON_PATH_FIELD_VALUE_SPECS),
					fieldValueSpecs);

			JSONObject visibilityMapJson = Utils.getJsonObject(json,
					JSON_PATH_FORM_SPECS_VISIBILITY_MAP);

			@SuppressWarnings("unchecked")
			Iterator<String> specIdsIterator = visibilityMapJson.keys();

			while (specIdsIterator.hasNext()) {
				String key = (String) specIdsIterator.next();
				long formSpecId = Long.parseLong(key);
				boolean active = visibilityMapJson.getBoolean(key);

				if (active) {
					activeFormSpecIds.add(formSpecId);
				} else {
					inactiveFormSpecIds.add(formSpecId);
				}
			}

			Utils.addForms(Utils.getJsonArray(json, JSON_PATH_ADDED_FORMS),
					addedForms, applicationContext);
			Utils.addForms(Utils.getJsonArray(json, JSON_PATH_MODIFIED_FORMS),
					addedForms, applicationContext);
			addDeletedItems(Utils.getJsonArray(json, JSON_PATH_DELETED_FORMS),
					deletedForms);

			Utils.addFields(Utils.getJsonArray(json, JSON_PATH_FIELDS), fields,
					applicationContext);

			addSectionSpecs(Utils.getJsonArray(json, JSON_PATH_SECTION_SPECS),
					sectionSpecs);
			addSectionFieldSpecs(
					Utils.getJsonArray(json, JSON_PATH_SECTION_FIELD_SPECS),
					sectionFieldSpecs);
			addSectionFieldValueSpecs(Utils.getJsonArray(json,
					JSON_PATH_SECTION_FIELD_VALUE_SPECS),
					sectionFieldValueSpecs);

			Utils.addSectionFields(
					Utils.getJsonArray(json, JSON_PATH_SECTION_FIELDS),
					sectionFields, applicationContext);

			addPageSpecs(Utils.getJsonArray(json, JSON_PATH_PAGE_SPECS),
					pageSpecs);

			addEntitySpecs(Utils.getJsonArray(json, JSON_PATH_ENTITY_SPECS),
					entitySpecs);
			addEntityFieldSpecs(
					Utils.getJsonArray(json, JSON_PATH_ENTITY_FIELD_SPECS),
					entityFieldSpecs);
			addEntityFieldValueSpecs(Utils.getJsonArray(json,
					JSON_PATH_ENTITY_FIELD_VALUE_SPECS), entityFieldValueSpecs);

			Utils.addEntities(
					Utils.getJsonArray(json, JSON_PATH_ADDED_ENTITIES),
					addedEntities, applicationContext);
			Utils.addEntities(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_ENTITIES),
					addedEntities, applicationContext);
			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_DELETED_ENTITIES),
					deletedEntities);

			Utils.addEntityFields(
					Utils.getJsonArray(json, JSON_PATH_ENTITY_FIELDS),
					entityFields, applicationContext);

			// TODO PROCESS WORK FLOWS
			Utils.addWorkFlows(Utils.getJsonArray(json, JSON_PATH_WORK_FLOWS),
					workFlowSpecs, applicationContext);

			Utils.addWorkFlowsFormSpecsMappings(Utils.getJsonArray(json,
					JSON_PATH_WORK_FLOW_FORM_SPECS_MAP),
					workFlowFormSpecMappings, applicationContext);

			Utils.addWorkFlowStages(
					Utils.getJsonArray(json, JSON_PATH_WORK_FLOW_STAGES),
					workFlowStages, applicationContext);

			Utils.addWorkFlowStatus(
					Utils.getJsonArray(json, JSON_PATH_WORK_FLOW_FORM_STATUS),
					workFlowStatusDtos, applicationContext);
			Utils.addWorkFlowsHistory(
					Utils.getJsonArray(json, JSON_PATH_WORK_FLOW_HISTORY),
					workFlowHistory, applicationContext);

			Utils.addArticles(
					Utils.getJsonArray(json, JSON_PATH_ADDED_ARTICLES),
					addedArticles, applicationContext);
			Utils.addArticles(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_ARTICLES),
					addedArticles, applicationContext);
			// addDeletedArticles(
			// Utils.getJsonArray(json, JSON_PATH_DELETED_ARTICLES),
			// deletedArticles);
			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_DELETED_ARTICLES),
					deletedArticles);

			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_MAPPED_ARTICLES),
					mappedArticlesList);

			// PROCESS WORKING HOURS
			JSONArray jsonArray = Utils.getJsonArray(json,
					JSON_PATH_WORKING_HOURS);

			hasWorkingHours = jsonArray != null;

			if (hasWorkingHours) {
				addWorkingHourDays(jsonArray, workingHours);
			}

			// PROCESS EXCEPTIONS
			isNewCalendar = Utils.getBoolean(
					Utils.getJsonObject(json, JSON_PATH_CALENDAR),
					JSON_NEW_CALENDAR);
			addExceptions(Utils.getJsonArray(json, JSON_PATH_ADDED_EXCEPTIONS),
					addedHolidays, addedSpecialWorkingDays);
			addExceptions(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_EXCEPTIONS),
					addedHolidays, addedSpecialWorkingDays);

			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_DELETED_EXCEPTIONS),
					deletedExceptions);

			// PROCESS NAMED LOCATIONS
			Utils.addLocations(
					Utils.getJsonArray(json, JSON_PATH_ADDED_LOCATIONS),
					addedLocations, applicationContext, false);
			Utils.addLocations(
					Utils.getJsonArray(json, JSON_PATH_MODIFIED_LOCATIONS),
					addedLocations, applicationContext, false);
			Utils.addLocations(
					Utils.getJsonArray(json, JSON_PATH_INTERESTED_LOCATIONS),
					addedLocations, applicationContext, false);

			addDeletedItems(
					Utils.getJsonArray(json, JSON_PATH_DELETED_LOCATIONS),
					deletedLocations);

			// PROCESS MESSAGES
			Utils.addMessages(Utils.getJsonArray(json, JSON_PATH_MESSAGES),
					addedMessages, applicationContext, false);
			JSONObject jsonRoutes = (JSONObject) Utils.getJson(json,
					JSON_PATH_ROUTES);

			// PROCESS ROUTE PATHS
			if (jsonRoutes != null) {
				Utils.addActivities(
						Utils.getJsonArray(jsonRoutes, JSON_PATH_ACTIVITIES),
						activities, applicationContext);
				Utils.addCompletedActivities(Utils.getJsonArray(jsonRoutes,
						JSON_PATH_COMPLETED_ACTIVITIES), completedActivities,
						applicationContext);
				Utils.addAssignedRoutes(Utils.getJsonArray(jsonRoutes,
						JSON_PATH_ASSIGNED_ROUTES), assignedRoutes,
						applicationContext);
				Utils.addCustomersStatus(Utils.getJsonArray(jsonRoutes,
						JSON_PATH_CUSTOMERS_STATUS), customerStatus,
						applicationContext);
				Utils.addCompletedAssignedRoutes(Utils.getJsonArray(jsonRoutes,
						JSON_PATH_COMPLETED_ASSIGNED_ACTIVITIES),
						completedAssignedRoutes, applicationContext);
			}
			// Process media ids
			addDeletedItems(Utils.getJsonArray(json, JSON_MEDIA_IDS), mediaIds);
			//
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON: " + e.toString(), e);
			return false;
		} catch (ParseException e) {
			Log.e(TAG, "Failed to parse note JSON: " + e.toString(), e);
			return false;
		}

		return true;
	}

	/**
	 * Adds the leaves found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addLeaves(JSONArray source, List<Leave> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of leaves: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Leave leave = Leave.parse(source.getJSONObject(i),
						applicationContext);

				if (leave != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Leave " + i + ": " + leave.toString());
					}
					destination.add(leave);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Leave " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the deleted items (leaves, exceptions, etc.) found in the given JSON
	 * array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addDeletedItems(JSONArray source, List<Long> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of deleted items: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				destination.add(source.getLong(i));
			}
		}
	}

	/**
	 * Adds the deleted items (leaves, exceptions, etc.) found in the given JSON
	 * array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addDeletedInvitations(JSONArray source, List<Long> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of deleted invitations: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject jsonObject = source.getJSONObject(i);
				destination.add(jsonObject.getLong(JSON_INVITATION_ID));
			}
		}
	}

	/**
	 * Adds the Visibility Dependency Criterias found in the given JSON array,
	 * to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	private void addVisibilityDependencyCriterias(JSONArray source,
			List<VisibilityCriteria> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of form specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				VisibilityCriteria visibilityCriteria = VisibilityCriteria
						.parse(source.getJSONObject(i), applicationContext);

				if (visibilityCriteria != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Visibility Dependency Criteria " + i + ": "
								+ visibilityCriteria.toString());
					}

					destination.add(visibilityCriteria);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Form Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the List Filtering Criteria found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	private void addListFilteringCriterias(JSONArray source,
			List<ListFilteringCriteria> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of form specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				ListFilteringCriteria listFilteringCriteria = ListFilteringCriteria
						.parse(source.getJSONObject(i), applicationContext);

				if (listFilteringCriteria != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "List Filtering Criteria " + i + ": "
								+ listFilteringCriteria.toString());
					}

					destination.add(listFilteringCriteria);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Form Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the form specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addFormSpecs(JSONArray source, List<FormSpec> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of form specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				FormSpec formSpec = FormSpec.parse(source.getJSONObject(i),
						applicationContext);

				if (formSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Form Spec " + i + ": " + formSpec.toString());
					}

					destination.add(formSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Form Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the field specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addFieldSpecs(JSONArray source, List<FieldSpec> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of field specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				FieldSpec fieldSpec = FieldSpec.parse(source.getJSONObject(i),
						applicationContext);

				if (fieldSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Field Spec " + i + ": " + fieldSpec.toString());
					}

					destination.add(fieldSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Field Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the field specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addFieldValueSpecs(JSONArray source,
			List<FieldValueSpec> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of field value specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				FieldValueSpec fieldValueSpec = FieldValueSpec.parse(
						source.getJSONObject(i), applicationContext);

				if (fieldValueSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Field value spec " + i + ": "
								+ fieldValueSpec.toString());
					}

					destination.add(fieldValueSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Field value spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the working hours found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addWorkingHourDays(JSONArray source,
			List<WorkingHour> destination) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of days: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				addWorkingHours(source.getJSONArray(i), destination);
			}
		}
	}

	/**
	 * Adds the working hours found in the given day's JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addWorkingHours(JSONArray source, List<WorkingHour> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of working hours: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkingHour workingHour = WorkingHour.parse(
						source.getJSONObject(i), applicationContext);

				if (workingHour != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Working hour " + i + ": "
										+ workingHour.toString());
					}

					destination.add(workingHour);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Working hour " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the exceptions (holidays and special working days) found in the
	 * given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param holidays
	 * @param specialWorkingDays
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addExceptions(JSONArray source, List<Holiday> holidays,
			List<SpecialWorkingDay> specialWorkingDays) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of exceptions: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject exception = source.getJSONObject(i);

				switch (exception.getInt(JSON_EXCEPTION_TYPE)) {
				case EXCEPTION_TYPE_HOLIDAY:
					Holiday holiday = Holiday.parse(exception,
							applicationContext);

					if (holiday != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Exception " + i + " (SWD): "
											+ holiday.toString());
						}

						holidays.add(holiday);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Exception " + i + " is null.");
						}
					}
					break;

				case EXCEPTION_TYPE_SPECIAL_WORKING_DAY:
					SpecialWorkingDay specialWorkingDay = SpecialWorkingDay
							.parse(exception, applicationContext);

					if (specialWorkingDay != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Exception " + i + " (SWD): "
									+ specialWorkingDay.toString());
						}

						specialWorkingDays.add(specialWorkingDay);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Exception " + i + " is null.");
						}
					}
					break;

				default:
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Unknown exception type: "
										+ exception.getInt(JSON_EXCEPTION_TYPE));
					}
				}
			}
		}
	}

	private void processSettings(JSONObject json) throws JSONException {
		if (json.isNull("settings")) {
			return;
		}

		JSONObject settingsJson = json.getJSONObject("settings");

		@SuppressWarnings("unchecked")
		Iterator<String> keysIterator = settingsJson.keys();

		String prevTrackingFrequency = settingsDao
				.getString(Settings.KEY_TRACKING_FREQUENCY);

		while (keysIterator.hasNext()) {
			String key = (String) keysIterator.next();

			if ("accessSettings".equals(key)) {
				continue;
			}

			String value = settingsJson.isNull(key) ? "" : settingsJson
					.getString(key);

			if (Settings.KEY_APP_LOGO_URL.equals(key)) {
				// update the app logo url, only if it is empty
				// so that it doesn't get overwritten, even before the download
				// finishes
				if (TextUtils.isEmpty(settingsDao
						.getString(Settings.KEY_APP_LOGO_URL))
						&& !TextUtils.isEmpty(value)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Saving setting: " + key + "=" + value);
					}

					settingsDao.saveSetting(key, value);
					Utils.startBftsIfRequired(applicationContext);
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Saving setting: " + key + "=" + value);
				}

				settingsDao.saveSetting(key, value);
			}

			if (Settings.KEY_TRACKING_FREQUENCY.equals(key)) {
				if (!TextUtils.equals(prevTrackingFrequency, value)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Restarting tracking alarm.");
					}

					EffortApplication.restartTrackingAlarm();
				}
			}
		}

		if (settingsJson.has("accessSettings")
				&& !settingsJson.isNull("accessSettings")) {
			processAccessSettings(settingsJson.getJSONObject("accessSettings"));
		}
	}

	/**
	 * Pass the accessSettings json node
	 * 
	 * @param json
	 * @throws JSONException
	 */
	private void processAccessSettings(JSONObject json) throws JSONException {
		@SuppressWarnings("unchecked")
		Iterator<String> keysIterator = json.keys();

		while (keysIterator.hasNext()) {
			String key = (String) keysIterator.next();
			String value = json.getString(key);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Saving access setting: " + key + "=" + value);
			}

			settingsDao.saveSetting(key, value);
		}
	}

	/**
	 * Adds the invitations found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param invitations
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addInvitations(JSONArray source,
			List<Invitation> invitations, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of invitations: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject invitationJson = source.getJSONObject(i);

				if (invitationJson != null) {
					Invitation invitation = Invitation.parse(invitationJson,
							applicationContext);

					if (invitation != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Invitation " + i + ": "
											+ invitation.toString());
						}

						invitations.add(invitation);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Invitation " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the customer types found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param types
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addCustomerTypes(JSONArray source,
			List<CustomerType> types, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of customer types: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject stateJson = source.getJSONObject(i);

				if (stateJson != null) {
					CustomerType type = CustomerType.parse(stateJson,
							applicationContext);

					if (type != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Customer type " + i + ": "
											+ type.toString());
						}

						types.add(type);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Customer type " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the job types found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param types
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addTypes(JSONArray source, List<JobType> types,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of job types: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject stateJson = source.getJSONObject(i);

				if (stateJson != null) {
					JobType type = JobType.parse(stateJson, applicationContext);

					if (type != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Type " + i + ": " + type.toString());
						}

						types.add(type);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Type " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the job states found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param states
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addStates(JSONArray source, List<JobState> states,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of job states: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject stateJson = source.getJSONObject(i);

				if (stateJson != null) {
					JobState state = JobState.parse(stateJson,
							applicationContext);

					if (state != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "State " + i + ": " + state.toString());
						}

						states.add(state);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "State " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the type state mappings found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param typeStateMappings
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addTypeStateMappings(JSONArray source,
			List<TypeStateMapping> typeStateMappings, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of type state mappings: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject mappingJson = source.getJSONObject(i);

				if (mappingJson != null) {
					TypeStateMapping mapping = TypeStateMapping.parse(
							mappingJson, applicationContext);

					if (mapping != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Mapping " + i + ": " + mapping.toString());
						}

						typeStateMappings.add(mapping);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Mapping " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the section specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addSectionSpecs(JSONArray source, List<SectionSpec> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of section specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				SectionSpec sectionSpec = SectionSpec.parse(
						source.getJSONObject(i), applicationContext);

				if (sectionSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Section Spec " + i + ": "
										+ sectionSpec.toString());
					}

					destination.add(sectionSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Section Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the section field specs found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addSectionFieldSpecs(JSONArray source,
			List<SectionFieldSpec> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of section field specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				SectionFieldSpec fieldSpec = SectionFieldSpec.parse(
						source.getJSONObject(i), applicationContext);

				if (fieldSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Section Field Spec " + i + ": "
										+ fieldSpec.toString());
					}

					destination.add(fieldSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Section Field Spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the section field value specs found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addSectionFieldValueSpecs(JSONArray source,
			List<SectionFieldValueSpec> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of section field value specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				SectionFieldValueSpec fieldValueSpec = SectionFieldValueSpec
						.parse(source.getJSONObject(i), applicationContext);

				if (fieldValueSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Section field value spec " + i + ": "
								+ fieldValueSpec.toString());
					}

					destination.add(fieldValueSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Section field value spec " + i
								+ " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the entity specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addEntitySpecs(JSONArray source, List<EntitySpec> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of entity specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				EntitySpec entitySpec = EntitySpec.parse(
						source.getJSONObject(i), applicationContext);

				if (entitySpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Entity spec " + i + ": "
										+ entitySpec.toString());
					}

					destination.add(entitySpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Entity spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the entity field specs found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addEntityFieldSpecs(JSONArray source,
			List<EntityFieldSpec> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of entity field specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				EntityFieldSpec fieldSpec = EntityFieldSpec.parse(
						source.getJSONObject(i), applicationContext);

				if (fieldSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Entity field spec " + i + ": "
										+ fieldSpec.toString());
					}

					destination.add(fieldSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Entity field spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the entity field value specs found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addEntityFieldValueSpecs(JSONArray source,
			List<EntityFieldValueSpec> destination) throws JSONException,
			ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of entity field value specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				EntityFieldValueSpec fieldValueSpec = EntityFieldValueSpec
						.parse(source.getJSONObject(i), applicationContext);

				if (fieldValueSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Entity field value spec " + i + ": "
								+ fieldValueSpec.toString());
					}

					destination.add(fieldValueSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Entity field value spec " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the page specs found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void addPageSpecs(JSONArray source, List<PageSpec> destination)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of page specs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				PageSpec pageSpec = PageSpec.parse(source.getJSONObject(i),
						applicationContext);

				if (pageSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Page spec " + i + ": " + pageSpec.toString());
					}

					destination.add(pageSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Page spec " + i + " is null.");
					}
				}
			}
		}
	}

	public List<Customer> getAddedCustomers() {
		return addedCustomers;
	}

	public List<CustomerType> getAddedCustomerTypes() {
		return addedCustomerTypes;
	}

	public List<JobType> getAddedTypes() {
		return addedTypes;
	}

	public List<JobState> getAddedStates() {
		return addedStates;
	}

	public List<TypeStateMapping> getAddedTypeStateMappings() {
		return addedTypeStateMappings;
	}

	public List<JobHistory> getAddedHistories() {
		return addedHistories;
	}

	public List<JobStageStatus> getAddedStatuses() {
		return addedStatuses;
	}

	public List<Job> getAddedJobs() {
		return addedJobs;
	}

	public List<Long> getDeletedJobs() {
		return deletedJobs;
	}

	public List<Invitation> getAddedInvitations() {
		return addedInvitations;
	}

	public List<Long> getDeletedInvitations() {
		return deletedInvitations;
	}

	public List<Note> getAddedNotes() {
		return addedNotes;
	}

	public List<Leave> getAddedLeaves() {
		return addedLeaves;
	}

	public List<Long> getDeletedLeaves() {
		return deletedLeaves;
	}

	public List<Employee> getAddedEmployees() {
		return addedEmployees;
	}

	public void setAddedEmployees(List<Employee> addedEmployees) {
		this.addedEmployees = addedEmployees;
	}

	public List<Long> getDeletedEmployees() {
		return deletedEmployees;
	}

	public void setDeletedEmployees(List<Long> deletedEmployees) {
		this.deletedEmployees = deletedEmployees;
	}

	public List<Long> getEmployeesUnder() {
		return employeesUnder;
	}

	public void setEmployeesUnder(List<Long> employeesUnder) {
		this.employeesUnder = employeesUnder;
	}

	public List<FormSpec> getFormSpecs() {
		return formSpecs;
	}

	public List<FieldSpec> getFieldSpecs() {
		return fieldSpecs;
	}

	public List<FieldValueSpec> getFieldValueSpecs() {
		return fieldValueSpecs;
	}

	public List<Form> getAddedForms() {
		return addedForms;
	}

	public List<Long> getDeletedForms() {
		return deletedForms;
	}

	public List<Field> getFields() {
		return fields;
	}

	public List<SectionSpec> getSectionSpecs() {
		return sectionSpecs;
	}

	public List<SectionFieldSpec> getSectionFieldSpecs() {
		return sectionFieldSpecs;
	}

	public List<SectionFieldValueSpec> getSectionFieldValueSpecs() {
		return sectionFieldValueSpecs;
	}

	public List<SectionField> getSectionFields() {
		return sectionFields;
	}

	public List<PageSpec> getPageSpecs() {
		return pageSpecs;
	}

	public List<EntitySpec> getEntitySpecs() {
		return entitySpecs;
	}

	public List<EntityFieldSpec> getEntityFieldSpecs() {
		return entityFieldSpecs;
	}

	public List<EntityFieldValueSpec> getEntityFieldValueSpecs() {
		return entityFieldValueSpecs;
	}

	public List<Entity> getAddedEntities() {
		return addedEntities;
	}

	public List<Long> getDeletedEntities() {
		return deletedEntities;
	}

	public List<EntityField> getEntityFields() {
		return entityFields;
	}

	public List<WorkingHour> getWorkingHours() {
		return workingHours;
	}

	public List<Holiday> getAddedHolidays() {
		return addedHolidays;
	}

	public List<SpecialWorkingDay> getAddedSpecialWorkingDays() {
		return addedSpecialWorkingDays;
	}

	public Date getRemoteSyncTime() {
		return remoteSyncTime;
	}

	public Date getLocalSyncTime() {
		return localSyncTime;
	}

	public Boolean isNewCalendar() {
		return isNewCalendar;
	}

	public boolean hasWorkingHours() {
		return hasWorkingHours;
	}

	public List<Long> getDeletedExceptions() {
		return deletedExceptions;
	}

	public List<Long> getActiveFormSpecIds() {
		return activeFormSpecIds;
	}

	public List<Long> getInactiveFormSpecIds() {
		return inactiveFormSpecIds;
	}

	public List<Article> getAddedArticles() {
		return addedArticles;
	}

	public List<NamedLocation> getAddedLocations() {
		return addedLocations;
	}

	public List<Message> getAddedMessages() {
		return addedMessages;
	}

	public List<Long> getDeletedLocations() {
		return deletedLocations;
	}

	public List<VisibilityCriteria> getVisibilityDependencyCriterias() {
		return visibilityDependencyCriterias;
	}

	public void setVisibilityDependencyCriterias(
			List<VisibilityCriteria> visibilityDependencyCriterias) {
		this.visibilityDependencyCriterias = visibilityDependencyCriterias;
	}

	public List<ListFilteringCriteria> getListFilteringCriterias() {
		return listFilteringCriterias;
	}

	public void setListFilteringCriterias(
			List<ListFilteringCriteria> listFilteringCriterias) {
		this.listFilteringCriterias = listFilteringCriterias;
	}

	public List<ActivitySpec> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivitySpec> activities) {
		this.activities = activities;
	}

	public List<CompletedActivity> getCompletedActivities() {
		return completedActivities;
	}

	public void setCompletedActivities(
			List<CompletedActivity> completedActivities) {
		this.completedActivities = completedActivities;
	}

	public List<CustomerStatusDto> getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(List<CustomerStatusDto> customerStatus) {
		this.customerStatus = customerStatus;
	}

	public List<AssignedRoute> getAssignedRoutes() {
		return assignedRoutes;
	}

	public void setAssignedRoutes(List<AssignedRoute> assignedRoutes) {
		this.assignedRoutes = assignedRoutes;
	}

	public List<AssignedRoute> getCompletedAssignedRoutes() {
		return completedAssignedRoutes;
	}

	public void setCompletedAssignedRoutes(
			List<AssignedRoute> completedAssignedRoutes) {
		this.completedAssignedRoutes = completedAssignedRoutes;
	}

	public List<WorkFlowSpec> getWorkFlowSpecs() {
		return workFlowSpecs;
	}

	public void setWorkFlowSpecs(List<WorkFlowSpec> workFlowSpecs) {
		this.workFlowSpecs = workFlowSpecs;
	}

	public List<WorkFlowStatusDto> getWorkFlowStatusDtos() {
		return workFlowStatusDtos;
	}

	public void setWorkFlowStatusDtos(List<WorkFlowStatusDto> workFlowStatusDtos) {
		this.workFlowStatusDtos = workFlowStatusDtos;
	}

	public List<WorkFlowHistory> getWorkFlowHistory() {
		return workFlowHistory;
	}

	public void setWorkFlowHistory(List<WorkFlowHistory> workFlowHistory) {
		this.workFlowHistory = workFlowHistory;
	}

	public List<WorkFlowStage> getWorkFlowStages() {
		return workFlowStages;
	}

	public void setWorkFlowStages(List<WorkFlowStage> workFlowStages) {
		this.workFlowStages = workFlowStages;
	}

	public List<WorkFlowFormSpecMapping> getWorkFlowFormSpecMappings() {
		return workFlowFormSpecMappings;
	}

	public void setWorkFlowFormSpecMappings(
			List<WorkFlowFormSpecMapping> workFlowFormSpecMappings) {
		this.workFlowFormSpecMappings = workFlowFormSpecMappings;
	}

	public List<Long> getDeletedArticles() {
		return deletedArticles;
	}

	public void setDeletedArticles(List<Long> deletedArticles) {
		this.deletedArticles = deletedArticles;
	}

	public List<Long> getMediaIds() {
		return mediaIds;
	}

	public void setMediaIds(List<Long> mediaIds) {
		this.mediaIds = mediaIds;
	}

	public List<Long> getMappedArticlesList() {
		return mappedArticlesList;
	}

	public void setMappedArticlesList(List<Long> mappedArticlesList) {
		this.mappedArticlesList = mappedArticlesList;
	}
}
