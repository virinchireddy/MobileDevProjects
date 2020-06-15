package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldSpecsDao;
import in.spoors.effort1.dao.EntityFieldValueSpecsDao;
import in.spoors.effort1.dao.EntityFieldsDao;
import in.spoors.effort1.dao.EntitySpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.dto.EntityFieldSpec;
import in.spoors.effort1.dto.SectionViewField;
import in.spoors.effort1.dto.ViewField;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Entities;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class EntityActivity extends ActionBarActivity {

	private static final String STORAGE_TIME_FORMAT_PATTERN = "HH:mm";

	public static final String TAG = "EntityActivity";

	private long localEntityId;
	private long entitySpecId;

	/**
	 * Entity fields that acts as the scratch pad
	 */
	private ArrayList<ViewField> currentFields;

	private CustomersDao customersDao;
	private EmployeesDao employeesDao;
	private EntitiesDao entitiesDao;
	private EntityFieldsDao fieldsDao;
	private EntitySpecsDao entitySpecsDao;
	private EntityFieldSpecsDao fieldSpecsDao;
	private EntityFieldValueSpecsDao fieldValueSpecsDao;

	private List<EntityFieldSpec> fieldSpecs;

	private String singular;

	// private FileDto fileDto;

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entity);
		customersDao = CustomersDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		entitiesDao = EntitiesDao.getInstance(getApplicationContext());
		fieldsDao = EntityFieldsDao.getInstance(getApplicationContext());
		entitySpecsDao = EntitySpecsDao.getInstance(getApplicationContext());
		fieldSpecsDao = EntityFieldSpecsDao
				.getInstance(getApplicationContext());
		fieldValueSpecsDao = EntityFieldValueSpecsDao
				.getInstance(getApplicationContext());
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());

		singular = settingsDao.getLabel(Settings.LABEL_LIST_SINGULAR_KEY,
				Settings.LABEL_LIST_SINGULAR_DEFAULT_VLAUE);

		if (savedInstanceState == null) {
			localEntityId = getIntent().getLongExtra(Entities._ID, 0);

			if (localEntityId == 0) {
				Toast.makeText(this, "Invalid " + singular + " id.",
						Toast.LENGTH_LONG).show();
				finish();
			}

			entitySpecId = entitiesDao.getEntitySpecId(localEntityId);
		} else {
			localEntityId = savedInstanceState.getLong("localEntityId");
			entitySpecId = savedInstanceState.getLong("entitySpecId");
			currentFields = (ArrayList<ViewField>) savedInstanceState
					.getSerializable("currentFields");
		}

		Entity entity = entitiesDao.getEntityWithLocalId(localEntityId);

		if (entity == null) {
			Toast.makeText(
					this,
					singular
							+ " cannot be viewed/modified, as it no longer exists locally.",
					Toast.LENGTH_LONG).show();
			finish();
		}

		String title = entitySpecsDao.getFormTitle(entitySpecId);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(title);
		// actionBar.setSubtitle("Form submission");

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for Local entity ID: "
					+ localEntityId);
		}

		fieldSpecs = fieldSpecsDao.getFieldSpecs(entitySpecId, false);

		if (currentFields == null) {
			currentFields = getFields(fieldSpecs);
		}

		showData();
	}

	@SuppressLint("UseSparseArrays")
	public ArrayList<ViewField> getFields(List<EntityFieldSpec> fieldSpecs) {
		ArrayList<ViewField> fields = new ArrayList<ViewField>(
				fieldSpecs.size());

		for (EntityFieldSpec fieldSpec : fieldSpecs) {
			ViewField viewField = new ViewField();
			viewField.setFormSpecId(fieldSpec.getEntitySpecId());
			viewField.setFieldSpecId(fieldSpec.getId());
			viewField.setLocalFormId(localEntityId);
			viewField.setType(fieldSpec.getType());
			viewField.setTypeExtra(fieldSpec.getTypeExtra());
			viewField.setComputed(fieldSpec.getComputed());
			viewField.setEnabledBarcode(fieldSpec.getBarcodeField());
			viewField.setFormula(fieldSpec.getFormula());
			viewField.setLabel(fieldSpec.getLabel());
			viewField.setIdentifier(fieldSpec.getIdentifier());
			viewField.setRequired(fieldSpec.getRequired());
			viewField.setDisplayOrder(fieldSpec.getDisplayOrder());

			EntityField field = fieldsDao.getField(localEntityId,
					fieldSpec.getId());

			if (field != null) {
				viewField.setLocalValue(field.getLocalValue());
				viewField.setRemoteValue(field.getRemoteValue());
				viewField.setLocalId(field.getLocalId());
			} else {
				if (viewField.getRequired()) {
					if (viewField.getType() == FieldSpecs.TYPE_DATE) {
						viewField.setRemoteValue(SQLiteDateTimeUtils
								.getCurrentSQLiteDate());
					} else if (viewField.getType() == FieldSpecs.TYPE_TIME) {
						Date time = getDefaultTime();
						SimpleDateFormat storageTimeFormat = new SimpleDateFormat(
								STORAGE_TIME_FORMAT_PATTERN);
						viewField
								.setRemoteValue(storageTimeFormat.format(time));
					}
				}
			}

			fields.add(viewField);
		}

		return fields;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entity, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localEntityId", localEntityId);
		outState.putLong("entitySpecId", entitySpecId);
		outState.putSerializable("currentFields", currentFields);
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
			boolean addAsteriskToRequiredFields) {
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
	public static TextView addLabel(Context context, ViewField viewField,
			LinearLayout layout, boolean addAsteriskToRequiredFields,
			boolean twoColumnLayout) {
		LayoutParams layoutParams = null;

		if (twoColumnLayout) {
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
		labelTV.setTextAppearance(context,
				android.R.style.TextAppearance_Medium);
		labelTV.setText(viewField.getLabel()
				+ (addAsteriskToRequiredFields && viewField.getRequired() ? "*"
						: ""));
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
	public static String getDisplayValue(Context context,
			CustomersDao customersDao, EmployeesDao employeesDao,
			EntitiesDao entitiesDao,
			EntityFieldValueSpecsDao fieldValueSpecsDao, ViewField viewField) {
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
				return Utils.getDateFormat(context)
						.format(SQLiteDateTimeUtils.getDate(viewField
								.getRemoteValue()));

			case FieldSpecs.TYPE_TIME:
				String timeStr = viewField.getRemoteValue();
				int hourOfDay = Integer.parseInt(timeStr.substring(0, 2));
				int minute = Integer.parseInt(timeStr.substring(3, 5));
				SimpleDateFormat displayTimeFormat = Utils
						.getTimeFormat(context);
				return displayTimeFormat.format(getTime(hourOfDay, minute));

			case FieldSpecs.TYPE_YES_OR_NO:
				return Boolean.parseBoolean(viewField.getRemoteValue()) ? "Yes"
						: "No";

			case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
				return fieldValueSpecsDao.getValue(Long.parseLong(viewField
						.getRemoteValue()));
			}
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showData() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Number of fields: " + currentFields.size());
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.entityLinearLayout);
		layout.removeAllViews();

		LayoutParams viewLayoutParams = getViewLayoutParams();

		for (final ViewField viewField : currentFields) {
			String value = getDisplayValue(this, customersDao, employeesDao,
					entitiesDao, fieldValueSpecsDao, viewField);

			TextView labelTV = addLabel(this, viewField, layout, false, false);

			TextView valueTV = new TextView(this);
			valueTV.setTextAppearance(this,
					android.R.style.TextAppearance_Small);
			linkify(this, viewField, labelTV, valueTV);
			valueTV.setText(value);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				valueTV.setTextIsSelectable(true);
			}

			layout.addView(valueTV, viewLayoutParams);
			addSeparator(this, layout);
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

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
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

	private static Date getTime(int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();

		cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);

		return cal.getTime();
	}

}
