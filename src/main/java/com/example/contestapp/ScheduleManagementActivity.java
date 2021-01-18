package com.example.contestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.contestapp.MainActivity.roomList;

public class ScheduleManagementActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    final static String EXTRA_MESSAGE = "message";
    final static String EXTRA_INT_MESSAGE = "int_message";
    String scheduleFor;
    int scheduleId;
    ListView listView;
    ArrayAdapter<String> listAdapter;
    List<String> dates;
    List<Long> datesToConvert;
    Calendar calendar;
    List<TimeFrameHandler> schedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_management);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        scheduleFor = intent.getStringExtra(EXTRA_MESSAGE);
        scheduleId = intent.getIntExtra(EXTRA_INT_MESSAGE, -1);

        if(scheduleId == 0)
        {
            getSupportActionBar().setTitle(scheduleFor+" (single room)");
        }

        if(scheduleId != 0)
        {
            getSupportActionBar().setTitle(scheduleFor+" (group)");
        }

        dates = new ArrayList<>();
        datesToConvert = new ArrayList<>();
        calendar = Calendar.getInstance();


        for(Room current:roomList)
        {
            if(scheduleId == 0 && current.getGroupId() == 0)
            {
                if(current.getBooleanSchedule() && current.getName().equals(scheduleFor))
                {
                    schedule = current.getTimeFrameHandlerList();
                }
            }

            if(scheduleId != 0 && current.getGroupId() != 0)
            {
                if(current.getBooleanSchedule() && current.getGroupName().equals(scheduleFor))
                {
                    schedule = current.getTimeFrameHandlerList();
                    break;
                }
            }
        }

        if(schedule != null)
        {
            for(TimeFrameHandler timeFrameHandler:schedule)
            {
                Long day = timeFrameHandler.getDay();
                if(!datesToConvert.contains(day))
                {
                    //String currentDateString = DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(day);
                    //dates.add(currentDateString);
                    datesToConvert.add(day);
                }
            }
            Collections.sort(datesToConvert, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {

                    return Long.compare(o1, o2);
                }
            });

            for(Long day:datesToConvert)
            {
                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(day);
                dates.add(currentDateString);
                //datesToConvert.add(day);
            }
        }

        listView = findViewById(R.id.datesContainer);
        listAdapter = new ArrayAdapter<>(ScheduleManagementActivity.this, android.R.layout.simple_list_item_1, dates);
        listView.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(ScheduleManagementActivity.this, ScheduleDateManagementActivity.class);
                intent.putExtra(ScheduleDateManagementActivity.EXTRA_MESSAGE, scheduleFor);
                intent.putExtra(ScheduleDateManagementActivity.EXTRA_INT_MESSAGE, scheduleId);
                intent.putExtra(ScheduleDateManagementActivity.EXTRA_DAY_MESSAGE, datesToConvert.get(position));
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(R.id.menuIco == item.getItemId())
        {
            Intent intent = new Intent(ScheduleManagementActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public void createSchedule(View view)
    {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, dayOfMonth,0,0,0);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(c.getTime());
        if(!dates.contains(currentDateString))
        {
            dates.add(currentDateString);
            datesToConvert.add(c.getTimeInMillis());
            listView.setAdapter(listAdapter);
        } else
            {
                Toast.makeText(ScheduleManagementActivity.this, "Schedule for this day already exists", Toast.LENGTH_LONG).show();
            }
    }
}