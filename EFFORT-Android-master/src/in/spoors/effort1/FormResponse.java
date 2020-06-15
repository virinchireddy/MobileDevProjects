package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldsDao;
import in.spoors.effort1.dao.FieldSpecsDao;
import in.spoors.effort1.dao.FieldValueSpecsDao;
import in.spoors.effort1.dao.FieldsDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.ListFilteringCriteriaDao;
import in.spoors.effort1.dao.PageSpecsDao;
import in.spoors.effort1.dao.SectionFieldSpecsDao;
import in.spoors.effort1.dao.SectionFieldValueSpecsDao;
import in.spoors.effort1.dao.SectionFieldsDao;
import in.spoors.effort1.dao.SectionSpecsDao;
import in.spoors.effort1.dao.VisibilityCriteriaDao;
import in.spoors.effort1.dao.WorkFlowFormSpecMappingsDao;
import in.spoors.effort1.dao.WorkFlowHistoriesDao;
import in.spoors.effort1.dao.WorkFlowSpecsDao;
import in.spoors.effort1.dao.WorkFlowStagesDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.Customer;
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
import in.spoors.effort1.dto.JobState;
import in.spoors.effort1.dto.JobType;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.dto.PageSpec;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.SectionFieldSpec;
import in.spoors.effort1.dto.SectionFieldValueSpec;
import in.spoors.effort1.dto.SectionSpec;
import in.spoors.effort1.dto.TypeStateMapping;
import in.spoors.effort1.dto.VisibilityCriteria;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.dto.WorkFlowHistory;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.dto.WorkingHour;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

/**
 * Sync response acts as a JSON parser and container that holds the results of a
 * successful sync.
 * 
 * @author tiru
 * 
 */
public class FormResponse {

	public static final String TAG = "FetchResponse";
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
	public static final String JSON_PATH_SECTION_SPECS = "forms/specs/sections";
	public static final String JSON_PATH_SECTION_FIELD_SPECS = "forms/specs/sectionFields";
	public static final String JSON_PATH_SECTION_FIELD_VALUE_SPECS = "forms/specs/sectionFieldValidValues";
	public static final String JSON_PATH_SECTION_FIELDS = "forms/data/sectionFields";
	public static final String JSON_PATH_PAGE_SPECS = "forms/specs/pageSpecs";

	public static final String JSON_PATH_WORK_FLOW_HISTORY = "forms/formWorkflow/workflowFormStatusHistory";
	public static final String JSON_PATH_WORK_FLOWS = "forms/formWorkflow/workflows";
	public static final String JSON_PATH_WORK_FLOW_FORM_STATUS = "forms/formWorkflow/workflowFormStatus";
	public static final String JSON_PATH_WORK_FLOW_STAGES = "forms/formWorkflow/workflowStages";
	public static final String JSON_PATH_WORK_FLOW_FORM_SPECS_MAP = "forms/formWorkflow/workflowEntityMap";

	private List<Customer> addedCustomers = new ArrayList<Customer>();
	private List<Entity> addedEntities = new ArrayList<Entity>();
	private List<EntityField> addedEntityFields = new ArrayList<EntityField>();
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
	private List<SectionSpec> sectionSpecs = new ArrayList<SectionSpec>();
	private List<SectionFieldSpec> sectionFieldSpecs = new ArrayList<SectionFieldSpec>();
	private List<SectionFieldValueSpec> sectionFieldValueSpecs = new ArrayList<SectionFieldValueSpec>();
	private List<SectionField> sectionFields = new ArrayList<SectionField>();
	private List<PageSpec> pageSpecs = new ArrayList<PageSpec>();

	// WORK FLOW
	private List<WorkFlowSpec> workFlowSpecs = new ArrayList<WorkFlowSpec>();
	private List<WorkFlowStatusDto> workFlowStatusDtos = new ArrayList<WorkFlowStatusDto>();
	private List<WorkFlowHistory> workFlowHistory = new ArrayList<WorkFlowHistory>();
	private List<WorkFlowStage> workFlowStages = new ArrayList<WorkFlowStage>();
	private List<WorkFlowFormSpecMapping> workFlowFormSpecMappings = new ArrayList<WorkFlowFormSpecMapping>();

	private Context applicationContext;

	// for saving purpose

	private CustomersDao customersDao;
	private EntitiesDao entitiesDao;
	private EntityFieldsDao entityFieldsDao;
	private FormSpecsDao formSpecsDao;
	private FieldsDao fieldsDao;
	private VisibilityCriteriaDao visibilityCriteriaDao;
	private ListFilteringCriteriaDao listFilteringCriteriaDao;
	private FormsDao formsDao;
	private PageSpecsDao pageSpecsDao;
	private FieldSpecsDao fieldSpecsDao;
	private SectionFieldsDao sectionFieldsDao;
	private SectionFieldSpecsDao sectionFieldSpecsDao;
	private SectionFieldValueSpecsDao sectionFieldValueSpecsDao;
	private SectionSpecsDao sectionSpecsDao;
	private FieldValueSpecsDao fieldValueSpecsDao;
	// WORKFLOW
	private WorkFlowSpecsDao workFlowSpecsDao;
	private WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao;
	private WorkFlowStagesDao workFlowStagesDao;
	private WorkFlowStatusDao workFlowStatusDao;
	private WorkFlowHistoriesDao workFlowHistoriesDao;
	private EmployeesDao employeesDao;
	private List<Employee> employees = new ArrayList<Employee>();

	public FormResponse(Context applicationContext) {
		this.applicationContext = applicationContext;
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

			// PROCESS FORM CUSTOMERS
			if (json.get("customers") instanceof JSONArray) {
				Utils.addCustomers(json.getJSONArray("customers"),
						addedCustomers, applicationContext, false);
			}

			if (json.get("employees") instanceof JSONArray) {
				Utils.addEmployees(json.getJSONArray("employees"), employees,
						applicationContext);
			}
			// PROCESS ENTITIES
			if (json.get("entities") instanceof JSONArray) {
				Utils.addEntities(json.getJSONArray("entities"), addedEntities,
						applicationContext);
			}

			if (json.get("entityFields") instanceof JSONArray) {
				Utils.addEntityFields(json.getJSONArray("entityFields"),
						addedEntityFields, applicationContext);
			}

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

	public void save(boolean cached) {

		customersDao = CustomersDao.getInstance(applicationContext);
		entitiesDao = EntitiesDao.getInstance(applicationContext);
		entityFieldsDao = EntityFieldsDao.getInstance(applicationContext);
		visibilityCriteriaDao = VisibilityCriteriaDao
				.getInstance(applicationContext);
		listFilteringCriteriaDao = ListFilteringCriteriaDao
				.getInstance(applicationContext);
		formSpecsDao = FormSpecsDao.getInstance(applicationContext);

		pageSpecsDao = PageSpecsDao.getInstance(applicationContext);
		fieldSpecsDao = FieldSpecsDao.getInstance(applicationContext);
		fieldValueSpecsDao = FieldValueSpecsDao.getInstance(applicationContext);

		sectionSpecsDao = SectionSpecsDao.getInstance(applicationContext);
		sectionFieldSpecsDao = SectionFieldSpecsDao
				.getInstance(applicationContext);
		sectionFieldValueSpecsDao = SectionFieldValueSpecsDao
				.getInstance(applicationContext);

		formsDao = FormsDao.getInstance(applicationContext);
		fieldsDao = FieldsDao.getInstance(applicationContext);
		sectionFieldsDao = SectionFieldsDao.getInstance(applicationContext);
		// WORKFLOW
		workFlowSpecsDao = WorkFlowSpecsDao.getInstance(applicationContext);
		workFlowFormSpecMappingsDao = WorkFlowFormSpecMappingsDao
				.getInstance(applicationContext);
		workFlowStagesDao = WorkFlowStagesDao.getInstance(applicationContext);
		workFlowStatusDao = WorkFlowStatusDao.getInstance(applicationContext);
		workFlowHistoriesDao = WorkFlowHistoriesDao
				.getInstance(applicationContext);
		employeesDao = EmployeesDao.getInstance(applicationContext);

		for (Customer customer : addedCustomers) {
			customersDao.save(customer);
		}

		for (Employee employee : employees) {
			employeesDao.save(employee);
		}

		for (Entity entity : addedEntities) {
			entitiesDao.save(entity);
		}

		for (EntityField field : addedEntityFields) {
			entityFieldsDao.save(field);
		}

		for (VisibilityCriteria visibilityCriteria : getVisibilityDependencyCriterias()) {
			visibilityCriteriaDao.save(visibilityCriteria);
		}

		for (ListFilteringCriteria listFilteringCriteria : getListFilteringCriterias()) {
			listFilteringCriteriaDao.save(listFilteringCriteria);
		}
		for (FormSpec spec : getFormSpecs()) {
			formSpecsDao.save(spec);
			pageSpecsDao.deletePageSpecs(spec.getId());
		}

		formSpecsDao.updateActive(getActiveFormSpecIds());
		formSpecsDao.updateInactive(getInactiveFormSpecIds());

		for (FieldSpec spec : getFieldSpecs()) {
			fieldSpecsDao.save(spec);
		}

		for (FieldValueSpec spec : getFieldValueSpecs()) {
			fieldValueSpecsDao.save(spec);
		}
		for (SectionSpec spec : getSectionSpecs()) {
			sectionSpecsDao.save(spec);
		}

		for (SectionFieldSpec spec : getSectionFieldSpecs()) {
			sectionFieldSpecsDao.save(spec);
		}

		for (SectionFieldValueSpec spec : getSectionFieldValueSpecs()) {
			sectionFieldValueSpecsDao.save(spec);
		}

		for (PageSpec spec : getPageSpecs()) {
			pageSpecsDao.save(spec);
		}
		addedForms = getAddedForms();

		for (Form form : addedForms) {
			if (form.getCached() == null) {
				form.setCached(cached);
			}

			formsDao.save(form);
		}
		for (Field field : getFields()) {

			fieldsDao.save(field);
		}

		for (Long remoteFormId : getDeletedForms()) {
			Long localFormId = formsDao.getLocalId(remoteFormId);

			if (localFormId != null) {
				formsDao.deleteForm(localFormId);
			}
		}

		for (SectionField field : getSectionFields()) {
			sectionFieldsDao.save(field);
		}

		// WORKFLOWS
		for (WorkFlowSpec workFlowSpec : getWorkFlowSpecs()) {
			workFlowSpecsDao.save(workFlowSpec);
		}
		for (WorkFlowFormSpecMapping workFlowFormSpecMapping : getWorkFlowFormSpecMappings()) {
			workFlowFormSpecMappingsDao.save(workFlowFormSpecMapping);
		}

		for (WorkFlowStage workFlowStage : getWorkFlowStages()) {
			workFlowStagesDao.save(workFlowStage);
		}

		for (WorkFlowStatusDto workFlowStatus : getWorkFlowStatusDtos()) {
			workFlowStatusDao.save(workFlowStatus);
		}

		for (WorkFlowHistory workFlowHistory : getWorkFlowHistory()) {
			workFlowHistoriesDao.save(workFlowHistory);
		}

	}

	public List<Customer> getAddedCustomers() {
		return addedCustomers;
	}

	public void setAddedCustomers(List<Customer> addedCustomers) {
		this.addedCustomers = addedCustomers;
	}

	public List<Entity> getAddedEntities() {
		return addedEntities;
	}

	public void setAddedEntities(List<Entity> addedEntities) {
		this.addedEntities = addedEntities;
	}

	public List<EntityField> getAddedEntityFields() {
		return addedEntityFields;
	}

	public void setAddedEntityFields(List<EntityField> addedEntityFields) {
		this.addedEntityFields = addedEntityFields;
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

	public List<Long> getActiveFormSpecIds() {
		return activeFormSpecIds;
	}

	public void setActiveFormSpecIds(List<Long> activeFormSpecIds) {
		this.activeFormSpecIds = activeFormSpecIds;
	}

	public List<Long> getInactiveFormSpecIds() {
		return inactiveFormSpecIds;
	}

	public void setInactiveFormSpecIds(List<Long> inactiveFormSpecIds) {
		this.inactiveFormSpecIds = inactiveFormSpecIds;
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

}
