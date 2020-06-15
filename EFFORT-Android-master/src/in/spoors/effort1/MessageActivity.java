package in.spoors.effort1;

import in.spoors.effort1.dao.MessagesDao;
import in.spoors.effort1.dto.Message;
import in.spoors.effort1.provider.EffortProvider.Messages;

import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends ActionBarActivity {

	public static final String TAG = "MessageActivity";

	private long messageId;

	/**
	 * Customer that acts as the scratch pad
	 */
	private Message currentMessage;

	private MessagesDao messagesDao;

	// view mode controls
	private TextView subjectTextView;
	private TextView bodyTextView;
	private TextView createdOnTextView;
	private SimpleDateFormat dateTimeFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		messagesDao = MessagesDao.getInstance(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		if (savedInstanceState == null) {
			messageId = getIntent().getLongExtra(Messages._ID, 0);
		} else {
			messageId = savedInstanceState.getLong("messageId");
			currentMessage = (Message) savedInstanceState
					.getSerializable("currentMessage");
		}

		if (messageId == 0) {
			Toast.makeText(this, "Invalid message ID.", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		currentMessage = messagesDao.getMessage(messageId);

		if (currentMessage == null) {
			Toast.makeText(this,
					"Message with message ID " + messageId + " is missing.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Message");

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for message ID: " + messageId);
		}

		// view mode widgets
		subjectTextView = (TextView) findViewById(R.id.subjectTextView);
		bodyTextView = (TextView) findViewById(R.id.bodyTextView);
		createdOnTextView = (TextView) findViewById(R.id.createdOnTextView);

		subjectTextView.setText(currentMessage.getSubject());
		bodyTextView.setText(currentMessage.getBody());
		createdOnTextView.setText(dateTimeFormat.format(currentMessage
				.getRemoteCreationTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("messageId", messageId);
		outState.putSerializable("currentMessage", currentMessage);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
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

		case R.id.deleteMessage:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Delete message menu item clicked.");
			}

			deleteMessage();

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void deleteMessage() {
		new AlertDialog.Builder(this)
				.setTitle("Confirm deletion")
				.setMessage("Are you sure you want to delete this message?")
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								int affectedRows = messagesDao
										.deleteMessage(messageId);

								if (affectedRows > 0) {
									Toast.makeText(MessageActivity.this,
											"Message has been deleted.",
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(MessageActivity.this,
											"Could not delete the message.",
											Toast.LENGTH_LONG).show();
								}

								finish();
							}
						})
				.setNegativeButton("Don't delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Toast.makeText(MessageActivity.this,
										"Cancelled message deletion.",
										Toast.LENGTH_LONG).show();
							}
						}).show();
	}

	public void onDeleteMessageButtonClick(View v) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Delete message button clicked.");
		}

		deleteMessage();
	}

}
