package com.example.heatingmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.contestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.heatingmanagementapp.ServerConnection.saveToDB;

public class GroupManagementActivity extends AppCompatActivity {

    EditText groupNameEditText;
    ArrayAdapter<String> listAdapter;
    List<Room> availableRoomList;
    List<Room> userSelectedItems;
    List<Room> userRejectedItems;
    List<String> groupNames;
    Set<String> groupNamesSet;
    int newGroupId;
    ListView groupListView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Collections.sort(MainActivity.roomList, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return Integer.compare(o1.getGroupId(), o2.getGroupId());
            }
        });

         //new ProgressDialog(GroupManagementActivity.this);

        groupNamesSet = new LinkedHashSet<>();

        for(Room room: MainActivity.roomList)
        {
            if(room.getGroupName() != null)
            {
                groupNamesSet.add(room.getGroupName());
            }
        }

        groupNames = new ArrayList<>();
        groupNames.addAll(groupNamesSet);

        groupNameEditText = findViewById(R.id.groupName);
        groupListView = findViewById(R.id.groupsListView);
        listAdapter = new ArrayAdapter<>(GroupManagementActivity.this, android.R.layout.simple_list_item_1, groupNames );
        groupListView.setAdapter(listAdapter);


        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String [] listItems;
                boolean [] checkedItems;
                int groupNumber = 0;
                final String selectedGroupName = String.valueOf(groupListView.getItemAtPosition(position));
                availableRoomList = new LinkedList<>();
                userSelectedItems = new LinkedList<>();
                userRejectedItems = new LinkedList<>();
                final List<TimeFrameHandler> schedule = new LinkedList<>();
                boolean hasSchedule = false;
                boolean hasActiveSchedule = false;
                int groupTemp = 0;

                for(Room room: MainActivity.roomList)
                {
                    if(room.getGroupId() == 0 || room.getGroupName().equals(selectedGroupName))
                    {
                        availableRoomList.add(room);
                    }
                }

                for(Room room:availableRoomList)
                {
                    if(room.getGroupId() != 0 && room.getGroupName().equals(selectedGroupName))
                    {
                        groupNumber = room.getGroupId();
                        hasSchedule = room.getBooleanSchedule();
                        hasActiveSchedule = room.getBooleanActiveSchedule();
                        groupTemp = room.getTemp();
                        if(hasSchedule)
                        {
                            schedule.addAll(room.getTimeFrameHandlerList());
                        }
                        break;
                    }
                }

                final int selectedGroupNumber = groupNumber;
                final boolean finalHasSchedule = hasSchedule;
                final boolean finalHasActiveSchedule = hasActiveSchedule;
                final int finalGroupTemp = groupTemp;

                listItems = new String[availableRoomList.size()];

                for(int i=0; i<availableRoomList.size(); i++)
                {
                    listItems[i] = availableRoomList.get(i).getName();
                }

                checkedItems = new boolean[listItems.length];

                for(int i=0; i<checkedItems.length; i++)
                {
                    if(availableRoomList.get(i).getGroupId()!=0 && selectedGroupName.equals(availableRoomList.get(i).getGroupName()))
                    {
                        checkedItems[i] = true;
                        userSelectedItems.add(availableRoomList.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupManagementActivity.this);
                builder.setTitle("Modify group");
                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        if(isChecked)
                        {
                            userSelectedItems.add(availableRoomList.get(which));
                            userRejectedItems.remove(availableRoomList.get(which));
                        } else
                        {
                            userSelectedItems.remove(availableRoomList.get(which));
                            userRejectedItems.add(availableRoomList.get(which));
                        }
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        for(Room current:userSelectedItems)
                        {
                            for(Room room: MainActivity.roomList)
                            {
                                if(current.getName().equals(room.getName()))
                                {
                                    room.setTemp(finalGroupTemp);
                                    room.setGroupId(selectedGroupNumber);
                                    room.setGroupName(selectedGroupName);
                                    if(finalHasSchedule)
                                    {
                                        room.setBooleanSchedule(true);
                                        room.setTimeFrameHandlerList(schedule);
                                        room.setBooleanActiveSchedule(finalHasActiveSchedule);
                                    }
                                    break;
                                }
                            }
                        }

                        for(Room current:userRejectedItems)
                        {
                            for(Room room: MainActivity.roomList)
                            {
                                if(current.getName().equals(room.getName()))
                                {
                                    room.setGroupId(0);
                                    room.setGroupName(null);
                                    room.setBooleanSchedule(false);
                                    room.setBooleanActiveSchedule(false);
                                    room.setTimeFrameHandlerList(null);
                                    break;
                                }
                            }
                        }

                        if(userSelectedItems.isEmpty())
                        {
                            groupNames.remove(selectedGroupName);
                            groupListView.setAdapter(listAdapter);
                        }

                        ServerConnection.saveToDB(MainActivity.roomList);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(Room current:availableRoomList)
                        {
                            for(Room room: MainActivity.roomList)
                            {
                                if(current.getId() == room.getId() )
                                {
                                    room.setGroupId(0);
                                    room.setGroupName(null);
                                    room.setBooleanSchedule(false);
                                    room.setBooleanActiveSchedule(false);
                                    room.setTimeFrameHandlerList(null);
                                    break;
                                }
                            }
                        }
                        groupNames.remove(selectedGroupName);
                        groupListView.setAdapter(listAdapter);
                        ServerConnection.saveToDB(MainActivity.roomList);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        groupListView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(GroupManagementActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    public void createGroup(View view)
    {
        final String groupName;
        String [] listItems;
        boolean [] checkedItems;

        groupName = groupNameEditText.getText().toString();

        if(!groupNamesSet.contains(groupName))
        {
            availableRoomList = new LinkedList<>();
            userSelectedItems = new LinkedList<>();
            userRejectedItems = new LinkedList<>();

            int groupId = 0;

            for(Room room: MainActivity.roomList)
            {
                if(room.getGroupName() != null)
                {
                    if(groupId < room.getGroupId())
                    {
                        groupId = room.getGroupId();
                    }
                }
            }

            newGroupId = groupId+1;

            if(groupNameEditText.getText().toString().isEmpty())
            {
                Toast.makeText(GroupManagementActivity.this, "Group name cannot be empty", Toast.LENGTH_LONG).show();
            } else {

                for (Room room : MainActivity.roomList) {
                    if (room.getGroupName() == null) {
                        availableRoomList.add(room);
                    }
                }

                listItems = new String[availableRoomList.size()];

                for (int i = 0; i < availableRoomList.size(); i++) {
                    listItems[i] = availableRoomList.get(i).getName();
                }

                checkedItems = new boolean[listItems.length];

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupManagementActivity.this);
                builder.setTitle("Available rooms");
                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        int currentRoom;
                        currentRoom = availableRoomList.get(which).getId();

                        if (isChecked) {
                            userSelectedItems.add(availableRoomList.get(which));
                        } else {
                            for (Room current : userSelectedItems) {
                                if (current.getId() == currentRoom) {
                                    current.setGroupName(null);
                                    current.setGroupId(0);
                                    userSelectedItems.remove(current);
                                    break;
                                }
                            }
                        }
                    }
                });

                builder.setCancelable(false);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Room current : userSelectedItems) {
                            for (Room room : MainActivity.roomList) {
                                if (current.getId() == room.getId()) {
                                    room.setGroupName(groupName);
                                    room.setGroupId(newGroupId);
                                    room.setTemp(20);
                                    break;
                                }
                            }
                        }
                        ServerConnection.saveToDB(MainActivity.roomList);
                        groupNames.add(groupName);
                        groupListView.setAdapter(listAdapter);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else
            {
                Toast.makeText(GroupManagementActivity.this, "This group name is already in use", Toast.LENGTH_LONG).show();
            }

    }
}