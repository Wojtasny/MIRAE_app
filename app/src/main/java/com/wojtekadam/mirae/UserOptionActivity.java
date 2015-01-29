package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserOptionActivity extends Activity {
    JSONParser jsonParser = new JSONParser();
    Button btnEditUser;
    Button btnPickADate;
    Button btnVisits;
    Button btnQueues;
    String pesel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_options);

        btnEditUser = (Button) findViewById(R.id.btnEditUser);
        btnPickADate = (Button) findViewById(R.id.btnPickADate);
        btnQueues = (Button) findViewById(R.id.btnQueues);
        btnVisits = (Button) findViewById(R.id.btnVisits);
        Intent i = getIntent();
        pesel = i.getStringExtra("pesel");

        new GetUserDetails().execute();

        btnQueues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AsyncTask test = new GetQueue().execute();
                Intent inciu = new Intent(getApplicationContext(), QueueActivity.class);
                inciu.putExtra("pesel",pesel);
                startActivity(inciu);
            }
        });

        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditUserActivity.class);
                // sending pesel to next activity
                in.putExtra("pesel", pesel);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);

            }
        });

        btnVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(getApplicationContext(), VisitsActivity.class);
                inte.putExtra(getString(R.string.TAG_PESEL), pesel);
                startActivity(inte);
            }
        });

        btnPickADate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SymptomsActivity.class);
                i.putExtra("pesel", pesel);
                startActivity(i);

            }
        });
    }


    class GetUserDetails extends AsyncTask<String, String, String> {

        String name;
        String surname;
        String phone;
        String address;
        String email;

        /**
         * Getting patient details in background thread
         */
        protected String doInBackground(String... params) {

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pesel", pesel));

            // getting user details by making HTTP request
            // Note that user details url will use GET request
            final JSONObject json = jsonParser.makeHttpRequest(getString(R.string.url_user_details), "GET", param);
            //check your log for json response
            Log.d("Single Product Details", json.toString());

            try {
                int success = json.getInt(getString(R.string.TAG_SUCCESS));
                Log.d("test", String.valueOf(success));

                if (success == 1) {

                    // successfully received product details
                    JSONObject patient = json.getJSONObject(getString(R.string.TAG_PATIENT)); // JSON Array

                    // get first user object from JSON Array
//                    JSONObject patient = patientOBJ.getJSONObject(0);

                    name = patient.getString(getString(R.string.TAG_NAME));

                    surname = patient.getString(getString(R.string.TAG_SURNAME));
                    pesel = patient.getString(getString(R.string.TAG_PESEL));
                    phone = patient.getString(getString(R.string.TAG_PHONE));
                    address = patient.getString(getString(R.string.TAG_ADDRESS));
                    email = patient.getString(getString(R.string.TAG_EMAIL));

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setContentView(R.layout.user_options);
                            Context context = getApplicationContext();
                            String wiadomosc = null;
                            try {
                                wiadomosc = json.getString(getString(R.string.TAG_Message));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Toast toast = Toast.makeText(context, "Wystąpił błąd: " + wiadomosc, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    finish();
                }
            } catch (JSONException e) {
                //wyjatek
            }

            return null;
        }

        protected void onPostExecute(String file_url) {


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            if (resultCode == RESULT_OK) {

                String name = data.getStringExtra("name");
                String surname = data.getStringExtra("surname");
                String pesel = data.getStringExtra("pesel");
                String phone = data.getStringExtra("phone");
                String address = data.getStringExtra("address");
                String email = data.getStringExtra("email");

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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

            if (success == 1) {
                setContentView(R.layout.user_options);
                Context context = getApplicationContext();
                String pozycja = null;
                try {
                    pozycja = json.getString(getString(R.string.TAG_POSITION));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(context, "Aktualna pozycja w kolejce: " + pozycja, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
