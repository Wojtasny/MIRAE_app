package com.wojtekadam.mirae;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
            Log.d("All Patients: ", json.toString());

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
                        int licz = 0;
                        // Storing each json item in variable
                        String id = c.getString(getString(R.string.TAG_ID)); if (id == "null") id = getString(R.string.brak_danych);
                        String med_id = c.getString(getString(R.string.TAG_MED_ID)); if (med_id == "null") med_id = getString(R.string.brak_danych);
                        String room_id = c.getString(getString(R.string.TAG_ROOM_ID)); if (room_id == "null") room_id = getString(R.string.brak_danych);
                        String price = c.getString(getString(R.string.TAG_PRICE)); if (price == "null") price = getString(R.string.brak_danych);
                        String start_time = c.getString(getString(R.string.TAG_START_TIME)); if (start_time == "null") start_time = getString(R.string.brak_danych);
                        String meds = c.getString(getString(R.string.TAG_MEDS)); if (meds == "null") meds = getString(R.string.brak_danych);
                        String symptoms = c.getString(getString(R.string.TAG_SYMPTOMS)); if (symptoms == "null") symptoms = getString(R.string.brak_danych);


                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(getString(R.string.TAG_ID), id);
                        map.put(getString(R.string.TAG_MED_ID), med_id);
                        map.put(getString(R.string.TAG_ROOM_ID), room_id);
                        map.put(getString(R.string.TAG_PRICE), price);
                        map.put(getString(R.string.TAG_START_TIME), start_time);
                        map.put(getString(R.string.TAG_MEDS), meds);
                        map.put(getString(R.string.TAG_SYMPTOMS), symptoms);

                        // adding HashList to ArrayList
                        visits_LIST.add(map);
                    }
                } else {
                    /*
                    to do co zrobic jak nie ma zadnych wizyt
                     */
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
