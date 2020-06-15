package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.provider.EffortProvider.JobStageStatuses;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class JobStageStatus {

	public static final String TAG = "JobStageStatus";
	public static final String JSON_LOCAL_JOB_ID = "clientEmpVisitId";
	public static final String JSON_REMOTE_JOB_ID = "empVisitId";
	public static final String JSON_STATE_ID = "visitStateId";
	public static final String JSON_DONE = "state";
	public static final String JSON_LOCAL_MODIFIED_TIME = "clientModifiedTime";

	private Long localJobId;
	private Long remoteJobId;
	private boolean done;
	private boolean dirty;
	private int stateId;
	private Date localModifiedTime;
	private Boolean temporary;

	public Long getLocalJobId() {
		return localJobId;
	}

	public void setLocalJobId(Long localJobId) {
		this.localJobId = localJobId;
	}

	public Long getRemoteJobId() {
		return remoteJobId;
	}

	public void setRemoteJobId(Long remoteJobId) {
		this.remoteJobId = remoteJobId;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public Date getLocalModifiedTime() {
		return localModifiedTime;
	}

	public void setLocalModifiedTime(Date localModifiedTime) {
		this.localModifiedTime = localModifiedTime;
	}

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	public void load(Cursor cursor, Context applicationContext) {
		JobsDao jobsDao = JobsDao.getInstance(applicationContext);

		localJobId = cursor.isNull(JobStageStatuses.LOCAL_JOB_ID_INDEX) ? null
				: cursor.getLong(JobStageStatuses.LOCAL_JOB_ID_INDEX);

		if (localJobId != null) {
			remoteJobId = jobsDao.getRemoteId(localJobId);
		}

		stateId = cursor.getInt(JobStageStatuses.STATE_ID_INDEX);
		done = Boolean.parseBoolean(cursor
				.getString(JobStageStatuses.DONE_INDEX));
		dirty = Boolean.parseBoolean(cursor
				.getString(JobStageStatuses.DIRTY_INDEX));
		localModifiedTime = SQLiteDateTimeUtils.getLocalTime(cursor
				.getString(JobStageStatuses.LOCAL_MODIFICATION_TIME_INDEX));
		temporary = cursor.isNull(JobStageStatuses.TEMPORARY_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(JobStageStatuses.TEMPORARY_INDEX));
	}

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for accessing resource strings, and creating DAO
	 *            objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static JobStageStatus parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteJobId = json.getLong(JSON_REMOTE_JOB_ID);
		Long localJobId = Utils.getLong(json, JSON_LOCAL_JOB_ID);

		if (localJobId == null) {
			JobsDao jobsDao = JobsDao.getInstance(applicationContext);
			localJobId = jobsDao.getLocalId(remoteJobId);
		}

		JobStageStatus status = new JobStageStatus();

		status.localJobId = localJobId;
		status.remoteJobId = remoteJobId;
		status.stateId = Utils.getInteger(json, JSON_STATE_ID);
		status.localModifiedTime = Utils
				.getDate(json, JSON_LOCAL_MODIFIED_TIME);
		status.done = Utils.getBoolean(json, JSON_DONE);

		return status;
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 *            Used for reading JSON path constants from resources.
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			// if remote job id is present, don't send the local job
			// id to the server
			if (remoteJobId != null) {
				json.put(JSON_REMOTE_JOB_ID, remoteJobId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_JOB_ID, localJobId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_STATE_ID, stateId);
			Utils.putValueOnlyIfNotNull(json, JSON_DONE, done);
			Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_MODIFIED_TIME,
					localModifiedTime);
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for job stage status: "
							+ e.toString(), e);
			return null;
		}

		return json;
	}

	@Override
	public String toString() {
		return "JobStageStatus [localJobId=" + localJobId + ", remoteJobId="
				+ remoteJobId + ", done=" + done + ", dirty=" + dirty
				+ ", stateId=" + stateId + ", localModifiedTime="
				+ localModifiedTime + ", temporary=" + temporary + "]";
	}

}
