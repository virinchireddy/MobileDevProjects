package in.spoors.effort1;

import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.provider.EffortProvider.Articles;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class ArticleActivity extends ActionBarActivity {

	public static final String TAG = "ArticleActivity";

	private long articleId;

	/**
	 * Customer that acts as the scratch pad
	 */
	private Article currentArticle;

	private ArticlesDao articlesDao;

	// view mode controls
	private View mediaLayout;
	private TextView titleTextView;
	private TextView descriptionTextView;
	private Button mediaButton;
	private TextView createdOnTextView;
	private TextView modifiedOnTextView;
	private TextView createdByTextView;
	private TextView modifiedByTextView;
	private SimpleDateFormat dateTimeFormat;
	private ProgressUpdateThread progressUpdateThread;

	private SettingsDao settingsDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		articlesDao = ArticlesDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		if (savedInstanceState == null) {
			articleId = getIntent().getLongExtra(Articles._ID, 0);
		} else {
			articleId = savedInstanceState.getLong("articleId");
			currentArticle = (Article) savedInstanceState
					.getSerializable("currentArticle");
		}

		if (articleId == 0) {
			Toast.makeText(
					this,
					"Invalid "
							+ settingsDao
									.getLabel(
											Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
											Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE)
							+ " ID.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		currentArticle = articlesDao.getArticleWithId(articleId);

		if (currentArticle == null) {
			Toast.makeText(
					this,
					settingsDao
							.getLabel(
									Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
									Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE)
							+ " with "
							+ settingsDao
									.getLabel(
											Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
											Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE)
							+ " ID " + articleId + " is missing.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(settingsDao.getLabel(
				Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
				Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE));
		actionBar.setSubtitle(settingsDao.getLabel(
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_KEY,
				Settings.LABEL_KNOWLEDGEBASE_SINGULAR_DEFAULT_VLAUE));

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for article ID: " + articleId);
		}

		// view mode widgets
		mediaLayout = findViewById(R.id.mediaLayout);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		mediaButton = (Button) findViewById(R.id.mediaButton);
		createdOnTextView = (TextView) findViewById(R.id.createdOnTextView);
		modifiedOnTextView = (TextView) findViewById(R.id.modifiedOnTextView);
		createdByTextView = (TextView) findViewById(R.id.createdByTextView);
		modifiedByTextView = (TextView) findViewById(R.id.modifiedByTextView);

		if (currentArticle.getMediaId() != null) {
			mediaLayout.setVisibility(View.VISIBLE);
			updateMediaButtonText();
		} else {
			mediaLayout.setVisibility(View.GONE);
		}

		titleTextView.setText(currentArticle.getTitle());
		descriptionTextView.setText(currentArticle.getDescription());
		createdOnTextView.setText(dateTimeFormat.format(currentArticle
				.getRemoteCreationTime()));
		modifiedOnTextView.setText(dateTimeFormat.format(currentArticle
				.getRemoteModificationTime()));
		createdByTextView.setText(currentArticle.getCreatedByName());
		modifiedByTextView.setText(currentArticle.getModifiedByName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("articleId", articleId);
		outState.putSerializable("currentArticle", currentArticle);
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

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Starts the background file transfer service, if:
	 * <ol>
	 * <li>Connected to network</li>
	 * <li>SD card is mounted</li>
	 * <li>Current job has any downloads that the user requested or has any
	 * pending uploads</li>
	 * <li>Service is not already running</li>
	 * </ol>
	 * 
	 * 
	 * @return true if all the pre-conditions hold, and the service is started
	 *         or it has already been running; false, otherwise.
	 */
	public boolean startBftsIfRequired() {
		boolean bftsRequired = Utils.isConnected(getApplicationContext())
				&& Utils.isSDCardValid(getApplicationContext(), false)
				&& articlesDao.hasPendingDownloads(getApplicationContext(),
						articleId);

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

		public ProgressUpdateThread() {
		}

		@Override
		public void run() {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Progress update thread started ");
			}

			while (!isInterrupted()) {
				// Restart proofs loader
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Refreshed progress percentage ");
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						currentArticle = articlesDao
								.getArticleWithId(articleId);
						updateMediaButtonText();
					}
				});

				if (!articlesDao.hasPendingDownloads(getApplicationContext(),
						articleId)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "No pending transfers for article "
								+ articleId
								+ ". Returning from progress update thread.");
					}

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

					return;
				}
			}
		}
	}

	private void updateMediaButtonText() {

		if (currentArticle == null) {
			return;
		}

		if (!TextUtils.isEmpty(currentArticle.getLocalMediaPath())) {
			// TODO
			if (Utils.isExists(currentArticle.getLocalMediaPath())) {
				mediaButton.setText("Open media");
			} else {
				mediaButton.setText("Download media");
				currentArticle.setDownloadRequested(false);
				currentArticle.setTransferPercentage(null);
				currentArticle.setLocalMediaPath(null);
			}
			// mediaButton.setText("Open media");
		} else {
			if (currentArticle.getDownloadRequested() == null
					|| currentArticle.getDownloadRequested() == false) {
				mediaButton.setText("Download media");
			} else {
				if (currentArticle.getTransferPercentage() == null
						|| currentArticle.getTransferPercentage() <= 0) {
					mediaButton.setText("Waiting in queue. Cancel");
				} else {
					mediaButton.setText(currentArticle.getTransferPercentage()
							+ "% downloaded. Cancel");
				}
			}
		}
	}

	public void onMediaButtonClick(View view) {
		Log.i(TAG, "onMediaButtonClick clicked");
		if (currentArticle == null) {
			return;
		}

		if (!TextUtils.isEmpty(currentArticle.getLocalMediaPath())) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			File file = new File(currentArticle.getLocalMediaPath());
			// mime type must be specified, otherwise
			// some devices such as HTC Desire Z try to open every file as a
			// PDF file.
			intent.setDataAndType(Uri.fromFile(file),
					currentArticle.getMimeType());

			PackageManager pm = getPackageManager();
			ComponentName cn = intent.resolveActivity(pm);

			if (cn == null) {
				Toast.makeText(this,
						"No viewer found for " + currentArticle.getMimeType(),
						Toast.LENGTH_LONG).show();
			} else {
				startActivity(intent);
			}
		} else {
			if (currentArticle.getDownloadRequested() == null
					|| currentArticle.getDownloadRequested() == false) {
				currentArticle.setDownloadRequested(true);
				articlesDao.save(currentArticle);
				startBftsIfRequired();
			} else {
				currentArticle.setDownloadRequested(false);
				currentArticle.setTransferPercentage(null);
				articlesDao.save(currentArticle);
			}
		}
	}

	@Override
	public void onBackPressed() {
		// articlesDao.cleanupSecuredArticles();
		Utils.cleanupSecureFiles(getApplicationContext());
		super.onBackPressed();
	}

}