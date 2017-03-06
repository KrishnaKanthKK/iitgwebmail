package com.abc.iitgwebmailnotifier.Services;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.abc.iitgwebmailnotifier.Activities.LoginActivity;
import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.models.User;

/**
 * Created by aarkay0602 on 13/2/17.
 */

public class LoginManager extends AsyncTask<Void, Void, String> {
    private LoginActivity activity;

    private String username;

    private String password;

    private String server;



    public LoginManager(LoginActivity activity, String username,String password,String server){
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.server = server;
    }


    protected void onPreExecute() {

        activity.getProgressBar().setVisibility(View.VISIBLE);
        activity.getSignInButton().setVisibility(View.INVISIBLE);

    }

    protected String doInBackground(Void... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        String response = pop3ssl.loginuser(username,password,server,"imap",true);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        Log.e("resp",response);
        return response;
    }

    protected void onPostExecute(String response) {

        activity.getProgressBar().setVisibility(View.GONE);
        activity.getSignInButton().setVisibility(View.VISIBLE);

        if(response.equals("FAILED")){
            activity.getSignInButton().setEnabled(true);
            Snackbar.make(activity.getLoginform(), "Error Signing in! Please try again.", Snackbar.LENGTH_LONG).show();
            return;
        }else if (response.equals("OK")){
            Log.e("user",username);
            JSONSerializer jsonSerializer = new JSONSerializer();
            User user = jsonSerializer.createUserObject(username,password,server);
            Log.e("user",username);
            activity.getSession().createUserLoginSession(user);

            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("email",username);
            intent.putExtra("password",password);
            intent.putExtra("callerType","login");
            activity.startActivity(intent);
            activity.finish();
        }else if (response.equals("Refused")){
            activity.getSignInButton().setEnabled(true);
            Snackbar.make(activity.getLoginform(), "Error Signing in! Connect to Local Network & try again.", Snackbar.LENGTH_LONG).show();
        }
    }
}
