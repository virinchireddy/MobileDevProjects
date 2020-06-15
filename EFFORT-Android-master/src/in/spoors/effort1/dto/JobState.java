package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.JobStates;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class JobState implements Comparable<JobState> {

	public static final String JSON_ID = "visitStateId";
	public static final String JSON_NAME = "visitStateValue";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_FORM_REQUIRED = "formMandatory";
	public static final String JSON_START_STATE = "startState";
	public static final String JSON_END_STATE = "endState";
	public static final String JSON_DEFAULT_STATE = "makeDefault";
	public static final String JSON_REVISITABLE = "revisitable";
	public static final String JSON_MIN_SUBMISSIONS = "minSubmissions";
	public static final String JSON_MAX_SUBMISSIONS = "maxSubmissions";
	public static final String JSON_MANDATORY_FOR_COMPLETION = "mandatoryToComplete";

	private int id;
	private String name;
	private Long formSpecId;
	private boolean formRequired;
	private boolean startState;
	private boolean endState;
	private boolean defaultState;
	private boolean revisitable;
	private int minSubmissions;
	private int maxSubmissions;
	private boolean mandatoryForCompletion;

	public JobState() {
	}

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static JobState parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		JobState jobState = new JobState();

		jobState.id = Utils.getInteger(json, JSON_ID);
		jobState.name = Utils.getString(json, JSON_NAME);
		jobState.startState = Utils.getBoolean(json, JSON_START_STATE);
		jobState.endState = Utils.getBoolean(json, JSON_END_STATE);
		jobState.defaultState = Utils.getBoolean(json, JSON_DEFAULT_STATE);
		jobState.formRequired = Utils.getBoolean(json, JSON_FORM_REQUIRED);
		jobState.revisitable = Utils.getBoolean(json, JSON_REVISITABLE);
		jobState.minSubmissions = Utils.getInteger(json, JSON_MIN_SUBMISSIONS);
		jobState.maxSubmissions = Utils.getInteger(json, JSON_MAX_SUBMISSIONS);
		jobState.mandatoryForCompletion = Utils.getBoolean(json,
				JSON_MANDATORY_FOR_COMPLETION);
		jobState.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);

		return jobState;
	}

	/**
	 * 
	 * @param values
	 *            fills this object, instead of creating a new one
	 * @return
	 */
	public ContentValues getContentValues(ContentValues values) {
		if (values == null) {
			values = new ContentValues();
		}

		Utils.putNullOrValue(values, JobStates._ID, id);
		Utils.putNullOrValue(values, JobStates.NAME, name);
		Utils.putNullOrValue(values, JobStates.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, JobStates.FORM_REQUIRED, formRequired);
		Utils.putNullOrValue(values, JobStates.START_STATE, startState);
		Utils.putNullOrValue(values, JobStates.END_STATE, endState);
		Utils.putNullOrValue(values, JobStates.DEFAULT_STATE, defaultState);
		Utils.putNullOrValue(values, JobStates.REVISITABLE, revisitable);
		Utils.putNullOrValue(values, JobStates.MIN_SUBMISSIONS, minSubmissions);
		Utils.putNullOrValue(values, JobStates.MAX_SUBMISSIONS, maxSubmissions);
		Utils.putNullOrValue(values, JobStates.MANDATORY_FOR_COMPLETION,
				mandatoryForCompletion);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.getInt(JobStates._ID_INDEX);
		name = cursor.getString(JobStates.NAME_INDEX);
		formSpecId = cursor.isNull(JobStates.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(JobStates.FORM_SPEC_ID_INDEX);
		formRequired = Boolean.parseBoolean(cursor
				.getString(JobStates.FORM_REQUIRED_INDEX));
		startState = Boolean.parseBoolean(cursor
				.getString(JobStates.START_STATE_INDEX));
		endState = Boolean.parseBoolean(cursor
				.getString(JobStates.END_STATE_INDEX));
		defaultState = Boolean.parseBoolean(cursor
				.getString(JobStates.DEFAULT_STATE_INDEX));
		revisitable = Boolean.parseBoolean(cursor
				.getString(JobStates.REVISITABLE_INDEX));
		minSubmissions = cursor.getInt(JobStates.MIN_SUBMISSIONS_INDEX);
		maxSubmissions = cursor.getInt(JobStates.MAX_SUBMISSIONS_INDEX);
		mandatoryForCompletion = Boolean.parseBoolean(cursor
				.getString(JobStates.MANDATORY_FOR_COMPLETION_INDEX));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public boolean isFormRequired() {
		return formRequired;
	}

	public void setFormRequired(boolean formRequired) {
		this.formRequired = formRequired;
	}

	public boolean isStartState() {
		return startState;
	}

	public void setStartState(boolean startState) {
		this.startState = startState;
	}

	public boolean isEndState() {
		return endState;
	}

	public void setEndState(boolean endState) {
		this.endState = endState;
	}

	public boolean isDefaultState() {
		return defaultState;
	}

	public void setDefaultState(boolean defaultState) {
		this.defaultState = defaultState;
	}

	public boolean isRevisitable() {
		return revisitable;
	}

	public void setRevisitable(boolean revisitable) {
		this.revisitable = revisitable;
	}

	public int getMinSubmissions() {
		return minSubmissions;
	}

	public void setMinSubmissions(int minSubmissions) {
		this.minSubmissions = minSubmissions;
	}

	public int getMaxSubmissions() {
		return maxSubmissions;
	}

	public void setMaxSubmissions(int maxSubmissions) {
		this.maxSubmissions = maxSubmissions;
	}

	public boolean isMandatoryForCompletion() {
		return mandatoryForCompletion;
	}

	public void setMandatoryForCompletion(boolean mandatoryForCompletion) {
		this.mandatoryForCompletion = mandatoryForCompletion;
	}

	@Override
	public int compareTo(JobState another) {
		return name.compareTo(another.name);
	}

	@Override
	public String toString() {
		return "JobState [id=" + id + ", name=" + name + ", formSpecId="
				+ formSpecId + ", formRequired=" + formRequired
				+ ", startState=" + startState + ", endState=" + endState
				+ ", defaultState=" + defaultState + ", revisitable="
				+ revisitable + ", minSubmissions=" + minSubmissions
				+ ", maxSubmissions=" + maxSubmissions
				+ ", mandatoryForCompletion=" + mandatoryForCompletion + "]";
	}

}