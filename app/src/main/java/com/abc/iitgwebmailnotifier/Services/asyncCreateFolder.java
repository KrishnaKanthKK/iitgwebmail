package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.Adapters.RecyclerAdapter;
import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.models.Email;

import java.util.List;

/**
 * Created by aarkay0602 on 18/2/17.
 */

public class asyncCreateFolder extends AsyncTask<Object, Object, String> {
    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    private String FolderName;


    public asyncCreateFolder(MainActivity activity, String user, String pass, String server, String FolderName){
        this.activity = activity;
        this.username = user;
        this.password = pass;
        this.server = server;
        this.FolderName = FolderName;
    }


    protected void onPreExecute() {
        Toast.makeText(activity.getApplicationContext(), "Creating Folder",
                Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        String response = pop3ssl.createFolder(username,password,server,FolderName);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return response;
    }

    protected void onPostExecute(String response) {
        if (response == "OK"){
            Toast.makeText(activity.getApplicationContext(), "Created Successfully",
                    Toast.LENGTH_SHORT).show();
            activity.getSubMenu().add(FolderName).setIcon(R.drawable.ic_folder_white_24dp);
        }else{
            Toast.makeText(activity.getApplicationContext(), "Error Creating Folder",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
