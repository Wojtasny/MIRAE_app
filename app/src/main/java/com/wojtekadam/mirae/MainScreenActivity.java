package com.wojtekadam.mirae;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainScreenActivity extends Activity {

    Button btnContinue;
    String pesel;
    EditText inputPESEL;
    ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    private static final String url_user_details = "http://pluton.kt.agh.edu.pl/~wwrobel/get_user_details.php";

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        inputPESEL = (EditText) findViewById(R.id.inputPESEL);
        // Buttons
        btnContinue =  (Button) findViewById(R.id.btnContinue);

        // view users click event
        btnContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pesel = inputPESEL.getText().toString();
                new GetUserDetails().execute();
            }
        });
    }
    class GetUserDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainScreenActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogMainScreen));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
            protected String doInBackground(String... params) {

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pesel", pesel));

            JSONObject json = jsonParser.makeHttpRequest(
                    url_user_details, "GET", param);
            //check log for json response
            Log.d("Single User Details", json.toString());

            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Intent i = new Intent(getApplicationContext(), UserOptionActivity.class);
                    i.putExtra("pesel", pesel);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                    startActivity(i);
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
        }
    }
}
