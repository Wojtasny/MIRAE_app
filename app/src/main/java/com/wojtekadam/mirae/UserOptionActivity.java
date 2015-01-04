package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserOptionActivity extends Activity {
    private static final String url_user_details = "http://pluton.kt.agh.edu.pl/~wwrobel/get_user_details.php";
    JSONParser jsonParser = new JSONParser();
    Button btnEditUser;
    Button btnPickADate;
    String pesel;

    TextView NAME;
    TextView SURNAME;
    TextView PESEL;
    TextView PHONE;
    TextView ADDRESS;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PATIENT = "patient";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_PESEL = "pesel";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_ADDRESS = "address";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_options);

        btnEditUser = (Button) findViewById(R.id.btnEditUser);
        btnPickADate = (Button) findViewById(R.id.btnPickADate);
        Intent i = getIntent();
        pesel = i.getStringExtra("pesel");

        new GetUserDetails().execute();

        btnEditUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditUserActivity.class);
                // sending pesel to next activity
                in.putExtra("pesel", pesel);

                // starting new activity and expecting some response back
                startActivityForResult(in, 0);

            }
        });

        btnPickADate.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PickADateActivity.class);
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


        /**
         * Getting patient details in background thread
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

            runOnUiThread(new Runnable() {
                public void run() {
                    // Edit Text
                    NAME = (TextView) findViewById(R.id.name);
                    SURNAME = (TextView) findViewById(R.id.surname);
                    PESEL = (TextView) findViewById(R.id.pesel);
                    PHONE = (TextView) findViewById(R.id.phone);
                    ADDRESS = (TextView) findViewById(R.id.address);

                    // display user data in EditText
                    NAME.setText(name);
                    SURNAME.setText(surname);
                    PESEL.setText(pesel);
                    PHONE.setText(phone);
                    ADDRESS.setText(address);

                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {

            if(resultCode == RESULT_OK){

                String name=data.getStringExtra("name");
                String surname=data.getStringExtra("surname");
                String pesel=data.getStringExtra("pesel");
                String phone=data.getStringExtra("phone");
                String address=data.getStringExtra("address");

                NAME = (TextView) findViewById(R.id.name);
                SURNAME = (TextView) findViewById(R.id.surname);
                PESEL = (TextView) findViewById(R.id.pesel);
                PHONE = (TextView) findViewById(R.id.phone);
                ADDRESS = (TextView) findViewById(R.id.address);

                // display user data in EditText
                NAME.setText(name);
                SURNAME.setText(surname);
                PESEL.setText(pesel);
                PHONE.setText(phone);
                ADDRESS.setText(address);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
