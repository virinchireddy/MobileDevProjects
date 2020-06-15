package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.InvitationsDao;
import in.spoors.effort1.dao.JobTypesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Invitation;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InvitationActivity extends ActionBarActivity {

	public static final String TAG = "InvitationActivity";

	private long invitationId;
	private Invitation invitation;
	private String jobType;
	private String customerName;
	private SettingsDao settingsDao;
	private CustomersDao customersDao;

	private TextView titleTextView;
	private TextView typeTextView;
	private LinearLayout customerLayout;
	private TextView customerLabel;
	private TextView customerTextView;
	private TextView startTimeTextView;
	private TextView endTimeTextView;
	private View descriptionLayout;
	private TextView descriptionTextView;
	private TextView acceptedTextView;
	private Button acceptButton;
	private View addressLayout;
	private View streetLayout;
	private TextView streetTextView;
	private View areaLayout;
	private TextView areaTextView;
	private View landmarkLayout;
	private TextView landmarkTextView;
	private View cityLayout;
	private TextView cityTextView;
	private View stateLayout;
	private TextView stateTextView;
	private View pinCodeLayout;
	private TextView pinCodeTextView;
	private View countryLayout;
	private TextView countryTextView;
	private View locationLayout;
	private TextView latitudeTextView;
	private TextView longitudeTextView;
	private View viewMapButtonInAddressSection;
	private String singular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());

		singular = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		typeTextView = (TextView) findViewById(R.id.typeTextView);
		startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
		endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
		descriptionLayout = findViewById(R.id.descriptionLayout);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		acceptedTextView = (TextView) findViewById(R.id.acceptedTextView);
		acceptButton = (Button) findViewById(R.id.acceptButton);
		acceptButton.setText("Accept this " + singular);
		customerLayout = (LinearLayout) findViewById(R.id.customerLayout);
		customerLabel = (TextView) findViewById(R.id.customerLabel);
		customerLabel.setText(settingsDao.getLabel(
				Settings.LABEL_CUSTOMER_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
		customerTextView = (TextView) findViewById(R.id.customerTextView);
		addressLayout = findViewById(R.id.addressLayout);
		streetLayout = findViewById(R.id.streetLayout);
		streetTextView = (TextView) findViewById(R.id.streetTextView);
		areaLayout = findViewById(R.id.areaLayout);
		areaTextView = (TextView) findViewById(R.id.areaTextView);
		landmarkLayout = findViewById(R.id.landmarkLayout);
		landmarkTextView = (TextView) findViewById(R.id.landmarkTextView);
		cityLayout = findViewById(R.id.cityLayout);
		cityTextView = (TextView) findViewById(R.id.cityTextView);
		stateLayout = findViewById(R.id.stateLayout);
		stateTextView = (TextView) findViewById(R.id.stateTextView);
		pinCodeLayout = findViewById(R.id.pinCodeLayout);
		pinCodeTextView = (TextView) findViewById(R.id.pinCodeTextView);
		countryLayout = findViewById(R.id.countryLayout);
		countryTextView = (TextView) findViewById(R.id.countryTextView);
		locationLayout = findViewById(R.id.locationLayout);
		latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
		longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
		viewMapButtonInAddressSection = findViewById(R.id.viewMapButtonInAddressSection);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		invitationId = getIntent().getLongExtra(EffortProvider.Invitations._ID,
				0);
		new ShowInvitationTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.invitation, menu);
		boolean hasModifyInvitationPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_INVITATION_MODIFY, true);

		menu.findItem(R.id.acceptInvitation)
				.setTitle("Accept this " + singular);
		menu.findItem(R.id.acceptInvitation).setVisible(
				hasModifyInvitationPermission
						&& invitation != null
						&& (invitation.getAccepted() == null || invitation
								.getAccepted() == false));
		return true;
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

			finish();
			break;

		case R.id.acceptInvitation:
			acceptInvitation();
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void onCustomerLinkClick(View view) {
		if (invitation != null && invitation.getLocalCustomerId() != null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching customer activity for customer id "
						+ invitation.getLocalCustomerId());
			}

			Intent intent = new Intent(this, CustomerActivity.class);
			intent.putExtra(Customers._ID, invitation.getLocalCustomerId());
			startActivity(intent);
		}
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

	private class ShowInvitationTask extends AsyncTask<Long, Integer, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			InvitationsDao invitationsDao = InvitationsDao
					.getInstance(getApplicationContext());
			invitation = invitationsDao.getInvitationWithId(invitationId);

			if (invitation != null) {
				invitationsDao.updateReadFlag(invitationId, true);
				JobTypesDao jobTypesDao = JobTypesDao
						.getInstance(getApplicationContext());
				jobType = jobTypesDao.getName(invitation.getTypeId());

				if (invitation.getLocalCustomerId() != null) {
					CustomersDao customersDao = CustomersDao
							.getInstance(getApplicationContext());
					customerName = customersDao
							.getCompanyNameWithLocalId(invitation
									.getLocalCustomerId());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (invitation == null) {
				Toast.makeText(
						InvitationActivity.this,
						"Oops! Invitation with invitation ID " + invitationId
								+ " is missing.", Toast.LENGTH_LONG).show();
				return;
			}

			titleTextView.setText(invitation.getTitle());
			typeTextView.setText(jobType);

			if (!TextUtils.isEmpty(customerName)) {
				customerTextView.setText(customerName);

				if (settingsDao.getBoolean(Settings.KEY_HIDE_CUSTOMER_INFO,
						false)
						|| !Utils.isMappedCustomer(settingsDao, customersDao,
								invitation.getLocalCustomerId())) {
					customerTextView.setTextColor(0xff000000);
				} else {
					OnClickListener listener = new OnClickListener() {
						@Override
						public void onClick(View v) {
							onCustomerLinkClick(v);
						}
					};

					customerTextView.setTextColor(0xff0000ee);
					customerTextView.setPaintFlags(customerTextView
							.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
					customerTextView.setOnClickListener(listener);
					customerLayout.setOnClickListener(listener);
					customerLabel.setOnClickListener(listener);
				}

				findViewById(R.id.customerLayout).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.customerLayout).setVisibility(View.GONE);
			}

			SimpleDateFormat dtf = Utils
					.getDateTimeFormat(getApplicationContext());

			startTimeTextView.setText(dtf.format(invitation.getStartTime()));
			endTimeTextView.setText(dtf.format(invitation.getEndTime()));

			descriptionTextView.setText(invitation.getDescription());
			descriptionLayout.setVisibility(Utils.getVisibility(invitation
					.getDescription()));

			acceptedTextView.setText(invitation.getAccepted() ? "Yes" : "No");
			acceptButton.setEnabled(!invitation.getAccepted());

			// address section
			String str = invitation.getStreet();
			streetTextView.setText(str);
			streetLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getArea();
			areaTextView.setText(str);
			areaLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getLandmark();
			landmarkTextView.setText(str);
			landmarkLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getCity();
			cityTextView.setText(str);
			cityLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getState();
			stateTextView.setText(str);
			stateLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getPinCode();
			pinCodeTextView.setText(str);
			pinCodeLayout.setVisibility(Utils.getVisibility(str));

			str = invitation.getCountry();
			countryTextView.setText(str);
			countryLayout.setVisibility(Utils.getVisibility(str));

			addressLayout.setVisibility(Utils.getVisibility(streetLayout,
					areaLayout, landmarkLayout, cityLayout, stateLayout,
					pinCodeLayout, countryLayout));

			if (invitation.getLatitude() != null
					&& invitation.getLongitude() != null) {
				latitudeTextView.setText(String.valueOf(invitation
						.getLatitude()));
				longitudeTextView.setText(String.valueOf(invitation
						.getLongitude()));
				locationLayout.setVisibility(View.VISIBLE);

				if (addressLayout.getVisibility() == View.VISIBLE) {
					viewMapButtonInAddressSection.setVisibility(View.GONE);
				}
			} else {
				locationLayout.setVisibility(View.GONE);
			}

			supportInvalidateOptionsMenu();
		}
	}

	private void acceptInvitation() {
		new AlertDialog.Builder(this)
				.setTitle("Confirm acceptance")
				.setMessage(
						"Are you sure you want to accept this " + singular
								+ "?")
				.setPositiveButton("Accept",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								InvitationsDao invitationsDao = InvitationsDao
										.getInstance(getApplicationContext());
								invitationsDao.acceptInvitation(invitation
										.getId());
								Toast.makeText(InvitationActivity.this,
										singular + " has been accepted.",
										Toast.LENGTH_LONG).show();
								finish();
								Utils.sync(getApplicationContext());
							}
						})
				.setNegativeButton("Don't accept",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Toast.makeText(
										InvitationActivity.this,
										singular + " has been left unaccepted.",
										Toast.LENGTH_LONG).show();
							}
						}).show();
	}

	public void onAcceptButtonClick(View view) {
		acceptInvitation();
	}

	public void onViewMapButtonClick(View view) {
		if (invitation == null) {
			return;
		}

		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra("name", invitation.getTitle());

		if (invitation.getLatitude() != null
				&& invitation.getLongitude() != null) {
			intent.putExtra("latitude", invitation.getLatitude());
			intent.putExtra("longitude", invitation.getLongitude());
		} else {
			String address = Utils.getAddressForMapDisplay(
					invitation.getStreet(), invitation.getArea(),
					invitation.getCity(), invitation.getState(),
					invitation.getCountry(), invitation.getPinCode());

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching map activity for address: " + address);
			}

			intent.putExtra("address", address);
		}

		startActivity(intent);
	}

}
