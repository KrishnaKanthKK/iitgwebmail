package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;

/**
 * Created by aarkay0602 on 12/3/17.
 */


public class TaskCanceler implements Runnable{
    private AsyncTask task;

    public TaskCanceler(AsyncTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING )
            task.cancel(true);
    }
}