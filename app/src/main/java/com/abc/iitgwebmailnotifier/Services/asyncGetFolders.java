package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.view.SubMenu;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.R;

import java.util.List;

/**
 * Created by aarkay0602 on 6/3/17.
 */

public class asyncGetFolders extends AsyncTask<Object, Object, List<String>> {
    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    public asyncGetFolders(MainActivity activity, String user, String pass, String server){
        this.activity = activity;
        this.username = user;
        this.password = pass;
        this.server = server;
    }


    protected void onPreExecute() {

    }

    protected List<String> doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        List<String> folderNames = pop3ssl.getFolderNames(username, password, server);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return folderNames;
    }

    protected void onPostExecute(List<String> response) {
        if (!response.isEmpty()){
            SubMenu subMenu = activity.getSubMenu();
            subMenu.clear();
            activity.populateNavigationFolderItems(subMenu);
            activity.populateNavigationFolderItemsExtra(response,subMenu);
        }else{
            Toast.makeText(activity.getApplicationContext(), "Error in Fetching Folders",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
