package com.wojtekadam.mirae;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class EditUserActivity extends Activity {

    EditText editNAME;
    EditText editSURNAME;
    EditText editPESEL;
    EditText editPHONE;
    EditText editADDRESS;
    Button btnSave;
    Button btnDelete;

    String pesel;


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> patientList;

    // single product url
    private static final String url_user_details = "http://pluton.kt.agh.edu.pl/~wwrobel/get_user_details.php";

    // url to update product
    private static final String url_update_user = "http://pluton.kt.agh.edu.pl/~wwrobel/update_user.php";

    // url to delete product
    private static final String url_delete_user = "http://pluton.kt.agh.edu.pl/~wwrobel/delete_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PATIENT = "patient";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_PESEL = "pesel";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_ADDRESS = "address";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pesel = i.getStringExtra(TAG_PESEL);

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

        String name;
        String surname;

        String phone;
        String address;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogEdit));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {


            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pesel", pesel));

            // getting user details by making HTTP request
            // Note that user details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    url_user_details, "GET", param);
            //check your log for json response
            Log.d("Single Product Details", json.toString());

            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray patientOBJ = json.getJSONArray(TAG_PATIENT); // JSON Array
                    // get first user object from JSON Array
                    JSONObject patient = patientOBJ.getJSONObject(0);
                    name = patient.getString(TAG_NAME);
                    surname = patient.getString(TAG_SURNAME);
                    pesel = patient.getString(TAG_PESEL);
                    phone = patient.getString(TAG_PHONE);
                    address = patient.getString(TAG_ADDRESS);

                }
            }
            catch(JSONException e){
                //wyjatek
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();


            runOnUiThread(new Runnable() {
                public void run() {
                    // Edit Text
                        editNAME = (EditText) findViewById(R.id.editNAME);
                        editSURNAME = (EditText) findViewById(R.id.editSURNAME);
                        editPESEL = (EditText) findViewById(R.id.editPESEL);
                        editPHONE = (EditText) findViewById(R.id.editPHONE);
                        editADDRESS = (EditText) findViewById(R.id.editADDRESS);

                        // display user data in EditText
                        editNAME.setText(name);
                        editSURNAME.setText(surname);
                        editPESEL.setText(pesel);
                        editPHONE.setText(phone);
                        editADDRESS.setText(address);


                }
            });

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
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Saving user
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String name = editNAME.getText().toString();
            String surname = editSURNAME.getText().toString();
            String pesel = editPESEL.getText().toString();
            String phone = editPHONE.getText().toString();
            String address = editADDRESS.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_SURNAME, surname));
            params.add(new BasicNameValuePair(TAG_PESEL, pesel));
            params.add(new BasicNameValuePair(TAG_PHONE, phone));
            params.add(new BasicNameValuePair(TAG_ADDRESS, address));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent returnIntent = new Intent();
                    // send result code 100 to notify about product update
                    returnIntent.putExtra(TAG_NAME, name);
                    returnIntent.putExtra(TAG_SURNAME, surname);
                    returnIntent.putExtra(TAG_PESEL, pesel);
                    returnIntent.putExtra(TAG_PHONE, phone);
                    returnIntent.putExtra(TAG_ADDRESS, address);
                    setResult(RESULT_OK, returnIntent);

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
            pDialog.setCancelable(false);
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
                params.add(new BasicNameValuePair("pesel", pesel));

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