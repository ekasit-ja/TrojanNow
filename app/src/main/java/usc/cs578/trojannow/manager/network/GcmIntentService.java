package usc.cs578.trojannow.manager.network;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.chat.Chat;
import usc.cs578.trojannow.manager.post.CommentViewer;
import usc.cs578.trojannow.manager.post.PostViewer;

/*
 * Created by Ekasit_Ja on 28-Apr-15.
 */
public class GcmIntentService extends IntentService {

	private static final String TAG = GcmIntentService.class.getSimpleName();
	public static final int NOTIFICATION_ID = 1;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty() && messageType != null) {
			switch (messageType) {
				case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR: {
					sendNotification("Send error: " + extras.toString(), null);
					break;
				}
				case GoogleCloudMessaging.MESSAGE_TYPE_DELETED: {
					sendNotification("Deleted messages on server: " +
							extras.toString(), null);
					// If it's a regular GCM message, do some work.
					break;
				}
				case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE: {
					// Post notification of received message.
					handleNotification(intent);
					Log.i(TAG, "Received: " + extras.toString());
					break;
				}
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void handleNotification(Intent intent) {
		String type = intent.getStringExtra(Url.notificationTypeKey);

		switch (type) {
			case Url.got_comment_type: {
				int post_id = Integer.parseInt(intent.getStringExtra(Url.postIdKey));
				if(CommentViewer.isActivityVisible() && CommentViewer.getCurrentPostId() == post_id) {
					Intent callbackIntent = new Intent(CommentViewer.class.getSimpleName());
					callbackIntent.putExtra(Method.statusKey, true);
					callbackIntent.putExtra(Method.methodKey, Method.autoLoadNewComment);
					String newCommentData = intent.getStringExtra(Url.newCommentDataKey);
					callbackIntent.putExtra(Method.newCommentDataKey, newCommentData);
					LocalBroadcastManager.getInstance(this).sendBroadcast(callbackIntent);
				}
				else {
					String content = "Someone comments your post. Check it out!";

					Intent custom_intent = new Intent(this, PostViewer.class);
					custom_intent.putExtra(Method.fromNotificationKey, true);
					custom_intent.putExtra(Method.fromNotificationMethodKey, Method.gotComment);
					custom_intent.putExtra(Method.postIdKey, post_id);

					sendNotification(content, custom_intent);
				}
				break;
			}
			case Url.got_chat_message: {
				if(Chat.isActivityVisible()) {
					Intent callbackIntent = new Intent(Method.autoLoadNewMessage);
					LocalBroadcastManager.getInstance(this).sendBroadcast(callbackIntent);
				}
				else {
					int from_user = Integer.parseInt(intent.getStringExtra(Url.fromUserKey));
					String content = intent.getStringExtra("content");

					Intent custom_intent = new Intent(this, PostViewer.class);
					custom_intent.putExtra(Method.fromNotificationKey, true);
					custom_intent.putExtra(Method.fromNotificationMethodKey, Method.gotChat);
					custom_intent.putExtra(Method.fromUserKey, from_user);

					sendNotification(content, custom_intent);
				}
				break;
			}
		}
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg, Intent intent) {
		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent;
		if (intent != null) {
			contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		} else {
			contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, PostViewer.class), 0);
		}

		// sound for notification
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setAutoCancel(true);
		mBuilder.setSmallIcon(R.mipmap.ic_launcher)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentTitle(getString(R.string.app_name))
				.setContentText(msg)
				.setSound(alarmSound);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
