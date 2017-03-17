package com.abc.iitgwebmailnotifier.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Services.LoginManager;
import com.abc.iitgwebmailnotifier.Services.TaskCanceler;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by aarkay0602 on 24/1/17.
 */

public class LoginActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private TextView email,password;
    private Button signInButton;
    private Spinner spinner;
    private String POP3server = "";
    private UserSessionManager session;
    private LinearLayout loginform;
    private LoginManager task;
    private Handler handler = new Handler();

    public LinearLayout getLoginform() {
        return loginform;
    }

    public TextView getEmail() {
        return email;
    }

    public TextView getPassword() {
        return password;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public String getPOP3server() {
        return POP3server;
    }

    public UserSessionManager getSession() {
        return session;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new UserSessionManager(getApplicationContext());

        email = (TextView) findViewById(R.id.webmailId);
        password = (TextView) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.signInButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginform = (LinearLayout) findViewById(R.id.login_form);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Servers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        final Intent intent = new Intent(this, MainActivity.class);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton.setEnabled(false);

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if(email.getText().toString().indexOf("@")>0){
                    email.setText("");
                    signInButton.setEnabled(true);
                    Snackbar.make(v, "Please enter a valid webmail id!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(password.getText().toString().trim().equals("")){
                    password.setText("");
                    signInButton.setEnabled(true);
                    Snackbar.make(v, "Please enter a valid password!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                String username = email.getText().toString();
                String pass = password.getText().toString().trim();
                task = new LoginManager(LoginActivity.this,username,pass,POP3server);

                TaskCanceler taskCanceler = new TaskCanceler(task,LoginActivity.this);
                handler.postDelayed(taskCanceler,12*1000);
                task.execute();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String server = adapterView.getItemAtPosition(i).toString();
        if (server.equals("Teesta")){
            POP3server = "202.141.80.12";
        }else if(server.equals("Naambor")){
            POP3server = "202.141.80.9";
        }else if (server.equals("Disang")){
            POP3server = "202.141.80.10";
        }else if (server.equals("Tamdil")){
            POP3server = "202.141.80.11";
        }else if (server.equals("Dikrong")){
            POP3server = "202.141.80.13";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
