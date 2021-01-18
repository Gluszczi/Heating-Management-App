package com.example.contestapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    public static List<Room> roomList = new LinkedList<>();
    public static long hashCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       dialog = new ProgressDialog(MainActivity.this);

        if(roomList.isEmpty())
        {
            new GetJsonDataFromServer(dialog).execute();

        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void goToRoomsActivity(View view) {

        Intent intent = new Intent(MainActivity.this, GroupActivity.class );
        startActivity(intent);
    }

    public void goToGroupManagementActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, GroupManagementActivity.class);
        startActivity(intent);
    }

    public void goToScheduleActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void goToAddRoomActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, AddRoomActivity.class);
        startActivity(intent);
    }

    public void goToMonitorActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, MonitorActivity.class);
        startActivity(intent);
    }
}
