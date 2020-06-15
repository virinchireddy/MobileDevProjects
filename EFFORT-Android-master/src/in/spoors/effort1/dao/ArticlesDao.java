package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.provider.EffortProvider.Articles;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class ArticlesDao {

	public static final String TAG = "ArticlesDao";
	private static ArticlesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static ArticlesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new ArticlesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private ArticlesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean articleWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Articles._ID
					+ ") AS count FROM " + Articles.TABLE + " WHERE "
					+ Articles._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(Article article) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Date now = new Date();

		if (article.getId() != null && articleWithIdExists(article.getId())) {

			Article oldArticle = getArticleWithId(article.getId());
			boolean isModified = isArticleModified(article, oldArticle);

			if (isModified) {
				article.setLocalModificationTime(new Date());
			} else {
				article.setLocalModificationTime(oldArticle
						.getLocalModificationTime());
			}

			if (article.getLocalCreationTime() == null) {
				article.setLocalCreationTime(oldArticle.getLocalCreationTime());
			}

			ContentValues values = article.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing article: " + article.toString());
			}

			db.update(Articles.TABLE, values,
					Articles._ID + " = " + article.getId(), null);

			return;
		}
		article.setLocalCreationTime(now);
		article.setLocalModificationTime(now);

		ContentValues values = article.getContentValues(null);
		db.insert(Articles.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new article: " + article.toString());
		}
	}

	private boolean isArticleModified(Article article1, Article article2) {

		if (article1 == null && article2 == null) {
			return false;
		}

		if (article1 == null || article1 == null) {
			return true;
		}

		// both the workflowstatus are not null
		if (!Utils.datesEqual(article1.getRemoteModificationTime(),
				article2.getRemoteModificationTime())
				|| !TextUtils.equals(article1.getTitle(), article2.getTitle())) {
			return true;
		}

		return false;
	}

	/**
	 * Updates only transfer percentage.
	 * 
	 * @param article
	 */
	public synchronized void updateTransferStatus(Article article) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Updating the existing article (for updating transfer percentage): "
							+ article.toString());
		}

		ContentValues values = new ContentValues();
		Utils.putNullOrValue(values, Articles.TRANSFER_PERCENTAGE,
				article.getTransferPercentage());

		db.update(Articles.TABLE, values,
				Articles._ID + " = " + article.getId(), null);
	}

	/**
	 * Delete note with the given local note ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteArticle(long articleId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Articles.TABLE, Articles._ID + " = "
				+ articleId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " articles with article id "
					+ articleId);
		}
	}

	public synchronized void deleteUnmappedArticles(String articleIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Articles.TABLE, Articles._ID + " NOT IN ("
				+ articleIds + ") AND " + Articles.PARENT_ID + " IS NOT NULL",
				null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " unmapped articles  "
					+ articleIds);
		}
	}

	public synchronized void cleanupSecuredArticles() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		List<Article> securedArticles = getSecuredArticles();

		if (securedArticles != null) {
			for (Article article : securedArticles) {
				if (!TextUtils.isEmpty(article.getLocalMediaPath())
						&& article.getLocalMediaPath().startsWith(
								Utils.getEffortPath())) {
					File file = new File(article.getLocalMediaPath());
					file.delete();

					article.setLocalMediaPath(null);

					ContentValues values = article.getContentValues(null);
					db.update(Articles.TABLE, values, Articles._ID + " = "
							+ article.getId(), null);
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Updating the existing article: "
										+ article.toString());
					}
				}
			}
		}

	}

	/**
	 * Returns the article with given article ID.
	 * 
	 * Null if the given article is not found.
	 * 
	 * @param id
	 * @return
	 */
	public Article getArticleWithId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Articles.TABLE, Articles.ALL_COLUMNS,
					Articles._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				Article article = new Article();
				article.load(cursor);
				return article;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	/**
	 * Checks whether there are any pending downloads.
	 * 
	 * @param applicationContext
	 *            Used for getting DBHelper instance.
	 * @param articleId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * @return
	 */
	public boolean hasPendingDownloads(Context applicationContext,
			Long articleId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Articles.TABLE, new String[] { Articles._ID },
					Articles.LOCAL_MEDIA_PATH
							+ " IS NULL AND "
							+ Articles.MEDIA_ID
							+ " IS NOT NULL AND "
							+ Articles.DOWNLOAD_REQUESTED
							+ " = 'true'"
							+ (articleId == null ? "" : " AND " + Articles._ID
									+ " = " + articleId), null, null, null,
					null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<Article> getPendingDownloads() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Article> articles = null;

		try {
			cursor = db.query(Articles.TABLE, Articles.ALL_COLUMNS,
					Articles.LOCAL_MEDIA_PATH + " IS NULL AND "
							+ Articles.MEDIA_ID + " IS NOT NULL AND "
							+ Articles.DOWNLOAD_REQUESTED + " = 'true'", null,
					null, null, null);

			if (cursor.getCount() > 0) {
				articles = new ArrayList<Article>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Article article = new Article();
				article.load(cursor);
				articles.add(article);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return articles;
	}

	public Article getNextPendingDownload() {
		List<Article> pendingDownloads = getPendingDownloads();

		if (pendingDownloads == null || pendingDownloads.size() <= 0) {
			return null;
		}

		if (pendingDownloads.size() > 0) {
			return pendingDownloads.get(0);
		}

		return null;
	}

	public synchronized void cancelAllDownloads() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Articles.DOWNLOAD_REQUESTED, "false");

		db.update(Articles.TABLE, values, null, null);
	}

	/**
	 * 
	 * @param id
	 * @return remote creation time of the article in SQLite Date Time format.
	 * 
	 */
	public String getRemoteCreationTime(Long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Articles.REMOTE_CREATION_TIME
					+ " FROM " + Articles.TABLE + " WHERE " + Articles._ID
					+ " = " + id, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return article id of the oldest fetched article
	 * 
	 */
	public Long getOldestFetchedArticledId() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Articles._ID + " FROM "
					+ Articles.TABLE + " WHERE " + Articles.GOT_VIA_SEARCH
					+ " = 'false' ORDER BY " + Articles.REMOTE_CREATION_TIME
					+ " LIMIT 1", null);

			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return article id of the parent article
	 * 
	 */
	public Long getParentWithArticleId(Long articleId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Articles.PARENT_ID + " FROM "
					+ Articles.TABLE + " WHERE " + Articles._ID + " = "
					+ articleId, null);

			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			} else {
				return 0l;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return article id of the parent article
	 * 
	 */
	public Long getRootDirectoryId() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Articles._ID + " FROM "
					+ Articles.TABLE + " WHERE " + Articles.PARENT_ID
					+ " IS NULL AND " + Articles.DELETED + " = 'false'", null);

			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			} else {
				return 0l;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<Article> getSecuredArticles() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Article> articles = null;

		try {
			cursor = db.query(Articles.TABLE, Articles.ALL_COLUMNS,
					Articles.LOCAL_MEDIA_PATH + " IS NOT NULL AND "
							+ Articles.IS_SECURED + " ='true'", null, null,
					null, null);

			if (cursor.getCount() > 0) {
				articles = new ArrayList<Article>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Article article = new Article();
				article.load(cursor);
				articles.add(article);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return articles;
	}

	public boolean pathExistsAtInSecureArticle(String absolutePath) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Articles._ID
					+ ") AS count FROM " + Articles.TABLE + " WHERE "
					+ Articles.LOCAL_MEDIA_PATH + " = '" + absolutePath
					+ "' AND " + Articles.IS_SECURED + " = 'false'", null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getAddedArticlesCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Articles._ID + ") FROM "
					+ Articles.TABLE + " WHERE " + Articles.LOCAL_CREATION_TIME
					+ " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"addedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr });

			cursor.moveToNext();

			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getModifiedArticlesCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Articles._ID + ") FROM "
					+ Articles.TABLE + " WHERE " + Articles.LOCAL_CREATION_TIME
					+ " <= ? AND " + Articles.LOCAL_MODIFICATION_TIME + " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"modifiedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr, afterStr });

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateDeletedFlag(List<Long> articleIds,
			boolean deleted, boolean isInCondition) {
		if (articleIds == null || articleIds.size() == 0) {
			return;
		}
		String condition = "";
		if (isInCondition) {
			condition = " IN ";
		} else {
			condition = " NOT IN ";
		}

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		String query = "UPDATE " + Articles.TABLE + " SET " + Articles.DELETED
				+ " = '" + deleted + "' WHERE " + Articles._ID + condition
				+ " (" + TextUtils.join(",", articleIds) + ")";

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Article update statement: " + query);
		}

		db.execSQL(query);
	}

	/**
	 * Get all the remote article IDs.
	 * 
	 * @return
	 */
	public List<Long> getAllArticleIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;

		try {
			cursor = db.query(Articles.TABLE, new String[] { Articles._ID },
					Articles._ID + " IS NOT NULL", null, null, null,
					Articles._ID);

			if (cursor.getCount() > 0) {
				ids = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return ids;
	}

}