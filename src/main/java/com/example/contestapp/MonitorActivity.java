package com.example.contestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.contestapp.MainActivity.roomList;

public class MonitorActivity extends AppCompatActivity {

    LinearLayout monitorLinearLayout;
    TextView textView;
    Calendar c;
    long day;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DATE);
        c.clear();
        c.set(year, month, dayOfMonth,0,0,0);
        day = c.getTimeInMillis();


        monitorLinearLayout = findViewById(R.id.monitorLinearLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(Room room:roomList)
        {
            View monitorRow = inflater.inflate(R.layout.monitor_row, null);
            if(room.getGroupId()%2 == 0)
            {
                monitorRow.setBackgroundColor(getResources().getColor(R.color.monitor_activity_color_light));
            } else
                {
                    monitorRow.setBackgroundColor(getResources().getColor(R.color.monitor_activity_color_dark));
                }
            monitorLinearLayout.addView(monitorRow, monitorLinearLayout.getChildCount());
            textView = monitorRow.findViewById(R.id.monitor_room_name);
            textView.setText(room.getName());
        }
        startMonitor();
    }

    private void startMonitor()
    {
        final Handler handler = new Handler();
        handler.post(new Runnable()
        {
            @Override
            public void run() {

                for(int i=0; i<roomList.size(); i++)
                {
                    View view = monitorLinearLayout.getChildAt(i);
                    textView = view.findViewById(R.id.monitor_temp_value);
                    List<TimeFrameHandler> timeFrameHandlerList;

                    if(!roomList.get(i).getBooleanActiveSchedule())
                    {
                        textView.setText(String.valueOf(roomList.get(i).getTemp()));
                    }

                    if(roomList.get(i).getBooleanActiveSchedule())
                    {
                        boolean result = false;
                        c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minutes = c.get(Calendar.MINUTE);
                        long now = hour*3600000 + minutes*60000;
                        timeFrameHandlerList = roomList.get(i).getTimeFrameHandlerList();
                        for(TimeFrameHandler tfh:timeFrameHandlerList)
                        {
                            if(day == tfh.getDay() && tfh.getStartTime() <= now && tfh.getEndTime() > now)
                            {
                                textView.setText(String.valueOf(tfh.getTempValue()));
                                result = true;
                                break;
                            }
                        }
                        if(!result)
                        {
                            textView.setText("");
                        }
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MonitorActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

}












