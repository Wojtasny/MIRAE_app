package com.wojtekadam.mirae;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditUserActivity extends Activity {

    EditText txtEid;
    EditText txtTitle;
    EditText txtCreatedAt;
    Button btnSave;
    Button btnDelete;

    String eid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_user_details = "http://pluton.kt.agh.edu.pl/~wwrobel/get_user_details.php";

    // url to update product
    private static final String url_update_user = "http://pluton.kt.agh.edu.pl/~wwrobel/update_user.php";

    // url to delete product
    private static final String url_delete_user = "http://pluton.kt.agh.edu.pl/~wwrobel/delete_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "users";
    private static final String TAG_EID = "eid";
    private static final String TAG_TITLE = "title";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);



           // uwaga uwaga wklejam jakis kod

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

            //az do tad mam usunąc go


        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        eid = i.getStringExtra(TAG_EID);

        // Getting complete product details in background thread
        new GetUserDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveUserDetails().execute();
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteUser().execute();
            }
        });

    }

    /**
     * Background Async Task to Get complete user details
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogEdit));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("eid", eid));

                        // getting user details by making HTTP request
                        // Note that user details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_user_details, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_USER); // JSON Array

                            // get first user object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // user with this eid found
                            // Edit Text
                            txtEid = (EditText) findViewById(R.id.EditEid);
                            txtTitle = (EditText) findViewById(R.id.EditTitle);

                            // display user data in EditText
                            txtEid.setText(product.getString(TAG_EID));
                            txtTitle.setText(product.getString(TAG_TITLE));


                        }else{
                            // user with eid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to  Save product Details
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogSaveUser));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving user
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String eid = txtEid.getText().toString();
            String title = txtTitle.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_EID, eid));
            params.add(new BasicNameValuePair(TAG_TITLE, title));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once user updated
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogDeletingUser));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting user
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("eid", eid));

                // getting user details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_user, "POST", params);

                // check your log for json response
                Log.d("Delete User", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // user successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
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
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
}