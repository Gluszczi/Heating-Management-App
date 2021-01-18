package com.example.heatingmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contestapp.R;

public class AddRoomActivity extends AppCompatActivity {

    EditText roomName;

    int basicId;
    int id;
    String name;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        basicId = 101;
        roomName = (EditText) findViewById(R.id.textView2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(AddRoomActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    public void saveToDB(View view) {
        String result;
        if(MainActivity.roomList != null)
        {
            id = basicId + MainActivity.roomList.size();
        } else
            {
                id = basicId;
            }

        name = roomName.getText().toString();

        Room r = new Room();
        r.setName(name);

        if(MainActivity.roomList.contains(r))
        {
            //Toast.makeText(AddRoomActivity.this, "Room name already exists", Toast.LENGTH_LONG).show();
            displayToastUnderButton(findViewById(R.id.button), "Room name already exists");
        } else
            {
                if(name.equals(""))
                {
                    //Toast.makeText(AddRoomActivity.this, "Please insert room name", Toast.LENGTH_LONG).show();
                    displayToastUnderButton(findViewById(R.id.button), "Please insert room name");
                } else
                {
                    value = 20;
                    result = ServerConnection.saveToDB(id, name, value);
                    if(result.equals("Data successfully saved to database\n"))
                    {
                        r.setId(id);
                        r.setTemp(value);
                        MainActivity.roomList.add(r);
                    }
                    //Toast.makeText(AddRoomActivity.this, result, Toast.LENGTH_LONG).show();
                    displayToastUnderButton(findViewById(R.id.button), result);
                }
            }
    }


    private void displayToastUnderButton(View v, String message)
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
                yOffset = (halfHeight + parentCenterY) - parentHeight;
            }
            else
            {
                yOffset = (parentCenterY + halfHeight) - parentHeight;
            }
        }

        Toast toast = Toast.makeText(AddRoomActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, xOffset, yOffset);
        toast.show();
    }
}