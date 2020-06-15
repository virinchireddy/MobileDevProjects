package in.spoors.effort1;

import in.spoors.effort1.dao.SimCardChangeSmsDao;
import in.spoors.effort1.dto.SimChangeMessage;

import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BackgroundSmsSendService extends WakefulIntentService {

	public static final String TAG = "BackgroundSmsSendService";

	private SimCardChangeSmsDao simCardChangeSmsDao;

	public BackgroundSmsSendService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent arg0) {

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In doWakefulWork.");
		}

		simCardChangeSmsDao = SimCardChangeSmsDao
				.getInstance(getApplicationContext());
		List<SimChangeMessage> unsentMessages = simCardChangeSmsDao
				.getAllUnsentMessages();

		for (SimChangeMessage simChangeMessage : unsentMessages) {
			Utils.processPendingSMS(simChangeMessage, getApplicationContext());
		}

	}

}
