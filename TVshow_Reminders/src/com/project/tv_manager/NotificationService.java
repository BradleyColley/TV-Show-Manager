package com.project.tv_manager;

/*
 * This class acts as constructing the notifications by retrieving 
 * the required data of title and body text from AddReminders class.
 * 
 */
import com.project.tvshow_reminders.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class NotificationService extends Service {
	public static MediaPlayer mp;
	public static NotificationManager notificationManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/*
	 * This method starts the service once the activity loads which 
	 * will build the notification for the alarm reminder.
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		String title = intent.getStringExtra("title"); //Retrieves the title from the intent.
		String text = intent.getStringExtra("message"); //Retrieves the body text from the intent.

		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		//Sets up a new pending intent to launch the CurrentReminder activity once the notification gets selected.
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, CurrentReminder.class), 0);
				
		//Constructs the notifications.
		Notification nBuilder = new Notification.Builder(this)
			.setContentTitle(title) //Sets title
	        .setContentText(text) //Sets body text
	        .setSmallIcon(R.drawable.ic_launcher) //Sets notification icon
	        .setContentIntent(pIntent) //Sets the intent action to start a new activity.
	        .setAutoCancel(true).build(); //Auto cancels the notification once pressed and builds it.
	
		notificationManager.notify(123, nBuilder); //Issues the notification passing the object through containing an ID.

		//Sets up the ring tune for the alarm notification.
		Uri tune = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		mp = MediaPlayer.create(getApplicationContext(), tune);
		mp.start();
		stopSelf();
	}
}