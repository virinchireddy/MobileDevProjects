package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Articles;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ArticlesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnQueryTextListener,
		RefreshListener {

	public static final String TAG = "ArticlesActivity";
	public static final int PAGE_SIZE = 10;

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat dateTimeFormat;

	// required for fetching the employee id
	private SettingsDao settingsDao;

	private ArticlesDao articlesDao;
	private Button loadMoreButton;
	private Long oldestArticleFetched;
	private String query;
	private boolean searchInProgress;
	private SearchView searchView;
	private Menu menu;
	private DrawerFragment drawerFragment;
	private Long parentId;
	public static String PARENT_ID = "parent_id";
	private Long rootDirectoryId;
	private boolean isCleanUpTaskRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_articles);
		setSupportProgressBarIndeterminateVisibility(true);
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		articlesDao = ArticlesDao.getInstance(getApplicationContext());
		loadMoreButton = (Button) findViewById(R.id.loadMoreButton);
		oldestArticleFetched = articlesDao.getOldestFetchedArticledId();
		rootDirectoryId = articlesDao.getRootDirectoryId();
		updateLoadMoreButtonText();

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(settingsDao.getLabel(
				Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
				Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE));
		updateSubtitle();

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No "
				+ settingsDao.getLabel(Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
						Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE)
				+ ".");

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		if (savedInstanceState == null) {
			parentId = getIntent().getLongExtra(PARENT_ID, 0);
			isCleanUpTaskRunning = getIntent().getBooleanExtra("clean", false);

		} else {
			parentId = savedInstanceState.getLong(PARENT_ID);
			isCleanUpTaskRunning = savedInstanceState.getBoolean("clean");
		}

		if (parentId == 0) {
			parentId = articlesDao.getRootDirectoryId();
		}
		if (!isCleanUpTaskRunning) {
			new CleanUpTask().execute();
		}
		String[] columns = { Articles.TITLE, Articles.REMOTE_MODIFICATION_TIME,
				Articles.MIME_TYPE };
		int[] views = { R.id.titleTextView, R.id.timeTextView,
				R.id.typeImageView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_article, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		getSupportLoaderManager().initLoader(0, null, this);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_kb, settingsDao
				.getLabel(Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
						Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE),
				null, this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		// getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		getMenuInflater().inflate(R.menu.articles, menu);
		this.menu = menu;
		long rootId = 0;
		if (rootDirectoryId != null) {
			rootId = rootDirectoryId;
		}
		if (parentId == 0 || parentId == rootId) {
			menu.findItem(R.id.folderBack).setVisible(false);
		} else {
			menu.findItem(R.id.folderBack).setVisible(true);
		}

		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint("Search for "
				+ settingsDao.getLabel(Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
						Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE));
		searchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(searchItem,
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionCollapse(MenuItem menuItem) {
						Log.i(TAG, "onMenuItemActionCollapse");
						return true;
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem menuItem) {
						Log.i(TAG, "onMenuItemActionExpand");
						query = "";
						getSupportLoaderManager().restartLoader(0, null,
								ArticlesActivity.this);
						updateSubtitle();
						return true;

					}

				});

		if (searchInProgress) {
			searchItem.setVisible(false);
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			searchItem.setVisible(!drawerFragment.isDrawerOpen());
			setSupportProgressBarIndeterminateVisibility(false);
		}

		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}
		String selection;
		updateSubtitle();
		//if (parentId == 0) {
			// selection = Articles.PARENT_ID + " IS NULL ";
		//	return null;
		//} else {
			selection = Articles.PARENT_ID + " = " + parentId + " AND "
					+ Articles.DELETED + " = 'false'";
		//}

		if (TextUtils.isEmpty(query)) {
			return new CursorLoader(getApplicationContext(),
					Articles.CONTENT_URI, Articles.ALL_COLUMNS, selection,
					null, Articles.TITLE);
		} else {
			return new CursorLoader(getApplicationContext(),
					Articles.CONTENT_URI, Articles.ALL_COLUMNS, "("
							+ Articles.TITLE + " LIKE ? OR "
							+ Articles.DESCRIPTION + " LIKE ?)" + " AND "
							+ Articles.DELETED + " = 'false'", new String[] {
							"%" + query + "%", "%" + query + "%" },
					Articles.TITLE);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		// DatabaseUtils.dumpCursor(cursor);
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}
		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		adapter.swapCursor(null);
	}

	class MyViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "ArticlesActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);
			Long fileType = cursor.getLong(Articles.FILE_TYPE_INDEX);
			switch (columnIndex) {
			case Articles.REMOTE_MODIFICATION_TIME_INDEX:
				TextView timeTV = (TextView) view;
				if (fileType != null && fileType == Article.TYPE_FOLDER) {
					timeTV.setVisibility(View.GONE);
				} else {
					timeTV.setVisibility(View.VISIBLE);
					timeTV.setText(dateTimeFormat.format(SQLiteDateTimeUtils.getLocalTime(cursor
							.getString(Articles.REMOTE_MODIFICATION_TIME_INDEX))));
				}
				return true;

			case Articles.MIME_TYPE_INDEX:
				ImageView typeImageView = (ImageView) view;

				if (fileType != null && fileType == Article.TYPE_FOLDER) {
					typeImageView.setImageResource(R.drawable.ic_action_folder);
					return true;
				}

				String mimeType = cursor.getString(Articles.MIME_TYPE_INDEX);

				if (TextUtils.isEmpty(mimeType)) {
					typeImageView
							.setImageResource(R.drawable.ic_note_type_text);
				} else {
					if (mimeType.startsWith("image")) {
						typeImageView
								.setImageResource(R.drawable.ic_note_type_image);
					} else if (mimeType.startsWith("video")) {
						typeImageView
								.setImageResource(R.drawable.ic_note_type_video);
					} else if (mimeType.startsWith("audio")) {
						typeImageView
								.setImageResource(R.drawable.ic_note_type_audio);
					} else {
						typeImageView
								.setImageResource(R.drawable.ic_note_type_file);
					}
				}

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
		ArticlesDao articlesDao = ArticlesDao
				.getInstance(getApplicationContext());
		Article article = articlesDao.getArticleWithId(id);
		if (article != null) {
			if (article.getFileType() != null
					&& article.getFileType() == Article.TYPE_FOLDER) {
				/*
				 * Intent intent = new Intent(this, ArticlesActivity.class);
				 * intent.putExtra(PARENT_ID, id); startActivity(intent);
				 * finish();
				 */
				// TODO
				this.query = "";
				parentId = id;
				if (menu != null) {
					menu.clear();
				}

				onCreateOptionsMenu(menu);
				getSupportLoaderManager().restartLoader(0, null, this);

			} else {
				Intent intent = new Intent(this, ArticleActivity.class);
				intent.putExtra(EffortProvider.Articles._ID, id);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(PARENT_ID, parentId);
		outState.putBoolean("clean", isCleanUpTaskRunning);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.folderBack:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateLoadMoreButtonText() {
		loadMoreButton.setText("Fetch more "
				+ settingsDao.getLabel(Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
						Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE)
				+ " from cloud");
	}

	public void onLoadMoreButtonClick(View view) {
		setSupportProgressBarIndeterminateVisibility(true);

		// if the article no longer exists in our local db, start from today
		String before = XsdDateTimeUtils.getXsdDateTimeFromLocalTime(Utils
				.getBeginningOfToday());

		String oldestFormCreationTime = null;

		if (oldestArticleFetched != null) {
			oldestFormCreationTime = articlesDao
					.getRemoteCreationTime(oldestArticleFetched);

			if (oldestFormCreationTime != null) {
				before = XsdDateTimeUtils
						.getXsdDateTimeFromSQLiteDateTime(oldestFormCreationTime);
			}
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Fetching articles before " + before);
		}

		loadMoreButton.setEnabled(false);
		new FetchPreviousArticlesTask().execute(before);
	}

	private class FetchPreviousArticlesTask extends
			AsyncTask<String, Integer, Long> {
		private List<Article> addedArticles = new ArrayList<Article>();

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			AndroidHttpClient httpClient = null;

			try {
				String url = getUrl(params[0]);

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch previous articles URL: " + url);
				}

				httpClient = AndroidHttpClient.newInstance("EFFORT");
				HttpGet httpGet = new HttpGet(url);
				Utils.setTimeouts(httpGet);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();

				if (!(obj instanceof JSONArray)) {
					Log.e(TAG,
							"Invalid previous articles response. Expected a JSON array but did not get it.");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"Received unexpected response from the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
					return null;
				}

				JSONArray jsonArray = (JSONArray) obj;
				Utils.addArticles(jsonArray, addedArticles,
						getApplicationContext());

				if (addedArticles.size() <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"No older "
											+ settingsDao
													.getLabel(
															Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
															Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE)
											+ " found.", Toast.LENGTH_LONG)
									.show();
						}
					});
				}

				for (Article article : addedArticles) {
					article.setGotViaSearch(false);
					articlesDao.save(article);
				}

				if (addedArticles.size() > 0) {
					return addedArticles.get(addedArticles.size() - 1).getId();
				}
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Fetch failed due to network error.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} finally {
				if (httpClient != null) {
					httpClient.close();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			setSupportProgressBarIndeterminateVisibility(false);
			if (result != null) {
				getSupportLoaderManager().restartLoader(0, null,
						ArticlesActivity.this);
				oldestArticleFetched = result;
			}

			setSupportProgressBarIndeterminateVisibility(false);
			loadMoreButton.setEnabled(true);
		}

		private String getUrl(String before) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/article/before/"
									+ settingsDao.getString("employeeId"));
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			builder.appendQueryParameter("before", before);
			builder.appendQueryParameter("pageSize", "" + PAGE_SIZE);
			Uri syncUri = builder.build();
			return syncUri.toString();
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query submitted: " + query);
		}

		this.query = query;
		updateSubtitle();
		getSupportLoaderManager().restartLoader(0, null, this);

		// TODO
		// if (searchView != null) {
		// searchInProgress = true;
		// // searchView.setQuery("", false);
		// setSupportProgressBarIndeterminateVisibility(true);
		// MenuItemCompat
		// .collapseActionView(menu.findItem(R.id.action_search));
		// new SearchArticlesTask().execute(query);
		// supportInvalidateOptionsMenu();
		// }

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query text changed: " + newText);
		}

		// if (TextUtils.isEmpty(newText)) {
		// this.query = null;
		// updateSubtitle();
		// getSupportLoaderManager().restartLoader(0, null, this);
		// }

		return true;
	}

	// private class SearchArticlesTask extends AsyncTask<String, Integer, Long>
	// {
	// private List<Article> addedArticles = new ArrayList<Article>();
	//
	// @Override
	// protected Long doInBackground(String... params) {
	// if (BuildConfig.DEBUG) {
	// Log.i(TAG, "In doInBackground.");
	// }
	//
	// AndroidHttpClient httpClient = null;
	//
	// try {
	// String url = getUrl(params[0]);
	//
	// if (BuildConfig.DEBUG) {
	// Log.i(TAG, "Search articles URL: " + url);
	// }
	//
	// httpClient = AndroidHttpClient.newInstance("EFFORT");
	// HttpGet httpGet = new HttpGet(url);
	// HttpResponse httpResponse = httpClient.execute(httpGet);
	// String response = EntityUtils
	// .toString(httpResponse.getEntity());
	//
	// if (BuildConfig.DEBUG) {
	// Log.d(TAG, "Response JSON: " + response);
	// }
	//
	// JSONTokener tokener = new JSONTokener(response);
	// Object obj = tokener.nextValue();
	//
	// if (!(obj instanceof JSONArray)) {
	// Log.e(TAG,
	// "Invalid previous articles response. Expected a JSON array but did not get it.");
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Toast.makeText(
	// getApplicationContext(),
	// "Received unexpected response from the cloud.",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// return null;
	// }
	//
	// JSONArray jsonArray = (JSONArray) obj;
	// Utils.addArticles(jsonArray, addedArticles,
	// getApplicationContext());
	//
	// if (addedArticles.size() <= 0) {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Toast.makeText(
	// getApplicationContext(),
	// "No "
	// + settingsDao
	// .getLabel(
	// Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
	// Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE)
	// + ".", Toast.LENGTH_LONG).show();
	// }
	// });
	// }
	//
	// for (Article article : addedArticles) {
	// article.setGotViaSearch(true);
	// articlesDao.save(article);
	// }
	//
	// if (addedArticles.size() > 0) {
	// return addedArticles.get(addedArticles.size() - 1).getId();
	// }
	// } catch (MalformedURLException e) {
	// Log.e(TAG, "Bad URL: " + e.toString(), e);
	// } catch (IOException e) {
	// Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
	//
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Toast.makeText(getApplicationContext(),
	// "Fetch failed due to network error.",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// } catch (JSONException e) {
	// Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
	// } catch (ParseException e) {
	// Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
	// } finally {
	// if (httpClient != null) {
	// httpClient.close();
	// }
	// }
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Long result) {
	// super.onPostExecute(result);
	// searchInProgress = false;
	//
	// if (BuildConfig.DEBUG) {
	// Log.d(TAG, "Search finished.");
	// }
	//
	// if (result != null) {
	// getSupportLoaderManager().restartLoader(0, null,
	// ArticlesActivity.this);
	// oldestArticleFetched = result;
	// }
	//
	// setSupportProgressBarIndeterminateVisibility(false);
	// loadMoreButton.setEnabled(true);
	// supportInvalidateOptionsMenu();
	// }
	//
	// private String getUrl(String before) {
	// String serverBaseUrl = getString(R.string.server_base_url);
	// Builder builder = Uri
	// .parse(serverBaseUrl)
	// .buildUpon()
	// .appendEncodedPath(
	// "service/article/search/"
	// + settingsDao.getString("employeeId"));
	// Utils.appendCommonQueryParameters(getApplicationContext(), builder);
	// builder.appendQueryParameter("query", query);
	// Uri syncUri = builder.build();
	// return syncUri.toString();
	// }
	// }

	private void updateSubtitle() {
		if (TextUtils.isEmpty(query)) {
			getSupportActionBar()
					.setSubtitle(
							"All "
									+ settingsDao
											.getLabel(
													Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
													Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE));
		} else {
			getSupportActionBar().setSubtitle("Matching " + query);
		}
	}

	@Override
	public void onBackPressed() {
		long rootId = 0;
		if (rootDirectoryId != null) {
			rootId = rootDirectoryId;
		}
		if (parentId == 0 || rootId == parentId) {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			super.onBackPressed();
		} else {
			Long parentWithArticleId = articlesDao
					.getParentWithArticleId(parentId);
			// Added for search empty
			query = "";
			if (menu != null) {
				menu.clear();
			}
			parentId = parentWithArticleId;
			onCreateOptionsMenu(menu);
			if (!isCleanUpTaskRunning) {
				new CleanUpTask().execute();
			}

			getSupportLoaderManager().restartLoader(0, null, this);
		}
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

	private class CleanUpTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {

			long rowsAffected = 0;
			isCleanUpTaskRunning = true;
			// articlesDao.cleanupSecuredArticles();
			Utils.cleanupSecureFiles(getApplicationContext());
			return rowsAffected;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}
			isCleanUpTaskRunning = false;
			getSupportLoaderManager()
					.initLoader(0, null, ArticlesActivity.this);

		}
	}

}
