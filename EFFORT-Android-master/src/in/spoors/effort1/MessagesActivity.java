package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.MessagesDao;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessagesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnCheckedChangeListener,
		RefreshListener {

	public static final String TAG = "MessagesActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat dateTimeFormat;
	private HashMap<Long, Boolean> checkedMap;
	private DrawerFragment drawerFragment;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		setContentView(R.layout.activity_messages);
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle("Messages");

		if (savedInstanceState == null) {
			checkedMap = new HashMap<Long, Boolean>();
		} else {
			checkedMap = (HashMap<Long, Boolean>) savedInstanceState
					.getSerializable("checkedMap");
		}

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = { Messages._ID, Messages.SUBJECT,
				Messages.REMOTE_CREATION_TIME, Messages.BODY };
		int[] views = { R.id.checkBox, R.id.subjectTextView, R.id.timeTextView,
				R.id.bodyTextView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_message, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_messages, "Messages",
				null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		menu.findItem(R.id.deleteMessages).setVisible(
				!drawerFragment.isDrawerOpen() && adapter.getCount() > 0);

		int len = listView.getCount();
		int checked = 0;
		for (Long id : checkedMap.keySet()) {
			if (checkedMap.get(id)) {
				checked++;
			}
		}

		if (checked == 0) {
			menu.findItem(R.id.action_select_all).setVisible(true);
			menu.findItem(R.id.action_unselect_all).setVisible(false);
		} else if (checked == len) {
			menu.findItem(R.id.action_select_all).setVisible(false);
			menu.findItem(R.id.action_unselect_all).setVisible(true);
		} else if (checked < len) {
			menu.findItem(R.id.action_select_all).setVisible(true);
			menu.findItem(R.id.action_unselect_all).setVisible(true);
		}

		menu.findItem(R.id.sub_menu).setTitle(checked + "  selected");
		menu.findItem(R.id.sub_menu).setVisible(
				!drawerFragment.isDrawerOpen() && adapter.getCount() > 0);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		return new CursorLoader(getApplicationContext(), Messages.CONTENT_URI,
				Messages.ALL_COLUMNS, null, null, Messages.REMOTE_CREATION_TIME
						+ " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		if (cursor.getCount() != checkedMap.size()) {
			checkedMap.clear();
		}

		while (cursor.moveToNext()) {
			if (!checkedMap.containsKey(cursor.getLong(Messages._ID_INDEX))) {
				checkedMap.put(cursor.getLong(Messages._ID_INDEX), false);
			}
		}

		adapter.swapCursor(cursor);
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}

		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		adapter.swapCursor(null);
		supportInvalidateOptionsMenu();
	}

	class MyViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "MessagesActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			TextView tv;

			switch (columnIndex) {
			case Messages._ID_INDEX:
				CheckBox checkBox = (CheckBox) view;
				long id = cursor.getLong(Messages._ID_INDEX);
				checkBox.setTag(id);
				Boolean checked = checkedMap.get(id);
				checkBox.setChecked(checked != null && checked);
				checkBox.setOnCheckedChangeListener(MessagesActivity.this);

				return true;

			case Messages.REMOTE_CREATION_TIME_INDEX:
				Date time = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Messages.REMOTE_CREATION_TIME_INDEX));

				tv = (TextView) view;
				tv.setText(dateTimeFormat.format(time));

				return true;

			case Messages.BODY_INDEX:

				TextView bodyTextView = (TextView) view;
				bodyTextView
						.setText(cursor.getString(Messages.BODY_INDEX) != null ? cursor
								.getString(Messages.BODY_INDEX) : "");

				return true;

			case Messages.SUBJECT_INDEX:
				int qualityType = cursor.getInt(Messages.QUALITY_TYPE_INDEX);
				int colourCode;

				switch (qualityType) {
				case 2:
					colourCode = getResources().getColor(R.color.green);
					break;

				case 1:
					colourCode = getResources().getColor(R.color.blue);
					break;
				case 0:
					colourCode = getResources().getColor(R.color.black);
					break;
				case -1:
					colourCode = getResources().getColor(R.color.orange);
					break;
				case -2:
					colourCode = getResources().getColor(R.color.red);
					break;

				default:
					colourCode = getResources().getColor(R.color.black);
					break;
				}

				TextView subjectTextView = (TextView) view;
				subjectTextView.setText(cursor
						.getString(Messages.SUBJECT_INDEX) != null ? cursor
						.getString(Messages.SUBJECT_INDEX) : "");
				subjectTextView.setTextColor(colourCode);

				return true;

			default:
				return false;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Parent: " + parent.toString() + ", View: "
							+ view.toString() + ", Position: " + position
							+ ", ID: " + id);
		}

		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(Leaves._ID, id);
		startActivity(intent);
	}

	private void changeAll(boolean checked) {
		for (Long id : checkedMap.keySet()) {
			checkedMap.put(id, checked);
		}

		adapter.notifyDataSetChanged();
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_select_all:
			changeAll(true);
			break;

		case R.id.action_unselect_all:
			changeAll(false);
			break;

		case android.R.id.home:
			onBackPressed();
			return true;

		case R.id.deleteMessages:
			List<Long> ids = new ArrayList<Long>();

			for (Long id : checkedMap.keySet()) {
				if (checkedMap.get(id)) {
					ids.add(id);
				}
			}

			if (ids.size() > 0) {
				deleteMessages(ids);
			} else {
				Toast.makeText(
						this,
						"For deleting messages, please select one or more messages and click this icon again. ",
						Toast.LENGTH_LONG).show();
			}
		}

		return super.onOptionsItemSelected(item);

	}

	private void deleteMessages(final List<Long> ids) {
		new AlertDialog.Builder(this)
				.setTitle("Confirm deletion")
				.setMessage(
						"Are you sure you want to delete the selected "
								+ ids.size() + " message(s)?")
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								MessagesDao messagesDao = MessagesDao
										.getInstance(getApplicationContext());

								int affectedRows = messagesDao
										.deleteMessages(ids);

								if (affectedRows > 0) {
									checkedMap.clear();
									Toast.makeText(
											MessagesActivity.this,
											"Selected message(s) have been deleted.",
											Toast.LENGTH_LONG).show();
									getSupportLoaderManager().restartLoader(0,
											null, MessagesActivity.this);
								} else {
									Toast.makeText(
											MessagesActivity.this,
											"Could not delete the selected message(s).",
											Toast.LENGTH_LONG).show();
								}

							}
						})
				.setNegativeButton("Don't delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Toast.makeText(MessagesActivity.this,
										"Cancelled message deletion.",
										Toast.LENGTH_LONG).show();
							}
						}).show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("checkedMap", checkedMap);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		super.onBackPressed();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		checkedMap.put((Long) buttonView.getTag(), isChecked);
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerFragment.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerFragment.onConfigurationChanged(newConfig);
	}

}