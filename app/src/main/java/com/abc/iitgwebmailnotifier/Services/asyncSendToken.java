package com.abc.iitgwebmailnotifier.Services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aarkay0602 on 10/3/17.
 */


public class asyncSendToken extends AsyncTask<Object, Object, String> {

    private String username,token,active;

    private static final String POST = "POST";

    private static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_VALUE = "application/json";


    public asyncSendToken(String user,String token,String active){
        this.username = user;
        this.token = token;
        this.active = active;
    }


    protected void onPreExecute() {
    }

    protected String doInBackground(Object... urls) {
        try {

            URL url = new URL("http://iitgwebmail.esy.es/gcm/process.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {

                connection.setRequestMethod(POST);
                connection.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE,
                        CONTENT_TYPE_VALUE);
                JSONSerializer jsonSerializer = new JSONSerializer();

                //Send request
                DataOutputStream outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.writeBytes(jsonSerializer.createJSONforSendingToken(username,token,active));
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
            if (response.equals("success")){
                Log.e("resp",response);
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
