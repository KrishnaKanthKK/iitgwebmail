package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.abc.iitgwebmailnotifier.Activities.EmailBodyAcitivity;
import com.abc.iitgwebmailnotifier.models.Body;

/**
 * Created by aarkay0602 on 16/2/17.
 */


public class asyncLoadBody extends AsyncTask<Object, Object, Body> {
    private EmailBodyAcitivity activity;

    private String username;

    private String password;

    private String server;

    private WebView webView;

    private long UID;

    private String folder;


    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;


    public asyncLoadBody(AsyncResponse asyncresponse, EmailBodyAcitivity activity, String username, String password, String server,long UID,String folder){
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.server = server;
        this.UID = UID;
        this.folder = folder;
        this.delegate = asyncresponse;
    }
    public asyncLoadBody(EmailBodyAcitivity activity, String username, String password, String server,long UID,String folder){
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.server = server;
        this.UID = UID;
        this.folder = folder;
    }



    protected void onPreExecute() {

        activity.getProgressBar().setVisibility(View.VISIBLE);
        activity.getBody().setVisibility(View.GONE);
    }

    protected Body doInBackground(Object... urls) {
        POP3ssl pop3ssl = new POP3ssl();
        Body response = pop3ssl.loadBody(username,password,server,UID,folder);
        //FirebaseRegisterAPIClient.getInstance().registerToken(credentials,true);
        return response;
    }

    protected void onPostExecute(Body response) {
        webView = activity.getWebView();
        activity.getProgressBar().setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        //delegate.processFinish(response);
        activity.getRecipients().setText("to: "+response.getRecipients());
        TextView body = activity.getBody();
        String bodyPart = response.getContent();
        if (response.isHtml()){
            body.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadData(bodyPart, "text/html", null);
            Log.e("web","web");
            /*    body.setAutoLinkMask(0);
            body.setMovementMethod(LinkMovementMethod.getInstance());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                body.setText(Html.fromHtml(bodyPart,Html.FROM_HTML_MODE_COMPACT));
            }else {
                body.setText(Html.fromHtml(bodyPart));
            }
        */}else{
            webView.setVisibility(View.GONE);
            Log.e("plain","plain");
            body.setVisibility(View.VISIBLE);
            body.setText(bodyPart);
        }
    }
    private boolean isHtml(String bodyPart){
        boolean b = false;
        String[] tags ={"<html>","<br","<!DOCTYPE","<head>","</span>","</p>","</div>"};
        for (String s : tags){
            if (bodyPart.contains(s)){
                b = true;
                break;
            }
        }

        return b;
    }


}
