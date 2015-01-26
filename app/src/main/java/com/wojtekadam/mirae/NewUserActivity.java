package com.wojtekadam.mirae;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputNAME;
    EditText inputSURNAME;
    EditText inputPESEL;
    EditText inputPHONE;
    EditText inputADDRESS;
    EditText inputEMAIL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Proszę uzupełnić dane",Toast.LENGTH_LONG);
        toast.show();

        // Edit Text
        inputNAME = (EditText) findViewById(R.id.inputNAME);
        inputSURNAME = (EditText) findViewById(R.id.inputSURNAME);
        inputPESEL = (EditText) findViewById(R.id.inputPESEL);
        inputPHONE = (EditText) findViewById(R.id.inputPHONE);
        inputADDRESS = (EditText) findViewById(R.id.inputADDRESS);
        inputEMAIL = (EditText) findViewById(R.id.inputEMAIL);


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
            String email = inputEMAIL.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(getString(R.string.TAG_NAME), name));
            params.add(new BasicNameValuePair(getString(R.string.TAG_SURNAME), surname));
            params.add(new BasicNameValuePair(getString(R.string.TAG_PESEL), pesel));
            params.add(new BasicNameValuePair(getString(R.string.TAG_PHONE), phone));
            params.add(new BasicNameValuePair(getString(R.string.TAG_ADDRESS), address));
            params.add(new BasicNameValuePair(getString(R.string.TAG_EMAIL), email));
            Log.d("User creation params",params.toString());
            // getting JSON Object
            // Note that create product url accepts POST method
            final JSONObject json_new_user = jsonParser.makeHttpRequest("http://pluton.kt.agh.edu.pl/~aniedzialkowski/mirae_php_scripts/new_user.php",
                    "POST", params);

            Log.d("Query json",json_new_user.toString());


            Log.d("Create Response", json_new_user.toString());

            // check for success tag
            try {
                int success = json_new_user.getInt(getString(R.string.TAG_SUCCESS));

                if (success == 1) {
                    // successfully created product
                    runOnUiThread(new Runnable() {
                                      public void run() {
                                          setContentView(R.layout.user_options);
                                          Context context = getApplicationContext();
                                          Toast toast = Toast.makeText(context, "Poprawnie utworzono profil użytkownika", Toast.LENGTH_LONG);
                                          toast.show();
                                      }
                                  });
                    Intent i = new Intent(getApplicationContext(), UserOptionActivity.class);
                    i.putExtra("pesel", pesel);
                    startActivity(i);

                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setContentView(R.layout.add_user);
                            Context context = getApplicationContext();
                            String wiadomosc = null;
                            try {
                                wiadomosc = json_new_user.getString(getString(R.string.TAG_Message));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Toast toast = Toast.makeText(context, "Wystąpił błąd: "+wiadomosc, Toast.LENGTH_LONG);
                            toast.show();
                            finish();
                        }
                    });
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