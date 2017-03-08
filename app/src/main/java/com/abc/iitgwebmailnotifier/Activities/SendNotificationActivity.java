package com.abc.iitgwebmailnotifier.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Services.JSONSerializer;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;
import com.abc.iitgwebmailnotifier.Services.asyncCreateFolder;
import com.abc.iitgwebmailnotifier.Services.asyncSendNotification;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarkay0602 on 8/3/17.
 */

public class SendNotificationActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private TextView From;
    private EditText webmailId;
    private EditText message;
    private Button back,send,subscribe;
    private String department;
    private String username;
    private SharedPreferences preferences;
    public static String SERVER_KEY = "AAAAiT3f4Mc:APA91bHRlNGXiNaqS22TXsGq4QLl1NrnNg0paDcESsAm_WGjgDWnm1xtc7SUEnus1agh-oksg8ozok-OwemtFEIdTWG7j1HyJdiBnr7gFMc5Mq1BGYppEOh5_lDzoXi4F_SwyuAaPN3N";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        spinner = (Spinner) findViewById(R.id.spinner);
        webmailId = (EditText) findViewById(R.id.webmailId);
        From = (TextView) findViewById(R.id.from);
        back = (Button) findViewById(R.id.back);
        send = (Button) findViewById(R.id.send);
        message = (EditText) findViewById(R.id.message);
        subscribe = (Button) findViewById(R.id.subscribe);

        preferences = getSharedPreferences(UserSessionManager.PREFER_NAME, getApplicationContext().MODE_PRIVATE);
        username = preferences.getString(UserSessionManager.KEY_USERNAME, "");

        FirebaseMessaging.getInstance().unsubscribeFromTopic("ECE");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Departments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        From.setText("From : " + username);


        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString();

                if (department.equals("Single device")){

                }else{
                    new asyncSendNotification(SendNotificationActivity.this,username,messageText,department).execute();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.create_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendNotificationActivity.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Subscribe to")
                        .setPositiveButton("subscribe",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        department = adapterView.getItemAtPosition(i).toString();
        if (!department.equals("Single device")){
            webmailId.setVisibility(View.GONE);
        }else {
            webmailId.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
