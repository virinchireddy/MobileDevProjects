package in.spoors.effort1;

import in.spoors.common.EffortDatePickerDialog;
import in.spoors.common.EffortTimePickerDialog;
import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldSpecsDao;
import in.spoors.effort1.dao.EntityFieldsDao;
import in.spoors.effort1.dao.FieldSpecsDao;
import in.spoors.effort1.dao.FieldValueSpecsDao;
import in.spoors.effort1.dao.FieldsDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.ListFilteringCriteriaDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.PageSpecsDao;
import in.spoors.effort1.dao.SectionFieldSpecsDao;
import in.spoors.effort1.dao.SectionFieldValueSpecsDao;
import in.spoors.effort1.dao.SectionFieldsDao;
import in.spoors.effort1.dao.SectionFilesDao;
import in.spoors.effort1.dao.SectionSpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.VisibilityCriteriaDao;
import in.spoors.effort1.dao.WorkFlowSpecsDao;
import in.spoors.effort1.dao.WorkFlowStagesDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.dto.EntityFieldSpec;
import in.spoors.effort1.dto.EntityFilterResultsDto;
import in.spoors.effort1.dto.Field;
import in.spoors.effort1.dto.FieldSpec;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.FormFile;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.dto.PageSpec;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.SectionFieldSpec;
import in.spoors.effort1.dto.SectionFile;
import in.spoors.effort1.dto.SectionSpec;
import in.spoors.effort1.dto.SectionViewField;
import in.spoors.effort1.dto.ViewField;
import in.spoors.effort1.dto.VisibilityCriteria;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.expr.EvalVisitor;
import in.spoors.effort1.expr.ExprLexer;
import in.spoors.effort1.expr.ExprParser;
import in.spoors.effort1.expr.ListVisitor;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Entities;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.zxing.client.android.Intents;

@SuppressLint("SimpleDateFormat")
public class FormActivity extends ActionBarActivity implements
		OnDateSetListener, OnTimeSetListener, TabListener,
		OnFocusChangeListener, OnItemSelectedListener, OnClickListener {

	boolean showTimingLog = false;
	long starttime = 0;
	public static long customerstartTime = 0;
	private static final String STORAGE_TIME_FORMAT_PATTERN = "HH:mm";

	public static final String TAG = "FormActivity";
	public static final int PICK_IMAGE = 3;
	public static final int PICK_SIGNATURE = 4;
	public static final int READ_BARCODE = 6;
	private long localFormId;
	private long formSpecId;
	private boolean editMode;
	public static final int PICK_CUSTOMER = 1;
	public static final int PICK_ENTITY = 5;
	public static final int PICK_LOCATION = 7;
	public static final int PICK_MULTI_LIST = 9;
	public static final int PICK_EMPLOYEE = 11;
	public static final int FORM_FIELD = 1;
	public static final int SECTION_FIELD = 2;
	public static final String LOCAL_CUSTOMER_ID = "localCustomerIdForJob";

	HashMap<String, EntityFilterResultsDto> cachedFilteringResults;

	/**
	 * Form fields on activity start
	 */
	private ArrayList<ViewField> originalFields;

	/**
	 * Form fields that acts as the scratch pad
	 */
	private ArrayList<ViewField> currentFields;
	private Context context;
	private SettingsDao settingsDao;
	private CustomersDao customersDao;
	private FormsDao formsDao;
	private FieldsDao fieldsDao;
	private FormSpecsDao formSpecsDao;
	private FieldSpecsDao fieldSpecsDao;
	private FieldValueSpecsDao fieldValueSpecsDao;
	private SectionFieldsDao sectionFieldsDao;
	private SectionSpecsDao sectionSpecsDao;
	private SectionFieldSpecsDao sectionFieldSpecsDao;
	private SectionFieldValueSpecsDao sectionFieldValueSpecsDao;
	private PageSpecsDao pageSpecsDao;
	private EntitiesDao entitiesDao;
	private EntityFieldSpecsDao entityFieldSpecsDao;
	private LocationsDao locationsDao;

	private EffortDatePickerDialog datePickerDialog;
	private EffortTimePickerDialog timePickerDialog;

	private SimpleDateFormat dateFormat;
	private SimpleDateFormat displayTimeFormat;
	private SimpleDateFormat storageTimeFormat;

	private ViewField pickerViewField;
	// private Button pickerButton;
	// private EditText pickerEditText;

	private String[] booleanOptionalOptions = { "Pick Yes or No", "Yes", "No" };

	private String mediaPath;

	private FormFilesDao formFilesDao;
	private SectionFilesDao sectionFilesDao;

	// For checking if a file is in use, before deleting the file
	private NotesDao notesDao;
	private WorkFlowSpecsDao workFlowSpecsDao;
	WorkFlowStatusDao workFlowStatusDao;
	private ProgressUpdateThread progressUpdateThread;
	private List<FieldSpec> fieldSpecs;
	private List<SectionSpec> sectionSpecs;
	private List<PageSpec> pageSpecs;
	private HashMap<Long, List<SectionFieldSpec>> sectionFieldSpecsMap;
	private HashMap<String, Double> valueMap = new HashMap<String, Double>();

	private String singular;
	private String formSpecTitle;

	private EmployeesDao employeesDao;
	private VisibilityCriteriaDao visibilityCriteriaDao;

	private boolean hasVisibilityCriteria;
	private boolean hasFilteringCriteria;

	private View focussed;
	private Handler handler;
	private ListFilteringCriteriaDao listFilteringCriteriaDao;
	int spinnersCount = 0;
	int onItemSelectedCount = 0;

	private Button refreshButton;
	private boolean showDataPending;
	public static final String ACTION_OPEN_WORKFLOW_FORM = "action_open_workflow_form";
	public static final String ACTION_CREATE_WORKFLOW_FORM = "action_create_workflow_form";

	private boolean loadInProgress;
	AlertDialog progressDialog;
	ProgressTask progressTask;
	private boolean modeChange = false;
	private boolean canCaptureMediaLocation;

	/*
	 * private ProgressFragment progressFragment; private FormFragment
	 * formFragment;
	 */

	// LinearLayout formLayout, progressLayout;

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		context = this;
		handler = new Handler();
		focussed = new View(context);
		refreshButton = (Button) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(FormActivity.this);
		listFilteringCriteriaDao = ListFilteringCriteriaDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		visibilityCriteriaDao = VisibilityCriteriaDao
				.getInstance(getApplicationContext());
		formsDao = FormsDao.getInstance(getApplicationContext());
		fieldsDao = FieldsDao.getInstance(getApplicationContext());
		formSpecsDao = FormSpecsDao.getInstance(getApplicationContext());

		fieldSpecsDao = FieldSpecsDao.getInstance(getApplicationContext());
		fieldValueSpecsDao = FieldValueSpecsDao
				.getInstance(getApplicationContext());
		formFilesDao = FormFilesDao.getInstance(getApplicationContext());
		sectionFieldsDao = SectionFieldsDao
				.getInstance(getApplicationContext());
		sectionSpecsDao = SectionSpecsDao.getInstance(getApplicationContext());
		sectionFieldSpecsDao = SectionFieldSpecsDao
				.getInstance(getApplicationContext());
		sectionFieldValueSpecsDao = SectionFieldValueSpecsDao
				.getInstance(getApplicationContext());
		sectionFilesDao = SectionFilesDao.getInstance(getApplicationContext());
		pageSpecsDao = PageSpecsDao.getInstance(getApplicationContext());
		entitiesDao = EntitiesDao.getInstance(getApplicationContext());
		entityFieldSpecsDao = EntityFieldSpecsDao
				.getInstance(getApplicationContext());
		notesDao = NotesDao.getInstance(getApplicationContext());
		locationsDao = LocationsDao.getInstance(getApplicationContext());

		dateFormat = Utils.getDateFormat(getApplicationContext());
		displayTimeFormat = Utils.getTimeFormat(getApplicationContext());
		storageTimeFormat = new SimpleDateFormat(STORAGE_TIME_FORMAT_PATTERN);

		workFlowSpecsDao = WorkFlowSpecsDao
				.getInstance(getApplicationContext());
		workFlowStatusDao = WorkFlowStatusDao
				.getInstance(getApplicationContext());
		singular = settingsDao.getLabel(Settings.LABEL_FORM_SINGULAR_KEY,
				Settings.LABEL_FORM_SINGULAR_DEFAULT_VLAUE);
		canCaptureMediaLocation = settingsDao.getBoolean(
				Settings.KEY_CAPTURE_LOCATION_ON_EVERY_FORM_FIELD_MEDIA, true);

		if (savedInstanceState == null) {
			cachedFilteringResults = new HashMap<String, EntityFilterResultsDto>();
			localFormId = getIntent().getLongExtra(Forms._ID, 0);
			editMode = getIntent().getBooleanExtra("editMode", false);
			modeChange = getIntent().getBooleanExtra("modeChange", false);
			if (localFormId != 0 && !editMode) {
				formSpecId = formsDao.getFormSpecId(localFormId);
			} else {
				formSpecId = getIntent().getLongExtra(Forms.FORM_SPEC_ID, 0);
			}
			if (editMode) {
				Long id = formSpecsDao.getLatestFormSpecId(formSpecId);
				if (id != null) {
					formSpecId = id;
				}
			}
		} else {
			cachedFilteringResults = (HashMap<String, EntityFilterResultsDto>) savedInstanceState
					.getSerializable("cachedFilteringResults");
			localFormId = savedInstanceState.getLong("localFormId");
			formSpecId = savedInstanceState.getLong("formSpecId");
			editMode = savedInstanceState.getBoolean("editMode");
			modeChange = savedInstanceState.getBoolean("modeChange");
			originalFields = (ArrayList<ViewField>) savedInstanceState
					.getSerializable("originalFields");
			currentFields = (ArrayList<ViewField>) savedInstanceState
					.getSerializable("currentFields");
			mediaPath = savedInstanceState.getString("mediaPath");
			ViewField viewField = (ViewField) savedInstanceState
					.getSerializable("pickerViewField");

			if (viewField != null) {
				if (viewField instanceof SectionViewField) {
					SectionViewField sectionViewField = (SectionViewField) viewField;

					pickerViewField = getSectionViewField(
							sectionViewField.getFieldSpecId(),
							sectionViewField.getSectionSpecId(),
							sectionViewField.getSectionInstanceId());
				} else {
					pickerViewField = getViewField(viewField.getFieldSpecId());
				}
			}
		}

		loadInProgress = true;
		setProgressDialog(getString(R.string.loading));
		new FetchTask().execute();
	}

	@SuppressLint("UseSparseArrays")
	public ArrayList<ViewField> getFields(List<FieldSpec> fieldSpecs) {
		ArrayList<ViewField> fields = new ArrayList<ViewField>(
				fieldSpecs.size() + sectionSpecs.size());

		for (FieldSpec fieldSpec : fieldSpecs) {
			ViewField viewField = new ViewField();
			viewField.setFormSpecId(fieldSpec.getFormSpecId());
			viewField.setFieldSpecId(fieldSpec.getId());
			viewField.setLocalFormId(localFormId);
			viewField.setType(fieldSpec.getType());
			viewField.setTypeExtra(fieldSpec.getTypeExtra());
			viewField.setComputed(fieldSpec.getComputed());
			viewField.setEnabledBarcode(fieldSpec.getBarcodeField());
			viewField.setFormula(fieldSpec.getFormula());
			viewField.setSelector(fieldSpec.getSelector());
			viewField.setLabel(fieldSpec.getLabel());
			viewField.setIdentifier(fieldSpec.getIdentifier());
			viewField.setRequired(fieldSpec.getRequired());
			viewField.setDisplayOrder(fieldSpec.getDisplayOrder());
			viewField.setPageId(fieldSpec.getPageId());
			viewField.setVisible(fieldSpec.getVisible());
			viewField.setEditable(fieldSpec.getEditable());
			viewField.setMinValue(fieldSpec.getMinValue());
			viewField.setMaxValue(fieldSpec.getMaxValue());
			viewField.setDefaultField(fieldSpec.getDefaultField());
			Field field = fieldsDao.getField(localFormId, fieldSpec.getId());

			if (field != null) {
				viewField.setLocalValue(field.getLocalValue());
				viewField.setRemoteValue(field.getRemoteValue());
				viewField.setLocalId(field.getLocalId());
			} else {

				if (viewField.getType() == FieldSpecs.TYPE_CUSTOMER) {
					if (editMode) {
						if ("fill".equals(getIntent().getAction())) {
							if (getIntent().hasExtra(LOCAL_CUSTOMER_ID)) {
								Long localCustomerId = getIntent()
										.getLongExtra(LOCAL_CUSTOMER_ID, 0);

								if (localCustomerId != null) {
									viewField.setLocalValue(localCustomerId
											.toString());
								}
							}
						}
						if ("fillFormForActivity".equals(getIntent()
								.getAction())) {
							if (getIntent()
									.hasExtra(
											EffortProvider.CompletedActivities.CUSTOMER_ID)) {
								long customerId = getIntent()
										.getLongExtra(
												EffortProvider.CompletedActivities.CUSTOMER_ID,
												0);
								viewField.setLocalValue(customersDao
										.getLocalId(customerId).toString());
								viewField.setRemoteValue(customerId + "");
							}
						}
					}

				}
			}

			if (fieldSpec.getType() == FieldSpecs.TYPE_IMAGE
					|| fieldSpec.getType() == FieldSpecs.TYPE_SIGNATURE) {
				// For all the image and signature fields, create file dto
				// objects, if they don't already exist
				// Ensure that they are cleaned up using the temporary flag
				// (when the form is discarded)
				FormFile formFile = formFilesDao.getFile(
						viewField.getFieldSpecId(), viewField.getLocalFormId());

				if (formFile == null) {
					formFile = new FormFile();
					formFile.setLocalFormId(localFormId);
					formFile.setFieldSpecId(fieldSpec.getId());
				}

				if (!TextUtils.isEmpty(formFile.getLocalMediaPath())) {
					// check if file exists
					File file = new File(formFile.getLocalMediaPath());

					final String toastMsg = "File is missing at path "
							+ formFile.getLocalMediaPath() + " for "
							+ viewField.getLabel();
					if (!file.exists()) {
						handler.post(new Thread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(context, toastMsg,
										Toast.LENGTH_LONG).show();

							}
						}));

						formFile.setLocalMediaPath(null);
					}
				}

				viewField.setFile(formFile);
			}

			fields.add(viewField);
		}

		for (SectionSpec sectionSpec : sectionSpecs) {
			SectionViewField section = new SectionViewField();
			section.setSectionSpecId(sectionSpec.getId());
			section.setSectionTitle(sectionSpec.getTitle());
			section.setMinEntries(sectionSpec.getMinEntries());
			section.setMaxEntries(sectionSpec.getMaxEntries());
			section.setDisplayOrder(sectionSpec.getDisplayOrder());
			section.setCollapsed(false);
			section.setPageId(sectionSpec.getPageId());
			List<Integer> sectionInstances = sectionFieldsDao
					.getSectionInstances(localFormId, sectionSpec.getId());

			for (int i = sectionInstances.size(); i < section.getMinEntries(); ++i) {
				sectionInstances.add(i);
			}

			LinkedHashMap<Integer, List<SectionViewField>> instanceFieldsMap = new LinkedHashMap<Integer, List<SectionViewField>>();
			section.setInstanceFieldsMap(instanceFieldsMap);

			for (Integer sectionInstance : sectionInstances) {
				addSectionInstance(section, sectionInstance);
			}

			int addAt = -1;

			for (int i = 0; i < fields.size(); ++i) {
				if (fields.get(i).getDisplayOrder() > section.getDisplayOrder()) {
					addAt = i;
					break;
				}
			}

			if (addAt == -1) {
				fields.add(section);
			} else {
				fields.add(addAt, section);
			}
		}

		return fields;
	}

	public void addSectionInstance(SectionViewField section,
			int sectionInstanceId) {
		List<SectionViewField> sectionFields = new ArrayList<SectionViewField>();

		List<SectionFieldSpec> sectionFieldSpecs = sectionFieldSpecsMap
				.get(section.getSectionSpecId());

		if (sectionFieldSpecs == null) {
			return;
		}

		for (SectionFieldSpec fieldSpec : sectionFieldSpecs) {
			SectionViewField viewField = new SectionViewField();
			viewField.setFormSpecId(fieldSpec.getFormSpecId());
			viewField.setFieldSpecId(fieldSpec.getId());
			viewField.setLocalFormId(localFormId);
			viewField.setSectionInstanceId(sectionInstanceId);
			viewField.setSectionSpecId(section.getSectionSpecId());
			viewField.setType(fieldSpec.getType());
			viewField.setTypeExtra(fieldSpec.getTypeExtra());
			viewField.setComputed(fieldSpec.getComputed());
			viewField.setEnabledBarcode(fieldSpec.getBarcodeField());
			viewField.setFormula(fieldSpec.getFormula());
			viewField.setSelector(fieldSpec.getSelector());
			viewField.setLabel(fieldSpec.getLabel());
			viewField.setIdentifier(fieldSpec.getIdentifier());
			viewField.setRequired(fieldSpec.getRequired());
			viewField.setDisplayOrder(fieldSpec.getDisplayOrder());
			viewField.setVisible(fieldSpec.getVisible());
			viewField.setEditable(fieldSpec.getEditable());
			viewField.setMinValue(fieldSpec.getMinValue());
			viewField.setMaxValue(fieldSpec.getMaxValue());
			viewField.setDefaultField(fieldSpec.getDefaultField());
			viewField.setCollapsed(false);

			SectionField field = sectionFieldsDao.getField(localFormId,
					fieldSpec.getId(), sectionInstanceId);

			if (field != null) {
				viewField.setLocalValue(field.getLocalValue());
				viewField.setRemoteValue(field.getRemoteValue());
				viewField.setLocalId(field.getLocalId());
			} else {
				if (viewField.getType() == FieldSpecs.TYPE_CUSTOMER) {
					if (editMode) {
						if ("fill".equals(getIntent().getAction())) {
							if (getIntent().hasExtra(LOCAL_CUSTOMER_ID)) {
								Long localCustomerId = getIntent()
										.getLongExtra(LOCAL_CUSTOMER_ID, 0);

								if (localCustomerId != null) {
									viewField.setLocalValue(localCustomerId
											.toString());
								}
							}
						}
						if ("fillFormForActivity".equals(getIntent()
								.getAction())) {
							if (getIntent()
									.hasExtra(
											EffortProvider.CompletedActivities.CUSTOMER_ID)) {
								long customerId = getIntent()
										.getLongExtra(
												EffortProvider.CompletedActivities.CUSTOMER_ID,
												0);
								viewField.setLocalValue(customersDao
										.getLocalId(customerId).toString());
								viewField.setRemoteValue(customerId + "");
							}
						}
					}

				}
			}

			if (fieldSpec.getType() == FieldSpecs.TYPE_IMAGE
					|| fieldSpec.getType() == FieldSpecs.TYPE_SIGNATURE) {
				// For all the image and signature fields, create file
				// dto
				// objects, if they don't already exist
				// Ensure that they are cleaned up using the temporary
				// flag
				// (when the form is discarded)
				SectionFile sectionFile = sectionFilesDao.getFile(
						viewField.getFieldSpecId(), viewField.getLocalFormId(),
						sectionInstanceId);

				if (sectionFile == null) {
					sectionFile = new SectionFile();
					sectionFile.setLocalFormId(localFormId);
					sectionFile.setFieldSpecId(fieldSpec.getId());
					sectionFile.setSectionInstanceId(sectionInstanceId);
				}

				if (!TextUtils.isEmpty(sectionFile.getLocalMediaPath())) {
					// check if file exists
					File file = new File(sectionFile.getLocalMediaPath());

					final String msg = "File is missing at path "
							+ sectionFile.getLocalMediaPath() + " for "
							+ viewField.getLabel();

					if (!file.exists()) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(context, msg, Toast.LENGTH_LONG)
										.show();
							}
						});

						sectionFile.setLocalMediaPath(null);
					}
				}

				viewField.setFile(sectionFile);
			}

			sectionFields.add(viewField);
		}

		section.getInstanceFieldsMap().put(sectionInstanceId, sectionFields);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		boolean workFlowEditable = false;
		boolean hasModifyFormPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_MODIFY, true);
		long localFormId;
		localFormId = getIntent().getLongExtra(Forms._ID, 0);

		WorkFlowStatusDto workFlowStatus = null;
		menu.findItem(R.id.saveWorkflow).setVisible(false);
		menu.findItem(R.id.viewWorkflowStatus).setVisible(false);

		if (localFormId != 0) {
			workFlowStatus = workFlowStatusDao
					.getWorkFlowStatusWithLocalFormId(localFormId);
			WorkFlowSpec workFlowSpecWithLocalFormId = workFlowSpecsDao
					.getWorkFlowSpecWithLocalFormId(localFormId);
			if (workFlowStatus != null) {
				if (!editMode) {
					menu.findItem(R.id.viewWorkflowStatus).setVisible(true);
				}
				menu.findItem(R.id.saveWorkflow).setVisible(false);
			} else if (workFlowSpecWithLocalFormId != null) {
				if (!editMode) {
					menu.findItem(R.id.saveWorkflow).setVisible(true);
				}
				menu.findItem(R.id.viewWorkflowStatus).setVisible(false);
			} else {
				menu.findItem(R.id.saveWorkflow).setVisible(false);
				menu.findItem(R.id.viewWorkflowStatus).setVisible(false);
			}
		}
		if (ACTION_OPEN_WORKFLOW_FORM.equals(getIntent().getAction())) {
			menu.findItem(R.id.saveWorkflow).setVisible(false);
			menu.findItem(R.id.viewWorkflowStatus).setVisible(false);
		}
		if (workFlowStatus != null) {
			WorkFlowSpec workFlowSpecWithId = workFlowSpecsDao
					.getWorkFlowSpecWithId(workFlowStatus.getWorkFlowId());
			if (workFlowSpecWithId != null) {
				workFlowEditable = workFlowSpecWithId.getEditable();
			}
			String empId = settingsDao.getString("employeeId");
			Long empLongId = null;

			if (empId != null) {
				empLongId = Long.parseLong(empId);
			}

			Long rank = employeesDao.getEmployeeRankWithId(empLongId);
			boolean isRejected = false;
			isRejected = Utils.isRejected(workFlowStatus.getCurrentRank(),
					rank, workFlowStatus.getManagerRank(),
					workFlowStatus.getEmpGroupId(),
					workFlowStatus.getStageType(), workFlowStatus.getStatus(),
					empLongId);

			if (isRejected
					&& (workFlowStatus.getStatus() != null && workFlowStatus
							.getStatus() != WorkFlowStatusDto.STATUS_CANCELLED)) {
				menu.findItem(R.id.editForm).setVisible(
						hasModifyFormPermission && !editMode
								&& workFlowEditable
								&& (isCreatedByMe() || isAssignedToMe()));
			} else if (workFlowStatus.getStatus() != null
					&& (workFlowStatus.getStatus() == WorkFlowStatusDto.STATUS_WAITING)
					&& workFlowStatus.getRemotelId() == null
					&& workFlowEditable
					&& (isCreatedByMe() || isAssignedToMe())) {
				menu.findItem(R.id.editForm).setVisible(true);
			} else {
				menu.findItem(R.id.editForm).setVisible(false);
			}
		} else {
			if (ACTION_OPEN_WORKFLOW_FORM.equals(getIntent().getAction())) {

				menu.findItem(R.id.editForm).setVisible(false);
			} else {
				// this is the default case , i.e when we created forms from
				// forms screen
				// either by clicking plus button or we came to edit already
				// submitted forms.
				menu.findItem(R.id.editForm).setVisible(
						hasModifyFormPermission && !editMode
								&& (isCreatedByMe() || isAssignedToMe()));
			}
		}
		// TODO
		// CHECK IF THIS FORM ASSOCIATED WITH WORKFLOW
		// isAlreadyExistedWorkFlowStatus();

		menu.findItem(R.id.printForm).setVisible(
				!TextUtils.isEmpty(formSpecsDao.getPrintTemplate(formSpecId))
						&& !editMode);
		menu.findItem(R.id.saveForm).setVisible(
				hasModifyFormPermission && editMode && !loadInProgress);
		menu.findItem(R.id.discardForm).setVisible(
				hasModifyFormPermission && editMode);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localFormId", localFormId);
		outState.putLong("formSpecId", formSpecId);
		outState.putBoolean("editMode", editMode);
		outState.putBoolean("modeChange", modeChange);
		outState.putSerializable("originalFields", originalFields);

		if (editMode) {
			useEditTextValues();
		}

		outState.putSerializable("currentFields", currentFields);
		outState.putString("mediaPath", mediaPath);
		outState.putSerializable("pickerViewField", pickerViewField);
		outState.putSerializable("cachedFilteringResults",
				cachedFilteringResults);
		if (getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
			outState.putInt("activeTab", getSupportActionBar()
					.getSelectedNavigationIndex());
		}

	}

	/**
	 * Adds the section title to the layout.
	 * 
	 * @param viewField
	 * @param layout
	 * @return The TextView for further customizations, if any.
	 */
	public static TextView addSectionTitle(Context context,
			SectionViewField section, LinearLayout layout,
			boolean addAsteriskToRequiredFields, boolean editMode) {
		LayoutParams layoutParams = null;

		if (editMode) {
			layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
		} else {
			layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		}

		layoutParams.leftMargin = context.getResources().getDimensionPixelSize(
				R.dimen.label_margin_left);
		layoutParams.rightMargin = context.getResources()
				.getDimensionPixelSize(R.dimen.label_margin_right);
		layoutParams.topMargin = context.getResources().getDimensionPixelSize(
				R.dimen.label_margin_top);
		layoutParams.bottomMargin = context.getResources()
				.getDimensionPixelSize(R.dimen.label_margin_bottom);

		TextView labelTV = new TextView(context);

		if (editMode) {
			labelTV.setGravity(Gravity.CENTER);
		}

		labelTV.setTextAppearance(context,
				android.R.style.TextAppearance_Medium);
		labelTV.setText(section.getSectionTitle()
				+ (addAsteriskToRequiredFields && section.getMinEntries() > 0 ? "*"
						: ""));
		layout.addView(labelTV, layoutParams);

		return labelTV;
	}

	/**
	 * Adds the label to the layout.
	 * 
	 * @param viewField
	 * @param layout
	 * @return The TextView for further customizations, if any.
	 */
	public TextView addLabel(Context context, ViewField viewField,
			LinearLayout layout, boolean addAsteriskToRequiredFields) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		layoutParams.leftMargin = context.getResources().getDimensionPixelSize(
				R.dimen.label_margin_left);
		layoutParams.rightMargin = context.getResources()
				.getDimensionPixelSize(R.dimen.label_margin_right);
		layoutParams.topMargin = context.getResources().getDimensionPixelSize(
				R.dimen.label_margin_top);
		layoutParams.bottomMargin = context.getResources()
				.getDimensionPixelSize(R.dimen.label_margin_bottom);

		TextView labelTV = new TextView(context);

		boolean computed = viewField.getComputed() == null ? false : viewField
				.getComputed();
		/*
		 * Html.fromHtml("<b>" + viewField.getLabel() +
		 * (addAsteriskToRequiredFields && viewField.getRequired() && !computed
		 * ? "*" : "") + "</b>")
		 */
		/*
		 * labelTV.setText(viewField.getLabel() + (addAsteriskToRequiredFields
		 * && viewField.getRequired() && !computed ? "*" : ""));
		 */
		labelTV.setTextAppearance(context,
				android.R.style.TextAppearance_Medium);

		if (editMode) {
			labelTV.setText(Html.fromHtml("<b>"
					+ viewField.getLabel()
					+ (addAsteriskToRequiredFields && viewField.getRequired()
							&& !computed ? "*" : "") + "</b>"));
		} else {
			labelTV.setText(viewField.getLabel()
					+ (addAsteriskToRequiredFields && viewField.getRequired()
							&& !computed ? "*" : ""));
		}

		layout.addView(labelTV, layoutParams);

		return labelTV;
	}

	public static void addSeparator(Context context, LinearLayout layout) {
		LayoutParams separatorParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, context.getResources()
						.getDimensionPixelSize(R.dimen.separator_height));
		View separator = new View(context);
		separator.setBackgroundResource(R.color.separatorBackground);
		layout.addView(separator, separatorParams);
	}

	/**
	 * Translates customerId, valueId to corresponding names. Formats date and
	 * boolean fields.
	 * 
	 * @param viewField
	 * @return
	 */
	public String getDisplayValue(ViewField viewField) {
		try {
			if (viewField.getType() == FieldSpecs.TYPE_CUSTOMER
					&& !TextUtils.isEmpty(viewField.getLocalValue())) {
				return customersDao.getCompanyNameWithLocalId(Long
						.parseLong(viewField.getLocalValue()));
			}

			if (viewField.getType() == FieldSpecs.TYPE_EMPLOYEE
					&& !TextUtils.isEmpty(viewField.getLocalValue())) {
				return employeesDao.getEmployeeNameWithLocalId(Long
						.parseLong(viewField.getLocalValue()));
			}
			if (viewField.getType() == FieldSpecs.TYPE_ENTITY
					&& !TextUtils.isEmpty(viewField.getLocalValue())) {
				return entitiesDao.getEntityName(Long.parseLong(viewField
						.getLocalValue()));
			}
			if (viewField.getType() == FieldSpecs.TYPE_MULTI_LIST) {
				String namesString = null;
				String selctedIdsAsString = viewField.getLocalValue();
				if (!TextUtils.isEmpty(selctedIdsAsString)) {
					String[] selectedIds = selctedIdsAsString.split(",");
					ArrayList<String> values = new ArrayList<String>();
					for (int i = 0; i < selectedIds.length; i++) {
						selectedIds[i] = selectedIds[i].trim();
						values.add(entitiesDao.getEntityName(Long
								.parseLong(selectedIds[i])));
					}
					namesString = TextUtils.join(", ", values);
				}
				return namesString;
			}

			if (viewField.getType() == FieldSpecs.TYPE_IMAGE
					|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
				if (!TextUtils.isEmpty(viewField.getLocalValue())) {
					return viewField.getLocalValue();
				} else {
					return viewField.getRemoteValue();
				}
			}

			if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
				switch (viewField.getType()) {
				case FieldSpecs.TYPE_DATE:
					return Utils.getDateFormat(this).format(
							SQLiteDateTimeUtils.getDate(viewField
									.getRemoteValue()));

				case FieldSpecs.TYPE_TIME:
					String timeStr = viewField.getRemoteValue();
					int hourOfDay = Integer.parseInt(timeStr.substring(0, 2));
					int minute = Integer.parseInt(timeStr.substring(3, 5));
					SimpleDateFormat displayTimeFormat = Utils
							.getTimeFormat(this);
					return displayTimeFormat.format(Utils.getTime(hourOfDay,
							minute));

				case FieldSpecs.TYPE_YES_OR_NO:
					return Boolean.parseBoolean(viewField.getRemoteValue()) ? "Yes"
							: "No";

				case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
					if (viewField instanceof SectionViewField) {
						return sectionFieldValueSpecsDao.getValue(Long
								.parseLong(viewField.getRemoteValue()));
					} else {
						return fieldValueSpecsDao.getValue(Long
								.parseLong(viewField.getRemoteValue()));
					}
				case FieldSpecs.TYPE_MULTI_SELECT_LIST:
					if (viewField instanceof SectionViewField) {
						String displayNamesString = null;
						String idsAsString = viewField.getRemoteValue();

						if (!TextUtils.isEmpty(idsAsString)) {
							String[] ids = idsAsString.split(",");
							ArrayList<String> values = new ArrayList<String>();

							for (int i = 0; i < ids.length; i++) {
								ids[i] = ids[i].trim();
								values.add(sectionFieldValueSpecsDao
										.getValue(Long.parseLong(ids[i])));
							}
							displayNamesString = TextUtils.join(", ", values);

						}
						return displayNamesString;
					} else {
						String displayNamesString = null;
						String idsAsString = viewField.getRemoteValue();
						if (!TextUtils.isEmpty(idsAsString)) {
							String[] ids = idsAsString.split(",");
							ArrayList<String> values = new ArrayList<String>();
							for (int i = 0; i < ids.length; i++) {
								ids[i] = ids[i].trim();
								values.add(fieldValueSpecsDao.getValue(Long
										.parseLong(ids[i])));
							}
							displayNamesString = TextUtils.join(", ", values);
						}
						return displayNamesString;
					}

				}

			}
		} catch (NumberFormatException e) {
			return "";
		} catch (IndexOutOfBoundsException e) {
			return "";
		} catch (RuntimeException e) {
			return "";
		}

		return viewField.getRemoteValue();
	}

	/**
	 * For customer fields, make both the label and value clickable.
	 * 
	 * @param viewField
	 * @param labelTV
	 * @param valueTV
	 */
	public static void linkify(final Context context, ViewField viewField,
			TextView labelTV, TextView valueTV) {
		switch (viewField.getType()) {
		case FieldSpecs.TYPE_EMAIL:
			valueTV.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
			break;

		case FieldSpecs.TYPE_PHONE:
			valueTV.setAutoLinkMask(Linkify.PHONE_NUMBERS);
			break;

		case FieldSpecs.TYPE_URL:
			valueTV.setAutoLinkMask(Linkify.WEB_URLS);
			break;

		case FieldSpecs.TYPE_CUSTOMER:
			if (!TextUtils.isEmpty(viewField.getLocalValue())) {
				final long localCustomerId = Long.parseLong(viewField
						.getLocalValue());
				SettingsDao settingsDao = SettingsDao.getInstance(context
						.getApplicationContext());
				CustomersDao customersDao = CustomersDao.getInstance(context
						.getApplicationContext());

				if (Utils.isMappedCustomer(settingsDao, customersDao,
						localCustomerId)) {
					valueTV.setTextColor(0xff0000ee);
					valueTV.setPaintFlags(valueTV.getPaintFlags()
							| Paint.UNDERLINE_TEXT_FLAG);

					OnClickListener listener = new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (BuildConfig.DEBUG) {
								Log.i(TAG,
										"Launching customer activity for local customer id "
												+ localCustomerId);
							}

							Intent intent = new Intent(context,
									CustomerActivity.class);
							intent.putExtra(Customers._ID, localCustomerId);
							context.startActivity(intent);
						}
					};

					valueTV.setOnClickListener(listener);
					labelTV.setOnClickListener(listener);
				} else {
					valueTV.setTextColor(0xff000000);
				}
			}

			break;
		case FieldSpecs.TYPE_EMPLOYEE:
			if (!TextUtils.isEmpty(viewField.getLocalValue())) {

				final long localEmployeeId = Long.parseLong(viewField
						.getLocalValue());
				valueTV.setTextColor(0xff0000ee);
				valueTV.setPaintFlags(valueTV.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);

				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Launching employee activity for employee id "
											+ localEmployeeId);
						}

						Intent intent = new Intent(context,
								EmployeeActivity.class);
						intent.putExtra(Employees._ID, localEmployeeId);
						context.startActivity(intent);
					}
				};

				valueTV.setOnClickListener(listener);
				labelTV.setOnClickListener(listener);

			}

			break;
		case FieldSpecs.TYPE_ENTITY:
			if (!TextUtils.isEmpty(viewField.getLocalValue())) {
				final long localEntityId = Long.parseLong(viewField
						.getLocalValue());

				valueTV.setTextColor(0xff0000ee);
				valueTV.setPaintFlags(valueTV.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);

				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Launching entity activity for local entity id "
											+ localEntityId);
						}

						Intent intent = new Intent(context,
								EntityActivity.class);
						intent.putExtra(Entities._ID, localEntityId);
						context.startActivity(intent);
					}
				};

				valueTV.setOnClickListener(listener);
				labelTV.setOnClickListener(listener);
			}

			break;

		case FieldSpecs.TYPE_MULTI_LIST:
			if (!TextUtils.isEmpty(valueTV.getTag().toString())) {
				final long localEntityId = Long.parseLong(valueTV.getTag()
						.toString());

				valueTV.setTextColor(0xff0000ee);
				valueTV.setPaintFlags(valueTV.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);

				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Launching entity activity for local entity id "
											+ localEntityId);
						}

						Intent intent = new Intent(context,
								EntityActivity.class);
						intent.putExtra(Entities._ID, localEntityId);
						context.startActivity(intent);
					}
				};

				valueTV.setOnClickListener(listener);
				// there are multiple items no mening to give navigation to the
				// label
				// labelTV.setOnClickListener(listener);
			}
		}

	}

	private LayoutParams getViewLayoutParams() {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.leftMargin = this.getResources().getDimensionPixelSize(
				R.dimen.view_margin_left);
		lp.rightMargin = this.getResources().getDimensionPixelSize(
				R.dimen.view_margin_right);
		lp.topMargin = this.getResources().getDimensionPixelSize(
				R.dimen.view_margin_top);
		lp.bottomMargin = this.getResources().getDimensionPixelSize(
				R.dimen.view_margin_bottom);

		return lp;
	}

	private LayoutParams getEditLayoutParams() {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.leftMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_left);
		lp.rightMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_right);
		lp.topMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_top);
		lp.bottomMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_bottom);

		return lp;
	}

	private LayoutParams getFlexibleEditLayoutParams() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
		lp.leftMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_left);
		lp.rightMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_right);
		lp.topMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_top);
		lp.bottomMargin = this.getResources().getDimensionPixelSize(
				R.dimen.edit_margin_bottom);

		return lp;
	}

	private LayoutParams getTwoColumnLayoutParams() {
		LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 50);

		return lp;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showData() {

		if (showTimingLog) {
			Log.v("delay", "showdata begining");
			starttime = System.currentTimeMillis();
		}

		if (editMode) {
			// if we come from view mode to edit mode, default values
			// are
			// again
			// computing
			// it should not happen
			if (modeChange) {
				modeChange = false;
				for (final ViewField viewField : currentFields) {
					if (viewField instanceof SectionViewField) {
						SectionViewField section = (SectionViewField) viewField;
						for (int instanceId : section.getInstanceFieldsMap()
								.keySet()) {
							for (SectionViewField sectionField : section
									.getInstanceFieldsMap().get(instanceId)) {
								if (sectionField.getDefaultField()) {
									isRefreshingNecessery(sectionField);
								}
							}
						}
					} else {
						if (viewField.getDefaultField()) {
							isRefreshingNecessery(viewField);
						}
					}
				}
			}
			useEditTextValues();
			refreshButton.setVisibility(View.VISIBLE);
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Number of fields: " + currentFields.size());
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.formLinearLayout);
		layout.removeAllViews();

		LayoutParams viewLayoutParams = getViewLayoutParams();
		LayoutParams editLayoutParams = getEditLayoutParams();

		for (final ViewField viewField : currentFields) {

			long fieldTime = System.currentTimeMillis();

			if (viewField.getEditable() != null && viewField.getEditable() == 0) {
				viewField.setItEnabledByCriteria(true);
			}

			if (viewField.getVisible() != null && viewField.getVisible() == 0) {
				viewField.setIsItVisibleByCriteria(true);
				continue;
			}
			List<VisibilityCriteria> visibilityCriterias = null;
			if (hasVisibilityCriteria) {
				visibilityCriterias = visibilityCriteriaDao
						.getVisibilityCriterias(viewField, FORM_FIELD);
			}
			if (getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
				PageSpec pageSpec = (PageSpec) getSupportActionBar()
						.getSelectedTab().getTag();

				if (!Utils.integersEqual(viewField.getPageId(),
						pageSpec.getPageId())) {
					continue;
				}
			}

			String value = null;

			if (!(viewField instanceof SectionViewField)) {
				value = getDisplayValue(viewField);
			}

			if (editMode) {
				if (viewField instanceof SectionViewField) {
					final SectionViewField section = (SectionViewField) viewField;
					LinearLayout horizontalLayout = new LinearLayout(this);
					horizontalLayout.setGravity(Gravity.CENTER);

					if (section.getInstanceFieldsMap().size() > 1) {
						boolean showExpandAll = false;
						boolean showCollapseAll = false;

						for (int instanceId : section.getInstanceFieldsMap()
								.keySet()) {
							for (SectionViewField sectionViewField : section
									.getInstanceFieldsMap().get(instanceId)) {
								if (sectionViewField.getCollapsed()) {
									showExpandAll = true;
								} else {
									showCollapseAll = true;
								}
							}
						}

						LayoutParams expandLP = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						expandLP.leftMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_left) * 2;
						expandLP.rightMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_right);
						expandLP.topMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_left);
						expandLP.bottomMargin = 0;

						ImageButton expandAllButton = new ImageButton(this);
						expandAllButton
								.setImageResource(R.drawable.ic_navigation_expand_inverse);
						expandAllButton.setBackgroundColor(Color.TRANSPARENT);
						expandAllButton.setPadding(0, 0, 0, 0);
						expandAllButton
								.setVisibility(showExpandAll ? View.VISIBLE
										: View.INVISIBLE);

						expandAllButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										setProgressDialog(getString(R.string.loading));
										handler.postDelayed(new Thread(
												new Runnable() {

													@Override
													public void run() {
														for (int instanceId : section
																.getInstanceFieldsMap()
																.keySet()) {
															for (SectionViewField sectionViewField : section
																	.getInstanceFieldsMap()
																	.get(instanceId)) {
																sectionViewField
																		.setCollapsed(false);
																showData();
																dismissProgrssDialog();
															}
														}
													}
												}), 250);
									}
								});

						horizontalLayout.addView(expandAllButton, expandLP);

						addSectionTitle(this, section, horizontalLayout, true,
								editMode);

						ImageButton collapseAllButton = new ImageButton(this);
						collapseAllButton
								.setImageResource(R.drawable.ic_navigation_collapse_inverse);
						collapseAllButton.setBackgroundColor(Color.TRANSPARENT);
						collapseAllButton.setPadding(0, 0, 0, 0);
						collapseAllButton
								.setVisibility(showCollapseAll ? View.VISIBLE
										: View.INVISIBLE);

						collapseAllButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										setProgressDialog(getString(R.string.loading));
										handler.postDelayed(new Thread(
												new Runnable() {

													@Override
													public void run() {
														for (int instanceId : section
																.getInstanceFieldsMap()
																.keySet()) {
															for (SectionViewField sectionViewField : section
																	.getInstanceFieldsMap()
																	.get(instanceId)) {
																sectionViewField
																		.setCollapsed(true);
																showData();
																dismissProgrssDialog();
															}
														}
													}
												}), 250);
									}
								});

						LayoutParams collapseLP = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						collapseLP.leftMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_left);
						collapseLP.rightMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_right) * 2;
						collapseLP.topMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.label_margin_left);
						collapseLP.bottomMargin = 0;

						horizontalLayout.addView(collapseAllButton, collapseLP);
					} else {
						addSectionTitle(this, section, horizontalLayout, true,
								editMode);
					}

					layout.addView(horizontalLayout);

					for (int instanceId : section.getInstanceFieldsMap()
							.keySet()) {
						showSectionData(section, instanceId);
					}

					if (section.getInstanceFieldsMap().size() < section
							.getMaxEntries()) {
						Button button = new Button(this);
						button.setText("Add");
						button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								setProgressDialog(getString(R.string.loading));

								handler.postDelayed(new Thread(new Runnable() {

									@Override
									public void run() {
										int maxInstanceId = 0;
										for (Integer instanceId : section
												.getInstanceFieldsMap()
												.keySet()) {
											if (instanceId > maxInstanceId) {
												maxInstanceId = instanceId;
											}
										}

										addSectionInstance(section,
												maxInstanceId + 1);
										showData();
										dismissProgrssDialog();
									}
								}), 250);
							}

						});

						layout.addView(button, viewLayoutParams);
					}

					addSeparator(this, layout);
					continue;
				}

				int visibility = 0;
				if (visibilityCriterias != null
						&& !visibilityCriterias.isEmpty()) {
					for (VisibilityCriteria visibilityCriteria : visibilityCriterias) {
						ViewField targetViewField = getViewField(
								visibilityCriteria.getTargetFieldExpression(),
								null);
						if (targetViewField != null) {
							int localVisibility = Utils
									.decideVisibilityBasedOnCriterias(
											targetViewField, visibilityCriteria);
							if (localVisibility == EffortProvider.VIS_TYPE_HIDE) {
								visibility = localVisibility;
								break;
							} else if (localVisibility == EffortProvider.VIS_TYPE_DISABLE) {
								visibility = localVisibility;
								continue;
							}
						}
					}
					if (visibility == EffortProvider.VIS_TYPE_HIDE) {
						viewField.setRemoteValue(null);
						viewField.setLocalValue(null);

						FormFile formFile = viewField.getFile();
						if (formFile != null) {

							formFile.setMimeType(null);
							formFile.setLocalMediaPath(null);
							formFile.setMediaId(null);
							formFile.setFileSize(null);

						}
						value = "";
						viewField.setIsItVisibleByCriteria(false);
						continue;
					} else {
						viewField.setIsItVisibleByCriteria(true);
					}

					if (visibility == EffortProvider.VIS_TYPE_DISABLE) {
						viewField.setItEnabledByCriteria(false);
					} else {
						viewField.setItEnabledByCriteria(true);
					}

				} else {
					viewField.setIsItVisibleByCriteria(true);
					viewField.setItEnabledByCriteria(true);
				}

				addLabel(this, viewField, layout, true);

				switch (viewField.getType()) {
				case FieldSpecs.TYPE_EMPLOYEE:

					final Button employeeButton = new Button(this);

					long time = System.currentTimeMillis();
					List<Long> employessIdsList = employeesDao.getEmployees();
					if (showTimingLog) {
						Log.v("delay",
								"in employees fetching "
										+ (System.currentTimeMillis() - time));
					}
					if (employessIdsList != null
							&& employessIdsList.size() == 1
							&& (viewField.getEditable() != null && viewField
									.getEditable() != 0)) {
						viewField.setLocalValue(employessIdsList.get(0) + "");
						value = getDisplayValue(viewField);
					}
					if (TextUtils.isEmpty(value)) {

						employeeButton
								.setText("Pick a "
										+ settingsDao
												.getLabel(
														Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
														Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE));
					} else {
						employeeButton.setText(value);
					}

					employeeButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = customerButton;
							pickerViewField = viewField;
							focussed = null;
							Intent intent = new Intent(FormActivity.this,
									EmployeesActivity.class);
							intent.setAction(EmployeesActivity.ACTION_PICK);
							startActivityForResult(intent, PICK_EMPLOYEE);
						}
					});
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						employeeButton.setEnabled(false);
					}
					layout.addView(employeeButton, editLayoutParams);
					viewField.setView(employeeButton);

					break;
				case FieldSpecs.TYPE_CUSTOMER:
					final Button customerButton = new Button(this);
					List<Long> customersIdsList = customersDao
							.getVisibleCustomers();

					long time1 = System.currentTimeMillis();
					if (showTimingLog) {
						Log.v("delay",
								"in customers fetching "
										+ (System.currentTimeMillis() - time1));
					}
					if (customersIdsList != null
							&& customersIdsList.size() == 1
							&& (viewField.getEditable() != null && viewField
									.getEditable() != 0)) {
						viewField.setLocalValue(customersIdsList.get(0) + "");
						value = getDisplayValue(viewField);
					}

					if (TextUtils.isEmpty(value)) {
						customerButton
								.setText("Pick a "
										+ settingsDao
												.getLabel(
														Settings.LABEL_CUSTOMER_SINGULAR_KEY,
														Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
					} else {
						customerButton.setText(value);
					}

					customerButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = customerButton;
							pickerViewField = viewField;
							focussed = null;
							Intent intent = new Intent(FormActivity.this,
									CustomersActivity.class);
							intent.setAction(CustomersActivity.ACTION_PICK);
							startActivityForResult(intent, PICK_CUSTOMER);
						}
					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						customerButton.setEnabled(false);
					}
					layout.addView(customerButton, editLayoutParams);
					viewField.setView(customerButton);
					break;

				case FieldSpecs.TYPE_ENTITY:
					final Button entityButton = new Button(this);

					long time2 = System.currentTimeMillis();
					if (!viewField.isItVisibleByCriteria()) {
						value = "";
						viewField.setLocalValue(null);
						viewField.setRemoteValue(null);
					} else {
						String query = entitiesDao
								.getFilteringCriteriaQuery(
										(ArrayList<ListFilteringCriteria>) getFilteringCriterias(
												viewField, FORM_FIELD, null),
										"", Long.parseLong(viewField
												.getTypeExtra()));
						EntityFilterResultsDto entityFilterResultsDto = cachedFilteringResults
								.get(viewField.getSelector());

						if (!TextUtils.isEmpty(viewField.getLocalValue())
								&& entityFilterResultsDto != null) {
							boolean canUseCachedResults = canUseCachedResults(
									entityFilterResultsDto.getIdList(),
									Long.parseLong(viewField.getLocalValue()));
							if (!canUseCachedResults) {
								entityFilterResultsDto = null;
							}
						}

						if (entityFilterResultsDto == null
								|| (entityFilterResultsDto != null && !query
										.equalsIgnoreCase(entityFilterResultsDto
												.getQuery()))) {
							entityFilterResultsDto = entitiesDao
									.getFilteredLocalEntityIdList(query);
						} else {
							if (showTimingLog) {
								Log.v("delay",
										"in entities fetching not executing "
												+ viewField.getSelector());
							}
						}
						entityFilterResultsDto.setSelector(viewField
								.getSelector());
						cachedFilteringResults.put(viewField.getSelector(),
								entityFilterResultsDto);
						if (showTimingLog) {
							Log.v("delay",
									"in entities fetching "
											+ viewField.getSelector()
											+ " "
											+ (System.currentTimeMillis() - time2));
						}

						List<String> idList = entityFilterResultsDto
								.getIdList();
						if (idList == null || idList.size() > 1) {
							if (Utils.getStringIndex(idList,
									viewField.getLocalValue()) == -1) {
								value = "";
								viewField.setLocalValue(null);
								viewField.setRemoteValue(null);
							}
						} else if (idList != null
								&& idList.size() == 1
								&& (viewField.getEditable() != null && viewField
										.getEditable() != 0)) {
							viewField.setLocalValue(idList.get(0));
							value = getDisplayValue(viewField);
						}
					}
					if (TextUtils.isEmpty(value)) {
						entityButton.setText("Pick " + viewField.getLabel());
					} else {
						entityButton.setText(value);
					}

					entityButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = entityButton;
							useEditTextValues();
							pickerViewField = viewField;
							focussed = null;
							List<ListFilteringCriteria> filteringCriterias = getFilteringCriterias(
									viewField, FORM_FIELD, null);
							Intent intent = new Intent(FormActivity.this,
									EntitiesActivity.class);
							intent.setAction(EntitiesActivity.ACTION_PICK);
							intent.putExtra(EffortProvider.EntitySpecs._ID,
									Long.parseLong(viewField.getTypeExtra()));
							intent.putExtra(
									"filteringCriterias",
									(ArrayList<ListFilteringCriteria>) filteringCriterias);
							startActivityForResult(intent, PICK_ENTITY);
						}
					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						entityButton.setEnabled(false);
					}
					layout.addView(entityButton, editLayoutParams);
					viewField.setView(entityButton);
					break;
				case FieldSpecs.TYPE_MULTI_LIST: {

					final Button multiListButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						multiListButton.setText("Pick " + viewField.getLabel());
					} else {
						multiListButton.setText(value);
					}

					multiListButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = entityButton;
							pickerViewField = viewField;
							focussed = null;
							String[] selectedIds = null;
							if (!TextUtils.isEmpty(viewField.getLocalValue())) {
								String selctedIdsAsString = viewField
										.getLocalValue();
								if (!TextUtils.isEmpty(selctedIdsAsString)) {
									selctedIdsAsString = selctedIdsAsString
											.replaceAll(" ", "");
									selectedIds = selctedIdsAsString.split(",");
								}
							} else if (!TextUtils.isEmpty(viewField
									.getRemoteValue())) {
								String selctedIdsAsString = viewField
										.getRemoteValue();
								if (!TextUtils.isEmpty(selctedIdsAsString)) {
									selctedIdsAsString.replace(" ", "");
									selectedIds = selctedIdsAsString.split(",");
									for (int i = 0; i < selectedIds.length; i++) {
										selectedIds[i] = selectedIds[i].trim();
										Long id = entitiesDao.getLocalId(Long
												.parseLong(selectedIds[i]));
										if (id != null) {
											selectedIds[i] = id + "";
										}
									}
								}
							}
							Intent intent = new Intent(FormActivity.this,
									EntitiesForMultiListActivity.class);
							intent.setAction(EntitiesForMultiListActivity.ACTION_PICK);
							intent.putExtra("ids", selectedIds);
							intent.putExtra(EffortProvider.EntitySpecs._ID,
									Long.parseLong(viewField.getTypeExtra()));
							startActivityForResult(intent, PICK_MULTI_LIST);
						}
					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						multiListButton.setEnabled(false);
					}
					layout.addView(multiListButton, editLayoutParams);
					viewField.setView(multiListButton);
				}
					break;

				case FieldSpecs.TYPE_IMAGE:
					final FormFile imageFileDto = viewField.getFile();

					if (imageFileDto != null
							&& (!TextUtils.isEmpty(imageFileDto
									.getLocalMediaPath()) || imageFileDto
									.getMediaId() != null)) {
						LinearLayout imageEditLayout = new LinearLayout(this);
						imageEditLayout.setOrientation(LinearLayout.HORIZONTAL);

						final Button viewImageButton = new Button(this);
						viewImageButton.setText(R.string.view_image);
						viewImageButton.setEnabled(!TextUtils
								.isEmpty(imageFileDto.getLocalMediaPath()));
						viewImageButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(imageFileDto
												.getLocalMediaPath())) {
											Intent intent = new Intent();
											intent.setAction(Intent.ACTION_VIEW);
											File file = new File(imageFileDto
													.getLocalMediaPath());
											intent.setDataAndType(
													Uri.fromFile(file),
													imageFileDto.getMimeType());
											startActivity(intent);
										}
									}
								});

						imageEditLayout.addView(viewImageButton,
								getTwoColumnLayoutParams());

						final Button deleteImageButton = new Button(this);
						deleteImageButton.setText(R.string.delete_image);
						deleteImageButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										String path = imageFileDto
												.getLocalMediaPath();
										imageFileDto.setLocalMediaPath(null);
										imageFileDto.setMediaId(null);
										showData();

										if (!TextUtils.isEmpty(path)
												&& path.startsWith(Utils
														.getEffortPath())) {

											if (isFileInUse(path)) {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is in use.");
												}
												return;
											} else {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is not in use.");
												}
											}

											File file = new File(path);

											if (file.delete()) {
												Toast.makeText(
														FormActivity.this,
														"Deleted " + path,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														FormActivity.this,
														"Could not delete "
																+ path,
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}

								});

						imageEditLayout.addView(deleteImageButton,
								getTwoColumnLayoutParams());
						layout.addView(imageEditLayout, editLayoutParams);
						if ((viewField.getEditable() != null && viewField
								.getEditable() == 0) || visibility == 2) {
							deleteImageButton.setEnabled(false);
						}
					}

					LinearLayout pickImageLayout = new LinearLayout(this);
					pickImageLayout.setOrientation(LinearLayout.HORIZONTAL);

					final Button captureImageButton = new Button(this);

					captureImageButton.setText(R.string.capture_image);

					captureImageButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									captureImage(FieldSpecs.TYPE_IMAGE);
								}
							});

					pickImageLayout.addView(captureImageButton,
							getTwoColumnLayoutParams());

					final Button pickImageButton = new Button(this);
					pickImageButton.setText(R.string.pick_image);
					pickImageButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							pickImage(PICK_IMAGE);
						}

					});
					boolean canPickFromGallary = settingsDao.getBoolean(
							Settings.KEY_PICK_FROM_GALLARY, true);
					if (!canPickFromGallary) {
						pickImageButton.setEnabled(false);
					}

					pickImageLayout.addView(pickImageButton,
							getTwoColumnLayoutParams());
					layout.addView(pickImageLayout, editLayoutParams);

					viewField.setView(captureImageButton);

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						captureImageButton.setEnabled(false);
						pickImageButton.setEnabled(false);
					}
					break;

				case FieldSpecs.TYPE_SIGNATURE:
					final FormFile signatureFileDto = viewField.getFile();

					if (!TextUtils
							.isEmpty(signatureFileDto.getLocalMediaPath())
							|| signatureFileDto.getMediaId() != null) {
						LinearLayout signatureEditLayout = new LinearLayout(
								this);
						signatureEditLayout
								.setOrientation(LinearLayout.HORIZONTAL);

						final Button viewSignatureButton = new Button(this);
						viewSignatureButton.setText(R.string.view_signature);
						viewSignatureButton.setEnabled(!TextUtils
								.isEmpty(signatureFileDto.getLocalMediaPath()));
						viewSignatureButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(signatureFileDto
												.getLocalMediaPath())) {

											Intent intent = new Intent();
											intent.setAction(Intent.ACTION_VIEW);
											File file = new File(
													signatureFileDto
															.getLocalMediaPath());
											intent.setDataAndType(Uri
													.fromFile(file),
													signatureFileDto
															.getMimeType());
											startActivity(intent);

										}
									}

								});

						signatureEditLayout.addView(viewSignatureButton,
								getTwoColumnLayoutParams());

						final Button deleteSignatureButton = new Button(this);
						deleteSignatureButton
								.setText(R.string.delete_signature);
						deleteSignatureButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										String path = signatureFileDto
												.getLocalMediaPath();
										signatureFileDto
												.setLocalMediaPath(null);
										signatureFileDto.setMediaId(null);
										showData();

										if (!TextUtils.isEmpty(path)
												&& path.startsWith(Utils
														.getEffortPath())) {

											if (isFileInUse(path)) {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is in use.");
												}

												return;
											} else {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is not in use.");
												}
											}

											File file = new File(path);
											if (file.delete()) {
												Toast.makeText(
														FormActivity.this,
														"Deleted " + path,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														FormActivity.this,
														"Could not delete "
																+ path,
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}

								});

						signatureEditLayout.addView(deleteSignatureButton,
								getTwoColumnLayoutParams());
						layout.addView(signatureEditLayout, editLayoutParams);
						if ((viewField.getEditable() != null && viewField
								.getEditable() == 0) || visibility == 2) {
							deleteSignatureButton.setEnabled(false);
						}
					}

					LinearLayout pickSignatureLayout = new LinearLayout(this);
					pickSignatureLayout.setOrientation(LinearLayout.HORIZONTAL);

					final Button signatureButton = new Button(this);
					signatureButton.setText(R.string.capture_signature);
					signatureButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							captureSignature(FieldSpecs.TYPE_SIGNATURE);
						}

					});

					pickSignatureLayout.addView(signatureButton,
							getTwoColumnLayoutParams());

					final Button pickSignatureButton = new Button(this);
					pickSignatureButton.setText(R.string.pick_signature);
					pickSignatureButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									pickSignatureImage(PICK_SIGNATURE);
								}

							});

					canPickFromGallary = settingsDao.getBoolean(
							Settings.KEY_PICK_FROM_GALLARY, true);
					if (!canPickFromGallary) {
						pickSignatureButton.setEnabled(false);
					}

					pickSignatureLayout.addView(pickSignatureButton,
							getTwoColumnLayoutParams());
					layout.addView(pickSignatureLayout, editLayoutParams);
					viewField.setView(signatureButton);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						signatureButton.setEnabled(false);
						pickSignatureButton.setEnabled(false);
					}
					break;

				case FieldSpecs.TYPE_DATE:
					final Button dateButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						dateButton.setText(R.string.pick_date);
					} else {
						dateButton.setText(value);
					}

					dateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = dateButton;
							pickerViewField = viewField;

							Calendar cal = Calendar.getInstance();

							if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
								cal.setTime(SQLiteDateTimeUtils
										.getDate(viewField.getRemoteValue()));
							}

							datePickerDialog = new EffortDatePickerDialog(0,
									FormActivity.this, FormActivity.this, cal);
							datePickerDialog.show();
						}

					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						dateButton.setEnabled(false);
					}
					if (hasFilteringCriteria || hasVisibilityCriteria) {
						dateButton.setOnFocusChangeListener(this);
					}
					layout.addView(dateButton, editLayoutParams);
					viewField.setView(dateButton);
					break;
				case FieldSpecs.TYPE_TIME:
					final Button timeButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						timeButton.setText(R.string.pick_time);
					} else {
						timeButton.setText(value);
					}

					timeButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = timeButton;
							pickerViewField = viewField;

							Calendar cal = Calendar.getInstance();
							cal.setTime(getDefaultTime());

							if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
								String timeStr = viewField.getRemoteValue();
								int hourOfDay = Integer.parseInt(timeStr
										.substring(0, 2));
								int minute = Integer.parseInt(timeStr
										.substring(3, 5));
								cal.setTime(Utils.getTime(hourOfDay, minute));
							}

							timePickerDialog = new EffortTimePickerDialog(0,
									FormActivity.this, FormActivity.this, cal);
							timePickerDialog.show();
						}

					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						timeButton.setEnabled(false);
					}
					if (hasFilteringCriteria || hasVisibilityCriteria) {
						timeButton.setOnFocusChangeListener(this);
					}
					layout.addView(timeButton, editLayoutParams);
					viewField.setView(timeButton);
					break;

				case FieldSpecs.TYPE_YES_OR_NO:
					spinnersCount = spinnersCount + 1;
					final Spinner booleanSpinner = new Spinner(this);

					String[] options = null;

					options = booleanOptionalOptions;

					ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
							this, android.R.layout.simple_spinner_item, options);
					spinnerAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					booleanSpinner.setAdapter(spinnerAdapter);

					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						booleanSpinner.setSelection(0);
					} else {
						boolean yesPicked = Boolean.parseBoolean(viewField
								.getRemoteValue());

						final int YES_INDEX = 1;
						final int NO_INDEX = 2;
						booleanSpinner.setSelection(yesPicked ? YES_INDEX
								: NO_INDEX);
					}

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						booleanSpinner.setEnabled(false);
					}
					layout.addView(booleanSpinner, editLayoutParams);
					viewField.setView(booleanSpinner);
					booleanSpinner.setOnItemSelectedListener(this);
					break;

				case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
					spinnersCount = spinnersCount + 1;
					final Spinner listSpinner = new Spinner(this);

					List<String> listOptions = null;

					listOptions = fieldValueSpecsDao.getValues(viewField
							.getFieldSpecId());
					listOptions.add(0, "Pick a value");

					ArrayAdapter<String> listSpinnerAdapter = new ArrayAdapter<String>(
							this, android.R.layout.simple_spinner_item,
							listOptions);
					listSpinnerAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					listSpinner.setAdapter(listSpinnerAdapter);

					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						listSpinner.setSelection(0);
					} else {
						listSpinner.setSelection(Utils.getPosition(listOptions,
								value));
					}

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						listSpinner.setEnabled(false);
					}
					layout.addView(listSpinner, editLayoutParams);
					viewField.setView(listSpinner);
					listSpinner.setOnItemSelectedListener(this);
					break;

				case FieldSpecs.TYPE_MULTI_SELECT_LIST: {
					String[] selectedIds = null;
					boolean[] selections;
					List<String> multiSelectOptionsList = new ArrayList<String>();

					if (viewField.getRequired()) {
						multiSelectOptionsList = fieldValueSpecsDao
								.getValues(viewField.getFieldSpecId());
					} else {
						multiSelectOptionsList = fieldValueSpecsDao
								.getValues(viewField.getFieldSpecId());
					}

					final Button selectButton = new Button(this);
					if (TextUtils.isEmpty(value)) {
						selectButton.setText("Pick " + viewField.getLabel()
								+ "(s)");
					} else {
						selectButton.setText(value);
					}
					final int length = multiSelectOptionsList.size();
					final String[] multipleoptions = new String[multiSelectOptionsList
							.size()];
					final String[] givenValues = (String[]) multiSelectOptionsList
							.toArray(multipleoptions);
					selections = new boolean[length];

					if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
						String selctedIdsAsString = viewField.getRemoteValue();
						if (!TextUtils.isEmpty(selctedIdsAsString)) {
							selctedIdsAsString = selctedIdsAsString.replace(
									" ", "");
							if (!TextUtils.isEmpty(selctedIdsAsString)) {
								selectedIds = selctedIdsAsString.split(",");
								if (selectedIds != null) {
									for (int i = 0; i < selectedIds.length; i++) {
										selectedIds[i] = selectedIds[i].trim();
										selectedIds[i] = fieldValueSpecsDao
												.getValue(Long
														.parseLong(selectedIds[i]));

										if (multiSelectOptionsList
												.contains(selectedIds[i])) {
											selections[multiSelectOptionsList
													.indexOf(selectedIds[i])] = true;
										}
									}
								}
							}
						}
					}

					final boolean tempSelection[] = selections;
					selectButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							AlertDialog.Builder builder = null;
							AlertDialog alert = null;
							builder = new AlertDialog.Builder(FormActivity.this);
							builder.setTitle(R.string.pick_values);

							builder.setMultiChoiceItems(givenValues,
									tempSelection,
									new OnMultiChoiceClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which, boolean isChecked) {
											tempSelection[which] = isChecked;
										}
									});

							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											String selectedValuesAsString = "";
											ArrayList<String> values = new ArrayList<String>();
											for (int i = 0; i < tempSelection.length; i++) {
												if (tempSelection[i]) {
													values.add(givenValues[i]);
												}
											}
											selectedValuesAsString = TextUtils
													.join(", ", values);

											if (TextUtils
													.isEmpty(selectedValuesAsString)) {
												selectButton.setText("Pick "
														+ viewField.getLabel()
														+ "(s)");
											} else {
												selectButton
														.setText(selectedValuesAsString);
											}

											if ((hasFilteringCriteria && listFilteringCriteriaDao
													.isThisFieldhasFilteringCriteria(
															formSpecId,
															viewField
																	.getSelector()))
													|| (hasVisibilityCriteria && visibilityCriteriaDao
															.isItRelatedToTargetExpression(
																	formSpecId,
																	viewField
																			.getSelector()))) {

												showProfressAndCallShowdata();
											}
										}
									});
							alert = builder.create();
							alert.show();
						}
					});
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						selectButton.setEnabled(false);
					}
					linearLayout.addView(selectButton, editLayoutParams);
					layout.addView(linearLayout);
					viewField.setView(selectButton);
				}
					break;
				case FieldSpecs.TYPE_LOCATION: {
					if (BuildConfig.DEBUG) {
						Log.v(TAG, "viewField.getType()" + viewField.getType());
					}
					final EditText editText = new EditText(this);
					Button pickLocationButton = new Button(this);
					editText.setHint(viewField.getRequired() ? "Required"
							: "Optional");
					editText.setText(value);
					editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					pickLocationButton.setText(R.string.pick_a_location);
					pickLocationButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									Intent intent = new Intent(
											FormActivity.this,
											LocationPickerFromMapActivity.class);
									try {
										if (!TextUtils.isEmpty(viewField
												.getRemoteValue())
												&& viewField.getRemoteValue()
														.indexOf(",") != -1) {
											String[] latLong = viewField
													.getRemoteValue()
													.split(",");
											intent.putExtra("latitude", Double
													.parseDouble(latLong[0]));
											intent.putExtra("longitude", Double
													.parseDouble(latLong[1]));
										}
										startActivityForResult(intent,
												PICK_LOCATION);
									} catch (NumberFormatException e) {
										e.printStackTrace();
									}
								}
							});
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					linearLayout.addView(editText, editLayoutParams);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						editText.setEnabled(false);
						pickLocationButton.setEnabled(false);
					}
					linearLayout.addView(pickLocationButton, editLayoutParams);
					layout.addView(linearLayout);
					if (hasFilteringCriteria || hasVisibilityCriteria) {
						editText.setOnFocusChangeListener(this);
					}
					editText.setTag(viewField);
					viewField.setView(editText);
					break;
				}

				default:
					final EditText editText = new EditText(this);
					final ImageButton barCodeButton = new ImageButton(this);
					boolean computed = viewField.getComputed() == null ? false
							: viewField.getComputed();
					boolean enableBarcode = viewField.getEnabledBarcode() == null ? false
							: viewField.getEnabledBarcode();
					if (computed) {
						editText.setEnabled(false);
						editText.setHint("Computed field");
					} else if (enableBarcode) {

						barCodeButton.setImageResource(R.drawable.barcode);
						barCodeButton.setBackgroundColor(Color.TRANSPARENT);
						barCodeButton.setPadding(
								0,
								0,
								this.getResources().getDimensionPixelSize(
										R.dimen.edit_margin_right), 0);
						barCodeButton
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));

						barCodeButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								pickerViewField = viewField;
								// Intent intent = new Intent(
								// "com.google.zxing.client.android.SCAN");
								Intent intent = new Intent(
										FormActivity.this,
										com.google.zxing.client.android.CaptureActivity.class);
								intent.setAction(Intents.Scan.ACTION);
								startActivityForResult(intent, READ_BARCODE);
							}

						});

					}
					editText.setHint(Utils.getHint(viewField));
					editText.setText(value);

					switch (viewField.getType()) {
					case FieldSpecs.TYPE_EMAIL:
						editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
						break;

					case FieldSpecs.TYPE_NUMBER:
					case FieldSpecs.TYPE_CURRENCY:
						editText.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_SIGNED
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);

						if (!computed && !viewField.getDefaultField()) {
							editText.addTextChangedListener(new MyTextWatcher());
						}

						break;

					case FieldSpecs.TYPE_PHONE:
						editText.setInputType(InputType.TYPE_CLASS_PHONE);
						break;

					case FieldSpecs.TYPE_URL:
						editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
						break;

					}

					// layout.addView(editText, editLayoutParams);
					// if (enableBarcode) {
					// layout.addView(barCodeButton);
					// }

					if (enableBarcode) {
						LinearLayout linearLayout = new LinearLayout(this);
						linearLayout.setOrientation(LinearLayout.HORIZONTAL);
						linearLayout.addView(editText,
								getFlexibleEditLayoutParams());
						linearLayout.addView(barCodeButton);
						layout.addView(linearLayout);
					} else {
						layout.addView(editText, editLayoutParams);
					}

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						editText.setEnabled(false);

						if (enableBarcode) {
							barCodeButton.setEnabled(false);
						}
					}

					if (hasFilteringCriteria || hasVisibilityCriteria) {
						editText.setOnFocusChangeListener(this);
						editText.setTag(viewField);
					}

					viewField.setView(editText);
				}

				addSeparator(this, layout);

			} else {
				if (viewField instanceof SectionViewField) {
					final SectionViewField section = (SectionViewField) viewField;
					addSectionTitle(this, section, layout, false, editMode);
					for (int instanceId : section.getInstanceFieldsMap()
							.keySet()) {
						showSectionData(section, instanceId);
					}

					continue;
				}

				FormFile formFile = null;

				if (viewField.getType() == FieldSpecs.TYPE_IMAGE
						|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
					formFile = formFilesDao.getFile(viewField.getFieldSpecId(),
							viewField.getLocalFormId());

					if (formFile == null
							|| (TextUtils.isEmpty(formFile.getLocalMediaPath()) && (formFile
									.getMediaId() == null || formFile
									.getMediaId() == -1))) {
						continue;
					}
				} else {
					if (TextUtils.isEmpty(value)) {
						continue;
					}
				}

				TextView labelTV = addLabel(this, viewField, layout, false);

				switch (viewField.getType()) {

				case FieldSpecs.TYPE_MULTI_LIST: {
					String selctedIdsAsString = viewField.getLocalValue();
					if (!TextUtils.isEmpty(selctedIdsAsString)) {
						String[] selectedIds = selctedIdsAsString.split(",");
						String[] labels = new String[selectedIds.length];
						for (int i = 0; i < selectedIds.length; i++) {
							selectedIds[i] = selectedIds[i].trim();
							labels[i] = entitiesDao.getEntityName(Long
									.parseLong(selectedIds[i]));
							TextView valueTV = new TextView(this);
							valueTV.setTextAppearance(this,
									android.R.style.TextAppearance_Small);
							valueTV.setText(labels[i]);
							// Did not find out some other way ro send data, so
							// just
							// add tha data by tag to the edittext
							valueTV.setTag(selectedIds[i]);
							linkify(this, viewField, labelTV, valueTV);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								valueTV.setTextIsSelectable(true);
							}

							layout.addView(valueTV, viewLayoutParams);
						}
					}
					break;
				}
				case FieldSpecs.TYPE_IMAGE:
				case FieldSpecs.TYPE_SIGNATURE:
					final FormFile fileDto = formFile;

					if (!TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
						ImageView imageView = new ImageView(this);
						imageView.setScaleType(ScaleType.CENTER_INSIDE);
						imageView.setAdjustViewBounds(true);
						imageView.setImageBitmap(Utils.decodeImageForDisplay(
								fileDto.getLocalMediaPath(), this));
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								File file = new File(fileDto
										.getLocalMediaPath());
								intent.setDataAndType(Uri.fromFile(file),
										fileDto.getMimeType());
								startActivity(intent);
							}

						});

						layout.addView(imageView, viewLayoutParams);
					}

					String action = fileDto.getActionString(this);
					if (!"Tap to view".equals(action)
							&& fileDto.getMediaId() != null) {
						Button button = new Button(this);
						button.setText(action);
						button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fileDto.getMediaId() != null) {
									fileDto.setLocalMediaPath(null);

									if (fileDto.getDownloadRequested() == null) {
										fileDto.setDownloadRequested(true);
									} else {
										fileDto.setDownloadRequested(!fileDto
												.getDownloadRequested());
									}

									formFilesDao.save(fileDto);
									startBftsIfRequired();
								}
							}

						});

						layout.addView(button, viewLayoutParams);
					}

					break;

				default:
					TextView valueTV = new TextView(this);
					valueTV.setTextAppearance(this,
							android.R.style.TextAppearance_Small);
					linkify(this, viewField, labelTV, valueTV);
					valueTV.setText(value);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						valueTV.setTextIsSelectable(true);
					}

					layout.addView(valueTV, viewLayoutParams);
				}

				addSeparator(this, layout);
			}
			if (showTimingLog) {
				Log.d("delay", "each field time " + viewField.getSelector()
						+ " " + (System.currentTimeMillis() - fieldTime));
			}
		}

		if (BuildConfig.DEBUG) {
			Log.v("focus", "show data");
		}

		if (focussed != null) {
			if (focussed instanceof EditText) {
				ViewField toBeFocussedViewField = getViewFieldFromView(focussed);

				if (toBeFocussedViewField != null
						&& toBeFocussedViewField.getView() != null) {
					toBeFocussedViewField.getView().requestFocus();
				}
			}
		}

		if (showTimingLog) {
			Log.d("delay",
					"showdata ending time "
							+ (System.currentTimeMillis() - starttime));
			starttime = System.currentTimeMillis();
		}
		if (editMode) {
			useEditTextValues();
		}
	}

	void showProfressAndCallShowdata() {
		setProgressDialog(getString(R.string.loading));
		handler.postDelayed(new Thread(new Runnable() {

			@Override
			public void run() {
				showData();
				dismissProgrssDialog();
				showDataPending = false;
			}
		}), 250);
	}

	/*
	 * void showProfressAndCallShowdata() { //
	 * setProgressDialog(getString(R.string.refreshing)); progressTask = new
	 * ProgressTask(); progressTask.execute(); handler.postDelayed(new
	 * Thread(new Runnable() {
	 * 
	 * @Override public void run() { showData(); // dismissProgrssDialog();
	 * progressTask.cancel(true); } }), 150); }
	 */

	protected List<ListFilteringCriteria> getFilteringCriterias(
			ViewField viewField, int field, Integer sectionInstanceId) {
		useEditTextValues();
		final List<ListFilteringCriteria> filteringCriterias = listFilteringCriteriaDao
				.getListFilteringCriterias(formSpecId,
						viewField.getFieldSpecId(), field);

		if (filteringCriterias != null) {
			for (ListFilteringCriteria criteria : filteringCriterias) {
				if (!TextUtils
						.isEmpty(criteria.getReferenceFieldExpressionId())) {
					ViewField valueField = getViewField(
							criteria.getReferenceFieldExpressionId(),
							sectionInstanceId);
					String valueToFilter = null;

					switch (valueField.getType()) {
					case FieldSpecs.TYPE_DATE:
					case FieldSpecs.TYPE_TIME:
					case FieldSpecs.TYPE_YES_OR_NO:
						valueToFilter = valueField.getRemoteValue();
						break;
					case FieldSpecs.TYPE_CUSTOMER:
						if (!TextUtils.isEmpty(valueField.getLocalValue())) {
							valueToFilter = ""
									+ customersDao.getRemoteId(Long
											.parseLong(valueField
													.getLocalValue()));
						}
						break;

					case FieldSpecs.TYPE_EMPLOYEE:
						if (!TextUtils.isEmpty(valueField.getLocalValue())) {
							valueToFilter = ""
									+ employeesDao.getRemoteId(Long
											.parseLong(valueField
													.getLocalValue()));
						}
						break;
					// need when list can contain list
					case FieldSpecs.TYPE_ENTITY:
						// for form field
						EntityFieldSpecsDao entityFieldSpecsDao = EntityFieldSpecsDao
								.getInstance(getApplicationContext());
						EntityFieldSpec formFieldEntityFieldSpec = entityFieldSpecsDao
								.getFieldSpec(criteria.getListFieldSpecId());
						// for list field
						FieldSpecsDao fieldSpecsDao = FieldSpecsDao
								.getInstance(getApplicationContext());
						FieldSpec fieldSpec = fieldSpecsDao
								.getFieldSpec(criteria.getFieldSpecId());

						if (!TextUtils.isEmpty(valueField.getLocalValue())
								&& formFieldEntityFieldSpec.getType() == FieldSpecs.TYPE_ENTITY
								&& fieldSpec.getType() == FieldSpecs.TYPE_ENTITY) {
							valueToFilter = ""
									+ entitiesDao.getRemoteId(Long
											.parseLong(valueField
													.getLocalValue()));
						} else {
							valueToFilter = getDisplayValue(valueField);
						}

						break;

					default:
						valueToFilter = getDisplayValue(valueField);
					}

					criteria.setValue(valueToFilter);
				}
			}
		}

		return filteringCriterias;
	}

	private ViewField getViewField(String selector, Integer sectionInstanceId) {
		for (ViewField viewField : currentFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;
				LinkedHashMap<Integer, List<SectionViewField>> linkedHashMap = section
						.getInstanceFieldsMap();
				List<SectionViewField> sectionViewFields = null;

				// when sectionInstanceId is not given, search in the first
				// instance
				if (sectionInstanceId == null) {

					if (linkedHashMap.size() > 0) {
						Object[] keys = linkedHashMap.keySet().toArray();
						sectionInstanceId = (Integer) keys[0];
					}
				}

				if (sectionInstanceId != null) {
					sectionViewFields = linkedHashMap.get(sectionInstanceId);
				}

				if (sectionViewFields != null) {
					for (SectionViewField sectionViewField : sectionViewFields) {
						if (TextUtils.equals(selector,
								sectionViewField.getSelector())) {
							return sectionViewField;
						}
					}
				}
			} else if (TextUtils.equals(selector, viewField.getSelector())) {
				return viewField;
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showSectionData(final SectionViewField section,
			final int sectionInstanceId) {
		if (editMode) {
			useEditTextValues();
		}

		final List<SectionViewField> fields = section.getInstanceFieldsMap()
				.get(sectionInstanceId);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Number of section fields: " + fields.size());
		}

		final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.formLinearLayout);
		final LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.rounded_corner_shape));

		LayoutParams viewLayoutParams = getViewLayoutParams();
		LayoutParams editLayoutParams = getEditLayoutParams();

		for (int i = 0; i < fields.size(); ++i) {
			long fieldTime = System.currentTimeMillis();

			final SectionViewField viewField = fields.get(i);

			if (viewField.getEditable() != null && viewField.getEditable() == 0) {
				viewField.setItEnabledByCriteria(true);
			}

			if (viewField.getVisible() != null && viewField.getVisible() == 0) {
				viewField.setIsItVisibleByCriteria(true);
				continue;
			}

			List<VisibilityCriteria> visibilityCriterias = null;

			if (hasVisibilityCriteria) {
				visibilityCriterias = visibilityCriteriaDao
						.getVisibilityCriterias(viewField, SECTION_FIELD);
			}

			int visibility = 0;
			String value = getDisplayValue(viewField);

			if (visibilityCriterias != null && !visibilityCriterias.isEmpty()) {
				for (VisibilityCriteria visibilityCriteria : visibilityCriterias) {
					ViewField targetViewField = getViewField(
							visibilityCriteria.getTargetFieldExpression(),
							sectionInstanceId);

					if (targetViewField != null) {
						int localVisibility = Utils
								.decideVisibilityBasedOnCriterias(
										targetViewField, visibilityCriteria);
						if (localVisibility == EffortProvider.VIS_TYPE_HIDE) {
							visibility = localVisibility;
							break;
						} else if (localVisibility == EffortProvider.VIS_TYPE_DISABLE) {
							visibility = localVisibility;
							continue;
						}
					}
				}

				if (visibility == EffortProvider.VIS_TYPE_HIDE) {
					viewField.setLocalValue(null);
					viewField.setRemoteValue(null);
					SectionFile sectionFile = (SectionFile) viewField.getFile();

					if (sectionFile != null) {

						sectionFile.setMimeType(null);
						sectionFile.setLocalMediaPath(null);
						sectionFile.setMediaId(null);
						sectionFile.setFileSize(null);

					}

					value = "";
					viewField.setLocalValue(null);
					viewField.setRemoteValue(null);
					viewField.setIsItVisibleByCriteria(false);
					continue;
				} else {
					viewField.setIsItVisibleByCriteria(true);
				}

				if (visibility == EffortProvider.VIS_TYPE_DISABLE) {
					viewField.setItEnabledByCriteria(false);
				} else {
					viewField.setItEnabledByCriteria(true);
				}

			} else {
				viewField.setIsItVisibleByCriteria(true);
				viewField.setItEnabledByCriteria(true);
			}

			if (editMode) {
				if (i == 0) {
					RelativeLayout relativeLayout = new RelativeLayout(this);

					if (section.getInstanceFieldsMap().size() > section
							.getMinEntries()) {
						ImageButton deleteButton = new ImageButton(this);
						deleteButton.setPadding(0, 0, 0, 0);
						deleteButton.setBackgroundColor(Color.TRANSPARENT);
						deleteButton
								.setImageResource(R.drawable.ic_content_remove_inverse);

						deleteButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								section.getInstanceFieldsMap().remove(
										sectionInstanceId);
								showProfressAndCallShowdata();
							}

						});

						RelativeLayout.LayoutParams left = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						left.leftMargin = this
								.getResources()
								.getDimensionPixelSize(R.dimen.edit_margin_left);
						left.rightMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.edit_margin_right);
						left.topMargin = this
								.getResources()
								.getDimensionPixelSize(R.dimen.edit_margin_left);
						left.bottomMargin = 0;
						left.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
								RelativeLayout.TRUE);
						relativeLayout.addView(deleteButton, left);
					}
					// add title in collapsed state
					if (fields != null
							&& fields.size() > 0
							&& !TextUtils
									.isEmpty(getDisplayValue(fields.get(0)))
							&& fields.get(0).getCollapsed()) {
						TextView heading = new TextView(this);
						heading.setText(getDisplayValue(fields.get(0)));
						heading.setPadding(0, 0, 0, 0);
						heading.setBackgroundColor(Color.TRANSPARENT);

						RelativeLayout.LayoutParams center = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						center.leftMargin = this
								.getResources()
								.getDimensionPixelSize(R.dimen.edit_margin_left);
						center.rightMargin = this.getResources()
								.getDimensionPixelSize(
										R.dimen.edit_margin_right);
						center.topMargin = this
								.getResources()
								.getDimensionPixelSize(R.dimen.edit_margin_left);
						center.bottomMargin = 0;
						center.addRule(RelativeLayout.CENTER_IN_PARENT);
						heading.setGravity(Gravity.CENTER_HORIZONTAL);
						relativeLayout.addView(heading, center);
					}

					final ImageButton collapseButton = new ImageButton(this);
					collapseButton
							.setImageResource(viewField.getCollapsed() ? R.drawable.ic_navigation_expand_inverse
									: R.drawable.ic_navigation_collapse_inverse);
					collapseButton.setBackgroundColor(Color.TRANSPARENT);
					collapseButton.setPadding(0, 0, 0, 0);
					collapseButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (fields != null && fields.size() > 0) {
								for (SectionViewField viewField : fields) {
									viewField.setCollapsed(!viewField
											.getCollapsed());
								}

								showProfressAndCallShowdata();
							}
						}
					});

					RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					right.leftMargin = this.getResources()
							.getDimensionPixelSize(R.dimen.edit_margin_left);
					right.rightMargin = this.getResources()
							.getDimensionPixelSize(R.dimen.edit_margin_right);
					right.topMargin = this.getResources()
							.getDimensionPixelSize(R.dimen.edit_margin_left);
					right.bottomMargin = 0;
					right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
							RelativeLayout.TRUE);
					relativeLayout.addView(collapseButton, right);

					layout.addView(relativeLayout);
				}

				addLabel(this, viewField, layout, true);
				switch (viewField.getType()) {
				case FieldSpecs.TYPE_EMPLOYEE:

					final Button employeeButton = new Button(this);

					long time = System.currentTimeMillis();
					List<Long> employessIdsList = employeesDao.getEmployees();
					if (showTimingLog) {
						Log.v("delay", "in employess fetching section "
								+ (System.currentTimeMillis() - time));
					}
					if (employessIdsList != null
							&& employessIdsList.size() == 1
							&& (viewField.getEditable() != null && viewField
									.getEditable() != 0)) {
						viewField.setLocalValue(employessIdsList.get(0) + "");
						value = getDisplayValue(viewField);
					}
					if (TextUtils.isEmpty(value)) {
						employeeButton
								.setText("Pick a "
										+ settingsDao
												.getLabel(
														Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
														Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE));
					} else {
						employeeButton.setText(value);
					}

					employeeButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = customerButton;
							pickerViewField = viewField;
							focussed = null;
							Intent intent = new Intent(FormActivity.this,
									EmployeesActivity.class);
							intent.setAction(EmployeesActivity.ACTION_PICK);
							startActivityForResult(intent, PICK_EMPLOYEE);
						}
					});
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						employeeButton.setEnabled(false);
					}
					layout.addView(employeeButton, editLayoutParams);
					viewField.setView(employeeButton);

					break;
				case FieldSpecs.TYPE_CUSTOMER:
					final Button customerButton = new Button(this);

					long time1 = System.currentTimeMillis();
					List<Long> customersIdsList = customersDao
							.getVisibleCustomers();
					if (showTimingLog) {
						Log.v("delay", "in customers fetching section "
								+ (System.currentTimeMillis() - time1));
					}
					if (customersIdsList != null
							&& customersIdsList.size() == 1
							&& (viewField.getEditable() != null && viewField
									.getEditable() != 0)) {
						viewField.setLocalValue(customersIdsList.get(0) + "");
						value = getDisplayValue(viewField);
					}

					if (TextUtils.isEmpty(value)) {
						customerButton
								.setText("Pick a "
										+ settingsDao
												.getLabel(
														Settings.LABEL_CUSTOMER_SINGULAR_KEY,
														Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
					} else {
						customerButton.setText(value);
					}

					customerButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = customerButton;
							pickerViewField = viewField;
							focussed = null;
							Intent intent = new Intent(FormActivity.this,
									CustomersActivity.class);
							intent.setAction(CustomersActivity.ACTION_PICK);
							startActivityForResult(intent, PICK_CUSTOMER);
						}
					});
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						customerButton.setEnabled(false);
					}

					layout.addView(customerButton, editLayoutParams);
					viewField.setView(customerButton);
					break;

				case FieldSpecs.TYPE_ENTITY:
					final Button entityButton = new Button(this);
					if (!viewField.isItVisibleByCriteria()) {
						value = "";
						viewField.setLocalValue(null);
						viewField.setRemoteValue(null);
					} else {
						String selector = "";
						if (viewField.getLocalValue() == null
								&& viewField.getRemoteValue() == null) {
							selector = viewField.getSelector() + "[-1]";
						} else {
							selector = viewField.getSelector()
									+ "["
									+ ((SectionViewField) viewField)
											.getSectionInstanceId() + "]";
						}
						long time2 = System.currentTimeMillis();
						String query = entitiesDao
								.getFilteringCriteriaQuery(
										(ArrayList<ListFilteringCriteria>) getFilteringCriterias(
												viewField, SECTION_FIELD,
												sectionInstanceId), "", Long
												.parseLong(viewField
														.getTypeExtra()));

						if (showTimingLog) {
							Log.v("delay", "section query " + query);
						}

						EntityFilterResultsDto entityFilterResultsDto = cachedFilteringResults
								.get(selector);

						if (!TextUtils.isEmpty(viewField.getLocalValue())
								&& entityFilterResultsDto != null) {
							boolean canUseCachedResults = canUseCachedResults(
									entityFilterResultsDto.getIdList(),
									Long.parseLong(viewField.getLocalValue()));
							if (!canUseCachedResults) {
								entityFilterResultsDto = null;
							}
						}

						if (entityFilterResultsDto == null
								|| (entityFilterResultsDto != null && !query
										.equalsIgnoreCase(entityFilterResultsDto
												.getQuery()))) {
							entityFilterResultsDto = entitiesDao
									.getFilteredLocalEntityIdList(query);
						} else {
							if (showTimingLog) {
								Log.v("delay",
										"in entities fetching section not executing "
												+ selector);
							}
						}
						entityFilterResultsDto.setSelector(selector);
						cachedFilteringResults.put(selector,
								entityFilterResultsDto);

						List<String> idList = entityFilterResultsDto
								.getIdList();
						// List<String> idList = entitiesDao
						// .getFilteredLocalEntityIdList(
						// (ArrayList<ListFilteringCriteria>)
						// getFilteringCriterias(
						// viewField, SECTION_FIELD,
						// sectionInstanceId), "",
						// Long.parseLong(viewField.getTypeExtra()));

						if (showTimingLog) {
							Log.v("delay",
									"in entities fetching section "
											+ selector
											+ " "
											+ (System.currentTimeMillis() - time2));
						}

						if (idList == null || idList.size() > 1) {
							if (Utils.getStringIndex(idList,
									viewField.getLocalValue()) == -1) {
								value = "";
								viewField.setLocalValue(null);
								viewField.setRemoteValue(null);
							}
						} else if (idList != null
								&& idList.size() == 1
								&& (viewField.getEditable() != null && viewField
										.getEditable() != 0)) {
							viewField.setLocalValue(idList.get(0));
							value = getDisplayValue(viewField);
						}
					}
					if (TextUtils.isEmpty(value)) {
						entityButton.setText("Pick " + viewField.getLabel());
					} else {
						entityButton.setText(value);
					}

					entityButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = entityButton;
							useEditTextValues();
							pickerViewField = viewField;
							focussed = null;
							List<ListFilteringCriteria> filteringCriterias = getFilteringCriterias(
									viewField, SECTION_FIELD, sectionInstanceId);
							Intent intent = new Intent(FormActivity.this,
									EntitiesActivity.class);
							intent.setAction(EntitiesActivity.ACTION_PICK);
							intent.putExtra(EffortProvider.EntitySpecs._ID,
									Long.parseLong(viewField.getTypeExtra()));
							intent.putExtra(
									"filteringCriterias",
									(ArrayList<ListFilteringCriteria>) filteringCriterias);
							startActivityForResult(intent, PICK_ENTITY);
						}
					});
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						entityButton.setEnabled(false);
					}

					layout.addView(entityButton, editLayoutParams);
					viewField.setView(entityButton);
					break;
				case FieldSpecs.TYPE_MULTI_LIST: {

					final Button multiListButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						multiListButton.setText("Pick " + viewField.getLabel());
					} else {
						multiListButton.setText(value);
					}

					multiListButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = entityButton;
							pickerViewField = viewField;
							focussed = null;
							String[] selectedIds = null;
							if (!TextUtils.isEmpty(viewField.getLocalValue())) {
								String selctedIdsAsString = viewField
										.getLocalValue();
								if (!TextUtils.isEmpty(selctedIdsAsString)) {
									selctedIdsAsString = selctedIdsAsString
											.replaceAll(" ", "");
									selectedIds = selctedIdsAsString.split(",");
								}
							} else if (!TextUtils.isEmpty(viewField
									.getRemoteValue())) {
								String selctedIdsAsString = viewField
										.getRemoteValue();
								if (!TextUtils.isEmpty(selctedIdsAsString)) {
									selctedIdsAsString.replace(" ", "");
									selectedIds = selctedIdsAsString.split(",");
									for (int i = 0; i < selectedIds.length; i++) {
										selectedIds[i] = selectedIds[i].trim();
										Long id = entitiesDao.getLocalId(Long
												.parseLong(selectedIds[i]));
										if (id != null) {
											selectedIds[i] = id + "";
										}

									}
								}
							}
							Intent intent = new Intent(FormActivity.this,
									EntitiesForMultiListActivity.class);
							intent.setAction(EntitiesForMultiListActivity.ACTION_PICK);
							intent.putExtra("ids", selectedIds);
							intent.putExtra(EffortProvider.EntitySpecs._ID,
									Long.parseLong(viewField.getTypeExtra()));
							startActivityForResult(intent, PICK_MULTI_LIST);
						}
					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						multiListButton.setEnabled(false);
					}
					layout.addView(multiListButton, editLayoutParams);
					viewField.setView(multiListButton);
				}
					break;
				case FieldSpecs.TYPE_IMAGE:
					final FormFile imageFileDto = viewField.getFile();

					if (!TextUtils.isEmpty(imageFileDto.getLocalMediaPath())
							|| imageFileDto.getMediaId() != null) {
						LinearLayout imageEditLayout = new LinearLayout(this);
						imageEditLayout.setOrientation(LinearLayout.HORIZONTAL);

						final Button viewImageButton = new Button(this);
						viewImageButton.setText(R.string.view_image);
						viewImageButton.setEnabled(!TextUtils
								.isEmpty(imageFileDto.getLocalMediaPath()));
						viewImageButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(imageFileDto
												.getLocalMediaPath())) {
											Intent intent = new Intent();
											intent.setAction(Intent.ACTION_VIEW);
											File file = new File(imageFileDto
													.getLocalMediaPath());
											intent.setDataAndType(
													Uri.fromFile(file),
													imageFileDto.getMimeType());
											startActivity(intent);
										}
									}
								});

						imageEditLayout.addView(viewImageButton,
								getTwoColumnLayoutParams());

						final Button deleteImageButton = new Button(this);
						deleteImageButton.setText(R.string.delete_image);
						deleteImageButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										String path = imageFileDto
												.getLocalMediaPath();
										imageFileDto.setLocalMediaPath(null);
										imageFileDto.setMediaId(null);
										showData();

										if (!TextUtils.isEmpty(path)
												&& path.startsWith(Utils
														.getEffortPath())) {

											if (isFileInUse(path)) {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is in use.");
												}
												return;
											} else {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is not in use.");
												}
											}

											File file = new File(path);

											if (file.delete()) {
												Toast.makeText(
														FormActivity.this,
														"Deleted " + path,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														FormActivity.this,
														"Could not delete "
																+ path,
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}

								});

						imageEditLayout.addView(deleteImageButton,
								getTwoColumnLayoutParams());
						layout.addView(imageEditLayout, editLayoutParams);
						if ((viewField.getEditable() != null && viewField
								.getEditable() == 0) || visibility == 2) {
							deleteImageButton.setEnabled(false);
						}
					}

					LinearLayout pickImageLayout = new LinearLayout(this);
					pickImageLayout.setOrientation(LinearLayout.HORIZONTAL);

					final Button captureImageButton = new Button(this);

					captureImageButton.setText(R.string.capture_image);

					captureImageButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									captureImage(FieldSpecs.TYPE_IMAGE);
								}
							});

					pickImageLayout.addView(captureImageButton,
							getTwoColumnLayoutParams());

					final Button pickImageButton = new Button(this);
					pickImageButton.setText(R.string.pick_image);
					pickImageButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							pickImage(PICK_IMAGE);
						}

					});

					boolean canPickFromGallary = settingsDao.getBoolean(
							Settings.KEY_PICK_FROM_GALLARY, true);
					if (!canPickFromGallary) {
						pickImageButton.setEnabled(false);
					}

					pickImageLayout.addView(pickImageButton,
							getTwoColumnLayoutParams());
					layout.addView(pickImageLayout, editLayoutParams);
					viewField.setView(captureImageButton);

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						pickImageButton.setEnabled(false);
						captureImageButton.setEnabled(false);
					}
					break;

				case FieldSpecs.TYPE_SIGNATURE:
					final FormFile signatureFileDto = viewField.getFile();

					if (!TextUtils
							.isEmpty(signatureFileDto.getLocalMediaPath())
							|| signatureFileDto.getMediaId() != null) {
						LinearLayout signatureEditLayout = new LinearLayout(
								this);
						signatureEditLayout
								.setOrientation(LinearLayout.HORIZONTAL);

						final Button viewSignatureButton = new Button(this);
						viewSignatureButton.setText(R.string.view_signature);
						viewSignatureButton.setEnabled(!TextUtils
								.isEmpty(signatureFileDto.getLocalMediaPath()));
						viewSignatureButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(signatureFileDto
												.getLocalMediaPath())) {

											Intent intent = new Intent();
											intent.setAction(Intent.ACTION_VIEW);
											File file = new File(
													signatureFileDto
															.getLocalMediaPath());
											intent.setDataAndType(Uri
													.fromFile(file),
													signatureFileDto
															.getMimeType());
											startActivity(intent);

										}
									}

								});

						signatureEditLayout.addView(viewSignatureButton,
								getTwoColumnLayoutParams());

						final Button deleteSignatureButton = new Button(this);
						deleteSignatureButton
								.setText(R.string.delete_signature);
						deleteSignatureButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										String path = signatureFileDto
												.getLocalMediaPath();
										signatureFileDto
												.setLocalMediaPath(null);
										signatureFileDto.setMediaId(null);
										showData();

										if (!TextUtils.isEmpty(path)
												&& path.startsWith(Utils
														.getEffortPath())) {

											if (isFileInUse(path)) {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is in use.");
												}

												return;
											} else {
												if (BuildConfig.DEBUG) {
													Log.i(TAG, path
															+ " is not in use.");
												}
											}

											File file = new File(path);
											if (file.delete()) {
												Toast.makeText(
														FormActivity.this,
														"Deleted " + path,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(
														FormActivity.this,
														"Could not delete "
																+ path,
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}

								});

						signatureEditLayout.addView(deleteSignatureButton,
								getTwoColumnLayoutParams());
						layout.addView(signatureEditLayout, editLayoutParams);
						if ((viewField.getEditable() != null && viewField
								.getEditable() == 0) || visibility == 2) {
							deleteSignatureButton.setEnabled(false);
						}
					}

					LinearLayout pickSignatureLayout = new LinearLayout(this);
					pickSignatureLayout.setOrientation(LinearLayout.HORIZONTAL);

					final Button signatureButton = new Button(this);
					signatureButton.setText(R.string.capture_signature);
					signatureButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							captureSignature(FieldSpecs.TYPE_SIGNATURE);
						}

					});

					pickSignatureLayout.addView(signatureButton,
							getTwoColumnLayoutParams());

					final Button pickSignatureButton = new Button(this);
					pickSignatureButton.setText(R.string.pick_signature);
					pickSignatureButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									pickSignatureImage(PICK_SIGNATURE);
								}

							});

					canPickFromGallary = settingsDao.getBoolean(
							Settings.KEY_PICK_FROM_GALLARY, true);
					if (!canPickFromGallary) {
						pickSignatureButton.setEnabled(false);
					}

					pickSignatureLayout.addView(pickSignatureButton,
							getTwoColumnLayoutParams());
					layout.addView(pickSignatureLayout, editLayoutParams);
					viewField.setView(signatureButton);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						signatureButton.setEnabled(false);
						pickSignatureButton.setEnabled(false);
					}
					break;

				case FieldSpecs.TYPE_DATE:
					final Button dateButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						dateButton.setText(R.string.pick_date);
					} else {
						dateButton.setText(value);
					}

					dateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = dateButton;
							pickerViewField = viewField;

							Calendar cal = Calendar.getInstance();

							if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
								cal.setTime(SQLiteDateTimeUtils
										.getDate(viewField.getRemoteValue()));
							}

							datePickerDialog = new EffortDatePickerDialog(0,
									FormActivity.this, FormActivity.this, cal);
							datePickerDialog.show();
						}

					});

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						dateButton.setEnabled(false);
					}
					layout.addView(dateButton, editLayoutParams);
					viewField.setView(dateButton);
					break;

				case FieldSpecs.TYPE_TIME:
					final Button timeButton = new Button(this);

					if (TextUtils.isEmpty(value)) {
						timeButton.setText(R.string.pick_time);
					} else {
						timeButton.setText(value);
					}

					timeButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// pickerButton = timeButton;
							pickerViewField = viewField;

							Calendar cal = Calendar.getInstance();
							cal.setTime(getDefaultTime());

							if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
								String timeStr = viewField.getRemoteValue();
								int hourOfDay = Integer.parseInt(timeStr
										.substring(0, 2));
								int minute = Integer.parseInt(timeStr
										.substring(3, 5));
								cal.setTime(Utils.getTime(hourOfDay, minute));
							}

							timePickerDialog = new EffortTimePickerDialog(0,
									FormActivity.this, FormActivity.this, cal);
							timePickerDialog.show();
						}

					});
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						timeButton.setEnabled(false);
					}

					layout.addView(timeButton, editLayoutParams);
					viewField.setView(timeButton);
					break;

				case FieldSpecs.TYPE_YES_OR_NO:
					spinnersCount = spinnersCount + 1;
					final Spinner booleanSpinner = new Spinner(this);

					String[] options = null;

					options = booleanOptionalOptions;

					ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
							this, android.R.layout.simple_spinner_item, options);
					spinnerAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					booleanSpinner.setAdapter(spinnerAdapter);

					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						booleanSpinner.setSelection(0);
					} else {
						boolean yesPicked = Boolean.parseBoolean(viewField
								.getRemoteValue());
						final int YES_INDEX = 1;
						final int NO_INDEX = 2;

						booleanSpinner.setSelection(yesPicked ? YES_INDEX
								: NO_INDEX);

					}

					layout.addView(booleanSpinner, editLayoutParams);
					viewField.setView(booleanSpinner);
					booleanSpinner.setOnItemSelectedListener(this);

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						booleanSpinner.setEnabled(false);
					}
					break;

				case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
					spinnersCount = spinnersCount + 1;
					final Spinner listSpinner = new Spinner(this);

					List<String> listOptions = null;

					listOptions = sectionFieldValueSpecsDao.getValues(viewField
							.getFieldSpecId());
					listOptions.add(0, "Pick a value");

					ArrayAdapter<String> listSpinnerAdapter = new ArrayAdapter<String>(
							this, android.R.layout.simple_spinner_item,
							listOptions);
					listSpinnerAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					listSpinner.setAdapter(listSpinnerAdapter);

					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						listSpinner.setSelection(0);
					} else {
						listSpinner.setSelection(Utils.getPosition(listOptions,
								value));
					}

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						listSpinner.setEnabled(false);
					}
					layout.addView(listSpinner, editLayoutParams);
					viewField.setView(listSpinner);
					listSpinner.setOnItemSelectedListener(this);
					break;
				case FieldSpecs.TYPE_MULTI_SELECT_LIST: {
					String[] selectedIds = null;
					boolean[] selections;
					List<String> multiSelectOptionsList = new ArrayList<String>();

					if (viewField.getRequired()) {
						multiSelectOptionsList = sectionFieldValueSpecsDao
								.getValues(viewField.getFieldSpecId());
					} else {
						multiSelectOptionsList = sectionFieldValueSpecsDao
								.getValues(viewField.getFieldSpecId());
						// multiSelectOptionsList.add(0,
						// getResources().getString(R.string.pick_values));
					}

					final Button selectButton = new Button(this);
					if (TextUtils.isEmpty(value)) {
						selectButton.setText("Pick " + viewField.getLabel()
								+ "(s)");
					} else {
						selectButton.setText(value);
					}
					final int length = multiSelectOptionsList.size();
					final String[] multipleoptions = new String[multiSelectOptionsList
							.size()];
					final String[] givenValues = (String[]) multiSelectOptionsList
							.toArray(multipleoptions);
					selections = new boolean[length];
					if (!TextUtils.isEmpty(viewField.getRemoteValue())) {
						String selctedIdsAsString = viewField.getRemoteValue();
						if (!TextUtils.isEmpty(selctedIdsAsString)) {
							selctedIdsAsString = selctedIdsAsString.replace(
									" ", "");
							selectedIds = selctedIdsAsString.split(",");
							if (selectedIds != null) {
								for (int j = 0; j < selectedIds.length; j++) {
									selectedIds[j] = selectedIds[j].trim();
									selectedIds[j] = sectionFieldValueSpecsDao
											.getValue(Long
													.parseLong(selectedIds[j]));

									if (multiSelectOptionsList
											.contains(selectedIds[j])) {
										selections[multiSelectOptionsList
												.indexOf(selectedIds[j])] = true;
									}
								}
							}
						}
					}

					final boolean tempSelection[] = selections;
					selectButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							pickerViewField = viewField;
							AlertDialog.Builder builder = null;
							AlertDialog alert = null;
							builder = new AlertDialog.Builder(FormActivity.this);
							builder.setTitle(R.string.pick_values);

							builder.setMultiChoiceItems(givenValues,
									tempSelection,
									new OnMultiChoiceClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which, boolean isChecked) {
											tempSelection[which] = isChecked;
										}
									});

							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											String selectedValuesAsString = "";
											ArrayList<String> values = new ArrayList<String>();

											for (int i = 0; i < tempSelection.length; i++) {
												if (tempSelection[i]) {
													values.add(givenValues[i]);

												}
											}
											selectedValuesAsString = TextUtils
													.join(", ", values);
											if (TextUtils
													.isEmpty(selectedValuesAsString)) {
												selectButton.setText("Pick "
														+ viewField.getLabel()
														+ "(s)");
											} else {
												selectButton
														.setText(selectedValuesAsString);
											}
											if ((hasFilteringCriteria && listFilteringCriteriaDao
													.isThisFieldhasFilteringCriteria(
															formSpecId,
															viewField
																	.getSelector()))
													|| (hasVisibilityCriteria && visibilityCriteriaDao
															.isItRelatedToTargetExpression(
																	formSpecId,
																	viewField
																			.getSelector()))) {
												showProfressAndCallShowdata();
											}
										}
									});
							alert = builder.create();
							alert.show();
						}
					});
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						selectButton.setEnabled(false);
					}
					linearLayout.addView(selectButton, editLayoutParams);
					layout.addView(linearLayout);
					viewField.setView(selectButton);
				}
					break;

				case FieldSpecs.TYPE_LOCATION: {
					if (BuildConfig.DEBUG) {
						Log.v(TAG, "viewField.getType()" + viewField.getType());
					}
					final EditText editText = new EditText(this);
					Button pickLocationButton = new Button(this);
					editText.setHint(viewField.getRequired() ? "Required"
							: "Optional");
					editText.setText(value);
					editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					pickLocationButton.setText(R.string.pick_a_location);
					pickLocationButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									pickerViewField = viewField;
									Intent intent = new Intent(
											FormActivity.this,
											LocationPickerFromMapActivity.class);
									try {
										if (!TextUtils.isEmpty(viewField
												.getRemoteValue())
												&& viewField.getRemoteValue()
														.indexOf(",") != -1) {
											String[] latLong = viewField
													.getRemoteValue()
													.split(",");
											intent.putExtra("latitude", Double
													.parseDouble(latLong[0]));
											intent.putExtra("longitude", Double
													.parseDouble(latLong[1]));
										}
										startActivityForResult(intent,
												PICK_LOCATION);
									} catch (NumberFormatException e) {
										e.printStackTrace();
									}
								}
							});
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					linearLayout.addView(editText, editLayoutParams);
					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						editText.setEnabled(false);
						pickLocationButton.setEnabled(false);
					}
					linearLayout.addView(pickLocationButton, editLayoutParams);
					layout.addView(linearLayout);
					if (hasFilteringCriteria || hasVisibilityCriteria) {
						editText.setOnFocusChangeListener(this);
					}
					editText.setTag(viewField);
					viewField.setView(editText);
					break;
				}
				default:
					EditText editText = new EditText(this);
					final ImageButton barCodeButton = new ImageButton(this);

					boolean computed = viewField.getComputed() == null ? false
							: viewField.getComputed();
					boolean enableBarcode = viewField.getEnabledBarcode() == null ? false
							: viewField.getEnabledBarcode();

					if (computed) {
						editText.setEnabled(false);
						editText.setHint("Computed field");
					} else if (enableBarcode) {

						barCodeButton
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));
						barCodeButton.setBackgroundColor(Color.TRANSPARENT);
						barCodeButton.setPadding(
								0,
								0,
								this.getResources().getDimensionPixelSize(
										R.dimen.edit_margin_right), 0);
						barCodeButton.setImageResource(R.drawable.barcode);
						barCodeButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// pickerButton = barCodeButton;
								pickerViewField = viewField;
								Intent intent = new Intent(
										FormActivity.this,
										com.google.zxing.client.android.CaptureActivity.class);
								intent.setAction(Intents.Scan.ACTION);
								startActivityForResult(intent, READ_BARCODE);
							}

						});

					}
					editText.setHint(Utils.getHint(viewField));
					editText.setText(value);

					switch (viewField.getType()) {
					case FieldSpecs.TYPE_EMAIL:
						editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
						break;

					case FieldSpecs.TYPE_NUMBER:
					case FieldSpecs.TYPE_CURRENCY:
						editText.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_SIGNED
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);

						if (!computed && !viewField.getDefaultField()) {
							editText.addTextChangedListener(new MyTextWatcher());
						}

						break;

					case FieldSpecs.TYPE_PHONE:
						editText.setInputType(InputType.TYPE_CLASS_PHONE);
						break;

					case FieldSpecs.TYPE_URL:
						editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
						break;

					}

					if (enableBarcode) {
						LinearLayout linearLayout = new LinearLayout(this);
						linearLayout.setOrientation(LinearLayout.HORIZONTAL);
						linearLayout.addView(editText,
								getFlexibleEditLayoutParams());
						linearLayout.addView(barCodeButton);
						layout.addView(linearLayout);
					} else {
						layout.addView(editText, editLayoutParams);
					}

					if ((viewField.getEditable() != null && viewField
							.getEditable() == 0) || visibility == 2) {
						editText.setEnabled(false);

						if (enableBarcode) {
							barCodeButton.setEnabled(false);
						}
					}

					if (hasFilteringCriteria || hasVisibilityCriteria) {
						editText.setOnFocusChangeListener(this);
						editText.setTag(viewField);
					}

					viewField.setView(editText);
				}

				if (i < fields.size() - 1) {
					addSeparator(this, layout);
				}
			} else {
				SectionFile sectionFile = null;

				if (viewField.getType() == FieldSpecs.TYPE_IMAGE
						|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
					sectionFile = sectionFilesDao.getFile(
							viewField.getFieldSpecId(),
							viewField.getLocalFormId(), sectionInstanceId);

					if (sectionFile == null
							|| (TextUtils.isEmpty(sectionFile
									.getLocalMediaPath()) && (sectionFile
									.getMediaId() == null || sectionFile
									.getMediaId() == -1))) {
						continue;
					}
				} else {
					if (TextUtils.isEmpty(value)) {
						continue;
					}
				}

				TextView labelTV = addLabel(this, viewField, layout, false);

				switch (viewField.getType()) {

				case FieldSpecs.TYPE_MULTI_LIST: {
					String selctedIdsAsString = viewField.getLocalValue();
					if (!TextUtils.isEmpty(selctedIdsAsString)) {
						String[] selectedIds = selctedIdsAsString.split(",");
						String[] labels = new String[selectedIds.length];
						for (int idsIndex = 0; idsIndex < selectedIds.length; idsIndex++) {
							selectedIds[idsIndex] = selectedIds[idsIndex]
									.trim();
							labels[idsIndex] = entitiesDao.getEntityName(Long
									.parseLong(selectedIds[idsIndex]));
							TextView valueTV = new TextView(this);
							valueTV.setTextAppearance(this,
									android.R.style.TextAppearance_Small);
							valueTV.setText(labels[idsIndex]);
							// Did not find out some other way ro send data, so
							// just
							// add tha data by tag to the edittext
							valueTV.setTag(selectedIds[idsIndex]);
							linkify(this, viewField, labelTV, valueTV);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								valueTV.setTextIsSelectable(true);
							}

							layout.addView(valueTV, viewLayoutParams);
						}
					}
					break;
				}
				case FieldSpecs.TYPE_IMAGE:
				case FieldSpecs.TYPE_SIGNATURE:
					final SectionFile fileDto = sectionFile;

					if (!TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
						ImageView imageView = new ImageView(this);
						imageView.setScaleType(ScaleType.CENTER_INSIDE);
						imageView.setAdjustViewBounds(true);
						imageView.setImageBitmap(Utils.decodeImageForDisplay(
								fileDto.getLocalMediaPath(), this));
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								File file = new File(fileDto
										.getLocalMediaPath());
								intent.setDataAndType(Uri.fromFile(file),
										fileDto.getMimeType());
								startActivity(intent);
							}

						});

						layout.addView(imageView, viewLayoutParams);
					}

					String action = fileDto.getActionString(this);
					if (!"Tap to view".equals(action)
							&& fileDto.getMediaId() != null) {
						Button button = new Button(this);
						button.setText(action);
						button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fileDto.getMediaId() != null) {
									fileDto.setLocalMediaPath(null);

									if (fileDto.getDownloadRequested() == null) {
										fileDto.setDownloadRequested(true);
									} else {
										fileDto.setDownloadRequested(!fileDto
												.getDownloadRequested());
									}

									sectionFilesDao.save(fileDto);
									startBftsIfRequired();
								}
							}

						});

						layout.addView(button, viewLayoutParams);
					}

					break;

				default:
					TextView valueTV = new TextView(this);
					valueTV.setTextAppearance(this,
							android.R.style.TextAppearance_Small);
					linkify(this, viewField, labelTV, valueTV);
					valueTV.setText(value);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						valueTV.setTextIsSelectable(true);
					}

					layout.addView(valueTV, viewLayoutParams);
				}

				if (i < fields.size() - 1) {
					addSeparator(this, layout);
				}// /////
			}

			if (showTimingLog) {
				Log.d("delay", "each field time " + viewField.getSelector()
						+ " " + (System.currentTimeMillis() - fieldTime));
			}
		}

		parentLayout.addView(layout, viewLayoutParams);

		if (editMode && fields != null && fields.size() > 0) {
			for (int i = 0; i < layout.getChildCount(); ++i) {
				View child = layout.getChildAt(i);

				if (!(child instanceof RelativeLayout)) {
					// if the first field is collapsed, assume that
					// the entire section is collapsed
					if (fields.get(0).getCollapsed()) {
						child.setVisibility(View.GONE);
					} else {
						child.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onOptionsItemSelected: groupId=" + item.getGroupId()
					+ ", itemId=" + item.getItemId());
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}

			onBackPressed();
			break;

		case R.id.editForm:
			modeChange = true;
			Long id = formSpecsDao.getLatestFormSpecId(formSpecId);
			if (formSpecId != id) {
				// editing an older version of the form
				finish();

				Intent intent = new Intent(this, FormActivity.class);
				intent.putExtra(EffortProvider.Forms._ID, localFormId);
				intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, getIntent()
						.getLongExtra(Forms.FORM_SPEC_ID, 0));
				intent.putExtra("editMode", true);
				intent.putExtra("modeChange", modeChange);
				startActivity(intent);
			} else {
				// editing the latest version of the form
				editMode = true;
				originalFields = getFields(fieldSpecs);
				currentFields = getFields(fieldSpecs);

				// invalidate to show save and discard buttons
				supportInvalidateOptionsMenu();
				showData();
			}

			break;

		case R.id.saveForm:

			if (ACTION_OPEN_WORKFLOW_FORM.equals(getIntent().getAction())) {
				saveNormalForm(false);
			} else {
				performValidationAndSave();
			}

			break;

		case R.id.discardForm:
			discard();
			break;

		case R.id.printForm:
			print();
			break;
		case R.id.saveWorkflow:
			saveWorkFlowWithDialogue();
			break;

		case R.id.viewWorkflowStatus:
			viewWorkFlowStatus();
			finish();
			break;
		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void saveWorkFlowWithDialogue() {
		new AlertDialog.Builder(context)
				.setTitle("Submit for approval?")
				.setMessage(
						"Do you want to submit this " + singular
								+ " for approval process?")
				.setPositiveButton("Submit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								saveWorkFlow(localFormId);
								finish();
								// saveNormalForm(false);
							}
						})
				.setNegativeButton("Don't submit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
							}
						}).show();
	}

	private void viewWorkFlowStatus() {
		Intent intent = new Intent(this, WorkFlowDetailsActivity.class);
		WorkFlowStatusDto workFlowStatus = workFlowStatusDao
				.getWorkFlowStatusWithLocalFormId(localFormId);
		if (workFlowStatus == null) {
			return;
		}
		// WorkFlowSpec workFlowSpec = workFlowSpecsDao
		// .getWorkFlowSpecWithLocalFormId(localFormId);
		formsDao.getFormSpecId(localFormId);
		formSpecsDao.getFormTitle(formsDao.getFormSpecId(localFormId));

		intent.putExtra("localFormId", localFormId);
		intent.putExtra("workflowName",
				formSpecsDao.getFormTitle(formsDao.getFormSpecId(localFormId)));
		intent.putExtra("formIdentifier", "");
		intent.putExtra("stageName", workFlowStatus.getStageName() + "("
				+ workFlowStatus.getStatusMessage() + ")");

		startActivity(intent);
	}

	private void useEditTextValue(ViewField viewField) {
		View view = viewField.getView();

		if (view instanceof EditText) {
			try {
				EditText editText = (EditText) view;
				viewField.setRemoteValue(Utils.getString(editText));
				if (viewField.getType() == FieldSpecs.TYPE_CURRENCY
						&& !TextUtils.isEmpty(viewField.getRemoteValue())) {
					Double value = Double.parseDouble(viewField
							.getRemoteValue());

					if (value != null) {
						DecimalFormat f = new DecimalFormat("##.00");
						viewField.setRemoteValue(f.format(value));
					} else {
						viewField.setRemoteValue(null);
					}
				}
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		} else if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			String value = String.valueOf(spinner.getSelectedItem());

			if (viewField.getType() == FieldSpecs.TYPE_YES_OR_NO) {
				if ("Yes".equals(value)) {
					viewField.setRemoteValue("true");
				} else if ("No".equals(value)) {
					viewField.setRemoteValue("false");
				} else {
					viewField.setRemoteValue(null);
				}
			} else if (viewField.getType() == FieldSpecs.TYPE_SINGLE_SELECT_LIST) {
				Long id = null;

				if (viewField instanceof SectionViewField) {
					id = sectionFieldValueSpecsDao.getId(
							viewField.getFieldSpecId(), value);
				} else {
					id = fieldValueSpecsDao.getId(viewField.getFieldSpecId(),
							value);
				}

				if (id != null) {
					viewField.setRemoteValue("" + id);
				} else {
					viewField.setRemoteValue(null);
				}
			}

		} else if (viewField.getType() == FieldSpecs.TYPE_MULTI_SELECT_LIST) {

			if (view instanceof Button) {
				String selctedValuesAsString = ((Button) view).getText()
						.toString();
				String idsString = null;
				if (!TextUtils.isEmpty(selctedValuesAsString)) {
					String[] selectedValues = selctedValuesAsString.split(",");
					ArrayList<String> values = new ArrayList<String>();

					for (int i = 0; i < selectedValues.length; i++) {
						selectedValues[i] = selectedValues[i].trim();
						if (viewField instanceof SectionViewField) {
							Long id = sectionFieldValueSpecsDao.getId(
									viewField.getFieldSpecId(),
									selectedValues[i]);
							if (id != null) {
								values.add(id + "");
							}
						} else {

							Long id = fieldValueSpecsDao.getId(
									viewField.getFieldSpecId(),
									selectedValues[i]);
							if (id != null) {
								values.add(id + "");
							}
						}
					}
					idsString = TextUtils.join(",", values);
				}
				if (idsString != null) {
					viewField.setRemoteValue(idsString);
				} else {
					viewField.setRemoteValue(null);
				}
			}

		}
	}

	private void useEditTextValues() {
		valueMap.clear();

		for (ViewField viewField : currentFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				for (int instanceId : section.getInstanceFieldsMap().keySet()) {
					for (SectionViewField sectionField : section
							.getInstanceFieldsMap().get(instanceId)) {
						useEditTextValue(sectionField);

						if ((sectionField.getType() == FieldSpecs.TYPE_NUMBER || sectionField
								.getType() == FieldSpecs.TYPE_CURRENCY)
								&& !sectionField.getComputed()
								&& !sectionField.getDefaultField()) {
							String key = sectionField.getSelector() + "["
									+ instanceId + "]";
							if (TextUtils
									.isEmpty(sectionField.getRemoteValue())) {
								valueMap.remove(key);
							} else {
								valueMap.put(key, parseDouble(sectionField
										.getRemoteValue()));
							}
						}
						if (sectionField.getType() == FieldSpecs.TYPE_ENTITY
								&& !sectionField.getComputed()
								&& !sectionField.getDefaultField()
								&& sectionField.isItVisibleByCriteria()) {

							if (sectionField.getLocalValue() != null) {
								Entity entityWithLocalId = entitiesDao
										.getEntityWithLocalId(Long
												.parseLong(sectionField
														.getLocalValue()));

								EntityFieldSpec idintifierFieldSpec = entityFieldSpecsDao
										.getIdintifierFieldSpecHavingNumberOrCurrency(entityWithLocalId
												.getEntitySpecId());

								if (idintifierFieldSpec != null) {
									String key = sectionField.getSelector()
											+ "[" + instanceId + "]";
									EntityFieldsDao entityFieldsDao = EntityFieldsDao
											.getInstance(getApplicationContext());
									EntityField field = entityFieldsDao
											.getField(entityWithLocalId
													.getLocalId(),
													idintifierFieldSpec.getId());

									if ((field != null && TextUtils
											.isEmpty(field.getRemoteValue()))
											|| field == null) {
										valueMap.remove(key);
									} else {
										valueMap.put(key, parseDouble(field
												.getRemoteValue()));
									}
								}

								List<EntityFieldSpec> idintifierFieldSpecs = entityFieldSpecsDao
										.getFieldSpecsHavingNumberOrCurrency(entityWithLocalId
												.getEntitySpecId());

								if (idintifierFieldSpecs != null) {
									for (EntityFieldSpec entityFieldSpec : idintifierFieldSpecs) {
										String key = sectionField.getSelector()
												+ "_"
												+ entityFieldSpec.getSelector()
												+ "[" + instanceId + "]";
										EntityFieldsDao entityFieldsDao = EntityFieldsDao
												.getInstance(getApplicationContext());
										EntityField field = entityFieldsDao
												.getField(entityWithLocalId
														.getLocalId(),
														entityFieldSpec.getId());

										if ((field != null && TextUtils
												.isEmpty(field.getRemoteValue()))
												|| field == null) {
											valueMap.remove(key);
										} else {
											valueMap.put(key, parseDouble(field
													.getRemoteValue()));
										}
									}
								}
							}
						}
					}
				}
			} else {
				useEditTextValue(viewField);

				boolean computed = viewField.getComputed() == null ? false
						: viewField.getComputed();

				if ((viewField.getType() == FieldSpecs.TYPE_NUMBER || viewField
						.getType() == FieldSpecs.TYPE_CURRENCY)
						&& !computed
						&& !viewField.getDefaultField()) {
					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						valueMap.remove(viewField.getSelector());
					} else {
						valueMap.put(viewField.getSelector(),
								parseDouble(viewField.getRemoteValue()));
					}
				}

				if (viewField.getType() == FieldSpecs.TYPE_ENTITY && !computed
						&& !viewField.getDefaultField()
						&& viewField.isItVisibleByCriteria()) {

					if (viewField.getLocalValue() != null) {
						Entity entityWithLocalId = entitiesDao
								.getEntityWithLocalId(Long.parseLong(viewField
										.getLocalValue()));

						EntityFieldSpec idintifierFieldSpec = entityFieldSpecsDao
								.getIdintifierFieldSpecHavingNumberOrCurrency(entityWithLocalId
										.getEntitySpecId());

						if (idintifierFieldSpec != null) {
							EntityFieldsDao entityFieldsDao = EntityFieldsDao
									.getInstance(getApplicationContext());
							EntityField field = entityFieldsDao.getField(
									entityWithLocalId.getLocalId(),
									idintifierFieldSpec.getId());

							if ((field != null && TextUtils.isEmpty(field
									.getRemoteValue())) || field == null) {
								valueMap.remove(viewField.getSelector());
							} else {
								valueMap.put(viewField.getSelector(),
										parseDouble(field.getRemoteValue()));
							}
						}

						List<EntityFieldSpec> idintifierFieldSpecs = entityFieldSpecsDao
								.getFieldSpecsHavingNumberOrCurrency(entityWithLocalId
										.getEntitySpecId());

						if (idintifierFieldSpecs != null) {
							for (EntityFieldSpec entityFieldSpec : idintifierFieldSpecs) {
								String key = viewField.getSelector() + "_"
										+ entityFieldSpec.getSelector();
								EntityFieldsDao entityFieldsDao = EntityFieldsDao
										.getInstance(getApplicationContext());
								EntityField field = entityFieldsDao.getField(
										entityWithLocalId.getLocalId(),
										entityFieldSpec.getId());

								if ((field != null && TextUtils.isEmpty(field
										.getRemoteValue())) || field == null) {
									valueMap.remove(key);
								} else {
									valueMap.put(key,
											parseDouble(field.getRemoteValue()));
								}
							}
						}
					}
				}

			}
		}

		evaluateComputedFields();
	}

	private void evaluateComputedFields() {
		EvalVisitor visitor = new EvalVisitor(valueMap, 0);

		for (ViewField viewField : currentFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				for (int instanceId : section.getInstanceFieldsMap().keySet()) {
					for (SectionViewField sectionField : section
							.getInstanceFieldsMap().get(instanceId)) {
						if ((sectionField.getType() == FieldSpecs.TYPE_NUMBER
								|| sectionField.getType() == FieldSpecs.TYPE_CURRENCY || sectionField
								.getType() == FieldSpecs.TYPE_ENTITY)
								&& (sectionField.getComputed() || (sectionField
										.getDefaultField() && isRefreshingNecessery(sectionField)))) {
							EvalVisitor sectionVisitor = new EvalVisitor(
									valueMap, instanceId);
							StringReader reader = new StringReader(
									sectionField.getFormula());
							ANTLRInputStream input = null;

							try {
								input = new ANTLRInputStream(reader);
							} catch (IOException e) {
								Toast.makeText(
										this,
										"Could not compute "
												+ sectionField.getLabel(),
										Toast.LENGTH_LONG).show();
								continue;
							}

							ExprLexer lexer = new ExprLexer(input);
							CommonTokenStream tokens = new CommonTokenStream(
									lexer);
							ExprParser parser = new ExprParser(tokens);
							ParseTree tree = parser.expr_();
							Double result = sectionVisitor.visit(tree);
							sectionField.setRemoteValue(result == null ? ""
									: String.format("%.2f", result));

							EditText editText = (EditText) sectionField
									.getView();

							if (editText != null) {
								editText.setText(result == null ? "" : String
										.format("%.2f", result));
							} else {
								// because at this point of time our
								// dependency
								// fields hashmap is ready
								// , but view is not ready , so clear those
								// values
								sectionField.setFormulaFieldValues(null);
							}
						}
					}
				}
			} else {
				boolean computed = viewField.getComputed() == null ? false
						: viewField.getComputed();

				if ((viewField.getType() == FieldSpecs.TYPE_NUMBER
						|| viewField.getType() == FieldSpecs.TYPE_CURRENCY || viewField
						.getType() == FieldSpecs.TYPE_ENTITY)
						&& (computed || (viewField.getDefaultField() && isRefreshingNecessery(viewField)))) {
					StringReader reader = new StringReader(
							viewField.getFormula());
					ANTLRInputStream input = null;

					try {
						input = new ANTLRInputStream(reader);
					} catch (IOException e) {
						Toast.makeText(this,
								"Could not compute " + viewField.getLabel(),
								Toast.LENGTH_LONG).show();
						continue;
					}

					ExprLexer lexer = new ExprLexer(input);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
					ExprParser parser = new ExprParser(tokens);
					ParseTree tree = parser.expr_();
					Double result = visitor.visit(tree);
					viewField.setRemoteValue(result == null ? "" : String
							.format("%.2f", result));

					EditText editText = (EditText) viewField.getView();

					if (editText != null) {
						editText.setText(result == null ? "" : String.format(
								"%.2f", result));

						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Setting edittext to "
									+ editText.getText().toString());
						}
					} else {
						// because at this point of time our dependency
						// fields
						// hashmap is ready
						// , but view is not ready , so clear those values
						viewField.setFormulaFieldValues(null);
					}

				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onBackPressed.");
		}

		if (editMode) {
			useEditTextValues();
			if (hasUnsavedChanges()) {
				DialogFragment dialogFragment = MyAlertDialogFragment
						.newInstance(R.string.alert_dialog_title);
				dialogFragment.show(getSupportFragmentManager(), "dialog");
			} else {
				discard();
			}
		} else {
			super.onBackPressed();
		}
	}

	public static class MyAlertDialogFragment extends DialogFragment {
		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			return new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setPositiveButton(R.string.alert_dialog_save,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									/*
									 * boolean type = ((FormActivity)
									 * (getActivity())) .getFormTypeOfSave();
									 */
									/*
									 * ((FormActivity) (getActivity())) .save();
									 */
									((FormActivity) (getActivity()))
											.saveNormalForm(false);
								}
							})
					.setNegativeButton(R.string.alert_dialog_discard,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((FormActivity) (getActivity())).discard();
								}
							}).create();
		}

	}

	private boolean fieldValid(ViewField viewField) {
		if (viewField.getComputed() != null && viewField.getComputed()) {
			return true;
		}

		// first validate edit restriction
		if (viewField.getEditable() != null && viewField.getEditable() != 0) {
			if (viewField.getRequired() && viewField.isItVisibleByCriteria()
					&& viewField.isItEnabledByCriteria()) {
				if (viewField.getType() == FieldSpecs.TYPE_CUSTOMER
						|| viewField.getType() == FieldSpecs.TYPE_EMPLOYEE
						|| viewField.getType() == FieldSpecs.TYPE_ENTITY
						|| viewField.getType() == FieldSpecs.TYPE_MULTI_LIST) {
					if (TextUtils.isEmpty(viewField.getLocalValue())) {
						Toast.makeText(
								this,
								viewField.getLabel()
										+ " is required, but not specified.",
								Toast.LENGTH_LONG).show();
						return false;
					}
				} else if (viewField.getType() == FieldSpecs.TYPE_IMAGE
						|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
					FormFile fileDto = viewField.getFile();

					if (TextUtils.isEmpty(fileDto.getLocalMediaPath())
							&& fileDto.getMediaId() == null) {
						Toast.makeText(
								this,
								viewField.getLabel()
										+ " is required, but not specified.",
								Toast.LENGTH_LONG).show();
						return false;
					}
				} else {
					if (TextUtils.isEmpty(viewField.getRemoteValue())) {
						Toast.makeText(
								this,
								viewField.getLabel()
										+ " is required, but not specified.",
								Toast.LENGTH_LONG).show();
						return false;
					}
				}
			}

			if (viewField.getType() == FieldSpecs.TYPE_EMAIL) {
				if (!Utils.isEmailAddressValid(viewField.getRemoteValue(),
						viewField.getRequired())
						&& viewField.isItVisibleByCriteria()
						&& viewField.isItEnabledByCriteria()) {
					Toast.makeText(
							this,
							viewField.getLabel()
									+ " is not a valid email address.",
							Toast.LENGTH_LONG).show();
					return false;
				}
			}

			if (viewField.getType() == FieldSpecs.TYPE_NUMBER
					|| viewField.getType() == FieldSpecs.TYPE_CURRENCY) {
				if (!Utils.isNumberValid(viewField.getRemoteValue(),
						viewField.getRequired())
						&& viewField.isItVisibleByCriteria()
						&& viewField.isItEnabledByCriteria()) {
					Toast.makeText(this,
							viewField.getLabel() + " is not number.",
							Toast.LENGTH_LONG).show();
					return false;
				} else {
					String errorMessage = Utils.validateNumberRange(
							viewField.getRemoteValue(), viewField.getRequired()
									&& viewField.isItVisibleByCriteria()
									&& viewField.isItEnabledByCriteria(),
							viewField.getMinValue(), viewField.getMaxValue());

					if (errorMessage != null) {
						Toast.makeText(this,
								viewField.getLabel() + errorMessage,
								Toast.LENGTH_LONG).show();
						return false;
					}
				}
			}

			if (viewField.getType() == FieldSpecs.TYPE_LOCATION) {
				if (!Utils.isLocationValid(viewField.getRemoteValue(),
						viewField.getRequired())
						&& viewField.isItVisibleByCriteria()
						&& viewField.isItEnabledByCriteria()) {
					Toast.makeText(this,
							viewField.getLabel() + " is not a valid location.",
							Toast.LENGTH_LONG).show();
					return false;
				}
			}

			if (viewField.getType() == FieldSpecs.TYPE_TEXT) {
				String validationError = Utils.validateLength(
						viewField.getRemoteValue(), viewField.getRequired(),
						viewField.getMinValue(), viewField.getMaxValue());

				if (validationError != null
						&& viewField.isItVisibleByCriteria()
						&& viewField.isItEnabledByCriteria()) {
					Toast.makeText(this,
							viewField.getLabel() + validationError,
							Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		return true;
	}

	void performValidationAndSave() {

		boolean isItWorkflowRelated = isWorkflowSaveDialogRequired();

		if (isItWorkflowRelated) {
			new AlertDialog.Builder(context)
					.setTitle("Action")
					.setMessage(
							"This "
									+ singular
									+ " is associated with a workflow. Do you want to submit this "
									+ singular + " for approval?")
					.setPositiveButton("Save only",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									saveNormalForm(false);
								}
							})
					.setNegativeButton("Submit for approval",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// saveWorkflowForm(true);
									saveNormalForm(true);
								}
							}).show();
		} else {
			saveNormalForm(false);
		}
	}

	private void saveNormalForm(boolean canFormWorkFlowStatus) {
		showData();
		useEditTextValues();

		for (ViewField viewField : currentFields) {

			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				for (int instanceId : section.getInstanceFieldsMap().keySet()) {
					for (SectionViewField sectionField : section
							.getInstanceFieldsMap().get(instanceId)) {
						if (!fieldValid(sectionField)) {
							return;
						}
					}
				}
			} else {
				if (!fieldValid(viewField)) {
					return;
				}
			}
		}

		Form form = null;

		if (localFormId != 0) {
			form = formsDao.getFormWithLocalId(localFormId);
		} else {
			form = new Form();
		}

		if (form == null) {
			Toast.makeText(
					this,
					singular
							+ " cannot be saved, as it no longer exists locally.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		form.setFormSpecId(formSpecId);
		form.setDirty(false);
		form.setCached(false);

		// save primary to get the localFormId to be used for fields
		formsDao.save(form);
		localFormId = form.getLocalId();

		fieldsDao.deleteFields(localFormId);
		sectionFieldsDao.deleteFields(localFormId);

		for (ViewField viewField : currentFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				int i = 1;
				for (int instanceId : section.getInstanceFieldsMap().keySet()) {
					for (SectionViewField sectionViewField : section
							.getInstanceFieldsMap().get(instanceId)) {
						SectionField field = new SectionField();

						field.setFieldSpecId(sectionViewField.getFieldSpecId());
						field.setFormSpecId(sectionViewField.getFormSpecId());
						field.setSectionSpecId(sectionViewField
								.getSectionSpecId());
						field.setSectionInstanceId(i);
						field.setLocalFormId(localFormId);
						field.setLocalValue(sectionViewField.getLocalValue());
						field.setRemoteValue(sectionViewField.getRemoteValue());

						if (sectionViewField.getType() == FieldSpecs.TYPE_IMAGE
								|| sectionViewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
							SectionFile sectionFile = (SectionFile) sectionViewField
									.getFile();

							if (sectionFile != null
									&& (!TextUtils.isEmpty(sectionFile
											.getLocalMediaPath()) || sectionFile
											.getMediaId() != null)
									&& sectionViewField.isItVisibleByCriteria()) {
								sectionFile.setSectionInstanceId(i);
								sectionFilesDao
										.save((SectionFile) sectionViewField
												.getFile());
								// Giving trigger to get location for
								// section
								// file
								if (sectionFile.getMediaId() == null
										&& sectionFile.getLocalMediaPath() != null) {

									if (sectionFile.isNewMedia()
											&& canCaptureMediaLocation) {
										locationsDao.deleteLocation(
												Locations.PURPOSE_SECTION_FILE,
												((SectionFile) sectionViewField
														.getFile())
														.getLocalId());
										captureLocation(
												EffortProvider.Locations.PURPOSE_SECTION_FILE,
												((SectionFile) sectionViewField
														.getFile())
														.getLocalId());
									}
								}
							} else {
								sectionFilesDao.deleteSectionFile(
										sectionViewField.getFieldSpecId(),
										sectionViewField.getLocalFormId(), i,
										false);
							}
						}

						if (sectionViewField.isItVisibleByCriteria()) {
							sectionFieldsDao.save(field);
						}
					}

					i++;
				}
			} else {
				Field field = new Field();

				field.setFieldSpecId(viewField.getFieldSpecId());
				field.setFormSpecId(viewField.getFormSpecId());
				field.setLocalFormId(localFormId);
				field.setLocalValue(viewField.getLocalValue());
				field.setRemoteValue(viewField.getRemoteValue());

				if (viewField.getType() == FieldSpecs.TYPE_IMAGE
						|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
					FormFile formFile = viewField.getFile();

					if (formFile != null
							&& (!TextUtils
									.isEmpty(formFile.getLocalMediaPath()) || formFile
									.getMediaId() != null)
							&& viewField.isItVisibleByCriteria()) {
						formFilesDao.save(viewField.getFile());
						// Giving trigger to get location for form file
						if (formFile.getMediaId() == null
								&& formFile.getLocalMediaPath() != null) {

							if (formFile.isNewMedia()
									&& canCaptureMediaLocation) {
								locationsDao.deleteLocation(
										Locations.PURPOSE_FORM_FILE, viewField
												.getFile().getLocalId());
								captureLocation(
										EffortProvider.Locations.PURPOSE_FORM_FILE,
										viewField.getFile().getLocalId());
							}
						}
					} else {
						formFilesDao.deleteFormFile(viewField.getFieldSpecId(),
								viewField.getLocalFormId(), false);
					}
				}
				if (viewField.isItVisibleByCriteria()) {
					fieldsDao.save(field);
				}
			}
		}

		// Now that all the fields are saved, set the dirty flag
		form.setDirty(true);

		boolean captureEveryTime = settingsDao.getBoolean(
				Settings.KEY_CAPTURE_LOCATION_ON_EVERY_FORM_SAVE, false);

		if (captureEveryTime) {
			captureLocation();
		} else {
			if (form.getTemporary() == null || form.getTemporary()) {
				captureLocation();
			}
		}

		form.setTemporary(false);

		if (form.getFilledById() == null) {
			form.setFilledById(settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
		}

		if (TextUtils.isEmpty(form.getFilledByName())) {
			form.setFilledByName(settingsDao
					.getString(Settings.KEY_EMPLOYEE_NAME));
		}

		form.setModifiedById(settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
		form.setModifiedByName(settingsDao
				.getString(Settings.KEY_EMPLOYEE_NAME));

		formsDao.save(form);

		// It will take a while to get the location, so don't trigger
		// an immediate sync when captureEveryTime is set
		if (form.getRemoteId() != null && !captureEveryTime) {
			Utils.sync(getApplicationContext());
		}

		Utils.startBftsIfRequired(getApplicationContext());

		if ("fill".equals(getIntent().getAction())) {
			Intent data = new Intent();
			data.putExtra("localFormId", form.getLocalId());

			if (getIntent().hasExtra(EffortProvider.Jobs.JOB_STATE_ID)) {
				data.putExtra(EffortProvider.Jobs.JOB_STATE_ID, getIntent()
						.getIntExtra(EffortProvider.Jobs.JOB_STATE_ID, 0));
			}
			if (getIntent().hasExtra(EffortProvider.JobHistories.TEMPORARY)) {
				boolean temparory = getIntent().getBooleanExtra(
						EffortProvider.JobHistories.TEMPORARY, false);
				form.setTemporary(temparory);
				formsDao.save(form);
			}

			setResult(RESULT_OK, data);
		} else if ("fillFormForActivity".equals(getIntent().getAction())) {
			Intent data = new Intent();
			data.putExtra("localFormId", form.getLocalId());
			if (getIntent().hasExtra(
					EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID)) {
				data.putExtra(
						EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID,
						getIntent()
								.getLongExtra(
										EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID,
										0));
			}

			if (getIntent().hasExtra(
					EffortProvider.CompletedActivities.ACTIVITY_ID)) {
				data.putExtra(
						EffortProvider.CompletedActivities.ACTIVITY_ID,
						getIntent().getLongExtra(
								EffortProvider.CompletedActivities.ACTIVITY_ID,
								0));
			}

			if (getIntent().hasExtra(CustomerActivities.FORM_VIEW)) {
				data.putExtra(CustomerActivities.FORM_VIEW, getIntent()
						.getLongExtra(CustomerActivities.FORM_VIEW, 0));
			}

			setResult(RESULT_OK, data);
		}

		if (canFormWorkFlowStatus) {
			Long localFormId = form.getLocalId();
			saveWorkFlow(localFormId);
		}
		finish();
	}

	private void saveWorkFlow(Long localFormId) {
		WorkFlowSpec workFlowSpec = workFlowSpecsDao
				.getWorkFlowSpecWithFormSpecId(formSpecId);
		if (localFormId == 0 || workFlowSpec == null) {
			return;
		}
		Long workFlowId = workFlowSpec.getId();
		WorkFlowStagesDao workFlowStagesDao = WorkFlowStagesDao
				.getInstance(getApplicationContext());
		WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
		workFlowStatus.setClientFormId(localFormId);
		// workFlowStatus.setFormSpecId(formSpecId);
		workFlowStatus.setCreatedBy(settingsDao
				.getLong(Settings.KEY_EMPLOYEE_ID));
		workFlowStatus.setWorkFlowId(workFlowId);
		workFlowStatus.setLocalCreationTime(new Date());
		workFlowStatus.setLocalModificationTime(new Date());
		workFlowStatus.setStatus(WorkFlowStatusDto.STATUS_WAITING);
		WorkFlowStage workFlowStage = workFlowStagesDao
				.getWorkFlowStageWithWorkFlowId(workFlowId);
		workFlowStatus.setStatusMessage("Waiting");
		if (workFlowStage != null) {
			workFlowStatus.setStageName(workFlowStage.getStageName());
		}
		workFlowStatus.setWorkFlowId(workFlowId);
		workFlowStatus.setCreatedTime(new Date());
		workFlowStatus.setModifiedTime(new Date());
		workFlowStatus.setDirty(true);
		workFlowStatusDao.save(workFlowStatus);

		Toast.makeText(getApplicationContext(), "Saved to workflow",
				Toast.LENGTH_SHORT).show();

	}

	/*
	 * void saveWorkflowForm() { Toast.makeText(getApplicationContext(),
	 * "work flow form", Toast.LENGTH_SHORT).show(); saveNormalForm(true); }
	 */

	private void discard() {
		// if physical files are missing, delete corresponding form file records
		// (only when
		// there is no valid media id)
		for (ViewField viewField : originalFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				for (int instanceId : section.getInstanceFieldsMap().keySet()) {
					for (SectionViewField sectionField : section
							.getInstanceFieldsMap().get(instanceId)) {
						if (sectionField.getType() == FieldSpecs.TYPE_IMAGE
								|| sectionField.getType() == FieldSpecs.TYPE_SIGNATURE) {
							SectionFile sectionFile = (SectionFile) sectionField
									.getFile();

							if (sectionFile != null
									&& !TextUtils.isEmpty(sectionFile
											.getLocalMediaPath())) {
								File file = new File(
										sectionFile.getLocalMediaPath());

								if (!file.exists()
										&& (sectionFile.getMediaId() == null || sectionFile
												.getMediaId() == -1)) {
									sectionFilesDao.deleteSectionFile(
											sectionField.getFieldSpecId(),
											sectionField.getLocalFormId(),
											instanceId, false);
								}
							}
						}
					}
				}

			} else {
				if (viewField.getType() == FieldSpecs.TYPE_IMAGE
						|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
					FormFile formFile = viewField.getFile();

					if (formFile != null
							&& !TextUtils.isEmpty(formFile.getLocalMediaPath())) {
						File file = new File(formFile.getLocalMediaPath());

						if (!file.exists()
								&& (formFile.getMediaId() == null || formFile
										.getMediaId() == -1)) {
							formFilesDao.deleteFormFile(
									viewField.getFieldSpecId(),
									viewField.getLocalFormId(), false);
						}
					}
				}
			}
		}

		finish();
	}

	private boolean fieldChanged(ViewField originalField, ViewField currentField) {
		if (currentField.getType() == FieldSpecs.TYPE_CUSTOMER
				|| currentField.getType() == FieldSpecs.TYPE_EMPLOYEE
				|| currentField.getType() == FieldSpecs.TYPE_ENTITY
				|| currentField.getType() == FieldSpecs.TYPE_MULTI_LIST) {
			if (!TextUtils.equals(currentField.getLocalValue(),
					originalField.getLocalValue())) {
				return true;
			}
		} else if (currentField.getType() == FieldSpecs.TYPE_IMAGE
				|| currentField.getType() == FieldSpecs.TYPE_SIGNATURE) {
			FormFile originalFile = originalField.getFile();
			FormFile currentFile = currentField.getFile();

			if (!TextUtils.isEmpty(originalFile.getLocalMediaPath())
					|| !TextUtils.isEmpty(currentFile.getLocalMediaPath())) {
				if (!TextUtils.equals(originalFile.getLocalMediaPath(),
						currentFile.getLocalMediaPath())) {
					return true;
				}
			} else {
				if (!Utils.longsEqual(originalFile.getMediaId(),
						currentFile.getMediaId())) {
					return true;
				}
			}
		} else {
			if (!TextUtils.equals(currentField.getRemoteValue(),
					originalField.getRemoteValue())) {
				return true;
			}
		}

		return false;
	}

	private boolean hasUnsavedChanges() {
		int size = currentFields.size();

		for (int i = 0; i < size; ++i) {
			ViewField originalField = originalFields.get(i);
			ViewField currentField = currentFields.get(i);

			if (currentField instanceof SectionViewField) {
				SectionViewField originalSection = (SectionViewField) originalField;
				SectionViewField currentSection = (SectionViewField) currentField;

				if (originalSection.getInstanceFieldsMap().size() != currentSection
						.getInstanceFieldsMap().size()) {
					return true;
				} else {
					for (int instanceId : originalSection
							.getInstanceFieldsMap().keySet()) {
						List<SectionViewField> originalSectionFields = originalSection
								.getInstanceFieldsMap().get(instanceId);
						List<SectionViewField> currentSectionFields = currentSection
								.getInstanceFieldsMap().get(instanceId);

						for (int fieldId = 0; fieldId < originalSectionFields
								.size(); ++fieldId) {
							if (fieldChanged(
									originalSectionFields.get(fieldId),
									currentSectionFields.get(fieldId))) {
								return true;
							}
						}
					}
				}
			} else {
				if (fieldChanged(originalField, currentField)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Date set for " + datePickerDialog.getUsedForViewId()
					+ ", " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}

		Calendar cal;

		cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, monthOfYear, dayOfMonth);

		if (pickerViewField != null && pickerViewField.getView() != null
				&& (pickerViewField.getView() instanceof Button)) {
			pickerViewField.setRemoteValue(SQLiteDateTimeUtils
					.getSQLiteDate(cal.getTime()));
			((Button) pickerViewField.getView()).setText(dateFormat.format(cal
					.getTime()));
			if ((hasFilteringCriteria && listFilteringCriteriaDao
					.isThisFieldhasFilteringCriteria(formSpecId,
							pickerViewField.getSelector()))
					|| (hasVisibilityCriteria && visibilityCriteriaDao
							.isItRelatedToTargetExpression(formSpecId,
									pickerViewField.getSelector()))) {
				showProfressAndCallShowdata();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			Log.v("flow", "onActivityResult");
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onActivityResult. requestCode=" + requestCode
					+ ", resultCode=" + resultCode + "(OK? "
					+ (resultCode == Activity.RESULT_OK) + ")");
		}

		super.onActivityResult(requestCode, resultCode, data);

		boolean compressMedia = settingsDao.getBoolean(
				Settings.KEY_COMPRESS_MEDIA, Settings.DEFAULT_COMPRESS_MEDIA);

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {

			case PICK_LOCATION:
				String latitude = data.getStringExtra("latitude");
				String longitude = data.getStringExtra("longitude");
				pickerViewField.setRemoteValue(latitude + "," + longitude);
				if (pickerViewField.getView() instanceof EditText) {
					((EditText) pickerViewField.getView()).setText(latitude
							+ "," + longitude);
					if ((hasFilteringCriteria && listFilteringCriteriaDao
							.isThisFieldhasFilteringCriteria(formSpecId,
									pickerViewField.getSelector()))
							|| (hasVisibilityCriteria && visibilityCriteriaDao
									.isItRelatedToTargetExpression(formSpecId,
											pickerViewField.getSelector()))) {
						showProfressAndCallShowdata();
					}
				}
				break;

			case PICK_EMPLOYEE:

				long localEmployeeId = data.getLongExtra("localEmployeeId", 0);

				if (localEmployeeId != 0) {
					pickerViewField.setLocalValue("" + localEmployeeId);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView())
								.setText(employeesDao
										.getEmployeeNameWithLocalId(localEmployeeId));
						if ((hasFilteringCriteria && listFilteringCriteriaDao
								.isThisFieldhasFilteringCriteria(formSpecId,
										pickerViewField.getSelector()))
								|| (hasVisibilityCriteria && visibilityCriteriaDao
										.isItRelatedToTargetExpression(
												formSpecId,
												pickerViewField.getSelector()))) {
							showProfressAndCallShowdata();
						}

					}
				} else {
					pickerViewField.setLocalValue(null);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView())
								.setText("Pick a Employee");
					}
				}

				break;

			case PICK_CUSTOMER:

				long localCustomerId = data.getLongExtra("localCustomerId", 0);

				if (localCustomerId != 0) {
					pickerViewField.setLocalValue("" + localCustomerId);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView())
								.setText(customersDao
										.getCompanyNameWithLocalId(localCustomerId));
						if ((hasFilteringCriteria && listFilteringCriteriaDao
								.isThisFieldhasFilteringCriteria(formSpecId,
										pickerViewField.getSelector()))
								|| (hasVisibilityCriteria && visibilityCriteriaDao
										.isItRelatedToTargetExpression(
												formSpecId,
												pickerViewField.getSelector()))) {
							showProfressAndCallShowdata();
						}
					}

				} else {
					pickerViewField.setLocalValue(null);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView())
								.setText("Pick a "
										+ settingsDao
												.getLabel(
														Settings.LABEL_CUSTOMER_SINGULAR_KEY,
														Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
					}
				}

				break;

			case PICK_ENTITY:

				long localEntityId = data.getLongExtra("localEntityId", 0);

				if (localEntityId != 0) {
					pickerViewField.setLocalValue("" + localEntityId);
					if (pickerViewField.getView() instanceof Button) {

						((Button) pickerViewField.getView())
								.setText(entitiesDao
										.getEntityName(localEntityId));
						Entity entityWithLocalId = entitiesDao
								.getEntityWithLocalId(localEntityId);
						boolean listContainsNumberOrCurrenyField = entityFieldSpecsDao
								.fieldSpecsWithTypeNumberOrCurrencyExists(entityWithLocalId
										.getEntitySpecId());

						if ((hasFilteringCriteria && listFilteringCriteriaDao
								.isThisFieldhasFilteringCriteria(formSpecId,
										pickerViewField.getSelector()))
								|| (hasVisibilityCriteria && visibilityCriteriaDao
										.isItRelatedToTargetExpression(
												formSpecId,
												pickerViewField.getSelector()))
								|| listContainsNumberOrCurrenyField) {
							showProfressAndCallShowdata();
						}
					}
				} else {
					pickerViewField.setLocalValue(null);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView()).setText("Pick "
								+ pickerViewField.getLabel());
					}
				}

				break;

			case PICK_MULTI_LIST:

				ArrayList<String> ids = data
						.getStringArrayListExtra("localEntityIds");
				String pickerViewLocalValue = null;
				String buttonDisplayValue = null;
				ArrayList<String> localValues = new ArrayList<String>();
				ArrayList<String> displayValues = new ArrayList<String>();
				if (ids != null && ids.size() > 0) {
					for (int i = 0; i < ids.size(); i++) {
						Log.v(TAG, "selected items are : " + ids.get(i));
						localValues.add(ids.get(i));
						displayValues.add(entitiesDao.getEntityName(Long
								.parseLong(ids.get(i))));
					}
					pickerViewLocalValue = TextUtils.join(", ", localValues);
					buttonDisplayValue = TextUtils.join(", ", displayValues);
					pickerViewField.setLocalValue(pickerViewLocalValue);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView())
								.setText(buttonDisplayValue);
						if ((hasFilteringCriteria && listFilteringCriteriaDao
								.isThisFieldhasFilteringCriteria(formSpecId,
										pickerViewField.getSelector()))
								|| (hasVisibilityCriteria && visibilityCriteriaDao
										.isItRelatedToTargetExpression(
												formSpecId,
												pickerViewField.getSelector()))) {
							showProfressAndCallShowdata();
						}
					}
				} else {
					pickerViewField.setLocalValue(null);
					if (pickerViewField.getView() instanceof Button) {
						((Button) pickerViewField.getView()).setText("Pick "
								+ pickerViewField.getLabel());
					}
				}

				break;

			case FieldSpecs.TYPE_IMAGE:
				if (mediaPath != null) {
					Utils.rotateImageIfRequired(mediaPath, this);
					if (compressMedia) {
						Utils.compressImage(mediaPath, this);
					}

					FormFile fileDto = pickerViewField.getFile();
					fileDto.setNewMedia(true);
					fileDto.setMimeType("image/jpeg");
					fileDto.setLocalMediaPath(mediaPath);
					fileDto.setMediaId(null);

					File file = new File(mediaPath);
					fileDto.setFileSize(file.length());
					showData();
				}

				break;

			case FieldSpecs.TYPE_SIGNATURE:
				if (mediaPath != null) {
					if (compressMedia) {
						Utils.compressImage(mediaPath, this);
					}

					FormFile fileDto = pickerViewField.getFile();

					fileDto.setNewMedia(true);
					fileDto.setMimeType("image/png");
					fileDto.setLocalMediaPath(mediaPath);
					fileDto.setMediaId(null);

					File file = new File(mediaPath);
					fileDto.setFileSize(file.length());
					showData();
				}

				break;

			case PICK_IMAGE:
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Intent data: " + data);
				}

				if (data != null && data.getData() != null) {
					FormFile fileDto = pickerViewField.getFile();
					fileDto.setNewMedia(true);
					Uri uri = data.getData();

					if (BuildConfig.DEBUG) {
						Log.v(TAG, "scheme is " + uri.getScheme());
					}
					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Images.ImageColumns.DATA,
												MediaStore.Images.ImageColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();

						if (TextUtils.isEmpty(cursor.getString(0))) {
							Toast.makeText(
									this,
									"Could not get the local media path. Please pick another image, or use another image source.",
									Toast.LENGTH_LONG).show();
							cursor.close();
							return;
						}

						if (compressMedia) {
							Utils.copyFile(this, cursor.getString(0), mediaPath);
							fileDto.setLocalMediaPath(mediaPath);
							fileDto.setMimeType("image/jpeg");
							Utils.compressImage(mediaPath, this);
						} else {
							fileDto.setLocalMediaPath(cursor.getString(0));
							fileDto.setMimeType(cursor.getString(1));
						}

						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						if (compressMedia) {
							Utils.copyFile(this, uri.getPath(), mediaPath);
							fileDto.setLocalMediaPath(mediaPath);
							fileDto.setMimeType("image/jpeg");
							Utils.compressImage(mediaPath, this);
						} else {
							fileDto.setMimeType(data.getType());
							fileDto.setLocalMediaPath(uri.getPath());
						}
					} else {
						Toast.makeText(
								this,
								"The item you picked is not a valid image. Please pick another image.",
								Toast.LENGTH_LONG).show();
						return;
					}

					if (!fileDto.getMimeType().startsWith("image/")) {
						Toast.makeText(
								this,
								"The item you picked is not an image. Please pick an image.",
								Toast.LENGTH_LONG).show();
						return;
					}

					File file = new File(fileDto.getLocalMediaPath());
					fileDto.setFileSize(file.length());
					fileDto.setMediaId(null);
					showData();
				}

				break;

			case PICK_SIGNATURE:
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Intent data: " + data);
				}

				if (data != null && data.getData() != null) {
					FormFile fileDto = pickerViewField.getFile();
					fileDto.setNewMedia(true);

					Uri uri = data.getData();

					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Images.ImageColumns.DATA,
												MediaStore.Images.ImageColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();

						if (TextUtils.isEmpty(cursor.getString(0))) {
							Toast.makeText(
									this,
									"Could not get the local media path. Please pick another image, or use another image source.",
									Toast.LENGTH_LONG).show();
							cursor.close();
							return;
						}

						fileDto.setLocalMediaPath(cursor.getString(0));
						fileDto.setMimeType(cursor.getString(1));
						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						fileDto.setMimeType(data.getType());
						fileDto.setLocalMediaPath(uri.getPath());
					} else {
						Toast.makeText(
								this,
								"The item you picked is not a valid image. Please pick another image.",
								Toast.LENGTH_LONG).show();
						return;
					}

					if (!fileDto.getMimeType().startsWith("image/")) {
						Toast.makeText(
								this,
								"The item you picked is not an image. Please pick an image.",
								Toast.LENGTH_LONG).show();
						return;
					}

					File file = new File(fileDto.getLocalMediaPath());
					fileDto.setFileSize(file.length());
					fileDto.setMediaId(null);
					showData();
				}

				break;

			case READ_BARCODE:
				if (resultCode == RESULT_OK) {
					String contents = data.getStringExtra("SCAN_RESULT");
					// Handle successful scan
					if (pickerViewField.getView() instanceof EditText) {
						((EditText) pickerViewField.getView())
								.setText(contents);
					}

				} else if (resultCode == RESULT_CANCELED) {
					// Handle cancel
				}
				break;
			default:
				break;
			}

		}
	}

	@Override
	protected void onPause() {
		if (BuildConfig.DEBUG) {
			Log.v("flow", "on pause");
		}
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		if (BuildConfig.DEBUG) {
			Log.v("flow", "on resume");
		}

	}

	private static Date getDefaultTime() {
		Calendar cal = Calendar.getInstance();

		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);

		return cal.getTime();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Date set for " + timePickerDialog.getUsedForViewId()
					+ ", " + hourOfDay + "" + minute);
		}

		if (pickerViewField != null && pickerViewField.getView() != null
				&& (pickerViewField.getView() instanceof Button)) {
			pickerViewField.setRemoteValue(storageTimeFormat.format(Utils
					.getTime(hourOfDay, minute)));
			((Button) pickerViewField.getView()).setText(displayTimeFormat
					.format(Utils.getTime(hourOfDay, minute)));
			if ((hasFilteringCriteria && listFilteringCriteriaDao
					.isThisFieldhasFilteringCriteria(formSpecId,
							pickerViewField.getSelector()))
					|| (hasVisibilityCriteria && visibilityCriteriaDao
							.isItRelatedToTargetExpression(formSpecId,
									pickerViewField.getSelector()))) {
				showProfressAndCallShowdata();
			}
		}
	}

	private void captureImage(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		// Lenovo P700i doesn't return the intent data in the expected format
		// if we don't specify the path explicitly
		// Explicitly specifying the path worked on HTC Desire Z also
		mediaPath = Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/img-"
				+ Utils.getDateTimeStamp() + ".jpg";

		File imageFile = new File(mediaPath);
		Uri outputFileUri = Uri.fromFile(imageFile);
		File imageDir = new File(imageFile.getParent());
		if (!imageDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + imageFile.getParent());
			}

			imageDir.mkdirs();
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Capturing image as: " + mediaPath);
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Note: You need to pass the URI, not the path!
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, requestCode);
	}

	private void captureSignature(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		mediaPath = Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/sign-"
				+ Utils.getDateTimeStamp() + ".png";

		File audioFile = new File(mediaPath);
		File audioDir = new File(audioFile.getParent());
		if (!audioDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + audioFile.getParent());
			}

			audioDir.mkdirs();
		}

		Uri outputFileUri = Uri.fromFile(audioFile);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Capturing signature as: " + mediaPath);
		}

		Intent intent = new Intent(this, SignatureCaptureActivity.class);

		// Note: You need to pass the URI, not the path!
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, requestCode);
	}

	private void pickImage(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		// Lenovo P700i doesn't return the intent data in the expected format
		// if we don't specify the path explicitly
		// Explicitly specifying the path worked on HTC Desire Z also
		mediaPath = Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/img-"
				+ Utils.getDateTimeStamp() + ".jpg";

		File imageFile = new File(mediaPath);
		File imageDir = new File(imageFile.getParent());
		if (!imageDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + imageFile.getParent());
			}

			imageDir.mkdirs();
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Picking compressed image as: " + mediaPath);
		}

		startActivityForResult(Intent.createChooser(intent, "Pick image from"),
				requestCode);
	}

	private void pickSignatureImage(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(
				Intent.createChooser(intent, "Pick signature image from"),
				requestCode);
	}

	/**
	 * Starts the background file transfer service, if:
	 * <ol>
	 * <li>Connected to network</li>
	 * <li>SD card is mounted</li>
	 * <li>Current form has any downloads that the user requested or has any
	 * pending uploads</li>
	 * <li>Service is not already running</li>
	 * </ol>
	 * 
	 * 
	 * @return true if all the pre-conditions hold, and the service is started
	 *         or it has already been running; false, otherwise.
	 */
	public boolean startBftsIfRequired() {
		boolean includeLargeFiles = Utils
				.includeLargeFiles(getApplicationContext());

		boolean bftsRequired = Utils.isConnected(getApplicationContext())
				&& Utils.isSDCardValid(getApplicationContext(), false)
				&& (formFilesDao.hasPendingTransfers(getApplicationContext(),
						localFormId, includeLargeFiles) || sectionFilesDao
						.hasPendingTransfers(getApplicationContext(),
								localFormId, includeLargeFiles));

		if (bftsRequired) {
			if (progressUpdateThread != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Interrupting the current progress update thread.");
				}

				progressUpdateThread.interrupt();
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting a new progress update thread.");
			}

			progressUpdateThread = new ProgressUpdateThread();
			progressUpdateThread.start();

			if (Utils.isServiceRunning(getApplicationContext(),
					"BackgroundFileTransferService")) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "BFTS is already running.");
				}

				return true;
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Starting BFTS.");
				}

				WakefulIntentService.sendWakefulWork(this,
						BackgroundFileTransferService.class);
				return true;
			}
		} else {
			return false;
		}
	}

	private class ProgressUpdateThread extends Thread {

		public static final String TAG = "ProgressUpdateThread";

		@Override
		public void run() {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Progress update thread started");
			}

			while (true) {

				if (isInterrupted()) {

					runOnUiThread(new Runnable() {
						public void run() {
							showData();
						}
					});

					return;
				}

				// Restart proofs loader
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Restrted Progress update thread ");
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showData();
					}
				});

				boolean includeLargeFiles = Utils
						.includeLargeFiles(getApplicationContext());

				if (!formFilesDao.hasPendingTransfers(getApplicationContext(),
						localFormId, includeLargeFiles)
						&& !sectionFilesDao.hasPendingTransfers(
								getApplicationContext(), localFormId,
								includeLargeFiles)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "No pending transfers for files "
								+ ". Returning from progress update thread.");
					}
					runOnUiThread(new Runnable() {
						public void run() {
							showData();
						}
					});
					return;
				}

				try {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Sleeping for a second.");
					}

					sleep(1000);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Woke up from sleep.");
					}
				} catch (InterruptedException e) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG,
								"Thread interrupted. Returning from run method.");
					}
					runOnUiThread(new Runnable() {
						public void run() {
							showData();
						}
					});
					return;
				}
			}
		}
	}

	private void captureLocation() {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE,
				EffortProvider.Locations.PURPOSE_FORM);
		intent.putExtra(EffortProvider.Locations.FOR_ID, localFormId);
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	private void captureLocation(int purpose, long forId) {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE, purpose);
		intent.putExtra(EffortProvider.Locations.FOR_ID, forId);
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	private boolean isFileInUse(String path) {
		for (ViewField viewField : currentFields) {
			// skip sections
			if (viewField.getType() == null) {
				continue;
			}

			if (viewField.getType() == FieldSpecs.TYPE_IMAGE
					|| viewField.getType() == FieldSpecs.TYPE_SIGNATURE) {
				if (viewField.getFile() != null
						&& TextUtils.equals(path, viewField.getFile()
								.getLocalMediaPath())) {
					return true;
				}
			}
		}

		if (notesDao.isFileNeededForSync(path)
				|| formFilesDao.isFileNeededForSync(path)) {
			return true;
		}

		return false;
	}

	private ViewField getViewField(long fieldSpecId) {
		for (ViewField viewField : currentFields) {
			if (viewField.getFieldSpecId() != null
					&& viewField.getFieldSpecId() == fieldSpecId) {
				return viewField;
			}
		}

		return null;
	}

	private ViewField getSectionViewField(long fieldSpecId, long sectionSpecId,
			int sectionInstanceId) {
		for (ViewField viewField : currentFields) {
			if (viewField instanceof SectionViewField) {
				SectionViewField section = (SectionViewField) viewField;

				if (section.getSectionSpecId() == sectionSpecId) {
					List<SectionViewField> sectionViewFields = section
							.getInstanceFieldsMap().get(sectionInstanceId);

					if (sectionViewFields != null) {
						for (SectionViewField sectionViewField : sectionViewFields) {
							if (sectionViewField.getFieldSpecId() == fieldSpecId) {
								return sectionViewField;
							}
						}
					}
				}
			}
		}

		return null;
	}

	class MyTextWatcher implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			useEditTextValues();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onTabSelected " + tab.getText());
		}

		showData();
		useEditTextValues();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onTabUnselected " + tab.getText());
		}

		View target = findViewById(R.id.formLinearLayout).findFocus();

		if (target != null) {
			InputMethodManager mgr = (InputMethodManager) getApplicationContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(target.getWindowToken(), 0);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onTabReselected " + tab.getText());
		}

		showData();
		useEditTextValues();
	}

	/**
	 * 
	 * @return
	 */
	public Double parseDouble(String value) {
		Double doubleValue = null;

		try {
			doubleValue = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Failed to parse " + value + " as a double value: ",
						e);
			}
		}

		return doubleValue;
	}

	private void printSection(String section, ByteArrayOutputStream baos)
			throws IOException {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In printSection");
		}

		for (ViewField field : currentFields) {
			if (field instanceof SectionViewField) {
				LinkedHashMap<Integer, List<SectionViewField>> instances = ((SectionViewField) field)
						.getInstanceFieldsMap();

				if (instances.size() > 0) {
					for (int instance : instances.keySet()) {
						List<SectionViewField> sectionFields = instances
								.get(instance);

						String tmpSection = section;

						for (SectionViewField sectionField : sectionFields) {
							if (!TextUtils.isEmpty(sectionField.getSelector())) {
								String value = getDisplayValue(sectionField);

								if (value == null) {
									value = "";
								} else {
									if (sectionField.getType() == FieldSpecs.TYPE_NUMBER
											|| sectionField.getType() == FieldSpecs.TYPE_CURRENCY) {
										value = "{number}," + value;
									}
								}

								tmpSection = tmpSection.replace("<"
										+ sectionField.getSelector() + ">",
										value);
							}
						}

						if (!tmpSection.equals(section)) {
							BufferedReader br = new BufferedReader(
									new StringReader(tmpSection));
							String line = null;

							while ((line = br.readLine()) != null) {
								printLine(line, baos);
							}
						}
					}
				}
			}
		}
	}

	private void printLine(String line, ByteArrayOutputStream baos)
			throws IOException {
		final int MAX_COLUMN_WIDTH = settingsDao.getInt(
				Settings.KEY_PRINTER_COLUMN_WIDTH,
				Settings.DEFAULT_PRINTER_COLUMN_WIDTH);
		String[] columns = line.split("\\|");

		for (int i = 0; i < columns.length; ++i) {
			columns[i] = columns[i].trim();
		}

		if (columns.length == 1) {
			baos.write(line.getBytes());
		} else {
			if (columns[0].trim().length() > MAX_COLUMN_WIDTH) {
				baos.write(columns[0].getBytes());
				baos.write("\n".getBytes());
				baos.write(String.format("%-" + MAX_COLUMN_WIDTH + "s", "")
						.getBytes());
			} else {
				baos.write(String.format("%-" + MAX_COLUMN_WIDTH + "s",
						columns[0]).getBytes());
			}

			baos.write(" : ".getBytes());

			if (columns[1].contains("{number},")) {
				Double value = null;
				String number = columns[1].split(",")[1];

				try {
					value = Double.parseDouble(number);
				} catch (NumberFormatException e) {
					// ignore
				}

				if (value != null) {
					baos.write(String.format("%" + MAX_COLUMN_WIDTH + ".2f",
							value).getBytes());
				} else {
					baos.write(number.getBytes());
				}
			} else {
				baos.write(columns[1].getBytes());
			}
		}

		baos.write("\n".getBytes());
	}

	private void print() {
		String printTemplate = formSpecsDao.getPrintTemplate(formSpecId);

		if (TextUtils.isEmpty(printTemplate)) {
			Toast.makeText(
					this,
					"Cannot print because mobile print template is empty. Please specify it in the web panel.",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (TextUtils.isEmpty(settingsDao.getString(Settings.KEY_PRINTER_NAME))) {
			Toast.makeText(
					this,
					"Please try again after choosing a printer in the Settings screen.",
					Toast.LENGTH_LONG).show();
			return;
		}

		Form form = formsDao.getFormWithLocalId(localFormId);

		if (!TextUtils.isEmpty(form.getFilledByName())) {
			printTemplate = printTemplate.replace("<-100>",
					form.getFilledByName());
		} else {
			printTemplate.replace("<-100>", "");
		}

		if (!TextUtils.isEmpty(form.getModifiedByName())) {
			printTemplate = printTemplate.replace("<-101>",
					form.getModifiedByName());
		} else {
			printTemplate.replace("<-101>", form.getModifiedByName());
		}

		if (form.getRemoteCreationTime() != null) {
			printTemplate = printTemplate.replace(
					"<-102>",
					Utils.getDateTimeFormat(this).format(
							form.getRemoteCreationTime()));
		} else if (form.getLocalCreationTime() != null) {
			printTemplate = printTemplate.replace("<-102>", Utils
					.getDateTimeFormat(this)
					.format(form.getLocalCreationTime()));
		}

		if (form.getRemoteModificationTime() != null) {
			printTemplate = printTemplate.replace(
					"<-103>",
					Utils.getDateTimeFormat(this).format(
							form.getRemoteModificationTime()));
		} else if (form.getLocalModificationTime() != null) {
			printTemplate = printTemplate.replace(
					"<-103>",
					Utils.getDateTimeFormat(this).format(
							form.getLocalModificationTime()));
		}

		for (ViewField field : currentFields) {
			if (!TextUtils.isEmpty(field.getSelector())) {
				String value = getDisplayValue(field);

				if (value == null) {
					value = "";
				} else {
					if (field.getType() == FieldSpecs.TYPE_NUMBER
							|| field.getType() == FieldSpecs.TYPE_CURRENCY) {
						value = "{number}," + value;
					}
				}

				printTemplate = printTemplate.replace("<" + field.getSelector()
						+ ">", value);
			}
		}

		BufferedReader br = new BufferedReader(new StringReader(printTemplate));
		String line = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringBuffer section = null;
		boolean inSection = false;

		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();

				if ("<section>".equals(line)) {
					inSection = true;
					section = new StringBuffer();
					continue;
				}

				if ("</section>".equals(line)) {
					inSection = false;
					printSection(section.toString(), baos);
					continue;
				}

				if (inSection) {
					section.append(line);
					section.append("\n");
					continue;
				}

				printLine(line, baos);
			}

			byte[] content = baos.toByteArray();
			Intent intent = new Intent(this, PrintActivity.class);
			intent.putExtra("title", formSpecTitle);
			intent.putExtra("content", content);
			startActivity(intent);
		} catch (IOException e) {
			Toast.makeText(this, "Could not print form.", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		final View v = view;
		if (hasFocus) {
			focussed = v;
		}

		if (showDataPending) {
			return;
		}
		if (!hasFocus && focussed == v) {

			handler.post(new Runnable() {
				@Override
				public void run() {
					ViewField viewField = getViewFieldFromView(v);
					if ((hasFilteringCriteria && listFilteringCriteriaDao
							.isThisFieldhasFilteringCriteria(formSpecId,
									viewField.getSelector()))
							|| (hasVisibilityCriteria && visibilityCriteriaDao
									.isItRelatedToTargetExpression(formSpecId,
											viewField.getSelector()))) {
						showDataPending = true;
						if (showTimingLog) {

							Log.v("observe", "showdata from onfocus change ");
						}
						showProfressAndCallShowdata();

					} else {
						showDataPending = false;
						if (showTimingLog) {

							Log.v("observe", "showdata no need of refresh ");
						}
					}
				}
			});
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "listener invoked " + onItemSelectedCount + " "
					+ spinnersCount);
		}
		onItemSelectedCount = onItemSelectedCount + 1;
		if (onItemSelectedCount > spinnersCount) {
			if (BuildConfig.DEBUG) {
				Log.v(TAG, "****condition met");
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					spinnersCount = 0;
					onItemSelectedCount = 0;
					showProfressAndCallShowdata();
				}
			});
		} else {
			if (BuildConfig.DEBUG) {
				Log.v(TAG, "####condition nt met");
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.refreshButton:
			focussed = null;
			showProfressAndCallShowdata();
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * @return true if dialog needs to be shown; false, otherwise.
	 */
	public boolean isWorkflowSaveDialogRequired() {
		return canSubmit(formSpecId);
	}

	private boolean canSubmit(long formSpecId) {
		FormSpecsDao formsDao = FormSpecsDao
				.getInstance(getApplicationContext());
		String unique_id = formsDao.getUniqueId(formSpecId);

		if (unique_id == null) {
			return false;
		}

		EmployeesDao employeesDao = EmployeesDao
				.getInstance(getApplicationContext());
		String empId = settingsDao.getString("employeeId");
		Long empLongId = null;

		if (empId != null) {
			empLongId = Long.parseLong(empId);
		}

		// IF RANK IS 0 and canSubmit is true we no need to submit the form to
		// workflow

		Long rank = employeesDao.getEmployeeRankWithId(empLongId);
		WorkFlowSpec workFlowSpec = workFlowSpecsDao
				.getWorkFlowSpecWithFormSpecUniqueId(unique_id);

		if (workFlowSpec == null) {
			return false;
		}

		if (rank != null && rank == 0 && workFlowSpec.getHasRoleBasedStages()) {
			return false;
		} else {
			return true;
		}
	}

	private void setProgressDialog(String message) {
		try {

			if (showTimingLog) {
				Log.v("delay", "setProgressDialog");
			}
			// dismiss progress dialog if there is any prevoious one
			dismissProgrssDialog();
			progressDialog = new AlertDialog.Builder(context).create();
			progressDialog.setCancelable(false);
			progressDialog.setMessage(message);
			// progressDialog.setIndeterminateDrawable(new Drawa);
			progressDialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dismissProgrssDialog() {
		try {
			if (showTimingLog) {
				Log.v("delay", "dismissProgrssDialog");
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ProgressTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setProgressDialog(getString(R.string.loading));
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!isCancelled()) {
				;
			}
			return null;

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissProgrssDialog();
			if (BuildConfig.DEBUG) {
				Log.v(TAG, "Cancelled...");
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	class FetchTask extends AsyncTask<Void, Void, Void> {

		Bundle savedInstanceState;

		@SuppressLint("UseSparseArrays")
		@Override
		protected Void doInBackground(Void... params) {
			if (localFormId == 0) {
				editMode = true;

				Long id = formSpecsDao.getLatestFormSpecId(formSpecId);
				if (id != null) {
					formSpecId = id;
				}
				Form form = new Form();

				form.setFormSpecId(formSpecId);
				form.setTemporary(true);
				form.setDirty(true);

				formsDao.save(form);

				localFormId = form.getLocalId();
			} else {
				Form form = formsDao.getFormWithLocalId(localFormId);

				final String toastMsg = singular
						+ " cannot be viewed/modified, as it no longer exists locally.";

				if (form == null) {
					handler.post(new Thread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context, toastMsg, Toast.LENGTH_LONG)
									.show();
						}
					}));

					finish();
				}
			}

			formSpecTitle = formSpecsDao.getFormTitle(formSpecId);
			hasVisibilityCriteria = visibilityCriteriaDao
					.hasVisibilityCriteria(formSpecId);
			hasFilteringCriteria = listFilteringCriteriaDao
					.hasFilteringCriteria(formSpecId);

			fieldSpecs = fieldSpecsDao.getFieldSpecs(formSpecId, false);
			sectionSpecs = sectionSpecsDao.getSectionSpecs(formSpecId);
			pageSpecs = pageSpecsDao.getPageSpecs(formSpecId);
			sectionFieldSpecsMap = new HashMap<Long, List<SectionFieldSpec>>();

			for (SectionSpec sectionSpec : sectionSpecs) {
				sectionFieldSpecsMap.put(sectionSpec.getId(),
						sectionFieldSpecsDao.getFieldSpecs(formSpecId,
								sectionSpec.getId(), false));
			}

			if (originalFields == null) {
				originalFields = getFields(fieldSpecs);
			}

			if (currentFields == null) {
				currentFields = getFields(fieldSpecs);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			ActionBar actionBar = getSupportActionBar();
			Utils.updateActionBar(actionBar);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(formSpecTitle);

			if (pageSpecs.size() > 1) {
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

				for (PageSpec pageSpec : pageSpecs) {
					Tab tab = actionBar.newTab();
					tab.setTag(pageSpec);
					tab.setText(pageSpec.getTitle());
					tab.setTabListener(FormActivity.this);
					actionBar.addTab(tab);
				}

				if (savedInstanceState != null) {
					actionBar.setSelectedNavigationItem(savedInstanceState
							.getInt("activeTab"));
				}

				// we don't need to call showData here, as it will get called
				// when the first tab is displayed
			} else {
				showData();
			}

			// actionBar.setSubtitle("Form submission");

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Activity launched for Local form ID: "
						+ localFormId);
			}

			if (!editMode) {
				startBftsIfRequired();
			}

			formsDao.updateCachedFlag(localFormId, false);

			loadInProgress = false;
			supportInvalidateOptionsMenu();
			dismissProgrssDialog();
		}
	}

	ViewField getViewFieldFromView(View v) {

		ViewField viewField = (ViewField) v.getTag();
		Integer instanceId = null;

		if (viewField instanceof SectionViewField) {
			SectionViewField sectionViewField = (SectionViewField) viewField;
			instanceId = sectionViewField.getSectionInstanceId();
		}

		ViewField toBeFocussedViewField = getViewField(viewField.getSelector(),
				instanceId);
		return toBeFocussedViewField;

	}

	boolean isCreatedByMe() {
		boolean isCreatedByMe = false;
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		Long myEmployeeID = settingsDao
				.getLong(EffortProvider.Settings.KEY_EMPLOYEE_ID);
		Form form = null;

		if (localFormId != 0) {
			form = formsDao.getFormWithLocalId(localFormId);
			if (form != null) {
				if (form.getFilledById() != null
						&& form.getFilledById().equals(myEmployeeID)) {
					isCreatedByMe = true;
				}

			}
		}
		return isCreatedByMe;
	}

	boolean isAssignedToMe() {
		boolean isAssignedToMe = false;
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		Long myEmployeeID = settingsDao
				.getLong(EffortProvider.Settings.KEY_EMPLOYEE_ID);
		Form form = null;

		if (localFormId != 0) {
			form = formsDao.getFormWithLocalId(localFormId);
			if (form != null) {

				if (form.getAssignedToId() != null
						&& form.getAssignedToId().equals(myEmployeeID)) {
					isAssignedToMe = true;
				}
			}
		}
		return isAssignedToMe;
	}

	/*
	 * returns true, if the dependency list is changed
	 */
	boolean isRefreshingNecessery(ViewField viewField) {

		boolean isValuesChanged = true;
		String formula = viewField.getFormula();

		HashMap<String, String> currentFieldValues = new HashMap<String, String>();
		Set<String> fieldset = getFormulaFields(formula);
		if (fieldset != null) {
			for (String token : fieldset) {
				String selector = TextUtils.split(token, "_")[0];

				if (viewField instanceof SectionViewField) {

					ViewField fieldViewField;
					if (selector.startsWith("S")) {
						// means it is sectionviewfield
						fieldViewField = getViewField(selector,
								((SectionViewField) viewField)
										.getSectionInstanceId());
					} else {
						fieldViewField = getViewField(selector, null);
					}

					if (fieldViewField != null) {
						if ((fieldViewField.getType() == FieldSpecs.TYPE_NUMBER || fieldViewField
								.getType() == FieldSpecs.TYPE_CURRENCY)) {
							currentFieldValues.put(token,
									fieldViewField.getRemoteValue());
						} else if (fieldViewField.getType() == FieldSpecs.TYPE_ENTITY) {
							currentFieldValues.put(token,
									fieldViewField.getLocalValue());
						}
					}
				} else {

					ViewField fieldViewField = getViewField(selector, null);
					if (fieldViewField != null) {
						if ((fieldViewField.getType() == FieldSpecs.TYPE_NUMBER || fieldViewField
								.getType() == FieldSpecs.TYPE_CURRENCY)) {
							currentFieldValues.put(token,
									fieldViewField.getRemoteValue());
						} else if (fieldViewField.getType() == FieldSpecs.TYPE_ENTITY) {
							currentFieldValues.put(token,
									fieldViewField.getLocalValue());
						}
					}
				}
			}
			HashMap<String, String> oldFieldValues = viewField
					.getFormulaFieldValues();
			if (Utils.hashMapsEqual(oldFieldValues, currentFieldValues)) {
				isValuesChanged = false;
			} else {
				viewField.setFormulaFieldValues(currentFieldValues);
				isValuesChanged = true;
			}
		} else {
			// this is the case when the formula conatins no identifiers except
			// constants
			// like formula=190
			if (viewField.getRemoteValue() != null) {
				viewField.setFormula(viewField.getRemoteValue());
			}
			isValuesChanged = true;
		}

		return isValuesChanged;
	}

	// will return tokens(identifiers) in the formula as a set
	Set<String> getFormulaFields(String formula) {
		ANTLRInputStream input = new ANTLRInputStream(formula);
		ExprLexer lexer = new ExprLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ExprParser parser = new ExprParser(tokens);
		ParseTree tree = parser.expr_();
		ListVisitor visitor = new ListVisitor();
		Set<String> set = visitor.visit(tree);
		return set;
	}

	public static boolean canUseCachedResults(List<String> cachedIds,
			Long pickedId) {
		if (cachedIds == null || cachedIds.size() <= 0 || pickedId == null) {
			return false;
		}

		int numItems = cachedIds.size();

		for (int i = 0; i < numItems; ++i) {
			if (pickedId <= Long.parseLong(cachedIds.get(i))) {
				return true;
			}
		}

		return false;
	}

}