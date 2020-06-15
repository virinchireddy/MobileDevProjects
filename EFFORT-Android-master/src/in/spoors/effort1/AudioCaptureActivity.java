package in.spoors.effort1;

import java.io.File;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

public class AudioCaptureActivity extends ActionBarActivity {

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

	public static final String TAG = "AudioCaptureActivity";

	/**
	 * The name of the Intent-extra used to indicate a content resolver Uri to
	 * be used to store the requested image or video.
	 * 
	 * As this constant is set to MediaStore.EXTRA_OUTPUT, you can use
	 * MediaStore.EXTRA_OUTPUT as well.
	 * 
	 */
	public static final String EXTRA_OUTPUT = MediaStore.EXTRA_OUTPUT;

	private boolean recordingInProgress = false;

	private MediaRecorder recorder;

	private Uri outputUri;

	private Button recordButton;
	private Button playButton;
	private Chronometer chronometer;
	private boolean playingInProgress;

	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_capture);

		Intent intent = getIntent();
		outputUri = (Uri) intent.getParcelableExtra(EXTRA_OUTPUT);

		if (outputUri == null) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "You must specified Uri for EXTRA_OUTPUT.");
			}

			setResult(RESULT_CANCELED);
			finish();
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Output URI: " + outputUri.toString());
			}
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "recordingInProgress=" + recordingInProgress);
			Log.i(TAG, "playingInProgress=" + playingInProgress);
			Log.i(TAG, "recorder=" + recorder);
			Log.i(TAG, "player=" + player);
		}

		recordButton = (Button) findViewById(R.id.recordButton);
		playButton = (Button) findViewById(R.id.playButton);
		playButton.setEnabled(false);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		chronometer.setVisibility(View.GONE);

		ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		Utils.updateActionBar(actionBar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_capture, menu);

		MenuItem save = menu.findItem(R.id.saveAudio);
		MenuItem discard = menu.findItem(R.id.discardAudio);
		discard.setEnabled(true);

		if (outputUri != null) {
			File file = new File(outputUri.getPath());
			save.setEnabled(file.exists());
		} else {
			save.setEnabled(false);
		}

		return true;
	}

	public void onRecordButtonClick(View view) {
		if (recordingInProgress) {
			recordButton.setEnabled(false);
			recordButton.setText(R.string.start_recording);
			recordingInProgress = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordButton.setEnabled(true);
			playButton.setEnabled(true);
			chronometer.stop();
			supportInvalidateOptionsMenu();
		} else {
			recordButton.setEnabled(false);
			recordButton.setText(R.string.stop_recording);

			// play button should be enabled only when recording has been
			// stopped
			playButton.setEnabled(false);

			recordingInProgress = true;

			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setOutputFile(outputUri.getPath());
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			try {
				recorder.prepare();
				recorder.start();
			} catch (Exception e) {
				Toast.makeText(this, "Unable to record audio.",
						Toast.LENGTH_LONG).show();
				if (BuildConfig.DEBUG) {
					Log.e(TAG,
							"Unable to record audio (failed to prepare/start): "
									+ e.toString(), e);
				}
			}

			recordButton.setEnabled(true);
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.setVisibility(View.VISIBLE);
			chronometer.start();
		}
	}

	public void onPlayButtonClick(View view) {
		if (playingInProgress) {
			playingInProgress = false;
			playButton.setText(R.string.start_playing);
			player.release();
			player = null;
		} else {
			playingInProgress = true;
			playButton.setText(R.string.stop_playing);
			player = new MediaPlayer();

			try {
				player.setDataSource(outputUri.getPath());
				player.prepare();
				player.start();
			} catch (Exception e) {
				Toast.makeText(this, "Unable to play audio.", Toast.LENGTH_LONG)
						.show();

				if (BuildConfig.DEBUG) {
					Log.e(TAG,
							"Unable to play audio (failed to prepare/start): "
									+ e.toString(), e);
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.saveAudio:
			save();
			break;

		case R.id.discardAudio:
			discard();
			break;

		case android.R.id.home:
			// TODO: let the user decide whether they want to save or discard
			if (fileExists()) {
				save();
			} else {
				discard();
			}

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private boolean fileExists() {
		if (outputUri != null) {
			File file = new File(outputUri.getPath());
			return file.exists();
		} else {
			return false;
		}
	}

	private void closeMediaObjects() {
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}

		if (player != null) {
			player.release();
			player = null;
		}
	}

	private void save() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "File has been saved to " + outputUri.toString());
		}

		closeMediaObjects();

		setResult(RESULT_OK);
		finish();
	}

	private void discard() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "User opted to discard " + outputUri.toString());
		}

		closeMediaObjects();

		if (fileExists()) {
			File file = new File(outputUri.getPath());

			if (file.delete()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Deleted " + outputUri.getPath());
				}
			} else {
				String message = "Could not delete " + outputUri.getPath();
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();

				if (BuildConfig.DEBUG) {
					Log.i(TAG, message);
				}
			}
		}

		setResult(RESULT_CANCELED);
		finish();
	}

}
