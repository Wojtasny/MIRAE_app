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
    EditText inputEid;
    EditText inputTitle;


    // url to create new product
    private static String url_create_user = "http://pluton.kt.agh.edu.pl/~wwrobel/new_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);

        // Edit Text
        inputEid = (EditText) findViewById(R.id.inputEid);
        inputTitle = (EditText) findViewById(R.id.inputTitle);


        // Create button
        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);

        // button click event
        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewUser().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

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
         * Creating user
         * */
        protected String doInBackground(String... args) {
            String eid = inputEid.getText().toString();
            String title = inputTitle.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("eid", eid));
            params.add(new BasicNameValuePair("title", title));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
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