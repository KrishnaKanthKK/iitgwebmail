package com.abc.iitgwebmailnotifier;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.Adapters.RecyclerAdapter;
import com.abc.iitgwebmailnotifier.Services.POP3ssl;
import com.abc.iitgwebmailnotifier.Services.asyncDeleteFolder;
import com.abc.iitgwebmailnotifier.Services.asyncGetFolders;
import com.abc.iitgwebmailnotifier.models.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarkay0602 on 14/2/17.
 */

public class loadRecentMails extends AsyncTask<Object, Object, List<Email>> {

    private MainActivity activity;

    private String username;

    private String password;

    private String server;

    private String folderName;

    private String callerType;

    private int mailset;

    public interface AsyncResponse {
        void processFinish(List<Email> output);
    }

    public AsyncResponse delegate = null;


    public loadRecentMails(MainActivity activity, String username,String password
            ,String server,String folderName,int mailset,String callerType){
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.server = server;
        this.folderName = folderName;
        this.callerType = callerType;
        this.mailset = mailset;
    }


    protected void onPreExecute() {
        if (!callerType.equals("pulldown")){
            activity.getProgressBar().setVisibility(View.VISIBLE);
            activity.getmRecyclerView().setVisibility(View.GONE);
            activity.getSwipeRefreshLayout().setEnabled(false);
        }
        Log.e("pre","pre");
    }

    protected List<Email> doInBackground(Object... urls) {
        Log.e("back","back");
        List<Email> response =new POP3ssl().getEmails(username,password,server,50,"END",folderName,mailset);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return response;
    }

    protected void onPostExecute(List<Email> response) {
        if (!callerType.equals("pulldown")){
            activity.getProgressBar().setVisibility(View.GONE);
            activity.getmRecyclerView().setVisibility(View.VISIBLE);
            activity.getSwipeRefreshLayout().setEnabled(true);
        }
        activity.getSwipeRefreshLayout().setRefreshing(false);
        RecyclerAdapter recyclerAdapter;
        RecyclerView recyclerView;
        SubMenu subMenu;
        subMenu = activity.getSubMenu();
        recyclerAdapter = activity.getmRecyclerAdapter();
        recyclerView = activity.getmRecyclerView();
        recyclerAdapter = new RecyclerAdapter(activity.getApplicationContext(),activity.getActiveFolder(),response);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        activity.setmRecyclerView(recyclerView);
        activity.setmRecyclerAdapter(recyclerAdapter);
        try {
            if (mailset*50<response.get(0).getTotalMails()){
                activity.getMailFilter().setText(String.valueOf(1+50*(mailset-1))+"-"+
                        String.valueOf(50*mailset)+" of "+response.get(0).getTotalMails());
            }else {
                activity.getMailFilter().setText(String.valueOf(1+50*(mailset-1))+"-"+
                        response.get(0).getTotalMails()+" of "+response.get(0).getTotalMails());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (callerType.equals("pulldown")){
            if (activity.getSubMenu().size() == 3){
                Log.e("size", String.valueOf(activity.getSubMenu().size()));
                POP3ssl pop3ssl = new POP3ssl();
                for (final String name : pop3ssl.getFolderNames(username, password, server)) {
                    if (!(name.equals("Sent") || name.equals("INBOX"))) {
                        subMenu.add(name).setIcon(R.drawable.ic_folder_white_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                MainActivity.mailSet =1;
                                new loadRecentMails(activity, username, password
                                        ,server, name,MainActivity.mailSet,"navigation").execute();
                                activity.setActiveFolder(name);
                                activity.setTitle(name);
                                return false;
                            }
                        });
                    }
                }
            }
        }else if (callerType.equals("oncreate")){
            new asyncGetFolders(activity,username,password,server).execute();
        }

    }
}
