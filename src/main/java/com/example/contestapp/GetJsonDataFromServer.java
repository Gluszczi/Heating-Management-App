package com.example.contestapp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import static com.example.contestapp.MainActivity.roomList;

public  class GetJsonDataFromServer extends AsyncTask<String , Void ,String> {
    String server_response;
    private ProgressDialog dialog;

    GetJsonDataFromServer(ProgressDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Loading data from database");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL("http://192.168.1.133/home/rooms/get");
            URL newurl = new URL(url.getProtocol(), url.getHost(), 8080, url.getFile());
            urlConnection = (HttpURLConnection) newurl.openConnection();
            urlConnection.setConnectTimeout(5000);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());

                try {

                    JSONArray jsonArray = new JSONArray(server_response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Room r = new Room();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = Integer.parseInt(jsonObject.getString("id"));
                        String name = jsonObject.getString("name");
                        int temp = Integer.parseInt(jsonObject.getString("temp"));
                        int groupId = Integer.parseInt(jsonObject.getString("groupId"));
                        if(!jsonObject.isNull("groupName")) {
                            String groupName = jsonObject.getString("groupName");
                            r.setGroupName(groupName);
                        } else
                            {
                                r.setGroupName(null);
                            }

                        r.setId(id);
                        r.setName(name);
                        r.setTemp(temp);
                        r.setGroupId(groupId);

                        boolean booleanSchedule = jsonObject.getBoolean("booleanSchedule");
                        r.setBooleanSchedule(booleanSchedule);
                        boolean booleanActiveSchedule = jsonObject.getBoolean("booleanActiveSchedule");
                        r.setBooleanActiveSchedule(booleanActiveSchedule);

                        if(booleanSchedule)
                        {
                            r.setTimeFrameHandlerList(new LinkedList<TimeFrameHandler>());
                            JSONArray jsonArray1 = jsonObject.getJSONArray("timeFrameHandlerList");
                            for(int j=0; j<jsonArray1.length(); j++)
                            {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                long day = Long.parseLong(jsonObject1.getString("day"));
                                long startTime = Long.parseLong(jsonObject1.getString("startTime"));
                                long endTime = Long.parseLong(jsonObject1.getString("endTime"));
                                int tempValue = Integer.parseInt(jsonObject1.getString("tempValue"));
                                TimeFrameHandler timeFrameHandler = new TimeFrameHandler(day, startTime, endTime, tempValue);
                                r.getTimeFrameHandlerList().add(timeFrameHandler);
                            }
                        }
                        roomList.add(r);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Log.v("CatalogClient", server_response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("Response", "" + server_response);
        Collections.sort(roomList, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return Integer.compare(o1.getGroupId(), o2.getGroupId());
            }
        });
        this.dialog.dismiss();
    }


    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}