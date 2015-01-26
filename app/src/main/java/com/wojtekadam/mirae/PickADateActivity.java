package com.wojtekadam.mirae;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PickADateActivity extends Activity {
    DatePicker calendar;
    TimePicker start_time;
    TimePicker end_time;

    Button btnReserve;
    ProgressDialog pDialog;
    String pesel;
    String dzien;

    String symptoms;
    String time_start;
    String time_end;
    JSONParser jsonParserek = new JSONParser();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_a_date);

        start_time = (TimePicker) findViewById(R.id.timePicker);
        start_time.setIs24HourView(true);
        start_time.setOnTimeChangedListener(mTimePickerListener);
        end_time = (TimePicker) findViewById(R.id.timePicker2);
        end_time.setIs24HourView(true);
        Intent i = getIntent();
        pesel = i.getStringExtra(getString(R.string.TAG_PESEL));
        symptoms = i.getStringExtra(getString(R.string.TAG_SYMPTOMS));


        btnReserve = (Button) findViewById(R.id.btnReserve);
        btnReserve.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                try {
                    String str_result = new Reserve().execute().get();
                }
                catch (ExecutionException e)
                {
//
                }
                catch (InterruptedException ex)
                {
//
                }
                Intent in = getIntent();
                setResult(100, in);
                finish();
            }
        });

        calendar = (DatePicker) findViewById(R.id.datePicker);
    }

    private static final int TIME_PICKER_INTERVAL=30;
    private boolean mIgnoreEvent=false;

    private TimePicker.OnTimeChangedListener mTimePickerListener=new TimePicker.OnTimeChangedListener(){
        public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute){
            if (mIgnoreEvent)
                return;
            if (minute%TIME_PICKER_INTERVAL!=0){
                int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
                minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
                if (minute==60)
                    minute=0;
                mIgnoreEvent=true;
                timePicker.setCurrentMinute(minute);
                mIgnoreEvent=false;

            }
            end_time.setCurrentHour(start_time.getCurrentHour());
            end_time.setCurrentMinute(start_time.getCurrentMinute());
        }
    };

    class Reserve extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PickADateActivity.this);
            pDialog.setMessage(getString(R.string.ProgressDialogPickADate));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... params){

            dzien = Integer.toString(calendar.getYear())+"-"+Integer.toString(calendar.getMonth()+1)+"-"+Integer.toString(calendar.getDayOfMonth());
            time_start = Integer.toString(start_time.getCurrentHour())+':'+Integer.toString(start_time.getCurrentMinute());
            time_end = Integer.toString(end_time.getCurrentHour())+':'+Integer.toString(end_time.getCurrentMinute());

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(getString(R.string.TAG_PESEL), pesel));
            param.add(new BasicNameValuePair(getString(R.string.TAG_SYMPTOMS), symptoms));
            param.add(new BasicNameValuePair("start_time", dzien+ " "+time_start));

            param.add(new BasicNameValuePair("end_time", dzien+ " "+time_end));
            final JSONObject jsonek = jsonParserek.makeHttpRequest(getString(R.string.url_reserve),"POST", param);

            // check log cat for response
            Log.d("Create Response", jsonek.toString());


            // check for success tag
            try {
                int success = jsonek.getInt(getString(R.string.TAG_SUCCESS));

                if (success == 1) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setContentView(R.layout.user_options);
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Rejestracja zakończona sukcesem",Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                    public void run() {
                        setContentView(R.layout.user_options);
                        Context context = getApplicationContext();
                        String wiadomosc = null;
                        try {
                            wiadomosc = jsonek.getString(getString(R.string.TAG_Message));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        Toast toast = Toast.makeText(context, "Wystąpił błąd: " + wiadomosc, Toast.LENGTH_LONG);
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


        protected void onPostExecute(String file_url) {
            // dismiss the dialog once user updated
            pDialog.dismiss();

        }
    }
}
