package in.spoors.effort1;

import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.CompletedActivitiesDao;
import in.spoors.effort1.dao.CustomerStatusDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.FieldsDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.InvitationsDao;
import in.spoors.effort1.dao.JobHistoriesDao;
import in.spoors.effort1.dao.JobStageStatusesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.LeavesDao;
import in.spoors.effort1.dao.NamedLocationsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.SectionFieldsDao;
import in.spoors.effort1.dao.SectionFilesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.dto.CompletedActivity;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.dto.Field;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.JobHistory;
import in.spoors.effort1.dto.JobStageStatus;
import in.spoors.effort1.dto.Leave;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.provider.EffortProvider;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Sync request acts as a container for all the data that needs to be synced
 * with the server, and composes and returns the JSON.
 * 
 * @author tiru
 * 
 */
public class SyncRequest {

	public static final String TAG = "SyncRequest";
	private Context applicationContext;
	private CustomersDao customersDao;
	private JobsDao jobsDao;
	private JobStageStatusesDao jobStageStatusesDao;
	private NotesDao notesDao;
	private JobHistoriesDao historiesDao;
	private LeavesDao leavesDao;
	private FormSpecsDao formSpecsDao;
	private FormsDao formsDao;
	private FieldsDao fieldsDao;
	private SectionFieldsDao sectionFieldsDao;
	private SettingsDao settingsDao;
	private InvitationsDao invitationsDao;
	private EntitiesDao entitiesDao;
	private NamedLocationsDao namedLocationsDao;

	private FormFilesDao formFilesDao;
	private SectionFilesDao sectionFilesDao;
	// private LocationsDao locationsDao;
	// Routes DAO
	private CompletedActivitiesDao completedActivitiesDao;
	private CustomerStatusDao customerStatusDao;
	private AssignedRoutesDao assignedRoutesDao;

	// WORKFLOWS DAO
	private WorkFlowStatusDao workFlowStatusDao;
	private ArticlesDao articlesDao;

	private List<Long> interestedCustomerIds;
	private List<Long> formSpecIds;
	private List<Customer> addedCustomers;
	private List<Customer> modifiedCustomers;
	private List<Job> addedJobs;
	private List<Job> modifiedJobs;
	private List<JobStageStatus> jobStageStatuses;
	private List<Note> addedNotes;
	private List<Note> modifiedNotes;
	private List<JobHistory> addedHistories;
	private List<Leave> addedLeaves;
	private List<Leave> modifiedLeaves;
	private List<Long> cancelledLeaves;
	private List<Form> addedForms;
	private List<Form> modifiedForms;
	private List<Field> addedFields;
	private List<SectionField> addedSectionFields;
	private List<Long> acceptedInvitations;
	private List<Long> interestedEntityIds;
	private List<NamedLocation> addedLocations;
	private List<NamedLocation> modifiedLocations;
	private List<Long> interestedLocationIds;
	private List<Long> interestedArticleIds;

	// Routes
	private List<CompletedActivity> completedActivities;
	private List<CustomerStatusDto> customersStatus;
	private List<AssignedRoute> completedAssignedRoutes;

	// WORK FLOW
	private List<WorkFlowStatusDto> workFlowStatusDtos;
	private List<LocationDto> mediaLocations;

	public SyncRequest(Context applicationContext) {
		this.applicationContext = applicationContext;
		customersDao = CustomersDao.getInstance(applicationContext);
		jobsDao = JobsDao.getInstance(applicationContext);
		jobStageStatusesDao = JobStageStatusesDao
				.getInstance(applicationContext);
		notesDao = NotesDao.getInstance(applicationContext);
		historiesDao = JobHistoriesDao.getInstance(applicationContext);
		leavesDao = LeavesDao.getInstance(applicationContext);
		formSpecsDao = FormSpecsDao.getInstance(applicationContext);
		formsDao = FormsDao.getInstance(applicationContext);
		fieldsDao = FieldsDao.getInstance(applicationContext);
		sectionFieldsDao = SectionFieldsDao.getInstance(applicationContext);
		settingsDao = SettingsDao.getInstance(applicationContext);
		invitationsDao = InvitationsDao.getInstance(applicationContext);
		entitiesDao = EntitiesDao.getInstance(applicationContext);
		namedLocationsDao = NamedLocationsDao.getInstance(applicationContext);

		// ROUTES
		completedActivitiesDao = CompletedActivitiesDao
				.getInstance(applicationContext);
		customerStatusDao = CustomerStatusDao.getInstance(applicationContext);
		assignedRoutesDao = AssignedRoutesDao.getInstance(applicationContext);
		// WORK FLOW
		workFlowStatusDao = WorkFlowStatusDao.getInstance(applicationContext);
		articlesDao = ArticlesDao.getInstance(applicationContext);
		formFilesDao = FormFilesDao.getInstance(applicationContext);
		sectionFilesDao = SectionFilesDao.getInstance(applicationContext);
		interestedCustomerIds = customersDao.getAllRemoteCustomerIds();
		formSpecIds = formSpecsDao.getAllFormSpecIds();
		addedCustomers = customersDao.getAddedCustomers();
		modifiedCustomers = customersDao.getModifiedCustomers();
		addedJobs = jobsDao.getAddedJobs();
		modifiedJobs = jobsDao.getModifiedJobs();
		jobStageStatuses = jobStageStatusesDao.getStatusesToBeSynced();
		addedNotes = notesDao.getAddedNotes();
		addedHistories = historiesDao.getAddedJobHistories();
		modifiedNotes = notesDao.getModifiedNotes();
		addedLeaves = leavesDao.getAddedLeaves();
		modifiedLeaves = leavesDao.getModifiedLeaves();
		cancelledLeaves = leavesDao.getCancelledLeaves();
		addedForms = formsDao.getAddedForms();
		modifiedForms = formsDao.getModifiedForms();
		acceptedInvitations = invitationsDao.getAcceptedInvitations();
		interestedEntityIds = entitiesDao.getAllRemoteEntityIds();
		addedLocations = namedLocationsDao.getAddedLocations();
		modifiedLocations = namedLocationsDao.getModifiedLocations();
		interestedLocationIds = namedLocationsDao.getAllRemoteLocationIds();
		interestedArticleIds = articlesDao.getAllArticleIds();
		completedActivities = completedActivitiesDao
				.getAddedUnSyncedCompletedActivivties();

		customersStatus = customerStatusDao.getUnsyncedCustomerStatus();
		completedAssignedRoutes = assignedRoutesDao.getUnsyncedAssignedRoutes();
		// workFlowStatusDtos = workFlowStatusDao.getUnSyncedWorkFlowStatus();
		workFlowStatusDtos = new ArrayList<WorkFlowStatusDto>();
		mediaLocations = new ArrayList<LocationDto>();

		List<WorkFlowStatusDto> addedWorkFlowStatusDtos = workFlowStatusDao
				.getAddedUnSyncedWorkFlowStatus();
		List<WorkFlowStatusDto> modifiedWorkFlowStatusDtos = workFlowStatusDao
				.getModifiedUnSyncedWorkFlowStatus();
		List<WorkFlowStatusDto> pendingWorkFlowStatusDtos = workFlowStatusDao
				.getPendingUnSyncedWorkFlowStatus();

		System.out.println("formFilesDao " + formFilesDao);
		if (BuildConfig.DEBUG) {
			Log.v("error", "error is " + formFilesDao);
		}
		List<LocationDto> locationsForFormFiles = formFilesDao
				.getLocationsForFormFiles();
		List<LocationDto> locationsForSectionFiles = sectionFilesDao
				.getLocationsForSectionFiles();

		if (locationsForFormFiles != null) {
			mediaLocations.addAll(locationsForFormFiles);
		}
		if (locationsForSectionFiles != null) {
			mediaLocations.addAll(locationsForSectionFiles);
		}

		if (addedWorkFlowStatusDtos != null) {
			workFlowStatusDtos.addAll(addedWorkFlowStatusDtos);
		}
		if (modifiedWorkFlowStatusDtos != null) {
			workFlowStatusDtos.addAll(modifiedWorkFlowStatusDtos);
		}
		if (pendingWorkFlowStatusDtos != null) {
			workFlowStatusDtos.addAll(pendingWorkFlowStatusDtos);
		}

		if (addedForms != null) {
			for (Form form : addedForms) {
				List<Field> fields = fieldsDao.getFields(form.getLocalId());

				if (fields != null) {
					if (addedFields == null) {
						addedFields = new ArrayList<Field>();
					}

					addedFields.addAll(fields);
				}

				List<SectionField> sectionFields = sectionFieldsDao
						.getFields(form.getLocalId());

				if (sectionFields != null) {
					if (addedSectionFields == null) {
						addedSectionFields = new ArrayList<SectionField>();
					}

					addedSectionFields.addAll(sectionFields);
				}
			}
		}

		if (modifiedForms != null) {
			for (Form form : modifiedForms) {
				List<Field> fields = fieldsDao.getFields(form.getLocalId());

				if (fields != null) {
					if (addedFields == null) {
						addedFields = new ArrayList<Field>();
					}

					addedFields.addAll(fields);
				}

				List<SectionField> sectionFields = sectionFieldsDao
						.getFields(form.getLocalId());

				if (sectionFields != null) {
					if (addedSectionFields == null) {
						addedSectionFields = new ArrayList<SectionField>();
					}

					addedSectionFields.addAll(sectionFields);
				}
			}
		}
	}

	public String getJsonString() {
		try {
			JSONObject syncRequest = new JSONObject();

			if (settingsDao.getBoolean("employeeHomeLocationDirty", false)) {
				syncRequest.put("homeLat",
						settingsDao.getString("employeeHomeLatitude"));
				syncRequest.put("homeLong",
						settingsDao.getString("employeeHomeLongitude"));
			}

			syncRequest.put("interestedCustomer",
					getLongsJsonArray(interestedCustomerIds));

			// TODO INTERESTED ARTICLES
			JSONObject interestedArticles = new JSONObject();
			interestedArticles.put("interestedArticles",
					getLongsJsonArray(interestedArticleIds));
			syncRequest.put("articles", interestedArticles);

			JSONObject customers = new JSONObject();
			customers.put("added", getCustomersJsonArray(addedCustomers));
			customers.put("modified", getCustomersJsonArray(modifiedCustomers));
			syncRequest.put("customers", customers);

			JSONObject jobs = new JSONObject();
			jobs.put("added", getJobsJsonArray(addedJobs));
			jobs.put("modified", getJobsJsonArray(modifiedJobs));
			jobs.put("histories", getHistoriesJsonArray(addedHistories));
			jobs.put("visitStates", getStatusesJsonArray(jobStageStatuses));

			JSONObject notes = new JSONObject();
			notes.put("added", getNotesJsonArray(addedNotes));
			notes.put("modified", getNotesJsonArray(modifiedNotes));

			jobs.put("comments", notes);

			syncRequest.put("visits", jobs);

			JSONObject leaves = new JSONObject();
			leaves.put("added", getLeavesJsonArray(addedLeaves));
			leaves.put("modified", getLeavesJsonArray(modifiedLeaves));
			leaves.put("deleted", getLongsJsonArray(cancelledLeaves));
			syncRequest.put("leaves", leaves);

			JSONObject forms = new JSONObject();
			JSONObject formsData = new JSONObject();
			formsData.put("added", getFormsJsonArray(addedForms));
			formsData.put("modified", getFormsJsonArray(modifiedForms));
			formsData.put("fields", getFieldsJsonArray(addedFields));
			formsData.put("sectionFields",
					getSectionFieldsJsonArray(addedSectionFields));

			// WORK FLOW
			JSONObject workFlowsStatus = new JSONObject();
			workFlowsStatus.put("workflowFormStatus",
					getWorkFlowStatusJsonArray(workFlowStatusDtos));
			JSONObject workFlowsData = new JSONObject();
			workFlowsData.put("data", workFlowsStatus);

			forms.put("data", formsData);
			forms.put("formSpecIds", getLongsJsonArray(formSpecIds));
			syncRequest.put("forms", forms);
			syncRequest.put("workflow", workFlowsData);

			JSONObject invitations = new JSONObject();
			invitations.put("accepted", getLongsJsonArray(acceptedInvitations));
			syncRequest.put("visitInvitations", invitations);

			JSONObject entities = new JSONObject();
			JSONObject entitiesData = new JSONObject();
			entitiesData.put("interestedEntities",
					getLongsJsonArray(interestedEntityIds));
			entities.put("data", entitiesData);
			syncRequest.put("entities", entities);

			JSONObject locations = new JSONObject();
			locations.put("added", getLocationsJsonArray(addedLocations));
			locations.put("modified", getLocationsJsonArray(modifiedLocations));
			syncRequest.put("namedLocations", locations);
			syncRequest.put("interestedNamedLocations",
					getLongsJsonArray(interestedLocationIds));

			// Routes json

			JSONObject routesJson = new JSONObject();
			routesJson.put("completedActivities",
					getCompletedActivitiesJsonArray(completedActivities));
			routesJson.put("customersStatus",
					getCustomersStatusJsonArray(customersStatus));
			routesJson
					.put("completedAssignedActivities",
							getCompletedAssignedRoutesJsonArray(completedAssignedRoutes));
			syncRequest.put("routes", routesJson);

			// Media locations json
			syncRequest.put("mediaLocations",
					getMediaLocationsJsonArray(mediaLocations));

			return syncRequest.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	private Object getWorkFlowStatusJsonArray(
			List<WorkFlowStatusDto> workFlowStatus) {
		JSONArray jsonArray = new JSONArray();

		if (workFlowStatus != null) {
			for (WorkFlowStatusDto wfStatus : workFlowStatus) {
				jsonArray.put(wfStatus.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getCompletedAssignedRoutesJsonArray(
			List<AssignedRoute> cmpAsgRoutes) {

		JSONArray jsonArray = new JSONArray();

		if (cmpAsgRoutes != null) {
			for (AssignedRoute cmpAsgndRoute : cmpAsgRoutes) {
				jsonArray.put(cmpAsgndRoute.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getMediaLocationsJsonArray(List<LocationDto> locationDtos)
			throws JSONException {

		JSONArray jsonArray = new JSONArray();

		if (locationDtos != null) {
			for (LocationDto location : locationDtos) {

				if (location != null) {
					JSONObject mediaLocation = new JSONObject();
					JSONObject locationObject = location.getJsonObject(
							applicationContext,
							EffortProvider.Locations.PURPOSE_FORM_FILE);

					Long mediaId = null;
					if (location.getPurpose() == EffortProvider.Locations.PURPOSE_FORM_FILE) {
						mediaId = formFilesDao.getMediaId(location.getForId());
					} else if (location.getPurpose() == EffortProvider.Locations.PURPOSE_SECTION_FILE) {
						mediaId = sectionFilesDao.getMediaId(location
								.getForId());
					}

					mediaLocation.put("mediaId", mediaId);
					mediaLocation.put("location", locationObject);
					jsonArray.put(mediaLocation);
				}
			}
		}

		return jsonArray;
	}

	private JSONArray getCustomersStatusJsonArray(
			List<CustomerStatusDto> customerStatus) {

		JSONArray jsonArray = new JSONArray();

		if (customerStatus != null) {
			for (CustomerStatusDto customerStatusDto : customerStatus) {
				jsonArray.put(customerStatusDto
						.getJsonObject(applicationContext));
			}
		}

		return jsonArray;

	}

	private JSONArray getCompletedActivitiesJsonArray(
			List<CompletedActivity> cmpActivities) {

		JSONArray jsonArray = new JSONArray();

		if (cmpActivities != null) {
			for (CompletedActivity completedActivity : cmpActivities) {
				jsonArray.put(completedActivity
						.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getCustomersJsonArray(List<Customer> customers) {
		JSONArray jsonArray = new JSONArray();

		if (customers != null) {
			for (Customer customer : customers) {
				jsonArray.put(customer.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getLongsJsonArray(List<Long> longs) {
		JSONArray jsonArray = new JSONArray();

		if (longs != null) {
			for (Long longValue : longs) {
				jsonArray.put(longValue);
			}
		}

		return jsonArray;
	}

	private JSONArray getJobsJsonArray(List<Job> jobs) {
		JSONArray jsonArray = new JSONArray();

		if (jobs != null) {
			for (Job job : jobs) {
				jsonArray.put(job.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getNotesJsonArray(List<Note> notes) {
		JSONArray jsonArray = new JSONArray();

		if (notes != null) {
			for (Note note : notes) {
				jsonArray.put(note.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getHistoriesJsonArray(List<JobHistory> histories) {
		JSONArray jsonArray = new JSONArray();

		if (histories != null) {
			for (JobHistory history : histories) {
				jsonArray.put(history.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getStatusesJsonArray(List<JobStageStatus> statuses) {
		JSONArray jsonArray = new JSONArray();

		if (statuses != null) {
			for (JobStageStatus status : statuses) {
				jsonArray.put(status.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getLeavesJsonArray(List<Leave> leaves) {
		JSONArray jsonArray = new JSONArray();

		if (leaves != null) {
			for (Leave leave : leaves) {
				jsonArray.put(leave.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getFormsJsonArray(List<Form> forms) {
		JSONArray jsonArray = new JSONArray();

		if (forms != null) {
			for (Form form : forms) {
				jsonArray.put(form.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	private JSONArray getFieldsJsonArray(List<Field> fields) {
		JSONArray jsonArray = new JSONArray();

		if (fields != null) {
			for (Field field : fields) {
				JSONObject jsonObject = field.getJsonObject(applicationContext);

				if (jsonObject != null) {
					jsonArray.put(jsonObject);
				}
			}
		}

		return jsonArray;
	}

	private JSONArray getSectionFieldsJsonArray(List<SectionField> fields) {
		JSONArray jsonArray = new JSONArray();

		if (fields != null) {
			for (SectionField field : fields) {
				JSONObject jsonObject = field.getJsonObject(applicationContext);

				if (jsonObject != null) {
					jsonArray.put(jsonObject);
				}
			}
		}

		return jsonArray;
	}

	private JSONArray getLocationsJsonArray(List<NamedLocation> locations) {
		JSONArray jsonArray = new JSONArray();

		if (locations != null) {
			for (NamedLocation location : locations) {
				jsonArray.put(location.getJsonObject(applicationContext));
			}
		}

		return jsonArray;
	}

	public List<Long> getInterestedCustomerIds() {
		return interestedCustomerIds;
	}

	public List<Customer> getAddedCustomers() {
		return addedCustomers;
	}

	public List<Customer> getModifiedCustomers() {
		return modifiedCustomers;
	}

	public List<Job> getAddedJobs() {
		return addedJobs;
	}

	public List<Job> getModifiedJobs() {
		return modifiedJobs;
	}

	public List<Note> getAddedNotes() {
		return addedNotes;
	}

	public List<Note> getModifiedNotes() {
		return modifiedNotes;
	}

	public List<JobHistory> getAddedHistories() {
		return addedHistories;
	}

	public List<JobStageStatus> getAddedStatuses() {
		return jobStageStatuses;
	}

	public List<Leave> getAddedLeaves() {
		return addedLeaves;
	}

	public List<Leave> getModifiedLeaves() {
		return modifiedLeaves;
	}

	public List<Long> getCancelledLeaves() {
		return cancelledLeaves;
	}

	public List<Form> getAddedForms() {
		return addedForms;
	}

	public List<Form> getModifiedForms() {
		return modifiedForms;
	}

	public List<Field> getAddedFields() {
		return addedFields;
	}

	public List<SectionField> getAddedSectionFields() {
		return addedSectionFields;
	}

	public List<Long> getInterestedLocationIds() {
		return interestedLocationIds;
	}

	public List<NamedLocation> getAddedLocations() {
		return addedLocations;
	}

	public List<NamedLocation> getModifiedLocations() {
		return modifiedLocations;
	}

	public List<LocationDto> getMediaLocations() {
		return mediaLocations;
	}

	public void setMediaLocations(List<LocationDto> mediaLocations) {
		this.mediaLocations = mediaLocations;
	}
}
