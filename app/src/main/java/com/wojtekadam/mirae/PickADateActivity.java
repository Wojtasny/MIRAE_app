package com.wojtekadam.mirae;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class PickADateActivity extends Activity {
    DatePicker calendar;
    TimePicker time;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_a_date);

        time = (TimePicker) findViewById(R.id.timePicker);
        time.setIs24HourView(true);
        time.setOnTimeChangedListener(mTimePickerListener);

        calendar = (DatePicker) findViewById(R.id.datePicker);
        CalendarView date = calendar.getCalendarView();

        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            @Override
            public void onSelectedDayChange(CalendarView view,
                                            int year, int month, int dayOfMonth) {
                month+=1;
                Toast.makeText(getApplicationContext(),
                        dayOfMonth +"/"+month+"/"+ year,Toast.LENGTH_LONG).show();}});
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

}
