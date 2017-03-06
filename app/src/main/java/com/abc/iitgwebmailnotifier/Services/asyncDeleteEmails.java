package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.EmailBodyAcitivity;
import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.Adapters.RecyclerAdapter;
import com.abc.iitgwebmailnotifier.models.Email;

import java.util.List;

/**
 * Created by aarkay0602 on 18/2/17.
 */


public class asyncDeleteEmails extends AsyncTask<Object, Object, String> {
    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    private List<Email> emails;

    private String folder;


    public asyncDeleteEmails(MainActivity activity, String user, String pass, String server, List<Email> emailsList,String folder){
        this.activity = activity;
        this.username = user;
        this.password = pass;
        this.server = server;
        this.emails = emailsList;
        this.folder = folder;
     }


    protected void onPreExecute() {
        Toast.makeText(activity.getApplicationContext(), "Deleting",
                Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        Log.e("email1", String.valueOf(emails.size()));
        String response = pop3ssl.deleteEmails(username,password,server,emails,folder);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return response;
    }

    protected void onPostExecute(String response) {
        if (response == "OK"){
            Toast.makeText(activity.getApplicationContext(), "Deleted Successfully",
                    Toast.LENGTH_SHORT).show();
            removeEmails(RecyclerAdapter.checkedEmails);
            RecyclerAdapter.checkedEmails.clear();
        }else{
            Toast.makeText(activity.getApplicationContext(), "Error Deleting",
                    Toast.LENGTH_SHORT).show();
        }
    }



    public void removeEmails(List<Email> emails) {
        for (Email e : emails){
            RecyclerAdapter.emails.remove(e);
            activity.getmRecyclerAdapter().notifyItemRemoved(e.getPosition());
            activity.getmRecyclerAdapter().notifyItemRangeChanged(e.getPosition(), RecyclerAdapter.emails.size());
        }
        Log.e("email", String.valueOf(emails.size()));


    }
}
