package com.wojtekadam.mirae;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class NewUserActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputNAME;
    EditText inputSURNAME;
    EditText inputPESEL;
    EditText inputPHONE;
    EditText inputADDRESS;


    // url to create new product
    private static String url_create_patient = "http://pluton.kt.agh.edu.pl/~wwrobel/new_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);

        // Edit Text
        inputNAME = (EditText) findViewById(R.id.inputNAME);
        inputSURNAME = (EditText) findViewById(R.id.inputSURNAME);
        inputPESEL = (EditText) findViewById(R.id.inputPESEL);
        inputPHONE = (EditText) findViewById(R.id.inputPHONE);
        inputADDRESS = (EditText) findViewById(R.id.inputADDRESS);


        // Create button
        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);

        // button click event
        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewPatient().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewPatient extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewUserActivity.this);
            pDialog.setMessage(getString(R.string.PorgressDialogCreatingUser));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating Patient
         * */
        protected String doInBackground(String... args) {
            String name = inputNAME.getText().toString();
            String surname = inputSURNAME.getText().toString();
            String pesel = inputPESEL.getText().toString();
            String phone = inputPHONE.getText().toString();
            String address = inputADDRESS.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("surname", surname));
            params.add(new BasicNameValuePair("pesel", pesel));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("address", address));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_patient,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllUsersActivity.class);
                    startActivity(i);

                    // closing this screen

                    finish();
                } else {
                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}