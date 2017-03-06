package com.abc.iitgwebmailnotifier.Firebase;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by aarkay0602 on 26/1/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences test = getSharedPreferences("MyPrefsFile",0);
        SharedPreferences.Editor editor = test.edit();
        editor.putString("testing",remoteMessage.getData().toString());
        editor.commit();
        Log.e("messageData",remoteMessage.getData().toString());

    }
}
