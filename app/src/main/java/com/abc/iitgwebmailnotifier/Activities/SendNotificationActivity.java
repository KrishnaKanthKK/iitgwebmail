package com.abc.iitgwebmailnotifier.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Services.JSONSerializer;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;
import com.abc.iitgwebmailnotifier.Services.asyncCreateFolder;
import com.abc.iitgwebmailnotifier.Services.asyncSendNotification;
import com.abc.iitgwebmailnotifier.Services.asyncSingleDevice;
import com.google.firebase.iid.FirebaseInstanceId;
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
    private EditText message,batch;
    private LinearLayout linear_multi_devices;
    private RadioGroup radioGroup;
    private Button back,send,subscribe,unsubscribe;
    private String department;
    private String username;
    private SharedPreferences preferences,preferences1;
    private SharedPreferences.Editor editor;
    private int MIN_BATCH_NUMBER = 2005;
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
        batch = (EditText) findViewById(R.id.batch);
        subscribe = (Button) findViewById(R.id.subscribe);
        unsubscribe = (Button) findViewById(R.id.unsubscribe);
        linear_multi_devices = (LinearLayout) findViewById(R.id.linear_multidevice);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Log.e("token", FirebaseInstanceId.getInstance().getToken());

        preferences = getSharedPreferences(UserSessionManager.PREFER_NAME, getApplicationContext().MODE_PRIVATE);
        username = preferences.getString(UserSessionManager.KEY_USERNAME, "");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Departments_array, R.layout.spinner_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        From.setText("From : " + username);

        preferences1 = getSharedPreferences("subscription",getApplicationContext().MODE_PRIVATE);
        if (!preferences1.getString("subscribe","").equals("")){
            unsubscribe.setVisibility(View.VISIBLE);
            Log.e("a",preferences1.getString("subscribe",""));
        }else{
            Log.e("b","b");
            unsubscribe.setVisibility(View.GONE);
        }
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences1 = getSharedPreferences("subscription",getApplicationContext().MODE_PRIVATE);
                String subscription = preferences1.getString("subscribe","");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(subscription);
                editor = preferences1.edit();
                editor.clear();
                editor.commit();
                view.setVisibility(View.GONE);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String recipient = webmailId.getText().toString();
                String messageText = message.getText().toString();

                if (department.equals("Single device")){
                    if (!messageText.equals("") && !recipient.equals("")){
                        new asyncSingleDevice(SendNotificationActivity.this,username,recipient,messageText).execute();
                    }

                }else{
                    try {
                        int batch_number = Integer.parseInt(batch.getText().toString());
                        if (batch_number > MIN_BATCH_NUMBER && batch_number < 2017){
                            int radio_id = radioGroup.getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton) findViewById(radio_id);
                            String radioText = radioButton.getText().toString();
                            new asyncSendNotification(SendNotificationActivity.this,username,messageText,department+batch_number+radioText).execute();
                        }else {
                            Toast.makeText(getApplicationContext(), "Only batch numbers between 2004 to 2017 are allowed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }


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
                final View promptsView = li.inflate(R.layout.subscribe_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendNotificationActivity.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final Spinner spinner1 = (Spinner) promptsView.findViewById(R.id.spinner1);
                final EditText batch = (EditText) promptsView.findViewById(R.id.batch);
                final RadioGroup radioGroup = (RadioGroup) promptsView.findViewById(R.id.radioGroup);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.Subscribe_array, R.layout.spinner_item);
                spinner1.setAdapter(adapter);
                spinner1.setOnItemSelectedListener(new OnSpinnerItemClicked());

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Subscribe to")
                        .setPositiveButton("subscribe",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int batch_number = Integer.parseInt(batch.getText().toString());
                                        int radio_id = radioGroup.getCheckedRadioButtonId();
                                        RadioButton radioButton = (RadioButton) promptsView.findViewById(radio_id);
                                        String radioText = radioButton.getText().toString();

                                        if (batch_number > MIN_BATCH_NUMBER && batch_number < 2017){
                                            preferences1 = getSharedPreferences("subscription",getApplicationContext().MODE_PRIVATE);
                                            if (preferences1.getString("subscribe","").equals("")){
                                                FirebaseMessaging.getInstance().subscribeToTopic(department+batch_number+radioText);
                                            }else {
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(preferences1.getString("subscribe",""));
                                                FirebaseMessaging.getInstance().subscribeToTopic(department+batch_number+radioText);
                                            }
                                            editor = preferences1.edit();
                                            editor.putString("subscribe",department+batch_number+radioText);
                                            editor.commit();
                                            Log.e("sub",department+batch_number+radioText);

                                        }else {
                                            {
                                                Toast.makeText(getApplicationContext(), "Only batch numbers between 2004-2017 are allowed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
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
            batch.requestFocus();
            linear_multi_devices.setVisibility(View.VISIBLE);

        }else {
            webmailId.setVisibility(View.VISIBLE);
            linear_multi_devices.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public class OnSpinnerItemClicked implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView,
                                   View view, int pos, long id) {
            department = adapterView.getItemAtPosition(pos).toString();
            Log.e("spinner", department);


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


}
