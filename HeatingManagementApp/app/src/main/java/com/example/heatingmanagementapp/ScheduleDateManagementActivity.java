package com.example.heatingmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.contestapp.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.example.heatingmanagementapp.ServerConnection.saveToDB;

public class ScheduleDateManagementActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    final static String EXTRA_MESSAGE = "message";
    final static String EXTRA_INT_MESSAGE = "int_message";
    final static String EXTRA_DAY_MESSAGE = "day_message";
    String scheduleFor;
    int scheduleId;
    LinearLayout parentLinearLayout;
    Calendar c;
    int hour, minute;
    int selectedHour = 00, selectedMinute = 00;
    TextView currentView;
    EditText currentEditView;
    TimeFrameHandler timeFrameHandler;
    private List<TimeFrameHandler> timeFrames;
    List<TimeFrameHandler> visibleTimeFrames;
    List<TimeFrameHandler> timeFramesToRemove;
    long day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_date_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        scheduleFor = intent.getStringExtra(EXTRA_MESSAGE);
        scheduleId = intent.getIntExtra(EXTRA_INT_MESSAGE,-1);
        day = intent.getLongExtra(EXTRA_DAY_MESSAGE, 0);

        getSupportActionBar().setTitle(DateFormat.getDateInstance(DateFormat.FULL).format(day));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleDateManagementActivity.this, ScheduleManagementActivity.class);
                intent.putExtra(ScheduleManagementActivity.EXTRA_MESSAGE, scheduleFor);
                intent.putExtra(ScheduleManagementActivity.EXTRA_INT_MESSAGE, scheduleId);
                startActivity(intent);
            }
        });


        parentLinearLayout = findViewById(R.id.timeFrameRow);

        Button addNewTimeFrameBtn = findViewById(R.id.addNewTimeFrameHandlerBtn);
        visibleTimeFrames = new LinkedList<>();

        for(Room room: MainActivity.roomList)
        {
            if(scheduleId == 0)
            {
                if(room.getName().equals(scheduleFor))
                {
                    if(room.getBooleanSchedule())
                    {
                        timeFrames = room.getTimeFrameHandlerList();
                        break;
                    } else
                        {
                            timeFrames = new LinkedList<>();
                            break;
                        }
                }
            }

            if(scheduleId != 0 && room.getGroupId() != 0)
            {
                if(room.getGroupName().equals(scheduleFor))
                {
                    if(room.getBooleanSchedule())
                    {
                        timeFrames = room.getTimeFrameHandlerList();
                        break;
                    } else
                        {
                            timeFrames = new LinkedList<>();
                            break;
                        }
                }
            }
        }

        for(TimeFrameHandler timeFrame:timeFrames)
        {
            if(timeFrame.getDay() == day)
            {
                visibleTimeFrames.add(timeFrame);
            }
        }

        Collections.sort(visibleTimeFrames, new TimeFrameComparator());

        if(!visibleTimeFrames.isEmpty())
        {
            for(int i=0; i<visibleTimeFrames.size(); i++)
            {
                if(visibleTimeFrames.get(i).getDay() == day)
                {
                    addNewTimeFrameBtn.performClick();
                    View v = parentLinearLayout.getChildAt(i);
                    currentView = v.findViewById(R.id.startTime);
                    currentView.setText(setTime(visibleTimeFrames.get(i).getStartTime()));
                    currentView = v.findViewById(R.id.endTime);
                    currentView.setText(setTime(visibleTimeFrames.get(i).getEndTime()));
                    currentEditView = v.findViewById(R.id.scheduleTempEditText);
                    currentEditView.setText(String.valueOf(visibleTimeFrames.get(i).getTempValue()));
                }
            }
        }

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
            Intent intent = new Intent(ScheduleDateManagementActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public void addNewTimeFrameHandler(View view)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.time_frame_row, null);
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount());
    }

    public void createSchedule(View view)
    {
        String time;
        long startTime;
        long endTime;
        int tempValue;

        try {

            visibleTimeFrames = new LinkedList<>();
            timeFramesToRemove = new LinkedList<>();

            for(int i=0; i<parentLinearLayout.getChildCount(); i++)
            {
                TextView text;
                EditText editText;

                View v = (View) parentLinearLayout.getChildAt(i);
                text = v.findViewById(R.id.startTime);
                time = (String) text.getText();
                hour = Integer.parseInt(time.split(":")[0]);
                minute = Integer.parseInt(time.split(":")[1]);
                startTime = hour*3600000 + minute*60000;
                text = v.findViewById(R.id.endTime);
                time = (String) text.getText();
                hour = Integer.parseInt(time.split(":")[0]);
                minute = Integer.parseInt(time.split(":")[1]);
                endTime = hour*3600000+minute*60000;
                editText = v.findViewById(R.id.scheduleTempEditText);
                tempValue = Integer.parseInt(editText.getText().toString());

                timeFrameHandler = new TimeFrameHandler(day, startTime, endTime, tempValue);
                visibleTimeFrames.add(timeFrameHandler);

            }
            Collections.sort(visibleTimeFrames, new TimeFrameComparator());

            if(checkTimeFrames(visibleTimeFrames))
            {
                for(TimeFrameHandler timeFrame:visibleTimeFrames)
                {
                    if(!timeFrames.contains(timeFrame))
                    {
                        timeFrames.add(timeFrame);
                    }
                }

                for(TimeFrameHandler timeFrame:timeFrames)
                {
                    if(timeFrame.getDay() == day)
                    {
                        if(!visibleTimeFrames.contains(timeFrame))
                        {
                            timeFramesToRemove.add(timeFrame);
                        }
                    }
                }

                timeFrames.removeAll(timeFramesToRemove);

                Collections.sort(timeFrames, new TimeFrameComparator());

                for(Room current: MainActivity.roomList)
                {
                    if(scheduleId == 0)
                    {
                        if(current.getName().equals(scheduleFor))
                        {
                            if(!timeFrames.isEmpty())
                            {
                                current.setBooleanSchedule(true);
                                current.setTimeFrameHandlerList(timeFrames);
                            } else
                                {
                                    current.setBooleanSchedule(false);
                                    current.setTimeFrameHandlerList(null);
                                    current.setBooleanActiveSchedule(false);
                                }
                        }
                    }

                    if(scheduleId != 0 && current.getGroupId()!=0)
                    {
                        if(current.getGroupName().equals(scheduleFor))
                        {
                            if(!timeFrames.isEmpty())
                            {
                                current.setBooleanSchedule(true);
                                current.setTimeFrameHandlerList(timeFrames);
                            } else
                                {
                                    current.setBooleanSchedule(false);
                                    current.setTimeFrameHandlerList(null);
                                    current.setBooleanActiveSchedule(false);
                                }
                        }
                    }
                }

                String result = ServerConnection.saveToDB(MainActivity.roomList);
                displayToastAboveButton(findViewById(R.id.createScheduleBtn), result);

            } else
            {
                displayToastAboveButton(findViewById(R.id.createScheduleBtn), "Incorrect timeframes, please make sure there are no time conflicts and the start times are less than the end times.");
            }

        } catch(Exception e)
        {
            e.printStackTrace();
            displayToastAboveButton(findViewById(R.id.createScheduleBtn), "Timeframe fields cannot be empty.");
        }

    }

    public void rejectSchedule(View view)
    {
        Intent intent = new Intent(ScheduleDateManagementActivity.this, ScheduleManagementActivity.class);
        intent.putExtra(ScheduleManagementActivity.EXTRA_MESSAGE, scheduleFor);
        intent.putExtra(ScheduleManagementActivity.EXTRA_INT_MESSAGE, scheduleId);
        startActivity(intent);
    }

    public void deleteTimeFrame(View view)
    {
        String time;
        long startTime = 0;
        long endTime = 0;
        int tempValue = 0;
        TextView text;
        EditText editText;

        View v = (View) view.getParent();
        text = v.findViewById(R.id.startTime);
        time = (String) text.getText();
        if(!time.equals(""))
        {
            hour = Integer.parseInt(time.split(":")[0]);
            minute = Integer.parseInt(time.split(":")[1]);
            startTime = hour*3600000 + minute*60000;
        }
            text = v.findViewById(R.id.endTime);
            time = (String) text.getText();
            if(!time.equals(""))
        {
            hour = Integer.parseInt(time.split(":")[0]);
            minute = Integer.parseInt(time.split(":")[1]);
            endTime = hour*3600000+minute*60000;
        }
        editText = v.findViewById(R.id.scheduleTempEditText);
        time = editText.getText().toString();
        if(!time.equals(""))
        {
            tempValue = Integer.parseInt(time);
        }

        timeFrameHandler = new TimeFrameHandler(day, startTime, endTime, tempValue);
        timeFrames.remove(timeFrameHandler);
        parentLinearLayout.removeView((View) view.getParent());
    }

    public void setStartTime(View view)
    {

        View v = (View) view.getParent();
        currentView = v.findViewById(R.id.startTime);
        String time = currentView.getText().toString();

        if(time.equals(""))
        {
            c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else
        {
            hour =  Integer.valueOf(time.split(":")[0]);
            minute = Integer.valueOf(time.split(":")[1]);
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleDateManagementActivity.this,TimePickerDialog.THEME_HOLO_DARK, ScheduleDateManagementActivity.this, hour, minute , true);
        timePickerDialog.show();
    }

    public void setEndTime(View view)
    {

        View v = (View) view.getParent();
        currentView = v.findViewById(R.id.endTime);
        String time = currentView.getText().toString();

        if(time.equals(""))
        {
            c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else
            {
                hour =  Integer.valueOf(time.split(":")[0]);
                minute = Integer.valueOf(time.split(":")[1]);
            }
        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleDateManagementActivity.this,TimePickerDialog.THEME_HOLO_DARK, ScheduleDateManagementActivity.this, hour, minute , true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        selectedHour = hourOfDay;
        selectedMinute = minute;
        currentView.setText(selectedHour+":"+selectedMinute);
    }

    public boolean checkTimeFrames(List<TimeFrameHandler> visibleTimeFrames)
    {
        for(TimeFrameHandler current:visibleTimeFrames)
        {
            if(current.getStartTime() >= current.getEndTime())
            {
                return false;
            }
        }

        for(int i=0; i<visibleTimeFrames.size()-1; i++)
        {
            if(visibleTimeFrames.get(i).getEndTime()>visibleTimeFrames.get(i+1).getStartTime())
            {
                return false;
            }
        }
        return true;
    }

    public String setTime(long milliseconds)
    {
        long hour = milliseconds/3600000;
        long minute = (milliseconds%3600000)/60000;
        return String.format("%02d:%02d",hour,minute);
    }

    private void displayToastAboveButton(View v, String message)
    {
        int xOffset = 0;
        int yOffset = 0;
        Rect gvr = new Rect();

        View parent = (View) v.getParent();
        int parentHeight = parent.getHeight();

        if (v.getGlobalVisibleRect(gvr))
        {
            View root = v.getRootView();

            int halfHeight = root.getBottom() / 2;

            int parentCenterY = ((gvr.bottom - gvr.top) / 2) + gvr.top;

            if (parentCenterY <= halfHeight)
            {
                yOffset = -(halfHeight - parentCenterY) - parentHeight;
            }
            else
            {
                yOffset = (parentCenterY - halfHeight) - parentHeight;
            }
        }

        Toast toast = Toast.makeText(ScheduleDateManagementActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, xOffset, yOffset);
        toast.show();
    }
}