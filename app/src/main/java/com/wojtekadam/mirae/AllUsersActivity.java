package com.wojtekadam.mirae;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllUsersActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> patientsList;

    // url to get all products list
    private static String url_all_patients = "http://pluton.kt.agh.edu.pl/~aniedzialkowski/mirae_php_scripts/get_all_users.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PATIENTS = "patients";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_PESEL = "pesel";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_ADDRESS = "address";

    // products JSONArray
    JSONArray patients = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users);

        // Hashmap for ListView
        patientsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllPatients().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single user
        // launching Edit User Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String PESEL = ((TextView) view.findViewById(R.id.pesel)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        UserOptionActivity.class);
                // sending pesel to next activity
                in.putExtra(TAG_PESEL, PESEL);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllPatients extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllUsersActivity.this);
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
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_patients, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Patients: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    patients = json.getJSONArray(TAG_PATIENTS);

                    // looping through All Products
                    for (int i = 0; i < patients.length(); i++) {
                        JSONObject c = patients.getJSONObject(i);

                        // Storing each json item in variable
                        String name = c.getString(TAG_NAME);
                        String surname = c.getString(TAG_SURNAME);
                        String pesel = c.getString(TAG_PESEL);
                        String phone = c.getString(TAG_PHONE);
                        String address = c.getString(TAG_ADDRESS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_NAME, name);
                        map.put(TAG_SURNAME, surname);
                        map.put(TAG_PESEL, pesel);
                        map.put(TAG_PHONE, phone);
                        map.put(TAG_ADDRESS, address);

                        // adding HashList to ArrayList
                        patientsList.add(map);
                    }
                } else {
                    // no patient found
                    // Launch Add New patient Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewUserActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
                            AllUsersActivity.this, patientsList,
                            R.layout.single_user, new String[] { TAG_NAME,
                            TAG_SURNAME, TAG_PESEL, TAG_PHONE, TAG_ADDRESS},
                            new int[] { R.id.name, R.id.surname, R.id.pesel, R.id.phone, R.id.address });
                    // updating listview
                    setListAdapter(adapter);

                }
            });

        }

    }
}