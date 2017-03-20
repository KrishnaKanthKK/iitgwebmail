package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.abc.iitgwebmailnotifier.Activities.LoginActivity;
import com.abc.iitgwebmailnotifier.Activities.MainActivity;

/**
 * Created by aarkay0602 on 12/3/17.
 */


public class TaskCanceler implements Runnable{
    private AsyncTask task;
    private Object activity;

    public TaskCanceler(AsyncTask task, Object activity) {
        this.activity = activity;
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING && activity instanceof MainActivity){
            task.cancel(true);
            ((MainActivity)activity).getProgressBar().setVisibility(View.GONE);
            ((MainActivity)activity).getErrorText().setVisibility(View.VISIBLE);
            ((MainActivity)activity).getmRecyclerView().setVisibility(View.GONE);
            ((MainActivity)activity).getSwipeRefreshLayout().setEnabled(true);
            ((MainActivity)activity).getSwipeRefreshLayout().setRefreshing(false);
        }else if (task.getStatus() == AsyncTask.Status.RUNNING && activity instanceof LoginActivity){
            task.cancel(true);
            ((LoginActivity)activity).getProgressBar().setVisibility(View.GONE);
            ((LoginActivity)activity).getSignInButton().setVisibility(View.VISIBLE);
            ((LoginActivity)activity).getSignInButton().setEnabled(true);
        }


    }
}