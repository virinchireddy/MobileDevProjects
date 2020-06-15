package in.spoors.effort1;

import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.SectionFilesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.dto.FormFile;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.dto.SectionFile;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.conn.AbstractClientConnAdapter;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BackgroundFileTransferService extends WakefulIntentService {

	public static final String TAG = "BackgroundFileTransferService";
	private NotesDao notesDao;
	private SettingsDao settingsDao;
	private FormFilesDao formFilesDao;
	private SectionFilesDao sectionFilesDao;
	private FormsDao formsDao;
	private ArticlesDao articlesDao;

	public BackgroundFileTransferService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In doWakefulWork.");
		}

		formsDao = FormsDao.getInstance(getApplicationContext());
		formFilesDao = FormFilesDao.getInstance(getApplicationContext());
		sectionFilesDao = SectionFilesDao.getInstance(getApplicationContext());
		notesDao = NotesDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		articlesDao = ArticlesDao.getInstance(getApplicationContext());

		while (true) {
			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Not connected to network. Not doing anything.");
				}

				return;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "SD card not mounted. Not doing anything.");
				}

				return;
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Checking if there are any files that need to be transferred.");
			}

			String logoUrl = settingsDao.getString(Settings.KEY_APP_LOGO_URL);
			if (!TextUtils.isEmpty(logoUrl)) {
				boolean succeeded = downloadFile(logoUrl
						+ "?width=144&height=144", Utils.getLocalLogoPath());

				if (succeeded) {
					settingsDao.saveSetting(Settings.KEY_APP_LOGO_URL, "");
				}
			}

			boolean noteDownloadsDone = handleNoteDownloads();
			boolean noteUploadsDone = handleNoteUploads();
			boolean formDownloadsDone = handleFormDownloads();
			boolean formUploadsDone = handleFormUploads();
			boolean sectionDownloadsDone = handleSectionDownloads();
			boolean sectionUploadsDone = handleSectionUploads();
			boolean articleDownloadsDone = handleArticleDownloads();

			if (noteDownloadsDone && noteUploadsDone && formDownloadsDone
					&& formUploadsDone && sectionDownloadsDone
					&& sectionUploadsDone && articleDownloadsDone) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"All uploads & downloads finished successfully. Returning from onHandleIntent.");
				}
				Utils.sync(getApplicationContext());
				break;
			}
		}
	}

	private boolean handleFormDownloads() {
		FormFile fileDto = null;

		boolean allSucceeded = true;

		while ((fileDto = formFilesDao.getNextPendingDownload()) != null) {
			boolean succeeded = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Downloading media " + ": noteId="
								+ fileDto.getLocalId() + ", mediaId="
								+ fileDto.getMediaId());
			}

			succeeded = downloadFormFile(fileDto);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleDownloads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleDownloads method.");
				}

				return false;
			}

		}

		return allSucceeded;
	}

	private boolean downloadFormFile(FormFile fileDto) {
		boolean downloadCancelled = false;
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"media/get/" + fileDto.getMediaId() + "/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media download URL: " + uri.toString());
		}

		HttpGet httpGet = new HttpGet(uri.toString());
		Utils.setTimeouts(httpGet);
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);

		String fileName = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response status: "
						+ response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			String contentType = entity.getContentType().getValue();
			in = entity.getContent();
			fileName = Utils.getNewPathForMediaType(fileDto.getMediaId(),
					contentType);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response entity: isChunked=" + entity.isChunked()
						+ ", isRepeatable=" + entity.isRepeatable()
						+ ", isStreaming=" + entity.isStreaming()
						+ ", contentLength=" + contentLength);
				Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Storing entity as " + fileName);
			}

			File dir = new File(new File(fileName).getParent());

			if (!dir.exists()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Creating directory: " + dir);
				}

				dir.mkdirs();
			}

			out = new FileOutputStream(fileName);
			final int BUFFER_LENGTH = 64 * 1024; // use a 64k buffer

			byte[] buffer = new byte[BUFFER_LENGTH];
			int totalBytesRead = 0;
			int bytesRead = 0;
			long startTime = System.currentTimeMillis();

			while ((bytesRead = in.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				out.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;

				if (System.currentTimeMillis() - startTime > 1000) {
					fileDto = formFilesDao.getFileWithLocalId(fileDto
							.getLocalId());

					// check if user cancelled the download
					if (fileDto.getDownloadRequested() == null
							|| !fileDto.getDownloadRequested()) {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Skipping download of "
											+ fileDto.getLocalId()
											+ " on user's cancellation request.");
						}

						downloadCancelled = true;
						break;
					}

					int transferPercentage = (int) (totalBytesRead * 100 / (double) contentLength);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "totalBytesRead=" + totalBytesRead
								+ ", contentLength=" + contentLength
								+ ", transferPercentage=" + transferPercentage);
					}

					fileDto.setTransferPercentage(transferPercentage);
					formFilesDao.updateTransferStatus(fileDto);
				}
			}

			if (downloadCancelled) {
				fileDto.setLocalMediaPath(null);
			} else {
				fileDto.setLocalMediaPath(fileName);
				fileDto.setFileSize(contentLength);
				fileDto.setMimeType(contentType);
				fileDto.setDownloadRequested(null);
				fileDto.setTransferPercentage(null);
				formFilesDao.save(fileDto);
			}

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error fetching " + uri.toString() + ": " + e.toString(), e);

			fileDto.setTransferPercentage(null);
			fileDto.setLocalMediaPath(null);
			formFilesDao.save(fileDto);

			if (fileName != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Deleting the partially downloaded file: "
							+ fileName);
				}

				File file = new File(fileName);
				file.delete();
			}

			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing input stream.");
				}
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing output stream.");
				}
			}

			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	private boolean handleSectionDownloads() {
		SectionFile fileDto = null;

		boolean allSucceeded = true;

		while ((fileDto = sectionFilesDao.getNextPendingDownload()) != null) {
			boolean succeeded = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Downloading section file " + ": fileId="
								+ fileDto.getLocalId() + ", mediaId="
								+ fileDto.getMediaId());
			}

			succeeded = downloadSectionFile(fileDto);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleDownloads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleDownloads method.");
				}

				return false;
			}

		}

		return allSucceeded;
	}

	private boolean downloadSectionFile(SectionFile fileDto) {
		boolean downloadCancelled = false;
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"media/get/" + fileDto.getMediaId() + "/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media download URL: " + uri.toString());
		}

		HttpGet httpGet = new HttpGet(uri.toString());
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);

		String fileName = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response status: "
						+ response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			String contentType = entity.getContentType().getValue();
			in = entity.getContent();
			fileName = Utils.getNewPathForMediaType(fileDto.getMediaId(),
					contentType);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response entity: isChunked=" + entity.isChunked()
						+ ", isRepeatable=" + entity.isRepeatable()
						+ ", isStreaming=" + entity.isStreaming()
						+ ", contentLength=" + contentLength);
				Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Storing entity as " + fileName);
			}

			File dir = new File(new File(fileName).getParent());

			if (!dir.exists()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Creating directory: " + dir);
				}

				dir.mkdirs();
			}

			out = new FileOutputStream(fileName);
			final int BUFFER_LENGTH = 64 * 1024; // use a 64k buffer

			byte[] buffer = new byte[BUFFER_LENGTH];
			int totalBytesRead = 0;
			int bytesRead = 0;
			long startTime = System.currentTimeMillis();

			while ((bytesRead = in.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				out.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;

				if (System.currentTimeMillis() - startTime > 1000) {
					fileDto = sectionFilesDao.getFileWithLocalId(fileDto
							.getLocalId());

					// check if user cancelled the download
					if (fileDto.getDownloadRequested() == null
							|| !fileDto.getDownloadRequested()) {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Skipping download of "
											+ fileDto.getLocalId()
											+ " on user's cancellation request.");
						}

						downloadCancelled = true;
						break;
					}

					int transferPercentage = (int) (totalBytesRead * 100 / (double) contentLength);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "totalBytesRead=" + totalBytesRead
								+ ", contentLength=" + contentLength
								+ ", transferPercentage=" + transferPercentage);
					}

					fileDto.setTransferPercentage(transferPercentage);
					sectionFilesDao.updateTransferStatus(fileDto);
				}
			}

			if (downloadCancelled) {
				fileDto.setLocalMediaPath(null);
			} else {
				fileDto.setLocalMediaPath(fileName);
				fileDto.setFileSize(contentLength);
				fileDto.setMimeType(contentType);
				fileDto.setDownloadRequested(null);
				fileDto.setTransferPercentage(null);
				sectionFilesDao.save(fileDto);
			}

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error fetching " + uri.toString() + ": " + e.toString(), e);

			fileDto.setTransferPercentage(null);
			fileDto.setLocalMediaPath(null);
			sectionFilesDao.save(fileDto);

			if (fileName != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Deleting the partially downloaded file: "
							+ fileName);
				}

				File file = new File(fileName);
				file.delete();
			}

			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing input stream.");
				}
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing output stream.");
				}
			}

			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * 
	 * @return true if all downloads succeeded, false otherwise.
	 */
	private boolean handleNoteDownloads() {
		Note note = null;

		boolean allSucceeded = true;

		while ((note = notesDao.getNextPendingDownload()) != null) {
			boolean succeeded = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Downloading media " + ": noteId=" + note.getLocalId()
								+ ", mediaId=" + note.getMediaId());
			}

			succeeded = downloadNoteFile(note);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleDownloads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleDownloads method.");
				}

				return false;
			}

		}

		return allSucceeded;
	}

	/**
	 * 
	 * @param note
	 * @return true if the download succeeded, false if download failed.
	 */
	private boolean downloadNoteFile(Note note) {
		boolean downloadCancelled = false;
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"media/get/" + note.getMediaId() + "/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media download URL: " + uri.toString());
		}

		HttpGet httpGet = new HttpGet(uri.toString());
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);

		String fileName = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response status: "
						+ response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			String contentType = entity.getContentType().getValue();
			in = entity.getContent();
			fileName = Utils.getNewPathForMediaType(note.getMediaId(),
					contentType);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response entity: isChunked=" + entity.isChunked()
						+ ", isRepeatable=" + entity.isRepeatable()
						+ ", isStreaming=" + entity.isStreaming()
						+ ", contentLength=" + contentLength);
				Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Storing entity as " + fileName);
			}

			File dir = new File(new File(fileName).getParent());

			if (!dir.exists()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Creating directory: " + dir);
				}

				dir.mkdirs();
			}

			out = new FileOutputStream(fileName);
			final int BUFFER_LENGTH = 64 * 1024; // use a 64k buffer

			byte[] buffer = new byte[BUFFER_LENGTH];
			int totalBytesRead = 0;
			int bytesRead = 0;
			long startTime = System.currentTimeMillis();

			while ((bytesRead = in.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				out.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;

				if (System.currentTimeMillis() - startTime > 1000) {
					note = notesDao.getNoteWithLocalId(note.getLocalId());

					// check if user cancelled the download
					if (note.getDownloadRequested() == null
							|| !note.getDownloadRequested()) {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Skipping download of "
											+ note.getLocalId()
											+ " on user's cancellation request.");
						}

						downloadCancelled = true;
						break;
					}

					int transferPercentage = (int) (totalBytesRead * 100 / (double) contentLength);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "totalBytesRead=" + totalBytesRead
								+ ", contentLength=" + contentLength
								+ ", transferPercentage=" + transferPercentage);
					}

					note.setTransferPercentage(transferPercentage);
					notesDao.updateTransferStatus(note);
				}
			}

			if (downloadCancelled) {
				note.setLocalMediaPath(null);
			} else {
				note.setLocalMediaPath(fileName);
				note.setFileSize(contentLength);
				note.setMimeType(contentType);
				note.setDownloadRequested(null);
				note.setTransferPercentage(null);
				notesDao.save(note);
			}

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error fetching " + uri.toString() + ": " + e.toString(), e);

			note.setTransferPercentage(null);
			note.setLocalMediaPath(null);
			notesDao.save(note);

			if (fileName != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Deleting the partially downloaded file: "
							+ fileName);
				}

				File file = new File(fileName);
				file.delete();
			}

			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing input stream.");
				}
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing output stream.");
				}
			}

			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * 
	 * @return true if the download succeeded, false if download failed.
	 */
	private boolean downloadFile(String url, String localFilePath) {
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "File download URL: " + url);
		}

		HttpGet httpGet = new HttpGet(url);
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);

		InputStream in = null;
		OutputStream out = null;

		try {
			HttpResponse response = httpClient.execute(httpGet, httpContext);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response status: "
						+ response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			String contentType = entity.getContentType().getValue();
			in = entity.getContent();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response entity: isChunked=" + entity.isChunked()
						+ ", isRepeatable=" + entity.isRepeatable()
						+ ", isStreaming=" + entity.isStreaming()
						+ ", contentLength=" + contentLength);
				Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Storing entity as " + localFilePath);
			}

			File dir = new File(new File(localFilePath).getParent());

			if (!dir.exists()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Creating directory: " + dir);
				}

				dir.mkdirs();
			}

			out = new FileOutputStream(localFilePath);
			final int BUFFER_LENGTH = 64 * 1024; // use a 64k buffer

			byte[] buffer = new byte[BUFFER_LENGTH];
			int bytesRead = 0;

			while ((bytesRead = in.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				out.write(buffer, 0, bytesRead);
			}

			return true;
		} catch (IOException e) {
			Log.w(TAG, "Error fetching " + url + ": " + e.toString(), e);

			if (localFilePath != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Deleting the partially downloaded file: "
							+ localFilePath);
				}

				File file = new File(localFilePath);
				file.delete();
			}

			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing input stream.");
				}
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing output stream.");
				}
			}

			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * 
	 * @return true if all uploads succeeded, false otherwise.
	 */
	private boolean handleNoteUploads() {
		String uploadSetting = settingsDao
				.getString(EffortProvider.Settings.KEY_UPLOAD);
		boolean uploadOnlyOnWifi = EffortProvider.Settings.UPLOAD_WIFI
				.equals(uploadSetting);
		boolean isConnectedToWifi = Utils
				.isConnectedToWifi(getApplicationContext());

		boolean includeLargeFiles = (uploadOnlyOnWifi && isConnectedToWifi)
				|| EffortProvider.Settings.UPLOAD_ANY.equals(uploadSetting);

		Note note = null;
		boolean allSucceeded = true;

		while ((note = notesDao.getNextPendingUpload(includeLargeFiles)) != null) {
			boolean succeeded = false;

			boolean isConnectedToWifiNow = Utils
					.isConnectedToWifi(getApplicationContext());

			if (uploadOnlyOnWifi) {
				if (isConnectedToWifi && !isConnectedToWifiNow) {
					return false;
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Uploading media: noteId=" + note.getLocalId()
						+ ", localMediaPath=" + note.getLocalMediaPath());
			}

			succeeded = uploadNoteFile(note, includeLargeFiles);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleUploads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleUploads method.");
				}

				return false;
			}
		}

		return allSucceeded;
	}

	private boolean handleFormUploads() {
		String uploadSetting = settingsDao
				.getString(EffortProvider.Settings.KEY_UPLOAD);
		boolean uploadOnlyOnWifi = EffortProvider.Settings.UPLOAD_WIFI
				.equals(uploadSetting);
		boolean isConnectedToWifi = Utils
				.isConnectedToWifi(getApplicationContext());

		boolean includeLargeFiles = (uploadOnlyOnWifi && isConnectedToWifi)
				|| EffortProvider.Settings.UPLOAD_ANY.equals(uploadSetting);

		FormFile fileDto = null;
		boolean allSucceeded = true;

		while ((fileDto = formFilesDao.getNextPendingUpload(includeLargeFiles)) != null) {
			boolean succeeded = false;

			boolean isConnectedToWifiNow = Utils
					.isConnectedToWifi(getApplicationContext());

			if (uploadOnlyOnWifi) {
				if (isConnectedToWifi && !isConnectedToWifiNow) {
					return false;
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Uploading media: noteId=" + fileDto.getLocalId()
						+ ", localMediaPath=" + fileDto.getLocalMediaPath());
			}

			succeeded = uploadFormFile(fileDto, includeLargeFiles);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleUploads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleUploads method.");
				}

				return false;
			}
		}

		return allSucceeded;
	}

	private boolean uploadFormFile(FormFile fileDto, boolean includeLargeFiles) {
		if (TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Upload form file called for a form with empty local media path.");
			}
			return true;
		} else {
			File file = new File(fileDto.getLocalMediaPath());

			if (!file.exists()) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Skipping upload form file because local file is missing at path "
									+ fileDto.getLocalMediaPath());
				}
				fileDto.setLocalMediaPath(null);
				formFilesDao.save(fileDto);
				return true;
			}
		}

		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri.parse(serverBaseUrl).buildUpon()
				.appendEncodedPath("media/upload/part/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		File file = new File(fileDto.getLocalMediaPath());
		String md5hash = Utils.getMD5Hash(fileDto.getLocalMediaPath());

		builder.appendQueryParameter("fileName", file.getName())
				.appendQueryParameter("contentType", fileDto.getMimeType())
				.appendQueryParameter("partChecksum", md5hash)
				.appendQueryParameter("finalChecksum", md5hash)
				.appendQueryParameter("fileId", "" + fileDto.getLocalId())
				.appendQueryParameter("partNo", "1")
				.appendQueryParameter("totalPart", "1");

		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media upload URL: " + uri.toString());
		}

		HttpPost httpPost = new HttpPost(uri.toString());

		FileEntity requestEntity = new FileEntity(new File(
				fileDto.getLocalMediaPath()), fileDto.getMimeType());
		httpPost.setEntity(requestEntity);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Request entity: isChunked=" + requestEntity.isChunked()
					+ ", isStreaming=" + requestEntity.isStreaming()
					+ ", isRepeatable=" + requestEntity.isRepeatable());
		}

		try {
			UploadFileProgressThread progressThread = new UploadFileProgressThread(
					httpContext, formFilesDao, fileDto, includeLargeFiles);
			progressThread.start();
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			progressThread.interrupt();

			// while (!progressThread.uploadFinished()) {
			// try {
			// Log.d(TAG, "Waiting for upload to finish.");
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// //ignore
			// }
			// }

			HttpEntity responseEntity = response.getEntity();
			long contentLength = responseEntity.getContentLength();
			// String contentType = responseEntity.getContentType().getValue();
			String jsonResponse = EntityUtils.toString(responseEntity);

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Response entity: isChunked="
								+ responseEntity.isChunked()
								+ ", isRepeatable="
								+ responseEntity.isRepeatable()
								+ ", isStreaming="
								+ responseEntity.isStreaming()
								+ ", contentLength=" + contentLength);
				// Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Response: " + jsonResponse);
			}

			Long mediaId = null;

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				mediaId = getMediaId(jsonResponse);
			} else {
				Log.w(TAG, "Media upload failed with status: "
						+ response.getStatusLine().toString());
			}

			if (mediaId == null) {
				Log.e(TAG, "Media ID is not found in the response.");
				fileDto.setTransferPercentage(null);
				fileDto.setMediaId(null);
				formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
				formFilesDao.save(fileDto);
				return false;
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving mediaId " + mediaId + " for local path "
						+ fileDto.getLocalMediaPath());
			}

			fileDto.setTransferPercentage(null);
			fileDto.setMediaId(mediaId);
			formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
			formFilesDao.save(fileDto);
			Utils.sync(getApplicationContext());

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error posting media to " + uri.toString() + ": "
							+ e.toString());
			e.printStackTrace();

			fileDto.setMediaId(null);
			fileDto.setTransferPercentage(null);
			formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
			formFilesDao.save(fileDto);

			return false;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

	}

	private boolean handleSectionUploads() {
		String uploadSetting = settingsDao
				.getString(EffortProvider.Settings.KEY_UPLOAD);
		boolean uploadOnlyOnWifi = EffortProvider.Settings.UPLOAD_WIFI
				.equals(uploadSetting);
		boolean isConnectedToWifi = Utils
				.isConnectedToWifi(getApplicationContext());

		boolean includeLargeFiles = (uploadOnlyOnWifi && isConnectedToWifi)
				|| EffortProvider.Settings.UPLOAD_ANY.equals(uploadSetting);

		SectionFile fileDto = null;
		boolean allSucceeded = true;

		while ((fileDto = sectionFilesDao
				.getNextPendingUpload(includeLargeFiles)) != null) {
			boolean succeeded = false;

			boolean isConnectedToWifiNow = Utils
					.isConnectedToWifi(getApplicationContext());

			if (uploadOnlyOnWifi) {
				if (isConnectedToWifi && !isConnectedToWifiNow) {
					return false;
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Uploading section file: fileId="
								+ fileDto.getLocalId() + ", localMediaPath="
								+ fileDto.getLocalMediaPath());
			}

			succeeded = uploadSectionFile(fileDto, includeLargeFiles);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleUploads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleUploads method.");
				}

				return false;
			}
		}

		return allSucceeded;
	}

	private boolean uploadSectionFile(SectionFile fileDto,
			boolean includeLargeFiles) {
		if (TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Upload section file called for a section file with empty local media path.");
			}
			return true;
		} else {
			File file = new File(fileDto.getLocalMediaPath());

			if (!file.exists()) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Skipping upload section file because local file is missing at path "
									+ fileDto.getLocalMediaPath());
				}
				fileDto.setLocalMediaPath(null);
				sectionFilesDao.save(fileDto);
				return true;
			}
		}

		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri.parse(serverBaseUrl).buildUpon()
				.appendEncodedPath("media/upload/part/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		File file = new File(fileDto.getLocalMediaPath());
		String md5hash = Utils.getMD5Hash(fileDto.getLocalMediaPath());

		builder.appendQueryParameter("fileName", file.getName())
				.appendQueryParameter("contentType", fileDto.getMimeType())
				.appendQueryParameter("partChecksum", md5hash)
				.appendQueryParameter("finalChecksum", md5hash)
				.appendQueryParameter("fileId", "" + fileDto.getLocalId())
				.appendQueryParameter("partNo", "1")
				.appendQueryParameter("totalPart", "1");

		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media upload URL: " + uri.toString());
		}

		HttpPost httpPost = new HttpPost(uri.toString());

		FileEntity requestEntity = new FileEntity(new File(
				fileDto.getLocalMediaPath()), fileDto.getMimeType());
		httpPost.setEntity(requestEntity);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Request entity: isChunked=" + requestEntity.isChunked()
					+ ", isStreaming=" + requestEntity.isStreaming()
					+ ", isRepeatable=" + requestEntity.isRepeatable());
		}

		try {
			UploadSectionFileProgressThread progressThread = new UploadSectionFileProgressThread(
					httpContext, sectionFilesDao, fileDto, includeLargeFiles);
			progressThread.start();
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			progressThread.interrupt();

			// while (!progressThread.uploadFinished()) {
			// try {
			// Log.d(TAG, "Waiting for upload to finish.");
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// //ignore
			// }
			// }

			HttpEntity responseEntity = response.getEntity();
			long contentLength = responseEntity.getContentLength();
			// String contentType = responseEntity.getContentType().getValue();
			String jsonResponse = EntityUtils.toString(responseEntity);

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Response entity: isChunked="
								+ responseEntity.isChunked()
								+ ", isRepeatable="
								+ responseEntity.isRepeatable()
								+ ", isStreaming="
								+ responseEntity.isStreaming()
								+ ", contentLength=" + contentLength);
				// Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Response: " + jsonResponse);
			}

			Long mediaId = null;

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				mediaId = getMediaId(jsonResponse);
			} else {
				Log.w(TAG, "Media upload failed with status: "
						+ response.getStatusLine().toString());
			}

			if (mediaId == null) {
				Log.e(TAG, "Media ID is not found in the response.");
				fileDto.setTransferPercentage(null);
				fileDto.setMediaId(null);
				formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
				sectionFilesDao.save(fileDto);
				return false;
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving mediaId " + mediaId + " for local path "
						+ fileDto.getLocalMediaPath());
			}

			fileDto.setTransferPercentage(null);
			fileDto.setMediaId(mediaId);
			formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
			sectionFilesDao.save(fileDto);
			Utils.sync(getApplicationContext());

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error posting media to " + uri.toString() + ": "
							+ e.toString());
			e.printStackTrace();

			fileDto.setMediaId(null);
			fileDto.setTransferPercentage(null);
			formsDao.updateDirtyFlag(fileDto.getLocalFormId(), true);
			sectionFilesDao.save(fileDto);

			return false;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

	}

	/**
	 * 
	 * @param note
	 * @return true if the upload succeeded, false if upload failed.
	 */
	private boolean uploadNoteFile(Note note, boolean includeLargeFiles) {
		if (TextUtils.isEmpty(note.getLocalMediaPath())) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Upload media called for a note with empty local media path.");
			}
			return true;
		} else {
			File file = new File(note.getLocalMediaPath());

			if (!file.exists()) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Skipping upload media because local file is missing at path "
									+ note.getLocalMediaPath());
				}
				note.setLocalMediaPath(null);
				notesDao.save(note);
				return true;
			}
		}

		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri.parse(serverBaseUrl).buildUpon()
				.appendEncodedPath("media/upload/part/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		File file = new File(note.getLocalMediaPath());
		String md5hash = Utils.getMD5Hash(note.getLocalMediaPath());

		builder.appendQueryParameter("fileName", file.getName())
				.appendQueryParameter("contentType", note.getMimeType())
				.appendQueryParameter("partChecksum", md5hash)
				.appendQueryParameter("finalChecksum", md5hash)
				.appendQueryParameter("fileId", "" + note.getLocalId())
				.appendQueryParameter("partNo", "1")
				.appendQueryParameter("totalPart", "1");

		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media upload URL: " + uri.toString());
		}

		HttpPost httpPost = new HttpPost(uri.toString());

		FileEntity requestEntity = new FileEntity(new File(
				note.getLocalMediaPath()), note.getMimeType());
		httpPost.setEntity(requestEntity);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Request entity: isChunked=" + requestEntity.isChunked()
					+ ", isStreaming=" + requestEntity.isStreaming()
					+ ", isRepeatable=" + requestEntity.isRepeatable());
		}

		try {
			UploadProgressThread progressThread = new UploadProgressThread(
					httpContext, notesDao, note, includeLargeFiles);
			progressThread.start();
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			progressThread.interrupt();

			// while (!progressThread.uploadFinished()) {
			// try {
			// Log.d(TAG, "Waiting for upload to finish.");
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// //ignore
			// }
			// }

			HttpEntity responseEntity = response.getEntity();
			long contentLength = responseEntity.getContentLength();
			// String contentType = responseEntity.getContentType().getValue();
			String jsonResponse = EntityUtils.toString(responseEntity);

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Response entity: isChunked="
								+ responseEntity.isChunked()
								+ ", isRepeatable="
								+ responseEntity.isRepeatable()
								+ ", isStreaming="
								+ responseEntity.isStreaming()
								+ ", contentLength=" + contentLength);
				// Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Response: " + jsonResponse);
			}

			Long mediaId = null;

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				mediaId = getMediaId(jsonResponse);
			} else {
				Log.w(TAG, "Media upload failed with status: "
						+ response.getStatusLine().toString());
			}

			if (mediaId == null) {
				Log.e(TAG, "Media ID is not found in the response.");
				note.setTransferPercentage(null);
				note.setMediaId(null);
				note.setDirty(null);
				notesDao.updateTransferStatus(note);
				return false;
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving mediaId " + mediaId + " for local path "
						+ note.getLocalMediaPath());
			}

			note.setTransferPercentage(null);
			note.setMediaId(mediaId);
			note.setDirty(true);
			notesDao.updateTransferStatus(note);
			Utils.sync(getApplicationContext());

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error posting media to " + uri.toString() + ": "
							+ e.toString());
			e.printStackTrace();

			note.setMediaId(null);
			note.setTransferPercentage(null);
			note.setDirty(false);
			notesDao.save(note);

			return false;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

	}

	/**
	 * 
	 * @param jsonResponse
	 * @return valid media ID or null if media id is not found in the response
	 */
	private Long getMediaId(String jsonResponse) {
		JSONTokener tokener = new JSONTokener(jsonResponse);

		try {
			JSONObject json = (JSONObject) tokener.nextValue();
			return json.getLong("mediaId");
		} catch (JSONException e) {
			return null;
		}
	}

	class UploadFileProgressThread extends Thread {

		private FormFile fileDto;
		private SyncBasicHttpContext httpContext;
		private FormFilesDao filesDao;
		private boolean uploadFinished;
		private boolean includeLargeFiles;

		public UploadFileProgressThread(SyncBasicHttpContext httpContext,
				FormFilesDao filesDao, FormFile fileDto,
				boolean includeLargeFiles) {
			super("UploadProgressThread");
			this.httpContext = httpContext;
			this.filesDao = filesDao;
			this.fileDto = fileDto;
			this.includeLargeFiles = includeLargeFiles;
		}

		public boolean uploadFinished() {
			return uploadFinished;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				AbstractClientConnAdapter connAdapter = (AbstractClientConnAdapter) httpContext
						.getAttribute(ExecutionContext.HTTP_CONNECTION);
				if (connAdapter == null) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Connection adapter is null.");
					}
				} else {
					if (!connAdapter.isOpen()) {
						return;
					}

					HttpConnectionMetrics metrics = connAdapter.getMetrics();

					if (metrics == null) {
						// don't return yet. you'll get it in a while
						if (BuildConfig.DEBUG) {
							Log.d(TAG, "Metrics object is null.");
						}
					} else {
						long sentBytes = metrics.getSentBytesCount();
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Metrics: requestCount="
											+ metrics.getRequestCount()
											+ ", responseCount="
											+ metrics.getResponseCount()
											+ ", sentBytes=" + sentBytes
											+ ", receivedBytes="
											+ metrics.getReceivedBytesCount());
						}

						int transferPercentage = (int) (sentBytes * 100 / (double) fileDto
								.getFileSize());

						// sentBytes include the query parameters, headers, etc.
						// Thus, sentBytes could be few bytes greater than
						// the file size. Put the cap at 100%.
						if (transferPercentage > 100) {
							transferPercentage = 100;
						}

						if (transferPercentage == 100) {
							uploadFinished = true;
						}

						if (BuildConfig.DEBUG) {
							Log.d(TAG, "sentBytes=" + sentBytes + ", fileSize="
									+ fileDto.getFileSize()
									+ ", transferPercentage="
									+ transferPercentage);
						}

						fileDto.setTransferPercentage(transferPercentage);
						filesDao.updateTransferStatus(fileDto);

						FormFile nextNote = filesDao
								.getNextPendingUpload(includeLargeFiles);

						if (nextNote != null
								&& !Utils.longsEqual(fileDto.getLocalId(),
										nextNote.getLocalId())) {
							try {
								connAdapter.shutdown();
							} catch (IOException e) {
								if (BuildConfig.DEBUG) {
									Log.d(TAG,
											"Ignored the IO exception while forced shutdown.",
											e);
								}
							}
						}
					}
				}

				try {
					sleep(1000);
				} catch (InterruptedException e) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG,
								"Thread interrupted while sleeping, returning from run method.");
					}

					return;
				}
			}
		}
	}

	class UploadSectionFileProgressThread extends Thread {

		private SectionFile fileDto;
		private SyncBasicHttpContext httpContext;
		private SectionFilesDao filesDao;
		private boolean uploadFinished;
		private boolean includeLargeFiles;

		public UploadSectionFileProgressThread(
				SyncBasicHttpContext httpContext, SectionFilesDao filesDao,
				SectionFile fileDto, boolean includeLargeFiles) {
			super("UploadProgressThread");
			this.httpContext = httpContext;
			this.filesDao = filesDao;
			this.fileDto = fileDto;
			this.includeLargeFiles = includeLargeFiles;
		}

		public boolean uploadFinished() {
			return uploadFinished;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				AbstractClientConnAdapter connAdapter = (AbstractClientConnAdapter) httpContext
						.getAttribute(ExecutionContext.HTTP_CONNECTION);
				if (connAdapter == null) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Connection adapter is null.");
					}
				} else {
					if (!connAdapter.isOpen()) {
						return;
					}

					HttpConnectionMetrics metrics = connAdapter.getMetrics();

					if (metrics == null) {
						// don't return yet. you'll get it in a while
						if (BuildConfig.DEBUG) {
							Log.d(TAG, "Metrics object is null.");
						}
					} else {
						long sentBytes = metrics.getSentBytesCount();
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Metrics: requestCount="
											+ metrics.getRequestCount()
											+ ", responseCount="
											+ metrics.getResponseCount()
											+ ", sentBytes=" + sentBytes
											+ ", receivedBytes="
											+ metrics.getReceivedBytesCount());
						}

						int transferPercentage = (int) (sentBytes * 100 / (double) fileDto
								.getFileSize());

						// sentBytes include the query parameters, headers, etc.
						// Thus, sentBytes could be few bytes greater than
						// the file size. Put the cap at 100%.
						if (transferPercentage > 100) {
							transferPercentage = 100;
						}

						if (transferPercentage == 100) {
							uploadFinished = true;
						}

						if (BuildConfig.DEBUG) {
							Log.d(TAG, "sentBytes=" + sentBytes + ", fileSize="
									+ fileDto.getFileSize()
									+ ", transferPercentage="
									+ transferPercentage);
						}

						fileDto.setTransferPercentage(transferPercentage);
						filesDao.updateTransferStatus(fileDto);

						SectionFile nextFile = filesDao
								.getNextPendingUpload(includeLargeFiles);

						if (nextFile != null
								&& !Utils.longsEqual(fileDto.getLocalId(),
										nextFile.getLocalId())) {
							try {
								connAdapter.shutdown();
							} catch (IOException e) {
								if (BuildConfig.DEBUG) {
									Log.d(TAG,
											"Ignored the IO exception while forced shutdown.",
											e);
								}
							}
						}
					}
				}

				try {
					sleep(1000);
				} catch (InterruptedException e) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG,
								"Thread interrupted while sleeping, returning from run method.");
					}

					return;
				}
			}
		}
	}

	class UploadProgressThread extends Thread {

		private Note note;
		private SyncBasicHttpContext httpContext;
		private NotesDao notesDao;
		private boolean uploadFinished;
		private boolean includeLargeFiles;

		public UploadProgressThread(SyncBasicHttpContext httpContext,
				NotesDao notesDao, Note note, boolean includeLargeFiles) {
			super("UploadProgressThread");
			this.httpContext = httpContext;
			this.notesDao = notesDao;
			this.note = note;
			this.includeLargeFiles = includeLargeFiles;
		}

		public boolean uploadFinished() {
			return uploadFinished;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				AbstractClientConnAdapter connAdapter = (AbstractClientConnAdapter) httpContext
						.getAttribute(ExecutionContext.HTTP_CONNECTION);
				if (connAdapter == null) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Connection adapter is null.");
					}
				} else {
					if (!connAdapter.isOpen()) {
						return;
					}

					HttpConnectionMetrics metrics = connAdapter.getMetrics();

					if (metrics == null) {
						// don't return yet. you'll get it in a while
						if (BuildConfig.DEBUG) {
							Log.d(TAG, "Metrics object is null.");
						}
					} else {
						long sentBytes = metrics.getSentBytesCount();
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Metrics: requestCount="
											+ metrics.getRequestCount()
											+ ", responseCount="
											+ metrics.getResponseCount()
											+ ", sentBytes=" + sentBytes
											+ ", receivedBytes="
											+ metrics.getReceivedBytesCount());
						}

						int transferPercentage = (int) (sentBytes * 100 / (double) note
								.getFileSize());

						// sentBytes include the query parameters, headers, etc.
						// Thus, sentBytes could be few bytes greater than
						// the file size. Put the cap at 100%.
						if (transferPercentage > 100) {
							transferPercentage = 100;
						}

						if (transferPercentage == 100) {
							uploadFinished = true;
						}

						if (BuildConfig.DEBUG) {
							Log.d(TAG, "sentBytes=" + sentBytes + ", fileSize="
									+ note.getFileSize()
									+ ", transferPercentage="
									+ transferPercentage);
						}

						note.setTransferPercentage(transferPercentage);
						notesDao.updateTransferStatus(note);

						Note nextNote = notesDao
								.getNextPendingUpload(includeLargeFiles);

						if (nextNote != null
								&& !Utils.longsEqual(note.getLocalId(),
										nextNote.getLocalId())) {
							try {
								connAdapter.shutdown();
							} catch (IOException e) {
								if (BuildConfig.DEBUG) {
									Log.d(TAG,
											"Ignored the IO exception while forced shutdown.",
											e);
								}
							}
						}
					}
				}

				try {
					sleep(1000);
				} catch (InterruptedException e) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG,
								"Thread interrupted while sleeping, returning from run method.");
					}

					return;
				}
			}
		}
	}

	/**
	 * 
	 * @return true if all downloads succeeded, false otherwise.
	 */
	private boolean handleArticleDownloads() {
		Article article = null;

		boolean allSucceeded = true;

		while ((article = articlesDao.getNextPendingDownload()) != null) {
			boolean succeeded = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Downloading article media " + ": article Id="
						+ article.getId() + ", mediaId=" + article.getMediaId());
			}

			succeeded = downloadArticleFile(article);

			if (!succeeded) {
				allSucceeded = false;
			}

			if (!Utils.isConnected(getApplicationContext())) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Not connected to network. Returning from handleDownloads method.");
				}

				return false;
			}

			if (!Utils.isSDCardValid(getApplicationContext(), false)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"SD card not mounted. Returning from handleDownloads method.");
				}

				return false;
			}

		}

		return allSucceeded;
	}

	/**
	 * 
	 * @param note
	 * @return true if the download succeeded, false if download failed.
	 */
	private boolean downloadArticleFile(Article article) {
		boolean downloadCancelled = false;
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("EFFORT");
		String employeeId = settingsDao.getString("employeeId");
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"media/get/" + article.getMediaId() + "/" + employeeId);
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		Uri uri = builder.build();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Media download URL: " + uri.toString());
		}

		HttpGet httpGet = new HttpGet(uri.toString());
		SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);

		String fileName = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response status: "
						+ response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			String contentType = entity.getContentType().getValue();
			in = entity.getContent();
			fileName = Utils.getNewPathForMediaTypeForKB(article.getMediaId(),
					contentType, getApplicationContext(),
					article.getIsSecured());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response entity: isChunked=" + entity.isChunked()
						+ ", isRepeatable=" + entity.isRepeatable()
						+ ", isStreaming=" + entity.isStreaming()
						+ ", contentLength=" + contentLength);
				Log.d(TAG, "Response entity: contentType" + contentType);
				Log.d(TAG, "Storing entity as " + fileName);
			}

			File dir = new File(new File(fileName).getParent());

			if (!dir.exists()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Creating directory: " + dir);
				}

				dir.mkdirs();
			}

			out = new FileOutputStream(fileName);
			final int BUFFER_LENGTH = 64 * 1024; // use a 64k buffer

			byte[] buffer = new byte[BUFFER_LENGTH];
			int totalBytesRead = 0;
			int bytesRead = 0;
			long startTime = System.currentTimeMillis();

			while ((bytesRead = in.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				out.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;

				if (System.currentTimeMillis() - startTime > 1000) {
					article = articlesDao.getArticleWithId(article.getId());

					// check if user cancelled the download
					if (article.getDownloadRequested() == null
							|| !article.getDownloadRequested()) {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Skipping download of "
											+ article.getId()
											+ " on user's cancellation request.");
						}

						downloadCancelled = true;
						break;
					}

					int transferPercentage = (int) (totalBytesRead * 100 / (double) contentLength);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "totalBytesRead=" + totalBytesRead
								+ ", contentLength=" + contentLength
								+ ", transferPercentage=" + transferPercentage);
					}

					article.setTransferPercentage(transferPercentage);
					articlesDao.updateTransferStatus(article);
				}
			}

			if (downloadCancelled) {
				article.setLocalMediaPath(null);
			} else {
				article.setLocalMediaPath(fileName);
				article.setFileSize(contentLength);
				article.setMimeType(contentType);
				article.setDownloadRequested(null);
				article.setTransferPercentage(null);
				articlesDao.save(article);
			}

			return true;
		} catch (IOException e) {
			Log.w(TAG,
					"Error fetching " + uri.toString() + ": " + e.toString(), e);

			article.setTransferPercentage(null);
			article.setLocalMediaPath(null);
			articlesDao.save(article);

			if (fileName != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Deleting the partially downloaded file: "
							+ fileName);
				}

				File file = new File(fileName);
				file.delete();
			}

			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing input stream.");
				}
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Ignored the exception caught while closing output stream.");
				}
			}

			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

}
