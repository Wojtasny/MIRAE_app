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
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickADateActivity extends Activity {
    DatePicker calendar;
    TimePicker time;
    Button btnReserve;
    ProgressDialog pDialog;
    String pesel;
    String dzien;
    String godzina;
    JSONParser jsonParser = new JSONParser();

    private static final String url_reserve = "http://pluton.kt.agh.edu.pl/~wwrobel/reserve.php";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_a_date);

        time = (TimePicker) findViewById(R.id.timePicker);
        time.setIs24HourView(true);
        time.setOnTimeChangedListener(mTimePickerListener);
        Intent i = getIntent();
        pesel = i.getStringExtra("pesel");

        btnReserve = (Button) findViewById(R.id.btnReserve);
        btnReserve.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                new Reserve().execute();
            }
        });

        calendar = (DatePicker) findViewById(R.id.datePicker);
        CalendarView date = calendar.getCalendarView();


//        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//            @Override
//            public void onSelectedDayChange(CalendarView view,
//                                            int year, int month, int dayOfMonth) {
//                month += 1;
//                Toast.makeText(getApplicationContext(),
//                        dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
//            }
//        });
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

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("pesel", pesel));
            //-----------------
            dzien = Integer.toString(calendar.getYear())+"/"+Integer.toString(calendar.getMonth()+1)+"/"+Integer.toString(calendar.getDayOfMonth());
            godzina = Integer.toString(time.getCurrentHour())+':'+Integer.toString(time.getCurrentMinute());
            Log.d("data",dzien);
            Log.d("godzina",godzina);
            runOnUiThread(new Runnable() {
                public void run() {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, dzien+" "+godzina,Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            //-----------------
//            JSONObject json = jsonParser.makeHttpRequest(
//                    url_reserve, "POST", param);
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once user updated
            pDialog.dismiss();
        }
    }
}
