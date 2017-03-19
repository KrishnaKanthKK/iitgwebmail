package com.abc.iitgwebmailnotifier.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by aarkay0602 on 26/1/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String GROUP_KEY_EMAILS = "group_key_emails";
    int activeNotifications ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("resp1",remoteMessage.getData().toString());

        if (remoteMessage.getData().containsKey("message")){
            Log.e("resp",remoteMessage.getData().toString());
            String title ="From : "+ remoteMessage.getData().get("sender")+"   To : " +remoteMessage.getData().get("recipients");
            String message = remoteMessage.getData().get("message");
            if (Build.VERSION.SDK_INT >= 24){
                sendNotificationForHIGHERVERSIONS(title,message);
            }else {
                sendNotification(title,message);
            }
        }else{
            String title =remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            if (Build.VERSION.SDK_INT >= 24){
                sendNotificationForHIGHERVERSIONS(title,message);
            }else {
                sendNotification(title,message);
            }

        }

    }


    private void sendNotification(String title, String message){
        NotificationCompat.Builder builder = createNotificationBuider(this, title, message);
        showNotification(this,builder.build(),(int) System.currentTimeMillis());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotificationForHIGHERVERSIONS(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.activeNotifications = manager.getActiveNotifications().length;

        if (this.activeNotifications == 0){
            NotificationCompat.Builder first = createNotificationBuider(this,title,message);

            showNotification(this, first.build(), 1);

        }else {
            NotificationCompat.Builder sumNotif = createNotificationBuider(this, "test", "test");
            sumNotif.setGroupSummary(true);
            showNotification(this, sumNotif.build(), 1000);
            NotificationCompat.Builder a = createNotificationBuider(this, title, message);
            showNotification(this,a.build(), (int) System.currentTimeMillis());
        }

    }

    public NotificationCompat.Builder createNotificationBuider(Context context,
                                                               String title, String message) {
        Intent i = new Intent(context,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setSound(defaultSoundUri)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message).setBigContentTitle(title))

                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.black))
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_EMAILS);
    }
    private void showNotification(Context context, Notification notification, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

}

