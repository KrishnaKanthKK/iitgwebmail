package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;

/**
 * Created by aarkay0602 on 12/3/17.
 */


public class TaskCanceler implements Runnable{
    private AsyncTask task;
    private MainActivity activity;

    public TaskCanceler(AsyncTask task, MainActivity activity) {
        this.activity = activity;
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING ){
            task.cancel(true);
            activity.getProgressBar().setVisibility(View.GONE);
            activity.getErrorText().setVisibility(View.VISIBLE);
            activity.getSwipeRefreshLayout().setEnabled(true);
            activity.getSwipeRefreshLayout().setRefreshing(false);
        }

    }
}