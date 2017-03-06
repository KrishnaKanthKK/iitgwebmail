package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.Adapters.RecyclerAdapter;
import com.abc.iitgwebmailnotifier.models.Email;

import java.util.List;

/**
 * Created by aarkay0602 on 18/2/17.
 */


public class asyncMoveMails extends AsyncTask<Object, Object, String> {

    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    private List<Email> emails;

    private String FromFolder;

    private String ToFolder;


    public asyncMoveMails(MainActivity activity, String user, String pass, String server, List<Email> emailsList, String From,String To){
        this.activity = activity;
        this.username = user;
        this.password = pass;
        this.server = server;
        this.emails = emailsList;
        this.FromFolder = From;
        this.ToFolder = To;
    }


    protected void onPreExecute() {
        Toast.makeText(activity.getApplicationContext(), "Moving",
                Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        String response = pop3ssl.moveEmails(username,password,server,emails,FromFolder,ToFolder);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return response;
    }

    protected void onPostExecute(String response) {
        if (response == "OK"){
            Toast.makeText(activity.getApplicationContext(), "Moved Successfully",
                    Toast.LENGTH_SHORT).show();
            removeEmails(RecyclerAdapter.checkedEmails);
            RecyclerAdapter.checkedEmails.clear();
        }else{
            Toast.makeText(activity.getApplicationContext(), "Error Moving",
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

