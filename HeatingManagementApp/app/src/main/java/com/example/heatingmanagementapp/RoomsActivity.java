package com.example.heatingmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.contestapp.R;

import java.util.LinkedList;
import java.util.List;

import static com.example.heatingmanagementapp.ServerConnection.saveToDB;

public class RoomsActivity extends AppCompatActivity {

    public static final String EXTRA_INT_MESSAGE = "intMessage";

    LinearLayout parentLinearLayout;
    LinearLayout switchBtnLinearLayout;
    int groupId;
    String groupName;
    ArrayAdapter<String> adapter;
    List<String> roomNames;
    ListView roomsContainer;
    RadioGroup radioGroup;
    RadioButton manualControl;
    RadioButton scheduledControl;
    boolean booleanSchedule;
    boolean booleanActiveSchedule;
    TextView currentText;
    Button tempBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parentLinearLayout = findViewById(R.id.dynamicLinearLayout);
        switchBtnLinearLayout = findViewById(R.id.switchBtnLinearLayout);
        roomsContainer = findViewById(R.id.roomContainer);
        roomNames = new LinkedList<>();

        Intent intent = getIntent();
        groupId = intent.getIntExtra(EXTRA_INT_MESSAGE, -1 );

        if(groupId == 0)
        {
            getSupportActionBar().setTitle("Rooms temperature");
        } else
            {
                getSupportActionBar().setTitle("Group temperature");
            }


        if(groupId == 0)
        {
            for(Room room: MainActivity.roomList)
            {
                if(room.getGroupId() == groupId)
                {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View roomView = inflater.inflate(R.layout.single_room_row, null);
                    parentLinearLayout.addView(roomView, (parentLinearLayout.getChildCount()));
                    CheckBox checkBox = roomView.findViewById(R.id.scheduleCheckBox);
                    currentText = roomView.findViewById(R.id.roomNameView);
                    currentText.setText(room.getName());
                    String x = currentText.getText().toString();
                    currentText = roomView.findViewById(R.id.tempValueView);
                    currentText.setText(String.valueOf(room.getTemp()));
                    booleanSchedule = room.getBooleanSchedule();
                    booleanActiveSchedule = room.getBooleanActiveSchedule();

                    Button up = roomView.findViewById(R.id.upTempValueBtn);
                    Button down = roomView.findViewById(R.id.downTempValueBtn);

                    if(!booleanSchedule)
                    {
                        checkBox.setEnabled(false);
                    }
                    if(booleanActiveSchedule)
                    {
                        checkBox.setChecked(true);
                        currentText.setVisibility(View.INVISIBLE);
                        up.setVisibility(View.INVISIBLE);
                        down.setVisibility(View.INVISIBLE);
                    }
                    if(!booleanActiveSchedule)
                    {
                        roomView.findViewById(R.id.calendar_ico_image).setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        if(groupId != 0)
        {
            for(Room room: MainActivity.roomList)
            {
                if(room.getGroupId() == groupId)
                {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View roomView = inflater.inflate(R.layout.group_row, null);
                    parentLinearLayout.addView(roomView, (parentLinearLayout.getChildCount()));
                    TextView currentText = null;
                    currentText = roomView.findViewById(R.id.roomNameView);
                    currentText.setText(room.getGroupName());
                    groupName = room.getGroupName();
                    String x = currentText.getText().toString();
                    currentText = roomView.findViewById(R.id.tempValueView);
                    currentText.setText(String.valueOf(room.getTemp()));
                    booleanSchedule = room.getBooleanSchedule();
                    booleanActiveSchedule = room.getBooleanActiveSchedule();
                    break;
                }
            }

            invalidateOptionsMenu();

            for(Room room: MainActivity.roomList)
            {
                if(room.getGroupId() == groupId)
                {
                    roomNames.add(room.getName());
                }
            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roomNames);
            roomsContainer.setAdapter(adapter);
        }

        if(groupId != 0)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View switchBtn = inflater.inflate(R.layout.radio_group, null);
            switchBtnLinearLayout.addView(switchBtn, switchBtnLinearLayout.getChildCount());

            radioGroup = findViewById(R.id.toggle);
            manualControl = findViewById(R.id.manualToggle);
            scheduledControl = findViewById(R.id.scheduledToggle);

            if(!booleanSchedule)
            {
                scheduledControl.setEnabled(false);
            }

            if(booleanActiveSchedule)
            {
                scheduledControl.toggle();
                tempBtn = findViewById(R.id.downTempValueBtn);
                tempBtn.setVisibility(View.INVISIBLE);
                tempBtn = findViewById(R.id.upTempValueBtn);
                tempBtn.setVisibility(View.INVISIBLE);
                currentText = findViewById(R.id.tempValueView);
                currentText.setVisibility(View.INVISIBLE);
            } else
                {
                    manualControl.toggle();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(groupId == 0)
        {
            inflater.inflate(R.menu.menu, menu);
        }

        if(groupId != 0)
        {
            inflater.inflate(R.menu.room_activity_menu, menu);
            if(!booleanActiveSchedule)
            {
                menu.getItem(0).setVisible(false);
            } else
                {
                    menu.getItem(0).setVisible(true);
                }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(R.id.menuIco == item.getItemId())
        {
        Intent intent = new Intent(RoomsActivity.this, MainActivity.class);
        startActivity(intent);
        }

        if(R.id.calendar_navi_ico == item.getItemId())
        {
            Intent intent = new Intent(RoomsActivity.this, ScheduleManagementActivity.class);
            intent.putExtra(ScheduleManagementActivity.EXTRA_MESSAGE, groupName);
            intent.putExtra(ScheduleManagementActivity.EXTRA_INT_MESSAGE, groupId);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        ServerConnection.saveToDB(MainActivity.roomList);
    }

    public void upTempValue(View view)
    {
        int groupTemp = 20;
        View v = (View) view.getParent();
        TextView currentText;

        if (groupId == 0) {
            currentText = v.findViewById(R.id.roomNameView);
            String roomName = currentText.getText().toString();
            for (Room room : MainActivity.roomList) {
                if (room.getName().equals(roomName)) {
                    room.upTempValue();
                    currentText = v.findViewById(R.id.tempValueView);
                    currentText.setText(String.valueOf(room.getTemp()));
                    break;
                }
            }
        }

        if (groupId != 0) {
            for (Room room : MainActivity.roomList) {
                if (room.getGroupId() == groupId) {
                    room.upTempValue();
                    groupTemp = room.getTemp();
                }
            }
            currentText = findViewById(R.id.tempValueView);
            currentText.setText(String.valueOf(groupTemp));
        }

    }

    public void downTempValue(View view)
    {
        View v = (View) view.getParent();
        TextView currentText;
        currentText = v.findViewById(R.id.roomNameView);
        int groupTemp = 20;

        if(groupId == 0) {
            String roomName = currentText.getText().toString();
            for (Room room : MainActivity.roomList) {
                if (room.getName().equals(roomName)) {
                    room.downTempValue();
                    currentText = v.findViewById(R.id.tempValueView);
                    currentText.setText(String.valueOf(room.getTemp()));
                    break;
                }
            }
        }

        if(groupId != 0)
        {
            for(Room room: MainActivity.roomList)
            {
                if(room.getGroupId() == groupId)
                {
                    room.downTempValue();
                    groupTemp = room.getTemp();
                }
            }
            currentText = findViewById(R.id.tempValueView);
            currentText.setText(String.valueOf(groupTemp));
        }
    }

    public void onRadioButtonClicked(View view)
    {
        int id = radioGroup.getCheckedRadioButtonId();
        currentText = findViewById(R.id.tempValueView);

        switch(id)
        {
            case R.id.manualToggle :
                for(Room room: MainActivity.roomList)
                {
                    if(room.getGroupId() == groupId)
                    {
                        room.setBooleanActiveSchedule(false);
                    }
                }
                booleanActiveSchedule = false;
                tempBtn = findViewById(R.id.downTempValueBtn);
                tempBtn.setVisibility(View.VISIBLE);
                tempBtn = findViewById(R.id.upTempValueBtn);
                tempBtn.setVisibility(View.VISIBLE);
                currentText.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            break;

            case R.id.scheduledToggle :
                for(Room room: MainActivity.roomList)
                {
                    if(room.getGroupId() == groupId)
                    {
                        room.setBooleanActiveSchedule(true);
                    }
                }
                booleanActiveSchedule = true;
                tempBtn = findViewById(R.id.downTempValueBtn);
                tempBtn.setVisibility(View.INVISIBLE);
                tempBtn = findViewById(R.id.upTempValueBtn);
                tempBtn.setVisibility(View.INVISIBLE);
                currentText.setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
            break;
        }
    }

    public void onCheckBoxClicked(View view)
    {
        View v = (View) view.getParent();
        TextView tempValue = (TextView) v.findViewById(R.id.tempValueView);
        TextView textView = v.findViewById(R.id.roomNameView);
        String name = textView.getText().toString();
        CheckBox checkBox =  v.findViewById(R.id.scheduleCheckBox);
        boolean checked = checkBox.isChecked();

        Button up = v.findViewById(R.id.upTempValueBtn);
        Button down = v.findViewById(R.id.downTempValueBtn);

        if(checked)
        {
            for(Room room: MainActivity.roomList)
            {
                if(room.getName().equals(name))
                {
                    room.setBooleanActiveSchedule(true);
                    tempValue.setVisibility(View.INVISIBLE);
                    up.setVisibility(View.INVISIBLE);
                    down.setVisibility(View.INVISIBLE);
                    break;
                }
            }
            booleanActiveSchedule = true;
            v.findViewById(R.id.calendar_ico_image).setVisibility(View.VISIBLE);
        } else
            {
                for(Room room: MainActivity.roomList)
                {
                    if(room.getName().equals(name))
                    {
                        room.setBooleanActiveSchedule(false);
                        tempValue.setVisibility(View.VISIBLE);
                        up.setVisibility(View.VISIBLE);
                        down.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                booleanActiveSchedule = false;
                v.findViewById(R.id.calendar_ico_image).setVisibility(View.INVISIBLE);
            }
    }

    public void onClickCalendarIco(View view)
    {
        View v =(View) view.getParent();
        TextView text = v.findViewById(R.id.roomNameView);
        String name = text.getText().toString();
        Intent intent = new Intent(RoomsActivity.this, ScheduleManagementActivity.class);
        intent.putExtra(ScheduleManagementActivity.EXTRA_MESSAGE, name);
        intent.putExtra(ScheduleManagementActivity.EXTRA_INT_MESSAGE, 0);
        startActivity(intent);
    }
}