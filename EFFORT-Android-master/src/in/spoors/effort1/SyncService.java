package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.ActivitySpecsDao;
import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.CompletedActivitiesDao;
import in.spoors.effort1.dao.CustomerStatusDao;
import in.spoors.effort1.dao.CustomerTypesDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldSpecsDao;
import in.spoors.effort1.dao.EntityFieldValueSpecsDao;
import in.spoors.effort1.dao.EntityFieldsDao;
import in.spoors.effort1.dao.EntitySpecsDao;
import in.spoors.effort1.dao.FieldSpecsDao;
import in.spoors.effort1.dao.FieldValueSpecsDao;
import in.spoors.effort1.dao.FieldsDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.HolidaysDao;
import in.spoors.effort1.dao.InvitationsDao;
import in.spoors.effort1.dao.JobHistoriesDao;
import in.spoors.effort1.dao.JobStageStatusesDao;
import in.spoors.effort1.dao.JobStatesDao;
import in.spoors.effort1.dao.JobTypesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.LeavesDao;
import in.spoors.effort1.dao.ListFilteringCriteriaDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.MessagesDao;
import in.spoors.effort1.dao.NamedLocationsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.PageSpecsDao;
import in.spoors.effort1.dao.SectionFieldSpecsDao;
import in.spoors.effort1.dao.SectionFieldValueSpecsDao;
import in.spoors.effort1.dao.SectionFieldsDao;
import in.spoors.effort1.dao.SectionFilesDao;
import in.spoors.effort1.dao.SectionSpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.SpecialWorkingDaysDao;
import in.spoors.effort1.dao.TypeStateMappingsDao;
import in.spoors.effort1.dao.VisibilityCriteriaDao;
import in.spoors.effort1.dao.WorkFlowFormSpecMappingsDao;
import in.spoors.effort1.dao.WorkFlowHistoriesDao;
import in.spoors.effort1.dao.WorkFlowSpecsDao;
import in.spoors.effort1.dao.WorkFlowStagesDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dao.WorkingHoursDao;
import in.spoors.effort1.dto.ActivitySpec;
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
import in.spoors.effort1.dto.FormFile;
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
import in.spoors.effort1.dto.SectionFile;
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
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SyncService extends WakefulIntentService {

	private static final String SYNCED_SUCCESSFULLY = "Synced successfully.";
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	private static final String SENDER_ID = "207825128165";

	public static final String TAG = "SyncService";

	public static boolean syncEnabled = true;

	/**
	 * Broadcasts this action when sync is done.
	 */
	public static final String ACTION_SYNC_DONE = "in.spoors.effort1.SYNC_DONE";

	private SettingsDao settingsDao;
	private CustomersDao customersDao;
	private CustomerTypesDao customerTypesDao;
	private JobTypesDao jobTypesDao;
	private JobStatesDao jobStatesDao;
	private TypeStateMappingsDao typeStateMappingsDao;
	private JobsDao jobsDao;
	private JobHistoriesDao jobHistoriesDao;
	private JobStageStatusesDao jobStageStatusesDao;
	private InvitationsDao invitationsDao;
	private NotesDao notesDao;
	private LocationsDao locationsDao;
	private LeavesDao leavesDao;
	private EmployeesDao employeesDao;
	private VisibilityCriteriaDao visibilityCriteriaDao;
	private ListFilteringCriteriaDao listFilteringCriteriaDao;
	private FormSpecsDao formSpecsDao;
	private FieldSpecsDao fieldSpecsDao;
	private FieldValueSpecsDao fieldValueSpecsDao;
	private FormsDao formsDao;
	private FieldsDao fieldsDao;
	private SectionSpecsDao sectionSpecsDao;
	private SectionFieldSpecsDao sectionFieldSpecsDao;
	private SectionFieldValueSpecsDao sectionFieldValueSpecsDao;
	private SectionFieldsDao sectionFieldsDao;
	private PageSpecsDao pageSpecsDao;
	private EntitySpecsDao entitySpecsDao;
	private EntityFieldSpecsDao entityFieldSpecsDao;
	private EntityFieldValueSpecsDao entityFieldValueSpecsDao;
	private EntitiesDao entitiesDao;
	private EntityFieldsDao entityFieldsDao;
	private ArticlesDao articlesDao;
	private WorkingHoursDao workingHoursDao;
	private HolidaysDao holidaysDao;
	private SpecialWorkingDaysDao swdDao;
	private NamedLocationsDao namedLocationsDao;
	private MessagesDao messagesDao;
	private FormFilesDao formFilesDao;
	private SectionFilesDao sectionFilesDao;

	// ROUTE PLAN
	private ActivitySpecsDao activitiesDao;
	private AssignedRoutesDao assignedRoutesDao;
	private CompletedActivitiesDao completedActivitiesDao;
	private CustomerStatusDao customerStatusDao;

	// WORKFLOW
	private WorkFlowSpecsDao workFlowSpecsDao;
	private WorkFlowStagesDao workFlowStagesDao;
	private WorkFlowStatusDao workFlowStatusDao;
	private WorkFlowHistoriesDao workFlowHistoriesDao;
	private WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao;

	private Handler handler;
	private String syncMessage = "Sync status unknown.";
	private String errorCode = null;
	private String description = null;
	private String smsc = null;
	private String code = null;
	private Date syncStartTime;

	public SyncService() {
		super(TAG);
		handler = new Handler();
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		EffortApplication.log(TAG, "In onHandleIntent.");
		syncMessage = "Sync status unknown.";
		errorCode = null;
		description = null;
		smsc = null;

		try {
			if (!syncEnabled) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Sync disabled. Doing nothing.");
				}

				syncMessage = "Sync disabled.";
				return;
			}

			settingsDao = SettingsDao.getInstance(getApplicationContext());

			String registrationId = getRegistrationId();

			if (TextUtils.isEmpty(registrationId)) {
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(this);
				String regid = null;

				try {
					regid = gcm.register(SENDER_ID);

					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Device registered with GCM, registration ID="
										+ regid);
					}

					settingsDao.saveSetting("gcm_registration_id", regid);
					settingsDao.saveSetting("gcm_app_version",
							Utils.getVersionName(getApplicationContext()));
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "Failed to register device with GCM", e);
					}

					settingsDao.saveSetting("gcm_registration_id", "");
				}
			}

			customerTypesDao = CustomerTypesDao
					.getInstance(getApplicationContext());
			jobTypesDao = JobTypesDao.getInstance(getApplicationContext());
			jobStatesDao = JobStatesDao.getInstance(getApplicationContext());
			typeStateMappingsDao = TypeStateMappingsDao
					.getInstance(getApplicationContext());
			jobsDao = JobsDao.getInstance(getApplicationContext());
			jobHistoriesDao = JobHistoriesDao
					.getInstance(getApplicationContext());
			jobStageStatusesDao = JobStageStatusesDao
					.getInstance(getApplicationContext());
			invitationsDao = InvitationsDao
					.getInstance(getApplicationContext());
			customersDao = CustomersDao.getInstance(getApplicationContext());
			notesDao = NotesDao.getInstance(getApplicationContext());
			locationsDao = LocationsDao.getInstance(getApplicationContext());
			leavesDao = LeavesDao.getInstance(getApplicationContext());
			employeesDao = EmployeesDao.getInstance(getApplicationContext());
			visibilityCriteriaDao = VisibilityCriteriaDao
					.getInstance(getApplicationContext());
			listFilteringCriteriaDao = ListFilteringCriteriaDao
					.getInstance(getApplicationContext());
			formSpecsDao = FormSpecsDao.getInstance(getApplicationContext());
			fieldSpecsDao = FieldSpecsDao.getInstance(getApplicationContext());
			fieldValueSpecsDao = FieldValueSpecsDao
					.getInstance(getApplicationContext());
			formsDao = FormsDao.getInstance(getApplicationContext());
			fieldsDao = FieldsDao.getInstance(getApplicationContext());
			sectionSpecsDao = SectionSpecsDao
					.getInstance(getApplicationContext());
			sectionFieldSpecsDao = SectionFieldSpecsDao
					.getInstance(getApplicationContext());
			sectionFieldValueSpecsDao = SectionFieldValueSpecsDao
					.getInstance(getApplicationContext());
			sectionFieldsDao = SectionFieldsDao
					.getInstance(getApplicationContext());
			pageSpecsDao = PageSpecsDao.getInstance(getApplicationContext());
			entitySpecsDao = EntitySpecsDao
					.getInstance(getApplicationContext());
			entityFieldSpecsDao = EntityFieldSpecsDao
					.getInstance(getApplicationContext());
			entityFieldValueSpecsDao = EntityFieldValueSpecsDao
					.getInstance(getApplicationContext());
			entitiesDao = EntitiesDao.getInstance(getApplicationContext());
			entityFieldsDao = EntityFieldsDao
					.getInstance(getApplicationContext());
			articlesDao = ArticlesDao.getInstance(getApplicationContext());
			workingHoursDao = WorkingHoursDao
					.getInstance(getApplicationContext());
			holidaysDao = HolidaysDao.getInstance(getApplicationContext());
			swdDao = SpecialWorkingDaysDao.getInstance(getApplicationContext());
			namedLocationsDao = NamedLocationsDao
					.getInstance(getApplicationContext());
			messagesDao = MessagesDao.getInstance(getApplicationContext());

			// ROUTE PLAN
			activitiesDao = ActivitySpecsDao
					.getInstance(getApplicationContext());
			assignedRoutesDao = AssignedRoutesDao
					.getInstance(getApplicationContext());
			completedActivitiesDao = CompletedActivitiesDao
					.getInstance(getApplicationContext());
			customerStatusDao = CustomerStatusDao
					.getInstance(getApplicationContext());

			// WORKFLOW
			workFlowSpecsDao = WorkFlowSpecsDao
					.getInstance(getApplicationContext());
			workFlowStagesDao = WorkFlowStagesDao
					.getInstance(getApplicationContext());

			workFlowStatusDao = WorkFlowStatusDao
					.getInstance(getApplicationContext());
			workFlowHistoriesDao = WorkFlowHistoriesDao
					.getInstance(getApplicationContext());
			workFlowFormSpecMappingsDao = WorkFlowFormSpecMappingsDao
					.getInstance(getApplicationContext());
			formFilesDao = FormFilesDao.getInstance(getApplicationContext());
			sectionFilesDao = SectionFilesDao
					.getInstance(getApplicationContext());

			code = settingsDao.getString(EffortProvider.Settings.KEY_CODE);

			if (TextUtils.isEmpty(code)) {
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				code = telephonyManager.getDeviceId();

				if (TextUtils.isEmpty(code)) {
					code = UUID.randomUUID().toString();
				}

				settingsDao.saveSetting(EffortProvider.Settings.KEY_CODE, code);
			}

			if (TextUtils.isEmpty(settingsDao
					.getString(EffortProvider.Settings.KEY_EMPLOYEE_ID))) {
				InitResponse response = init(intent.hasExtra("override"));

				if (response == null) {
					syncMessage = "Provisioning check failed. Please ensure that you are connected to the Internet.";
					if (EffortApplication.isActivityVisible()) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(),
										syncMessage, Toast.LENGTH_LONG).show();
							}
						});
					}
				}

				if (!handleInitResponse(response)) {
					return;
				}
			}

			do {
				syncStartTime = new Date();
				// TODO SAVING TO DB SYNC STARTING TIME IN MILLIS
				settingsDao
						.saveSetting(
								EffortProvider.Settings.KEY_LOCAL_SYNC_START_TIME_IN_MILLIS,
								System.currentTimeMillis() + "");
				syncMessage = performSync();

				if (!SYNCED_SUCCESSFULLY.equals(syncMessage)) {
					break;
				}
			} while (customersDao.hasUnsyncedChanges()
					|| jobsDao.hasUnsyncedChanges()
					|| notesDao.hasUnsyncedChanges()
					|| leavesDao.hasUnsyncedChanges()
					|| namedLocationsDao.hasUnsyncedChanges());

			if (EffortApplication.isActivityVisible()) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (SYNCED_SUCCESSFULLY.equals(syncMessage)) {
							String message = getCountsSince(syncStartTime);
							// WORKFLOW NOTIFICATION
							String workFlowStatusMessage = getWorkFlowStatusMessage(syncStartTime);
							String articlesMessage = getArticlesMessage(syncStartTime);

							if (!TextUtils.isEmpty(workFlowStatusMessage)) {
								Utils.showWorkFlowsNotification(
										getApplicationContext(),
										workFlowStatusMessage);
							}

							if (!TextUtils.isEmpty(articlesMessage)) {
								Utils.showArtclesNotification(
										getApplicationContext(),
										articlesMessage);
							}

							if (message != null) {
								Toast.makeText(getApplicationContext(),
										syncMessage + " " + message,
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										syncMessage, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									syncMessage, Toast.LENGTH_LONG).show();
						}
					}
				});

				if (InitResponse.CODE_EMPLOYEE_DISABLED.equals(errorCode)) {
					ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

					// for (RunningAppProcessInfo rapi :
					// activityManager.getRunningAppProcesses()) {
					// Log.i(TAG, "pid: " + rapi.pid + ", processName: " +
					// rapi.processName);
					// }
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// exception ignored
					}
					activityManager
							.killBackgroundProcesses("in.spoors.effort1");
					System.exit(1);
				}

			} else {
				Date pausedTime = settingsDao.getDate("activityPausedTime",
						syncStartTime);
				String message = getCountsSince(pausedTime);
				// WORKFLOW NOTIFICATION
				String workFlowStatusMessage = getWorkFlowStatusMessage(pausedTime);
				String articlesMessage = getArticlesMessage(pausedTime);

				if (!TextUtils.isEmpty(workFlowStatusMessage)) {
					Utils.showWorkFlowsNotification(getApplicationContext(),
							workFlowStatusMessage);
				}

				if (!TextUtils.isEmpty(articlesMessage)) {
					Utils.showArtclesNotification(getApplicationContext(),
							articlesMessage);
				}

				if (message != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Notification: " + message);
					}
					if ((!TextUtils.isEmpty(workFlowStatusMessage) && message
							.trim().equalsIgnoreCase(
									workFlowStatusMessage.trim()))
							|| (!TextUtils.isEmpty(articlesMessage) && message
									.trim().equalsIgnoreCase(
											articlesMessage.trim()))) {

					} else {
						Utils.showNotification(getApplicationContext(), message);
					}
				}
			}
		} finally {
			Intent broadcastIntent = new Intent(ACTION_SYNC_DONE);
			broadcastIntent.putExtra("message", syncMessage);

			if (errorCode != null) {
				broadcastIntent.putExtra("code", errorCode);
				broadcastIntent.putExtra("description", description);
				broadcastIntent.putExtra("smsc", smsc);
			}

			sendBroadcast(broadcastIntent);
		}
	}

	public String getWorkFlowStatusMessage(Date since) {
		int addedWorkFlows = workFlowStatusDao
				.getAddedWorkFlowStatusCount(since);
		int modifiedWorkFlows = workFlowStatusDao
				.getModifiedWorkFlowStatusCount(since);
		StringBuffer message = new StringBuffer();
		if (addedWorkFlows > 0) {
			message.append("New workflow status: ");
			message.append(addedWorkFlows);
			message.append(". ");
		}

		if (modifiedWorkFlows > 0) {
			message.append("Modified workflow status: ");
			message.append(modifiedWorkFlows);
			message.append(".");
		}
		return message.toString();
	}

	public String getArticlesMessage(Date since) {
		int addedArticles = articlesDao.getAddedArticlesCount(since);
		int modifiedArticles = articlesDao.getModifiedArticlesCount(since);

		String singularArticle = settingsDao.getLabel(
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE);

		// String pluralArticle = settingsDao.getLabel(
		// Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
		// Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE);

		StringBuffer message = new StringBuffer();
		if (addedArticles > 0) {
			message.append("New " + singularArticle + ": ");
			message.append(addedArticles);
			message.append(". ");
		}

		if (modifiedArticles > 0) {
			message.append("Modified " + singularArticle + ": ");
			message.append(modifiedArticles);
			message.append(".");
		}
		return message.toString();
	}

	private void wipeData() {
		jobsDao.deleteAllJobs();
		DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String dbPath = db.getPath();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleting database: " + dbPath);
		}

		File dbFile = new File(dbPath);
		boolean deleted = dbFile.delete();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted database? " + deleted);
		}

		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/EFFORT/");

		if (folder != null && folder.exists()) {
			for (File file : folder.listFiles()) {
				if (!file.isDirectory()) {
					file.delete();
				}
			}

			folder.delete();
		}
	}

	private InitResponse init(boolean override) {
		AndroidHttpClient httpClient = null;

		try {
			String url = getInitUrl(override);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Init URL: " + url);
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");
			HttpGet httpGet = new HttpGet(url);
			Utils.setTimeouts(httpGet);
			HttpResponse httpResponse = httpClient.execute(httpGet);

			String response = EntityUtils.toString(httpResponse.getEntity());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response JSON: " + response);
			}

			InitResponse initResponse = InitResponse.parse(response);

			return initResponse;
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad Sync URL: " + e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

		return null;
	}

	private String getInitUrl(boolean override) {
		String serverBaseUrl = getString(R.string.server_base_url);
		String code = settingsDao.getString(EffortProvider.Settings.KEY_CODE);

		Builder builder = Uri.parse(serverBaseUrl).buildUpon()
				.appendEncodedPath("service/init/" + code);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		if (override) {
			builder.appendQueryParameter("override", "1");
		}

		Uri uri = builder.build();
		return uri.toString();
	}

	/**
	 * 
	 * @return the sync status message
	 */
	private String performSync() {
		// send pending messages related to sim card change
		WakefulIntentService.sendWakefulWork(this,
				BackgroundSmsSendService.class);

		syncMessage = "Sync status unknown.";

		AndroidHttpClient httpClient = null;

		try {
			String syncUrl = getSyncUrl();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Sync URL: " + syncUrl);
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");

			HttpPost httpPost = new HttpPost(syncUrl);
			Utils.setTimeouts(httpPost);
			SyncRequest syncRequest = new SyncRequest(getApplicationContext());
			String requestJson = syncRequest.getJsonString();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Request JSON: " + requestJson);
			}

			HttpEntity requestEntity = new StringEntity(requestJson);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setEntity(requestEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Response Status code: "
						+ httpResponse.getStatusLine().getStatusCode());
			}

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				SyncResponse syncResponse = new SyncResponse(
						getApplicationContext());

				boolean succeeded = syncResponse.parse(response);

				if (!succeeded) {
					InitResponse initResponse = InitResponse.parse(response);
					handleInitResponse(initResponse);
				}

				if (succeeded) {
					syncMessage = SYNCED_SUCCESSFULLY;
					onSyncSuccess(syncRequest, syncResponse);
					settingsDao
							.saveSetting(
									"localSyncTime",
									SQLiteDateTimeUtils
											.getSQLiteDateTimeFromLocalTime(syncResponse
													.getLocalSyncTime()));
					settingsDao
							.saveSetting(
									"remoteSyncTime",
									SQLiteDateTimeUtils
											.getSQLiteDateTimeFromLocalTime(syncResponse
													.getRemoteSyncTime()));
				}
			} else {
				syncMessage = "Sync failed, unexpected response from cloud.";
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad Sync URL: " + e.toString(), e);
			syncMessage = "Sync failed, due to bad URL.";
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
			syncMessage = "Sync failed, due to network error.";
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Sending tracks after sync completion.");
			}

			// set alarms here, so that alarms will be resetted every time the
			// sync operation is performed.
			Utils.setAlarmsForJobs(getApplicationContext());

			// Not checking whether there are unsynced tracks or not, to
			// ensure that timed out locations are finalized
			WakefulIntentService.sendWakefulWork(this, SendTracksService.class);
		}

		return syncMessage;
	}

	private String getSyncUrl() {
		String remoteSyncTime = settingsDao
				.getString(Settings.KEY_REMOTE_SYNC_TIME);

		String xsdTime = null;

		if (!TextUtils.isEmpty(remoteSyncTime)) {
			xsdTime = XsdDateTimeUtils
					.getXsdDateTimeFromSQLiteDateTime(remoteSyncTime);
		}

		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"service/sync/" + settingsDao.getString("employeeId"));
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		if (xsdTime != null) {
			builder.appendQueryParameter("syncTime", xsdTime);
		}

		builder.appendQueryParameter("isTimeZoneAware", "1");
		builder.appendQueryParameter("hasAutoComputeCapability", "1");

		Uri syncUri = builder.build();
		return syncUri.toString();
	}

	/**
	 * 
	 * @param initResponse
	 * @return true if init succeeded (i.e. can proceed with sync), false
	 *         otherwise.
	 */
	private boolean handleInitResponse(InitResponse initResponse) {
		if (initResponse == null) {
			return false;
		}

		if (TextUtils.isEmpty(initResponse.getEmployeeId())) {

			syncMessage = "Sync failed: activation pending.";

			if (InitResponse.CODE_BAD_JSON.equals(initResponse.getCode())) {
				syncMessage = "Sync failed due to an unknown error. Please contact technical support.";
			} else if (InitResponse.CODE_NOT_AUTHORIZED.equals(initResponse
					.getCode())) {
				errorCode = initResponse.getCode();
				description = initResponse.getDescription();
				smsc = initResponse.getSmsc();
			} else if (InitResponse.CODE_SWITCH_DEVICE.equals(initResponse
					.getCode())) {
				errorCode = initResponse.getCode();
				description = initResponse.getDescription();
			} else if (InitResponse.CODE_EMPLOYEE_DISABLED.equals(initResponse
					.getCode())) {
				// in this cases we can clear employee data like, his job/work
				// alarms, data, and database
				// so maintain a flag for this purpose
				Utils.cancelAlarmsForDay(getApplicationContext());
				SettingsDao.getInstance(getApplicationContext()).getSettings()
						.clear();
				wipeData();
				errorCode = initResponse.getCode();
				description = initResponse.getDescription();
				syncMessage = "You are no longer authorized to use EFFORT. Exiting.";
			} else if (InitResponse.CODE_COMPANY_INACTIVE.equals(initResponse
					.getCode())) {
				syncMessage = "Sync failed: " + initResponse.getDescription();
				// errorCode = initResponse.getCode();
				// description = initResponse.getDescription();
			}

			return false;
		} else {
			settingsDao.saveSetting(EffortProvider.Settings.KEY_EMPLOYEE_ID,
					initResponse.getEmployeeId());
			settingsDao.saveSetting(EffortProvider.Settings.KEY_EMPLOYEE_NAME,
					initResponse.getEmployeeName());
			settingsDao.saveSetting(EffortProvider.Settings.KEY_EMPLOYEE_RANK,
					initResponse.getEmployeeRank());

			return true;
		}
	}

	private Job getJobWithLocaId(List<Job> jobs, long localJobId) {
		if (jobs == null) {
			return null;
		}

		for (Job job : jobs) {
			if (job.getLocalId() != null && job.getLocalId() == localJobId) {
				return job;
			}
		}

		return null;
	}

	private Form getFormWithLocaId(List<Form> forms, long localFormId) {
		if (forms == null) {
			return null;
		}

		for (Form form : forms) {
			if (form.getLocalId() != null && form.getLocalId() == localFormId) {
				return form;
			}
		}

		return null;
	}

	private Note getNoteWithLocaId(List<Note> notes, long localNoteId) {
		if (notes == null) {
			return null;
		}

		for (Note note : notes) {
			if (note.getLocalId() != null && note.getLocalId() == localNoteId) {
				return note;
			}
		}

		return null;
	}

	private JobStageStatus getStatus(List<JobStageStatus> statuses,
			long localJobId, int stateId) {
		if (statuses == null) {
			return null;
		}

		for (JobStageStatus status : statuses) {
			if (Utils.longsEqual(status.getLocalJobId(), localJobId)
					&& Utils.integersEqual(status.getStateId(), stateId)) {
				return status;
			}
		}

		return null;
	}

	private void onSyncSuccess(SyncRequest request, SyncResponse response) {
		List<CustomerType> localCustomerTypes = customerTypesDao
				.getCustomerTypes();

		if (response.getAddedCustomerTypes().size() > 0
				&& (localCustomerTypes == null || (localCustomerTypes != null && customerTypesDao
						.areTypesDifferent(localCustomerTypes,
								response.getAddedCustomerTypes())))) {
			customerTypesDao.deleteAll();
			for (CustomerType type : response.getAddedCustomerTypes()) {
				customerTypesDao.save(type);
			}
		}

		for (Customer customer : response.getAddedCustomers()) {
			customersDao.save(customer);
		}

		for (Employee employee : response.getAddedEmployees()) {
			employeesDao.save(employee);
		}

		for (Long remoteId : response.getDeletedEmployees()) {
			employeesDao.deleteLeaveWithRemoteId(remoteId);
		}

		for (EntitySpec spec : response.getEntitySpecs()) {
			entitySpecsDao.save(spec);
		}

		for (EntityFieldSpec spec : response.getEntityFieldSpecs()) {
			entityFieldSpecsDao.save(spec);
		}

		for (EntityFieldValueSpec spec : response.getEntityFieldValueSpecs()) {
			entityFieldValueSpecsDao.save(spec);
		}

		for (VisibilityCriteria visibilityCriteria : response
				.getVisibilityDependencyCriterias()) {
			visibilityCriteriaDao.save(visibilityCriteria);
		}

		for (ListFilteringCriteria listFilteringCriteria : response
				.getListFilteringCriterias()) {
			listFilteringCriteriaDao.save(listFilteringCriteria);
		}
		for (FormSpec spec : response.getFormSpecs()) {
			formSpecsDao.save(spec);
			pageSpecsDao.deletePageSpecs(spec.getId());
		}

		formSpecsDao.updateActive(response.getActiveFormSpecIds());
		formSpecsDao.updateInactive(response.getInactiveFormSpecIds());

		for (FieldSpec spec : response.getFieldSpecs()) {
			fieldSpecsDao.save(spec);
		}

		for (FieldValueSpec spec : response.getFieldValueSpecs()) {
			fieldValueSpecsDao.save(spec);
		}

		for (SectionSpec spec : response.getSectionSpecs()) {
			sectionSpecsDao.save(spec);
		}

		for (SectionFieldSpec spec : response.getSectionFieldSpecs()) {
			sectionFieldSpecsDao.save(spec);
		}

		for (SectionFieldValueSpec spec : response.getSectionFieldValueSpecs()) {
			sectionFieldValueSpecsDao.save(spec);
		}

		for (PageSpec spec : response.getPageSpecs()) {
			pageSpecsDao.save(spec);
		}

		for (Entity entity : response.getAddedEntities()) {
			entitiesDao.save(entity);
		}

		for (EntityField field : response.getEntityFields()) {
			entityFieldsDao.save(field);
		}

		entitiesDao.updateDeletedFlag(response.getDeletedEntities(), true);

		for (Form form : response.getAddedForms()) {
			if (form.getLocalId() != null) {
				Form formNow = formsDao.getFormWithLocalId(form.getLocalId());
				Form requestForm = getFormWithLocaId(
						request.getModifiedForms(), form.getLocalId());

				if (requestForm == null) {
					requestForm = getFormWithLocaId(request.getAddedForms(),
							form.getLocalId());
				}

				if (formNow != null
						&& requestForm != null
						&& !Utils.datesEqual(
								formNow.getLocalModificationTime(),
								requestForm.getLocalModificationTime())) {
					form.setDirty(formNow.getDirty());
				}
			}

			formsDao.save(form);

			Utils.deleteFormFieldsThatAreDeletedAtServerSide(
					getApplicationContext(), form.getLocalId(),
					response.getFields());
			Utils.deleteSectionFieldsThatAreDeletedAtServerSide(
					getApplicationContext(), form.getLocalId(),
					response.getSectionFields());

			if (form.getRemoteLocationId() != null) {
				// locationsDao.updateRemoteLocationId(form.getLocalId(),
				// form.getRemoteLocationId());
				locationsDao.deleteFormLocations(form.getLocalId());
			}
		}

		for (Field field : response.getFields()) {
			if (field.getLocalFormId() != null) {
				Form formNow = formsDao.getFormWithLocalId(field
						.getLocalFormId());
				Form requestForm = getFormWithLocaId(
						request.getModifiedForms(), field.getLocalFormId());

				if (requestForm == null) {
					requestForm = getFormWithLocaId(request.getAddedForms(),
							field.getLocalFormId());
				}

				if (requestForm == null
						|| (formNow != null && requestForm != null && Utils
								.datesEqual(formNow.getLocalModificationTime(),
										requestForm.getLocalModificationTime()))) {
					fieldsDao.save(field);
				} else {
					Log.i(TAG,
							"Not saving the field because it is modified locally: "
									+ field.toString());
				}

			} else {
				fieldsDao.save(field);
			}
		}

		for (SectionField field : response.getSectionFields()) {
			if (field.getLocalFormId() != null) {
				Form formNow = formsDao.getFormWithLocalId(field
						.getLocalFormId());
				Form requestForm = getFormWithLocaId(
						request.getModifiedForms(), field.getLocalFormId());

				if (requestForm == null) {
					requestForm = getFormWithLocaId(request.getAddedForms(),
							field.getLocalFormId());
				}

				if (requestForm == null
						|| (formNow != null && requestForm != null && Utils
								.datesEqual(formNow.getLocalModificationTime(),
										requestForm.getLocalModificationTime()))) {
					sectionFieldsDao.save(field);
				} else {
					Log.i(TAG,
							"Not saving the section field because it is modified locally: "
									+ field.toString());
				}

			} else {
				sectionFieldsDao.save(field);
			}
		}

		for (Long remoteFormId : response.getDeletedForms()) {
			Long localFormId = formsDao.getLocalId(remoteFormId);

			if (localFormId != null) {
				formsDao.deleteForm(localFormId);
			}
		}

		for (Article article : response.getAddedArticles()) {
			article.setGotViaSearch(false);
			articlesDao.save(article);
		}
		// Deleting articles
		for (Long articleId : response.getDeletedArticles()) {

			if (articleId != null) {
				articlesDao.deleteArticle(articleId);
			}
		}

		// mappedArticlesList
		List<Long> mappedArticlesList = response.getMappedArticlesList();
		if (mappedArticlesList != null && mappedArticlesList.size() > 0) {
			String mappedArticlesIdsString = TextUtils.join(",",
					mappedArticlesList);
			if (!TextUtils.isEmpty(mappedArticlesIdsString)) {
				articlesDao.deleteUnmappedArticles(mappedArticlesIdsString);
			}
		}

		List<JobType> localTypes = jobTypesDao.getJobTypes();

		if (response.getAddedTypes().size() > 0
				&& (localTypes == null || (localTypes != null && jobTypesDao
						.areTypesDifferent(localTypes, response.getAddedTypes())))) {
			jobTypesDao.deleteAll();
			for (JobType type : response.getAddedTypes()) {
				jobTypesDao.save(type);
			}
		}

		List<JobState> localStates = jobStatesDao.getJobStates();

		if (response.getAddedStates().size() > 0
				&& (localStates == null || (localStates != null && jobStatesDao
						.areStatesDifferent(localStates,
								response.getAddedStates())))) {
			for (JobState state : response.getAddedStates()) {
				jobStatesDao.save(state);
			}
		}

		List<TypeStateMapping> localMappings = typeStateMappingsDao
				.getMappings();

		if (response.getAddedTypeStateMappings().size() > 0
				&& (localMappings == null || (localMappings != null && typeStateMappingsDao
						.areMappingsDifferent(localMappings,
								response.getAddedTypeStateMappings())))) {
			typeStateMappingsDao.deleteAll();
			for (TypeStateMapping mapping : response
					.getAddedTypeStateMappings()) {
				typeStateMappingsDao.save(mapping);
			}
		}

		for (Job job : response.getAddedJobs()) {
			if (job.getLocalId() != null) {
				Job requestJob = getJobWithLocaId(request.getAddedJobs(),
						job.getLocalId());

				// the job we got, is due to a job added locally
				if (requestJob != null) {
					Job localJobNow = jobsDao.getJobWithLocalId(job
							.getLocalId());

					if (Utils.datesEqual(requestJob.getLocalModificationTime(),
							localJobNow.getLocalModificationTime())) {
						job.setDirty(false);
						jobsDao.save(job);
					} else {
						jobsDao.updateRemoteId(job.getLocalId(),
								job.getRemoteId());
						// job was modified after the sync was initiated, save
						// the remote id, but ignore rest of the content
						// don't clear the dirty flag
					}

					continue;
				}

				requestJob = getJobWithLocaId(request.getModifiedJobs(),
						job.getLocalId());

				// the job we got, is due to a job modified locally
				if (requestJob != null) {
					Job localJobNow = jobsDao.getJobWithLocalId(job
							.getLocalId());

					if (Utils.datesEqual(requestJob.getLocalModificationTime(),
							localJobNow.getLocalModificationTime())) {
						job.setDirty(false);
						jobsDao.save(job);
					} else {
						// something changed locally, and we already have the
						// remote id mapping
						// don't overwrite the local changes, keep the dirty
						// flag as is
					}

					continue;
				}

				Job localJobNow = jobsDao.getJobWithLocalId(job.getLocalId());

				// job indeed has changed on the server-side
				if (!Utils.datesEqual(job.getRemoteModificationTime(),
						localJobNow.getRemoteModificationTime())) {
					job.setRead(false);
					job.setDirty(false);
					jobsDao.save(job);
				}
			} else {
				job.setRead(false);
				job.setDirty(false);
				jobsDao.save(job);
			}
		}

		for (Long remoteJobId : response.getDeletedJobs()) {
			Long localJobId = jobsDao.getLocalId(remoteJobId);

			if (localJobId != null) {
				jobsDao.deleteJob(localJobId);
			}
		}

		for (Invitation invitation : response.getAddedInvitations()) {
			invitationsDao.save(invitation);
		}

		for (Long invitationId : response.getDeletedInvitations()) {
			invitationsDao.deleteInvitation(invitationId);
		}

		for (Note note : response.getAddedNotes()) {
			if (note.getLocalId() != null) {
				Note requestNote = getNoteWithLocaId(request.getAddedNotes(),
						note.getLocalId());

				// the note we got, is due to a note added locally
				if (requestNote != null) {
					Note localNoteNow = notesDao.getNoteWithLocalId(note
							.getLocalId());

					if (Utils.datesEqual(
							requestNote.getLocalModificationTime(),
							localNoteNow.getLocalModificationTime())
							&& Utils.longsEqual(requestNote.getMediaId(),
									localNoteNow.getMediaId())) {
						note.setDirty(false);
						notesDao.save(note);
						locationsDao.updateDirtyFlag(note.getLocalId(), false);
					} else {
						notesDao.updateRemoteId(note.getLocalId(),
								note.getRemoteId());
						// note was modified after the sync was initiated, save
						// the remote id, but ignore rest of the content
						// don't clear the dirty flag
					}

					continue;
				}

				requestNote = getNoteWithLocaId(request.getModifiedNotes(),
						note.getLocalId());

				// the note we got, is due to a note modified locally
				if (requestNote != null) {
					Note localNoteNow = notesDao.getNoteWithLocalId(note
							.getLocalId());

					if (Utils.datesEqual(
							requestNote.getLocalModificationTime(),
							localNoteNow.getLocalModificationTime())
							&& Utils.longsEqual(requestNote.getMediaId(),
									localNoteNow.getMediaId())) {
						note.setDirty(false);
						notesDao.save(note);
						locationsDao.updateDirtyFlag(note.getLocalId(), false);
					} else {
						// something changed locally, and we already have the
						// remote id mapping
						// don't overwrite the local changes, keep the dirty
						// flag as is
					}

					continue;
				}

				Note localNoteNow = notesDao.getNoteWithLocalId(note
						.getLocalId());

				// note indeed has changed on the server-side
				if (!localNoteNow.getDirty()
						&& Utils.longsEqual(note.getMediaId(),
								localNoteNow.getMediaId())
						&& notesDao.notesAreDifferent(note, localNoteNow)) {
					note.setDirty(false);
					notesDao.save(note);
					locationsDao.updateDirtyFlag(note.getLocalId(), false);
					jobsDao.updateReadFlag(note.getLocalJobId(), false);
				}
			} else {
				note.setDirty(false);
				notesDao.save(note);
				locationsDao.updateDirtyFlag(note.getLocalId(), false);
				jobsDao.updateReadFlag(note.getLocalJobId(), false);
			}
		}

		if (response.getAddedHistories().size() > 0) {
			for (JobHistory history : response.getAddedHistories()) {
				if (history.getLocalId() != null) {
					locationsDao.deleteHistoryLocations(history.getLocalId());
				}

				jobHistoriesDao.save(history);
			}
		}

		if (response.getAddedStatuses().size() > 0) {
			for (JobStageStatus responseStatus : response.getAddedStatuses()) {
				Long localJobId = responseStatus.getLocalJobId();

				if (localJobId == null) {
					localJobId = jobsDao.getLocalId(responseStatus
							.getRemoteJobId());
				}

				JobStageStatus requestStatus = getStatus(
						request.getAddedStatuses(), localJobId,
						responseStatus.getStateId());

				Date modifiedTimeNow = jobStageStatusesDao
						.getLocalModifiedTime(localJobId,
								responseStatus.getStateId());
				if (!(requestStatus != null && modifiedTimeNow != null && modifiedTimeNow
						.after(requestStatus.getLocalModifiedTime()))) {
					jobStageStatusesDao.updateDone(localJobId,
							responseStatus.getStateId(),
							responseStatus.isDone(), false, false);
				}
			}
		}

		for (Leave leave : response.getAddedLeaves()) {
			leavesDao.save(leave);
		}

		for (long remoteLeaveId : response.getDeletedLeaves()) {
			leavesDao.deleteLeaveWithRemoteId(remoteLeaveId);
		}

		// Saving employee under me ids to the setting table
		String employeesIds = TextUtils.join(",", response.getEmployeesUnder());
		settingsDao.saveSetting(Settings.KEY_EMPLOYEE_UNDER_ID, employeesIds);

		if (request.getCancelledLeaves() != null) {
			for (long remoteLeaveId : request.getCancelledLeaves()) {
				if (!response.getDeletedLeaves().contains(remoteLeaveId)) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"We did not get remote leave ID "
										+ remoteLeaveId
										+ " in the deleted leaves array. Deleting it from the device anyway.");
					}

					leavesDao.deleteLeaveWithRemoteId(remoteLeaveId);
				}
			}
		}

		if (response.isNewCalendar() != null && response.isNewCalendar()) {
			holidaysDao.deleteAll();
			swdDao.deleteAll();
		} else {
			for (long exceptionId : response.getDeletedExceptions()) {
				holidaysDao.deleteHoliday(exceptionId);
				swdDao.deleteSpecialWorkingDay(exceptionId);
			}
		}

		if (response.hasWorkingHours()) {
			workingHoursDao.deleteAll();
			if (response.getWorkingHours() != null
					&& response.getWorkingHours().size() > 0) {
				for (WorkingHour wh : response.getWorkingHours()) {
					workingHoursDao.save(wh);
				}
			}
		}

		if (response.getAddedHolidays() != null
				&& response.getAddedHolidays().size() > 0) {
			for (Holiday holiday : response.getAddedHolidays()) {
				holidaysDao.deleteHoliday(holiday.getId());
				holidaysDao.save(holiday);
			}
		}

		if (response.getAddedSpecialWorkingDays() != null
				&& response.getAddedSpecialWorkingDays().size() > 0) {
			for (SpecialWorkingDay swd : response.getAddedSpecialWorkingDays()) {
				swdDao.deleteSpecialWorkingDay(swd.getId());
				swdDao.save(swd);
			}
		}

		for (NamedLocation location : response.getAddedLocations()) {
			namedLocationsDao.save(location);
		}

		for (long remoteLeaveId : response.getDeletedLocations()) {
			namedLocationsDao.deleteLocationWithRemoteId(remoteLeaveId);
		}

		for (Message message : response.getAddedMessages()) {
			messagesDao.save(message);
		}

		// ROUTE PLAN
		for (ActivitySpec activity : response.getActivities()) {
			activitiesDao.save(activity);
		}
		// UPDATING CLIENT_FORM_ID'S WITH REMOTE_ID
		// for (Form form : response.getAddedForms()) {
		// if (form.getLocalId() != null) {
		// completedActivitiesDao.updateClientFormIdWithRemoteFormId(form);
		// }
		// }
		// UPTO HERE
		for (CompletedActivity activity : response.getCompletedActivities()) {
			completedActivitiesDao.save(activity);
		}

		for (CustomerStatusDto customerStatus : response.getCustomerStatus()) {
			customerStatus.setDirty(false);
			customerStatusDao.save(customerStatus);
			if (customerStatus != null && customerStatus.getLoaclId() != null) {
				locationsDao.deleteAssignedRouteLocations(customerStatus
						.getLoaclId());
			}
		}

		for (AssignedRoute assignedRoute : response.getAssignedRoutes()) {
			assignedRoutesDao.save(assignedRoute);
		}
		for (AssignedRoute completedAssignedRoute : response
				.getCompletedAssignedRoutes()) {

			assignedRoutesDao
					.saveCompletedAssignedRoute(completedAssignedRoute);

			if (completedAssignedRoute != null
					&& completedAssignedRoute.getId() != null) {
				locationsDao
						.deleteAssignedRouteLocations(completedAssignedRoute
								.getId());
			}
		}

		// WORKFLOWS
		for (WorkFlowSpec workFlowSpec : response.getWorkFlowSpecs()) {
			workFlowSpecsDao.save(workFlowSpec);
		}

		// Deleting before inserting mapping records
		// if (response.getWorkFlowFormSpecMappings() != null
		// && response.getWorkFlowFormSpecMappings().size() > 0) {
		// workFlowFormSpecMappingsDao.deleteAll();
		// }
		// Inserting all records
		for (WorkFlowFormSpecMapping workFlowFormSpecMapping : response
				.getWorkFlowFormSpecMappings()) {
			workFlowFormSpecMappingsDao.save(workFlowFormSpecMapping);
		}

		for (WorkFlowStage workFlowStage : response.getWorkFlowStages()) {
			workFlowStagesDao.save(workFlowStage);
		}

		for (WorkFlowStatusDto workFlowStatus : response
				.getWorkFlowStatusDtos()) {
			workFlowStatusDao.save(workFlowStatus);
		}

		for (WorkFlowHistory workFlowHistory : response.getWorkFlowHistory()) {
			workFlowHistoriesDao.save(workFlowHistory);
		}

		for (long mediaId : response.getMediaIds()) {
			FormFile formFileWithMediaId = formFilesDao
					.getFileWithMediaId(mediaId);

			if (formFileWithMediaId != null) {
				locationsDao.deleteLocation(Locations.PURPOSE_FORM_FILE,
						formFileWithMediaId.getLocalId());
			} else {
				SectionFile sectionFileWithMediaId = sectionFilesDao
						.getFileWithMediaId(mediaId);
				if (sectionFileWithMediaId != null) {
					locationsDao.deleteLocation(Locations.PURPOSE_SECTION_FILE,
							sectionFileWithMediaId.getLocalId());
				}
			}
		}

		settingsDao.saveSetting("employeeHomeLocationDirty", "false");

		boolean prevTrackOnStatus = settingsDao.getBoolean("trackOn", true);
		boolean trackOn = Utils.canTrackNow(getApplicationContext());

		if (trackOn
				&& settingsDao.getBoolean(
						Settings.KEY_PREFETCH_NEARBY_CUSTOMERS, false)) {
			Intent intent = new Intent(getApplicationContext(),
					LocationCaptureService.class);
			intent.putExtra(EffortProvider.Locations.PURPOSE,
					EffortProvider.Locations.PURPOSE_NEARBY_CUSTOMERS);
			WakefulIntentService.sendWakefulWork(getApplicationContext(),
					intent);
		}

		if (trackOn != prevTrackOnStatus) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Restarting sync alarm.");
			}

			EffortApplication.restartSyncAlarm();
			settingsDao.saveSetting("trackOn", "" + trackOn);
		}

		deleteObjectsWithoutMappingAfterSync(request);
		Utils.updateCustomerDistances(getApplicationContext());
	}

	private void deleteObjectsWithoutMappingAfterSync(SyncRequest request) {
		// ensure that we received remote ID for everything we have sent
		if (request.getAddedCustomers() != null) {
			for (Customer customer : request.getAddedCustomers()) {
				if (customersDao.getRemoteId(customer.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ customer.toString());
					}

					customersDao.deleteCustomer(customer.getLocalId());
				}
			}
		}

		if (request.getAddedJobs() != null) {
			for (Job job : request.getAddedJobs()) {
				if (jobsDao.getRemoteId(job.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ job.toString());
					}

					jobsDao.deleteJob(job.getLocalId());
				}
			}
		}

		if (request.getAddedHistories() != null) {
			for (JobHistory history : request.getAddedHistories()) {
				if (jobHistoriesDao.getRemoteId(history.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ history.toString());
					}

					jobHistoriesDao.deleteJobHistory(history.getLocalId());
				}
			}
		}

		if (request.getAddedNotes() != null) {
			for (Note note : request.getAddedNotes()) {
				if (notesDao.getRemoteId(note.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ note.toString());
					}

					notesDao.deleteNote(note.getLocalId());
				}
			}
		}

		if (request.getAddedLeaves() != null) {
			for (Leave leave : request.getAddedLeaves()) {
				if (leavesDao.getRemoteId(leave.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ leave.toString());
					}

					leavesDao.deleteLeaveWithLocalId(leave.getLocalId());
				}
			}
		}

		if (request.getAddedForms() != null) {
			for (Form form : request.getAddedForms()) {
				if (formsDao.getRemoteId(form.getLocalId()) == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Removing local object because we didn't get remote ID from cloud: "
										+ form.toString());
					}

					formsDao.deleteForm(form.getLocalId());
				}
			}
		}
	}

	private String getCountsSince(Date since) {
		int jobsAdded = jobsDao.getAddedCount(since);
		int jobsmodified = jobsDao.getModifiedCount(since);
		int invitationsAdded = invitationsDao.getAddedCount(since);
		int invitationsModified = invitationsDao.getModifiedCount(since);
		int addedWorkFlows = workFlowStatusDao
				.getAddedWorkFlowStatusCount(since);
		int modifiedWorkFlows = workFlowStatusDao
				.getModifiedWorkFlowStatusCount(since);
		int messagesAdded = messagesDao.getAddedCount(since);

		int addedArticles = articlesDao.getAddedArticlesCount(since);
		int modifiedArticles = articlesDao.getModifiedArticlesCount(since);

		String singular = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
		String plural = settingsDao.getLabel(Settings.LABEL_JOB_PLURAL_KEY,
				Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE);
		String singularArticle = settingsDao.getLabel(
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE);

		// String pluralArticle = settingsDao.getLabel(
		// Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
		// Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE);

		if (jobsAdded > 0 || jobsmodified > 0 || invitationsAdded > 0
				|| invitationsModified > 0 || messagesAdded > 0
				|| addedArticles > 0 || modifiedArticles > 0
				|| addedWorkFlows > 0 || modifiedWorkFlows > 0) {
			StringBuffer message = new StringBuffer();

			if (jobsAdded > 0) {
				message.append("New " + plural + ": ");
				message.append(jobsAdded);
				message.append(". ");
			}

			if (jobsmodified > 0) {
				message.append("Modified " + plural + ": ");
				message.append(jobsmodified);
				message.append(".");
			}

			if (invitationsAdded > 0) {
				message.append("\nNew " + singular + " invitations: ");
				message.append(invitationsAdded);
				message.append(". ");
			}

			if (invitationsModified > 0) {
				message.append("Modified " + singular + " invitations: ");
				message.append(invitationsModified);
				message.append(". ");
			}

			if (messagesAdded > 0) {
				message.append("\nNew messages: ");
				message.append(messagesAdded);
				Message effortMessage = messagesDao.getMostRecentMessage();

				if (effortMessage == null) {
					message.append(".");
				} else {
					message.append(". Latest message: "
							+ effortMessage.getSubject());
				}
			}
			if (addedArticles > 0) {
				message.append("\nNew " + singularArticle + ": ");
				message.append(addedArticles);
				message.append(". ");
			}

			if (modifiedArticles > 0) {
				message.append("Modified " + singularArticle + ": ");
				message.append(modifiedArticles);
				message.append(".");
			}

			if (addedWorkFlows > 0) {
				message.append("\nNew workflow status: ");
				message.append(addedWorkFlows);
				message.append(". ");
			}

			if (modifiedWorkFlows > 0) {
				message.append("Modified workflow status: ");
				message.append(modifiedWorkFlows);
				message.append(".");
			}

			return message.toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId() {
		String registrationId = settingsDao.getString("gcm_registration_id");

		if (TextUtils.isEmpty(registrationId)) {
			Log.i(TAG, "Registration not found.");
			return "";
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		if (!TextUtils.equals(Utils.getVersionName(getApplicationContext()),
				settingsDao.getString("gcm_app_version"))) {
			Log.i(TAG, "App version changed.");
			return "";
		}

		return registrationId;
	}

}
