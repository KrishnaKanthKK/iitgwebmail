package com.abc.iitgwebmailnotifier.Activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Services.POP3ssl;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;
import com.abc.iitgwebmailnotifier.Services.asyncLoadBody;
import com.abc.iitgwebmailnotifier.loadRecentMails;
import com.abc.iitgwebmailnotifier.models.Email;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class EmailBodyAcitivity extends AppCompatActivity {
    private TextView subject,from,recipients,time,body;

    private String content = "";

    private String bodyPart;

    private SharedPreferences sharedPreferences;

    private String username;

    private String password;

    private String server;

    private String folder;

    private ProgressBar progressBar;

    private WebView webView;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public WebView getWebView() {
        return webView;
    }

    public TextView getBody() {
        return body;
    }

    public TextView getRecipients() {
        return recipients;
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_body);
        sharedPreferences = getSharedPreferences(UserSessionManager.PREFER_NAME,getApplicationContext().MODE_PRIVATE);
        username = sharedPreferences.getString(UserSessionManager.KEY_USERNAME,"");
        password = sharedPreferences.getString(UserSessionManager.KEY_PASSWORD,"");
        server = sharedPreferences.getString(UserSessionManager.KEY_SERVER,"");

        subject = (TextView) findViewById(R.id.subject_in_body_activity);
        from = (TextView) findViewById(R.id.frm_in_body_activity);
        recipients = (TextView) findViewById(R.id.recipients);
        time = (TextView) findViewById(R.id.time);
        body = (TextView) findViewById(R.id.body);
        webView = (WebView) findViewById(R.id.webview);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_body);
        progressBar.setVisibility(View.VISIBLE);
        body.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.afop_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Email email = getIntent().getParcelableExtra("email");
        subject.setText(email.getSubject());
        from.setText(email.getFrom());
        time.setText(email.getSentDate().toString());
        folder = email.getFromFolder();
     /*   asyncLoadBody asyncTask = (asyncLoadBody) new asyncLoadBody(new asyncLoadBody.AsyncResponse(){
            @Override
            public void processFinish(String output) {
                bodyPart = output;
            }
        },EmailBodyAcitivity.this,username,password,server,email.getUID(),folder).execute();*/
        new asyncLoadBody(EmailBodyAcitivity.this,username,password,server,email.getUID(),folder).execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
