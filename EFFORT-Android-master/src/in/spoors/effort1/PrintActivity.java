package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class PrintActivity extends Activity {
	private StateChangeReceiver stateChangeReceiver;
	private static final int ENABLE_BLUETOOTH = 0;
	private static final String TAG = "PrintActivity";
	private BluetoothAdapter bluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);

		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth.isEnabled()) {
			print();
		} else {
			stateChangeReceiver = new StateChangeReceiver();
			registerReceiver(stateChangeReceiver, new IntentFilter(
					BluetoothAdapter.ACTION_STATE_CHANGED));
			startActivityForResult(new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE), ENABLE_BLUETOOTH);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.print, menu);
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (stateChangeReceiver != null) {
			unregisterReceiver(stateChangeReceiver);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ENABLE_BLUETOOTH && resultCode != RESULT_OK) {
			Toast.makeText(this, "You must enable bluetooth to print.",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void print() {
		byte[] content = getIntent().getByteArrayExtra("content");
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		String address = settingsDao.getString(Settings.KEY_PRINTER_ADDRESS);

		try {
			BluetoothDevice device = bluetooth.getRemoteDevice(address);
			BluetoothSocket socket = device
					.createRfcommSocketToServiceRecord(UUID.fromString(settingsDao
							.getString(Settings.KEY_PRINTER_SERVICE_RECORD_UUID)));
			socket.connect();
			OutputStream out = socket.getOutputStream();
			out.write(content);
			out.write("\n\n\n".getBytes());
			out.flush();
			out.close();
			Toast.makeText(
					this,
					getIntent().getStringExtra("title")
							+ " printed successfully.", Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "Failed to connect to bluetooth printer.", e);
			}
			Toast.makeText(
					this,
					"Failed to print on "
							+ settingsDao.getString(Settings.KEY_PRINTER_NAME)
							+ ". Please ensure your printer is turned on and is working.",
					Toast.LENGTH_LONG).show();
		}

		finish();
	}

	class StateChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In stateChangeReceiver's onReceive. intent="
						+ intent + ", extras=" + extras);
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					Log.i(TAG, "key=" + key + ", value="
							+ value.getClass().getName());
				}

				if (intent.hasExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE)) {
					Log.i(TAG,
							"Previous state="
									+ getStateName(intent
											.getIntExtra(
													BluetoothAdapter.EXTRA_PREVIOUS_STATE,
													-1)));
				}
			}

			if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG,
							"State="
									+ getStateName(intent.getIntExtra(
											BluetoothAdapter.EXTRA_STATE, -1)));
				}

				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
					print();
				}
			}
		}
	}

	private String getStateName(int state) {
		switch (state) {
		case BluetoothAdapter.STATE_CONNECTED:
			return "connected";
		case BluetoothAdapter.STATE_CONNECTING:
			return "connecting";
		case BluetoothAdapter.STATE_DISCONNECTED:
			return "disconnected";
		case BluetoothAdapter.STATE_DISCONNECTING:
			return "disconnecting";
		case BluetoothAdapter.STATE_OFF:
			return "off";
		case BluetoothAdapter.STATE_ON:
			return "on";
		case BluetoothAdapter.STATE_TURNING_OFF:
			return "turningOff";
		case BluetoothAdapter.STATE_TURNING_ON:
			return "turningOn";
		}

		return "" + state;
	}

}
