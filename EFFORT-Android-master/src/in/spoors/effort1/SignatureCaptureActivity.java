package in.spoors.effort1;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class SignatureCaptureActivity extends ActionBarActivity {

	public static final String TAG = "SignatureCaptureActivity";

	/**
	 * The name of the Intent-extra used to indicate a content resolver Uri to
	 * be used to store the requested image or video.
	 * 
	 * As this constant is set to MediaStore.EXTRA_OUTPUT, you can use
	 * MediaStore.EXTRA_OUTPUT as well.
	 * 
	 */
	public static final String EXTRA_OUTPUT = MediaStore.EXTRA_OUTPUT;
	private Uri outputUri;
	private boolean signatureCaptured;
	private Bitmap bitmap;
	private LinearLayout signatureLayout;
	private SignatureView signatureView;
	private Button clearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature_capture);

		Intent intent = getIntent();
		outputUri = (Uri) intent.getParcelableExtra(EXTRA_OUTPUT);

		if (outputUri == null) {
			Log.e(TAG, "You must specify Uri for EXTRA_OUTPUT.");
			setResult(RESULT_CANCELED);
			finish();
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Output URI: " + outputUri.toString());
			}
		}

		signatureLayout = (LinearLayout) findViewById(R.id.signatureLayout);
		signatureView = new SignatureView(this, null);
		signatureView.setBackgroundColor(Color.WHITE);
		signatureLayout.addView(signatureView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		clearButton = (Button) findViewById(R.id.clearButton);

		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (BuildConfig.DEBUG) {
					Log.v(TAG, "Panel Cleared");
				}

				signatureView.clear();
				signatureCaptured = false;
				supportInvalidateOptionsMenu();
			}
		});

		ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		Utils.updateActionBar(actionBar);
	}

	public void onSaveButtonClick(View v) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "Save Clicked");
		}

		save();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signature_capture, menu);
		menu.findItem(R.id.saveSignature).setEnabled(signatureCaptured);
		menu.findItem(R.id.discardSignature).setEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.saveSignature:
			save();
			break;

		case R.id.discardSignature:
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

	private boolean fileExists() {
		if (outputUri != null) {
			File file = new File(outputUri.getPath());
			return file.exists();
		} else {
			return false;
		}
	}

	private void save() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "File has been saved to " + outputUri.toString());
		}

		signatureLayout.setDrawingCacheEnabled(true);
		signatureView.save(signatureLayout);
		setResult(RESULT_OK);
		finish();
	}

	private void discard() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "User opted to discard " + outputUri.toString());
		}

		if (fileExists()) {
			File file = new File(outputUri.getPath());

			if (file.delete()) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Deleted " + outputUri.getPath());
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Could not delete " + outputUri.getPath());
				}
			}
		}

		setResult(RESULT_CANCELED);
		finish();
	}

	public class SignatureView extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private Paint paint = new Paint();
		private Path path = new Path();

		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		List<Point> dotPoints = new ArrayList<Point>();
		boolean cleared = false;

		public SignatureView(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public void save(View v) {
			if (BuildConfig.DEBUG) {
				Log.v(TAG, "Width: " + v.getWidth());
				Log.v(TAG, "Height: " + v.getHeight());
			}

			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(signatureLayout.getWidth(),
						signatureLayout.getHeight(), Bitmap.Config.RGB_565);
			}

			Canvas canvas = new Canvas(bitmap);
			try {
				File file = new File(outputUri.getPath());
				FileOutputStream os = new FileOutputStream(file);

				v.draw(canvas);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
				os.flush();
				os.close();
				String url = Images.Media.insertImage(getContentResolver(),
						bitmap, "title", null);

				if (BuildConfig.DEBUG) {
					Log.v(TAG, "url: " + url);
				}

				// In case you want to delete the file
				// boolean deleted = mypath.delete();
				// Log.v("log_tag","deleted: " + mypath.toString() + deleted);
				// If you want to convert the image to string use base64
				// converter

			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, e.toString(), e);
				}
			}
		}

		public void clear() {
			path.reset();
			cleared = true;
			dotPoints = new ArrayList<Point>();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (BuildConfig.DEBUG) {
				Log.i("sign", "ondraw");
			}
			canvas.drawPath(path, paint);
			if (!cleared) {
				for (int i = 0; i < dotPoints.size(); i++) {
					canvas.drawPoint(dotPoints.get(i).x, dotPoints.get(i).y,
							paint);
				}
			} else {
				cleared = false;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			signatureCaptured = true;
			supportInvalidateOptionsMenu();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (BuildConfig.DEBUG) {
					Log.i("sign", "on touch down " + eventX + " , " + eventY);
				}
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;

			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:
				if (BuildConfig.DEBUG) {
					Log.i("sign", "on touch up" + eventX + " , " + eventY);
				}
				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}

				if (lastTouchX == eventX || lastTouchY == eventY) {
					Point dotpoint = new Point();
					dotpoint.x = (int) eventX;
					dotpoint.y = (int) eventY;
					dotPoints.add(dotpoint);
				} else {
					path.lineTo(eventX, eventY);
				}

				break;

			default:
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Ignored touch event: " + event.toString());
				}
				return false;
			}

			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (BuildConfig.DEBUG) {
				Log.i("sign", "expand dirty rect");
			}
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}

			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			if (BuildConfig.DEBUG) {
				Log.i("sign", "reset dirty rect");
			}
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}
}
