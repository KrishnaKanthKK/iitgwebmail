package com.abc.iitgwebmailnotifier.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.BuildConfig;
import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Adapters.RecyclerAdapter;
import com.abc.iitgwebmailnotifier.Services.POP3ssl;
import com.abc.iitgwebmailnotifier.Services.TaskCanceler;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;
import com.abc.iitgwebmailnotifier.Services.asyncCopyMails;
import com.abc.iitgwebmailnotifier.Services.asyncCreateFolder;
import com.abc.iitgwebmailnotifier.Services.asyncDeleteEmails;
import com.abc.iitgwebmailnotifier.Services.asyncDeleteFolder;
import com.abc.iitgwebmailnotifier.Services.asyncGetFolders;
import com.abc.iitgwebmailnotifier.Services.asyncMoveMails;
import com.abc.iitgwebmailnotifier.loadRecentMails;
import com.abc.iitgwebmailnotifier.models.Email;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.R.attr.id;
import static android.R.attr.subMenuArrow;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Handler handler = new Handler();
    private TaskCanceler taskCanceler;


    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String callerType = "null";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private UserSessionManager mSession;
    private ProgressBar progressBar,progressTitle;
    private SharedPreferences preferences;
    private String username;
    private String password;
    private String server;
    private String spinnerFolderName;
    private String activeFolder = "INBOX";
    private TextView navHeaderUsername;
    private SubMenu subMenu;
    private Spinner spinner1, spinner2, spinner3, spinner4;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;
    private Button switchButton,lessButton,greaterButton;
    private TextView mailFilter;
    private LinearLayout linearFilter;
    public loadRecentMails task;
    public static int mailSet = 1;
    public List<String> folderNames = new ArrayList<>();
    public TextView ErrorText;


    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void setmRecyclerAdapter(RecyclerAdapter mRecyclerAdapter) {
        this.mRecyclerAdapter = mRecyclerAdapter;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public void setActiveFolder(String activeFolder) {
        this.activeFolder = activeFolder;
    }

    public void setSubMenu(SubMenu subMenu) {
        this.subMenu = subMenu;
    }

    public RecyclerAdapter getmRecyclerAdapter() {
        return mRecyclerAdapter;
    }

    public Button getSwitchButton() {
        return switchButton;
    }

    public TextView getMailFilter() {
        return mailFilter;
    }

    public String getActiveFolder() {
        return activeFolder;
    }

    public SubMenu getSubMenu() {
        return subMenu;
    }

    public TextView getErrorText() {
        return ErrorText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        swipeRefreshLayout =(SwipeRefreshLayout) findViewById(R.id.pulldownswipe);
        preferences = getSharedPreferences(UserSessionManager.PREFER_NAME, getApplicationContext().MODE_PRIVATE);
        username = preferences.getString(UserSessionManager.KEY_USERNAME, "");
        password = preferences.getString(UserSessionManager.KEY_PASSWORD, "");
        server = preferences.getString(UserSessionManager.KEY_SERVER, "");

        mSession = new UserSessionManager(getApplicationContext(),username);
        if (mSession.checkLogin()) {
            finish();
            return;
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_inbox);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_recycler);
        progressBar.setVisibility(View.GONE);
        webView = (WebView) findViewById(R.id.webview_main);
        switchButton = (Button) findViewById(R.id.switchButton);
        lessButton =(Button) findViewById(R.id.button_lessthan);
        greaterButton = (Button) findViewById(R.id.button_greaterthan);
        mailFilter =(TextView) findViewById(R.id.textview_mail_filter);
        linearFilter = (LinearLayout) findViewById(R.id.linear_filter);
        ErrorText = (TextView) findViewById(R.id.error_text);

        MainActivity.this.setTitle("Inbox");

        if (!mobileData(getApplicationContext())){
            task = new loadRecentMails(MainActivity.this, username, password, server,
                    activeFolder,mailSet,"oncreate");
            taskCanceler = new TaskCanceler(task,MainActivity.this);
            handler.postDelayed(taskCanceler, 15*1000);
            task.execute();
        }else{
            getmRecyclerView().setVisibility(View.GONE);
            getErrorText().setVisibility(View.VISIBLE);
        }

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getSwipeRefreshLayout().setRefreshing(false);
                    task.cancel(true);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (webView.getVisibility()==View.VISIBLE){
                    getErrorText().setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    webView.stopLoading();
                    webView.clearHistory();
                    getSwipeRefreshLayout().setEnabled(true);
                    MainActivity.this.setTitle(activeFolder);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    switchButton.setText("Switch to Webview");
                }else {
                    getErrorText().setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    MainActivity.this.setTitle("Webview");
                    populateWebView();
                    switchButton.setText("Switch to Normal");
                }
            }
        });
        lessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mobileData(getApplicationContext())){
                    if (mailSet>1){
                        mailSet--;
                        task = new loadRecentMails(MainActivity.this, username, password, server,
                                activeFolder,mailSet,"lessbutton");
                        task.execute();
                    }
                }

            }
        });
        greaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("mailset", String.valueOf(mailSet));
                if (!mobileData(getApplicationContext())){
                    try {
                        if (mailSet < (float) RecyclerAdapter.emails.get(0).getTotalMails()/50){
                            mailSet++;
                            task = new loadRecentMails(MainActivity.this, username, password, server,
                                    activeFolder,mailSet,"greaterbutton");
                            task.execute();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }
        });
        try {
            SharedPreferences data = getSharedPreferences("MyPrefsFile", 0);
            Log.e("main", data.getString("testing", "novalue"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else if (user == null && !callerType.equals("login")){
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out"+callerType);
                }
                // ...
            }
        };

        Bundle b = getIntent().getExtras();
        try {
            createAccount((String) b.get("email"),"password");
            callerType = (String) b.get("callerType");

        }catch (Exception e){
            e.printStackTrace();
        }
*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        navHeaderUsername = (TextView) hView.findViewById(R.id.nav_header_textView);
        navHeaderUsername.setText(username + "@iitg.ernet.in");
        Menu m = navigationView.getMenu();
        subMenu = m.addSubMenu("Folders");

        navigationView.setNavigationItemSelectedListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mobileData(getApplicationContext())){
                    mailSet = 1;
                    task = new loadRecentMails(MainActivity.this, username, password, server,
                            activeFolder,mailSet,"pulldown");
                    taskCanceler = new TaskCanceler(task,MainActivity.this);
                    handler.postDelayed(taskCanceler, 15*1000);
                    task.execute();
                }else {
                    getErrorText().setVisibility(View.VISIBLE);
                    getmRecyclerView().setVisibility(View.GONE);
                    getSwipeRefreshLayout().setRefreshing(false);
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(),R.color.colorTheme));
        }else{
            swipeRefreshLayout.setColorSchemeColors(getApplicationContext().getResources().getColor(R.color.colorTheme));
        }




/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                new loadRecentMails(MainActivity.this, username, password, server,
                        activeFolder,mailSet,"oncreate").execute();
                Log.e("dsa","adsjkads");

            }
        }).start();
*/
        //checkForUpdate();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!(activeFolder.equals("INBOX")||activeFolder.equals("Sent"))){
            menu.findItem(R.id.deleteFolder).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    new asyncDeleteFolder(MainActivity.this,username,password,server,activeFolder).execute();
                    return false;
                }
            });
        }else{
            menu.findItem(R.id.deleteFolder).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.remote_config_defaults.
        int id = item.getItemId();
        Log.e("item","item");
        //noinspection SimplifiableIfStatement
        if (id == R.id.move || id == R.id.copy) {
            if (RecyclerAdapter.checkedEmails.size() >= 1){
                try {
                    populateAlert(id);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                Toast.makeText(getApplicationContext(), "No messages were selected",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.delete) {
            if (RecyclerAdapter.checkedEmails.size() >= 1){
                new asyncDeleteEmails(MainActivity.this, username, password, server, RecyclerAdapter.checkedEmails, activeFolder).execute();
                Log.e("size", String.valueOf(RecyclerAdapter.checkedEmails.size()));
            }else {
                Toast.makeText(getApplicationContext(), "No messages were selected",
                        Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            callerType = "logout";
            signOut();
        } else if (id == R.id.filter) {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptsView = li.inflate(R.layout.filter_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Light_DialogWhenLarge);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            
            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);
            // set dialog message
            alertDialogBuilder
                    .setPositiveButton("Create",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String filtername = userInput.getText().toString();
                                    if (!filtername.equals("")) {
                                        Log.e("filter", filtername);
                                    }
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

        }else if (id == R.id.notification){
            try {

                boolean flag = task.cancel(true);
                Log.e("flag", String.valueOf(flag));
                getProgressBar().setVisibility(View.GONE);
                getSwipeRefreshLayout().setRefreshing(false);
                getSwipeRefreshLayout().setEnabled(true);


            }catch (Exception e){
                e.printStackTrace();
            }
            Intent i = new Intent(MainActivity.this,SendNotificationActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/


    private void createAccount(final String email, final String password) {
        Log.e(TAG, "createAccount:" + email);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("success", "success");
                            //User registered successfully
                        } else if (task.getException().getMessage() == "The email address is already in use by another account.") {
                            Log.e("Response", String.valueOf(task.getException()));
                            signIn(email, password);

                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.e(TAG, "signIn:" + email);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "signInWithEmail:failed", task.getException());
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        //mAuth.signOut();
        mSession.logoutUser();
        Log.e("logout", "success");
    }

    private void checkForUpdate() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 3600;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                            String test = mFirebaseRemoteConfig.getString("test");
                            Log.e("test", test);
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }


    public class OnSpinnerItemClicked implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView,
                                   View view, int pos, long id) {
            spinnerFolderName = adapterView.getItemAtPosition(pos).toString();
            Log.e("spinner", spinnerFolderName);


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    public void populateNavigationFolderItems(SubMenu s){

        s.add("Create Folder").setIcon(R.drawable.ic_create_new_folder_white_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.create_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Create new folder")
                        .setPositiveButton("Create",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        List<String> foldernames = new ArrayList<>();
                                        String foldername = userInput.getText().toString();
                                        if (!foldername.equals("")) {
                                            new asyncCreateFolder(MainActivity.this, username, password, server, foldername).execute();
                                            foldernames.add(foldername);
                                            populateNavigationFolderItemsExtra(foldernames,getSubMenu());
                                            dialog.cancel();
                                        }
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
                return false;
            }
        });
        s.add("Inbox").setIcon(R.drawable.ic_inbox_white_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mobileData(getApplicationContext())){
                    mailSet = 1;
                    task = new loadRecentMails(MainActivity.this, username, password,
                            server, "INBOX",mailSet,"navigation");
                    task.execute();
                    activeFolder = "INBOX";
                    MainActivity.this.setTitle("Inbox");
                }
                return false;
            }
        });
        s.add("Sent").setIcon(R.drawable.ic_send_white_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mobileData(getApplicationContext())){
                    mailSet = 1;
                    task =new loadRecentMails(MainActivity.this, username, password, server,
                            "Sent",mailSet,"navigation");
                    task.execute();
                    activeFolder = "Sent";
                    MainActivity.this.setTitle("Sent");
                }
                return false;
            }
        });

    }
    public void populateNavigationFolderItemsExtra(final List<String> folderNames, SubMenu s){
        for (final String name : folderNames) {
            if (!(name.equals("Sent") || name.equals("INBOX"))) {
                s.add(name).setIcon(R.drawable.ic_folder_white_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mailSet =1;
                        String caller = "navigation";
                        if (folderNames.size()==1){
                            caller = "create";
                        }
                        if (!mobileData(getApplicationContext())){
                            task = new loadRecentMails(MainActivity.this, username, password, server
                                    , name,mailSet,caller);
                            task.execute();
                            activeFolder = name;
                            MainActivity.this.setTitle(name);
                        }
                        return false;
                    }
                });
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    private void populateAlert(int id){
        final POP3ssl pop3ssl = new POP3ssl();
        LayoutInflater li = LayoutInflater.from(getApplicationContext());

        View promptsView = li.inflate(R.layout.move_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final List<String> folderNames = asyncGetFolders.folders;

        String[] arraySpinner = new String[folderNames.size()];
        for (int i = 0; i < folderNames.size(); i++) {
            arraySpinner[i] = folderNames.get(i);
        }
        Spinner spinner = (Spinner) promptsView.findViewById(R.id.spinner_move);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, arraySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnSpinnerItemClicked());
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        if (id == R.id.move) {
            alertDialogBuilder
                    .setTitle("Move to")
                    .setPositiveButton("Move",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new asyncMoveMails(MainActivity.this, username, password, server, RecyclerAdapter.checkedEmails, activeFolder, spinnerFolderName).execute();
                                }
                            });
        } else if (id == R.id.copy) {
            alertDialogBuilder
                    .setTitle("Copy to")
                    .setPositiveButton("Copy",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new asyncCopyMails(MainActivity.this, username, password, server, RecyclerAdapter.checkedEmails, activeFolder, spinnerFolderName).execute();
                                }
                            });
        }
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void populateWebView(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl("https://webmail.iitg.ernet.in/src/login.php");

        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                getProgressBar().setVisibility(View.VISIBLE);
                view.getSettings().setLoadWithOverviewMode(true);
                view.getSettings().setUseWideViewPort(true);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("test",view.getUrl());
                getProgressBar().setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                view.requestFocus(View.FOCUS_DOWN);

                view.loadUrl("javascript:var uselessvar1 = document.getElementsByName('login_username')[0].value = '"+username+"';" +
                        "var uselessvar =document.getElementsByName('secretkey')[0].value='"+password+"';");
                try {
                    if (view.getUrl().equals("https://webmail.iitg.ernet.in/src/login.php")){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public boolean mobileData(Context context){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}



