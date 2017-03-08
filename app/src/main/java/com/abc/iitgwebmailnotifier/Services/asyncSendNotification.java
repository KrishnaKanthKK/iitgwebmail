package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;
import android.view.SubMenu;
import android.widget.Toast;

import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.Activities.SendNotificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by aarkay0602 on 8/3/17.
 */


public class asyncSendNotification extends AsyncTask<Object, Object, String> {
    private SendNotificationActivity activity;

    private String username;

    private String message;

    private String department;

    private static final String POST = "POST";

    private static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_VALUE = "application/json";


    public asyncSendNotification(SendNotificationActivity activity, String user, String message, String department){
        this.activity = activity;
        this.username = user;
        this.message = message;
        this.department = department;
    }


    protected void onPreExecute() {
        Toast.makeText(activity.getApplicationContext(), "Sending message",
                Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(Object... urls) {
        try {

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {

                connection.setRequestMethod(POST);
                connection.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE,
                        CONTENT_TYPE_VALUE);
                connection.setRequestProperty("Authorization","key="+activity.SERVER_KEY);

                JSONSerializer jsonSerializer = new JSONSerializer();

                //Send request
                DataOutputStream outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.writeBytes(jsonSerializer.createJSONforTopicMessage(department,message,username));
                outputStream.flush();
                outputStream.close();

                //Get response
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String response = stringBuilder.toString();

                return response;
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        try {
            if (!response.equals(null)){
                Log.e("resp",response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("message_id")){
                    Toast.makeText(activity.getApplicationContext(), "Message sent successfully",
                            Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            }else {
                Toast.makeText(activity.getApplicationContext(), "Check your internet connection and try again",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            Toast.makeText(activity.getApplicationContext(), "Check your internet connection and try again",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
