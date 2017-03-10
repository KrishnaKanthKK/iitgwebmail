package com.abc.iitgwebmailnotifier.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.abc.iitgwebmailnotifier.Activities.LoginActivity;
import com.abc.iitgwebmailnotifier.models.User;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by aarkay0602 on 13/2/17.
 */


public class UserSessionManager {

    SharedPreferences preferences;

    SharedPreferences.Editor editor;

    Context context;

    String token = FirebaseInstanceId.getInstance().getToken();
    String username;

    public static final String PREFER_NAME = "SharedPreferences";

    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_USERNAME = "RestaurantId";

    public static final String KEY_PASSWORD = "RestaurantName";

    public static final String KEY_SERVER = "AuthenticationToken";

    public UserSessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        username = getUserDetails(context).get(KEY_USERNAME);
    }

    public void createUserLoginSession(User user){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_SERVER, user.getServer());
        editor.commit();
    }

    public boolean checkLogin(){

        if(!this.isUserLoggedIn()){
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            new asyncSendToken(username,token,"false").execute();

            return true;
        }

        new asyncSendToken(username,token,"true").execute();

        return false;
    }

    public boolean isUserLoggedIn(){
        return preferences.getBoolean(IS_USER_LOGIN, false);
    }

    public HashMap<String, String> getUserDetails(Context context){
        HashMap<String, String> user = new HashMap<>();
        preferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        user.put(KEY_USERNAME, preferences.getString(KEY_USERNAME, null));
        user.put(KEY_PASSWORD, preferences.getString(KEY_PASSWORD, null));
        user.put(KEY_SERVER, preferences.getString(KEY_SERVER, null));
        return user;
    }

    public void logoutUser(){

        new asyncSendToken(username,token,"false").execute();

        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

//        FirebaseRegisterAPIClient.getInstance().registerToken(credentials,false);

    }


}
