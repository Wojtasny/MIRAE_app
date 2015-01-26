package com.wojtekadam.mirae;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VisitsActivity extends ListActivity {
    ProgressDialog pDialog;
    String pesel;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> visits_LIST;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visits);

        visits_LIST = new ArrayList<HashMap<String, String>>();

        Intent i = getIntent();
        pesel = i.getStringExtra(getString(R.string.TAG_PESEL));
        new LoadAllVisits().execute();
    }
    class LoadAllVisits extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VisitsActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogLoadingUsers));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(getString(R.string.TAG_PESEL), pesel));

            JSONObject json = jsonParser.makeHttpRequest(getString(R.string.url_visits), "GET", param);

            // Check your log cat for JSON response
            Log.d("All Appointments: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(getString(R.string.TAG_SUCCESS));

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    //appointments = json.getJSONArray(getString(R.string.TAG_APPOINTMENTS);
                    JSONArray appointmentOBJ = json.getJSONArray(getString(R.string.TAG_APPOINTMENTS)); // JSON Array

                    // looping through All Products
                    for (int i = 0; i < appointmentOBJ.length(); i++) {
                        JSONObject c = appointmentOBJ.getJSONObject(i);

                        String[] tablica = new String[7];
                        int ile = 0;
                        // Storing each json item in variable



                        tablica[ile] = c.getString(getString(R.string.TAG_ID)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_MED_ID)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_ROOM_ID)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_PRICE)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_START_TIME)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_MEDS)); ile++;
                        tablica[ile] = c.getString(getString(R.string.TAG_SYMPTOMS));

                        int pom = 0;
                        for(String x : tablica){
                            if (x == "null") tablica[pom] = getString(R.string.brak_danych);
                            pom++;
                        }

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        ile = 0;
                        // adding each child node to HashMap key => value
                        map.put(getString(R.string.TAG_ID), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_MED_ID), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_ROOM_ID), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_PRICE), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_START_TIME), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_MEDS), tablica[ile]); ile++;
                        map.put(getString(R.string.TAG_SYMPTOMS), tablica[ile]);
                        // adding HashList to ArrayList
                        visits_LIST.add(map);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setContentView(R.layout.user_options);
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Brak wizyt do wyświetlenia", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    finish();
                    }
                } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            VisitsActivity.this, visits_LIST,
                            R.layout.single_appointment, new String[] { getString(R.string.TAG_ID),
                            getString(R.string.TAG_MED_ID), getString(R.string.TAG_ROOM_ID), getString(R.string.TAG_PRICE),
                            getString(R.string.TAG_START_TIME), getString(R.string.TAG_MEDS),getString(R.string.TAG_SYMPTOMS) },
                            new int[] { R.id.id, R.id.med_id, R.id.room_id, R.id.price, R.id.start_time, R.id.meds, R.id.symptoms });
                    // updating listview
                    setListAdapter(adapter);

                }
            });

        }

    }
}
