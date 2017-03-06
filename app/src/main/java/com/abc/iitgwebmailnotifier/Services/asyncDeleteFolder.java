package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.loadRecentMails;

/**
 * Created by aarkay0602 on 22/2/17.
 */


public class asyncDeleteFolder extends AsyncTask<Object, Object, String> {
    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    private String FolderName;


    public asyncDeleteFolder(MainActivity activity, String user, String pass, String server, String FolderName){
        this.activity = activity;
        this.username = user;
        this.password = pass;
        this.server = server;
        this.FolderName = FolderName;
    }


    protected void onPreExecute() {
        Toast.makeText(activity.getApplicationContext(), "Deleting Folder",
                Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        String response = pop3ssl.deleteFolder(username,password,server,FolderName);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        Log.e("resp",response);
        return response;
    }

    protected void onPostExecute(String response) {
        if (response == "OK"){
            Toast.makeText(activity.getApplicationContext(), "Deleted Successfully",
                    Toast.LENGTH_SHORT).show();
            activity.setTitle("Inbox");
            MainActivity.mailSet = 1;
            new loadRecentMails(activity,username,password
                    ,server,"INBOX",MainActivity.mailSet,"folderdelete").execute();
        }else{
            Toast.makeText(activity.getApplicationContext(), "Error Deleting Folder",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
