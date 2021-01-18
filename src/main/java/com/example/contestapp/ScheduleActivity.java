package com.example.contestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.contestapp.MainActivity.roomList;


public class ScheduleActivity extends AppCompatActivity
{

    ListView scheduleManagementListView;
    MyListAdapter listAdapter;
    List<String> names;
    List<String> itemTypeList;
    List<Integer> images;
    Integer[] imagesId;
    String[] namesArray;
    String[] itemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Collections.sort(roomList, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return Integer.compare(o1.getGroupId(), o2.getGroupId());
            }
        });

        names = new ArrayList<>();
        itemTypeList = new ArrayList<>();
        images = new ArrayList<>();

        for(Room room:roomList)
        {
            if(room.getGroupId() == 0)
            {
                //names.add(room.getName()+" "+"(single room)");
                names.add(room.getName());
                itemTypeList.add("single room");
                if (room.getBooleanSchedule() && !room.getTimeFrameHandlerList().isEmpty())
                {
                    images.add(R.drawable.calendarico);
                } else
                    {
                        images.add(R.drawable.blankbackground);
                    }
            }
        }

        for(Room room:roomList)
        {
            if(room.getGroupId() !=0 && !names.contains(room.getGroupName()))
            {
                //names.add(room.getGroupName()+" "+"(group)");
                names.add(room.getGroupName());
                itemTypeList.add("group");
                if (room.getBooleanSchedule() && !room.getTimeFrameHandlerList().isEmpty())
                {
                    images.add(R.drawable.calendarico);
                } else
                {
                    images.add(R.drawable.blankbackground);
                }
            }
        }

        namesArray = new String[names.size()];
        itemType = new String[itemTypeList.size()];
        imagesId = new Integer[images.size()];

        for(int i=0; i<images.size(); i++)
        {
            namesArray[i] = names.get(i);
            itemType[i] = itemTypeList.get(i) ;
            imagesId[i] = images.get(i);
        }


        scheduleManagementListView = findViewById(R.id.scheduleListView);
        listAdapter = new MyListAdapter(ScheduleActivity.this, namesArray, itemType, imagesId);
        scheduleManagementListView.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //String message =  (String) scheduleManagementListView.getItemAtPosition(position);
                int messageId = -1;
                TextView text;
                text = view.findViewById(R.id.typeTextView);
                String message = text.getText().toString();

                if(message.equals("single room"))
                {
                    messageId = 0;
                    //message = message.replace(" (single room)","");
                    text = view.findViewById(R.id.textView);
                    message = text.getText().toString();
                }
                if(message.equals("group"))
                {
                    //message = message.replace(" (group)","");
                    text = view.findViewById(R.id.textView);
                    message = text.getText().toString();
                    for(Room room:roomList)
                    {
                        if(room.getGroupId() != 0 && room.getGroupName().equals(message))
                        {
                            messageId = room.getGroupId();
                            break;
                        }
                    }
                }
                Intent intent = new Intent(ScheduleActivity.this, ScheduleManagementActivity.class);
                intent.putExtra(ScheduleManagementActivity.EXTRA_MESSAGE, message);
                intent.putExtra(ScheduleManagementActivity.EXTRA_INT_MESSAGE, messageId);
                startActivity(intent);
            }
        };

        scheduleManagementListView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

}