package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wojci_000 on 2015-01-29.
 */
public class QueueActivity extends Activity {
    String pesel;
    JSONParser jsonParser = new JSONParser();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int success = 0;
        Intent i = getIntent();
        pesel = i.getStringExtra("pesel");
        new GetQueue().execute();
        finish();

    }
    class GetQueue extends AsyncTask<String, String, String> {
        private JSONObject json;

        protected String doInBackground(String... params) {

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pesel", pesel));

            // getting user details by making HTTP request
            // Note that user details url will use GET request
            json = jsonParser.makeHttpRequest(getString(R.string.url_user_queue), "GET", param);
            //check your log for json response
            Log.d("Queue", json.toString());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            int success = 0;
            try {
                success = json.getInt(getString(R.string.TAG_SUCCESS));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            String message;
            if (success == 1) {
                setContentView(R.layout.user_options);
                Context context = getApplicationContext();
                String pozycja = null;
                try {
                    pozycja = json.getString(getString(R.string.TAG_POSITION));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pozycja.equals("-1") ){
                     message = "Nie jesteś jeszcze w kolejce";
                }
                else{
                    message = "Aktualna pozycja w kolejce: " + pozycja;
                }
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
