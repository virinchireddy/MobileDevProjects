package com.info.socialnetworking.app.pushnotification;

import java.util.List;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.info.socialnetworking.app.Fragment.ChatListPage;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ChatIndividualDetails;
import com.info.socialnetworking.app.helper.ChatListDetails;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.ChatIndividualPage;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(),
						extras.getString("user_id"),
						extras.getString("friend_id"),
						extras.getString("username"),
						extras.getString("msg_sent_time"),
						extras.getString("nav_type"));
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification(
						"Deleted messages on server: " + extras.toString(),
						extras.getString("user_id"),
						extras.getString("friend_id"),
						extras.getString("username"),
						extras.getString("msg_sent_time"),
						extras.getString("nav_type"));
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				/*
				 * for (int i = 0; i < 3; i++) { Log.i(TAG,"Working... " + (i +
				 * 1) + "/5 @ " + SystemClock.elapsedRealtime()); try {
				 * Thread.sleep(5000); } catch (InterruptedException e) { }
				 * 
				 * }
				 */
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification("" + extras.get(Config.MESSAGE_KEY),
						extras.getString("user_id"),
						extras.getString("friend_id"),
						extras.getString("username"),
						extras.getString("msg_sent_time"),
						extras.getString("nav_type"));
				for (String key : extras.keySet()) {
					Log.d("myApplication", key + " is a key in the bundle");
				}
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg, String user_id, String friend_id,
			String friend_username, String timeStamp, String nav_type) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		if (nav_type.equals("Chat Message")) {
			Log.v("navigation", "Chat Message");

			checkStatusOfApplication(msg, user_id, friend_id, friend_username,
					timeStamp);
		} else if (nav_type.equals("Friendship")) {
			Log.v("navigation", "Friendship");

			sendNotificationFriend(msg, user_id, friend_id, friend_username);
		}
		Log.d(TAG, "Notification sent successfully.");
	}

	// check if the application is opened or closed

	@SuppressWarnings("deprecation")
	private void checkStatusOfApplication(String msg, String user_id,
			String friend_id, String friend_username, String timeStamp) {
		// TODO Auto-generated method stub
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		// Log.d("topActivity", "CURRENT Activity ::"+
		// taskInfo.get(0).topActivity.getClassName());
		// Log.v("user_id", user_id + "/" + friend_id);
		// checking if the user is present in individual chat screen

		if (taskInfo.get(0).topActivity.getClassName().equals(
				"com.info.socialnetworking.app.meetatbars.ChatIndividualPage")) {
			// for checking if the user is present in same screen

			if (ChatIndividualPage.user_id.equals(friend_id)
					&& ChatIndividualPage.friend_id.equals(user_id)) {
				// for instant messaging
				String[] finalString = {
						"0",
						msg.substring(1, msg.length() - 1).replace("\\n", "\n"),
						timeStamp };
				ChatIndividualPage.chatList.add(new ChatIndividualDetails(
						finalString));
				ChatIndividualPage.mAdapter
						.notifyItemInserted(ChatIndividualPage.chatList.size());
				// ChatIndividualPage.etMessage.setText("");
				ChatIndividualPage.mRecyclerView
						.smoothScrollToPosition(ChatIndividualPage.chatList
								.size());
				final MediaPlayer mPlayer2;
				mPlayer2 = MediaPlayer.create(getApplicationContext(),
						R.raw.send_message);
				mPlayer2.start();
				mPlayer2.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mPlayer2.stop();
					}
				});

			} else
				showNotificationMessage(msg, user_id, friend_id,
						friend_username);
		}
		// check if the use is avilabel in home screen
		else if (taskInfo.get(0).topActivity.getClassName().equals("com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen")) {
			try {
				Fragment f = BarHopperHomeScreen.activity.getFragmentManager()
						.findFragmentById(R.id.flContainer);
				if (f instanceof ChatListPage) {

					for (int i = 0; i < ChatListPage.chatList.size(); i++) {

						ChatListDetails singleUserDetails = (ChatListDetails) ChatListPage.chatList
								.get(i);
						if (singleUserDetails.getChatUserName().equals(
								friend_username)) {
							Log.v("TestingChat", "success" + i);
							int unReadMessages = Integer
									.parseInt(singleUserDetails
											.getChatUnreadMessages());
							unReadMessages = unReadMessages + 1;
							singleUserDetails
									.setChatUnreadMessages(unReadMessages + "");
							ChatListPage.mAdapter.notifyItemChanged(i);

						} else
							Log.v("TestingChat", "failure" + i);
					}
				}else
					showNotificationMessage(msg, user_id, friend_id, friend_username);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			showNotificationMessage(msg, user_id, friend_id, friend_username);

	}

	private void showNotificationMessage(String msg, String user_id,
			String friend_id, String friend_username) {
		// TODO Auto-generated method stub
		Intent in = new Intent(this, ChatIndividualPage.class);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		in.putExtra("user_id", friend_id);
		in.putExtra("friend_id", user_id);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setContentTitle(friend_username)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(
						msg.substring(1, msg.length() - 1).replace("\\n", "\n"))
				.setSound(
						Uri.parse("android.resource://" + getPackageName()
								+ "/" + R.raw.incoming));
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBuilder.setSmallIcon(R.drawable.notification_lollipop);
		} else {
			mBuilder.setSmallIcon(R.drawable.notification_prelollipop);
		}

		PendingIntent contentIntent = PendingIntent.getActivity(this,
				(int) System.currentTimeMillis(), in,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// mBuilder.setExtras(extras)
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setAutoCancel(true);
		mNotificationManager
				.notify(Integer.parseInt(user_id), mBuilder.build());
	}

	private void sendNotificationFriend(String msg, String user_id,
			String friend_id, String friend_username) {

		MainMenu.clickedmenu = 6;
		// here user id nothing but application user id
		MainMenu.user_id = Integer.parseInt(friend_id);
		Log.v("navigation", "am here");
		Intent in = new Intent(this, BarHopperHomeScreen.class);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setContentTitle(friend_username)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(
						msg.substring(1, msg.length() - 1).replace("\\n", "\n"))
				.setSound(
						Uri.parse("android.resource://" + getPackageName()
								+ "/" + R.raw.incoming));
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBuilder.setSmallIcon(R.drawable.notification_lollipop);
		} else {
			mBuilder.setSmallIcon(R.drawable.notification_prelollipop);
		}

		PendingIntent contentIntent = PendingIntent.getActivity(this,
				(int) System.currentTimeMillis(), in,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// mBuilder.setExtras(extras)
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setAutoCancel(true);
		mNotificationManager
				.notify(Integer.parseInt(user_id), mBuilder.build());
	}

}
