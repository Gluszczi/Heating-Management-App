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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.contestapp.MainActivity.roomList;

 public class GroupActivity extends AppCompatActivity {

    ArrayAdapter<String> listAdapter;
    ListView groupList;
    List<String> groupNames;
    Set<String> groupNamesSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Temperature Management");

        Collections.sort(roomList, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return Integer.compare(o1.getGroupId(), o2.getGroupId());
            }
        });

        groupNamesSet = new LinkedHashSet<>();
        groupNames = new LinkedList<>();

        for(Room room:roomList)
        {
            if(room.getGroupId()!= 0)
            {
                groupNamesSet.add(room.getGroupName());
            }
        }

        groupNames.addAll(groupNamesSet);
        groupNames.add(0, "Show rooms without group");
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        groupList = findViewById(R.id.groupContainer);
        groupList.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int idMessage = 0;
                Intent intent = new Intent(GroupActivity.this, RoomsActivity.class);
                for(Room room:roomList)
                {
                    if(room.getGroupName()!= null && room.getGroupName().equals(groupNames.get(position)))
                    {
                        idMessage = room.getGroupId();
                        break;
                    }
                }

                intent.putExtra(RoomsActivity.EXTRA_INT_MESSAGE, idMessage);
                startActivity(intent);
            }
        };
        groupList.setOnItemClickListener(itemClickListener);
    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         Intent intent = new Intent(GroupActivity.this, MainActivity.class);
         startActivity(intent);
         return true;
     }
}