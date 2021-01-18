package com.example.heatingmanagementapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServerConnection
{
    private ServerConnection() {};

    public static String saveToDB(int id, String name, int value)
    {
        String result = "error occurred";

        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("id", id);
            post_dict.put("name", name);
            post_dict.put("temp", value);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            AsyncTask<String, String, String> postTask = new ServerConnection.SendJsonDataToServer("http://192.168.1.133/saveToDBNewRoom").execute(String.valueOf(post_dict));
            try {
                result = postTask.get();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String saveToDB(List<Room> roomList)
    {
        String result = "error occurred";
        JSONArray jsonArray = new JSONArray();

        for(Room room:roomList)
        {
            int id = room.getId();
            String name = room.getName();
            int value = room.getTemp();
            String groupName;
            int groupId;
            JSONObject post_dict = new JSONObject();
            if(room.getGroupName() != null) {
                groupName = room.getGroupName();
                groupId = room.getGroupId();
            } else
            {
                groupName = null;
                groupId = 0;
            }
            try {
                post_dict.put("id", id);
                post_dict.put("name", name);
                post_dict.put("temp", value);
                post_dict.put("groupId", groupId );
                post_dict.put("groupName", groupName);
                post_dict.put("booleanSchedule", room.getBooleanSchedule());
                post_dict.put("booleanActiveSchedule", room.getBooleanActiveSchedule());
                if(room.getBooleanSchedule())
                {
                    JSONArray jarray = new JSONArray();
                    for(TimeFrameHandler tfh:room.getTimeFrameHandlerList())
                    {
                        JSONObject jobject = new JSONObject();
                        jobject.put("day", Long.toString(tfh.getDay()));
                        jobject.put("startTime", Long.toString(tfh.getStartTime()));
                        jobject.put("endTime", Long.toString(tfh.getEndTime()));
                        jobject.put("tempValue", Integer.toString(tfh.getTempValue()));
                        jarray.put(jobject);
                    }
                    post_dict.put("timeFrames", jarray);
                }
                jsonArray.put(post_dict);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonArray.length() > 0) {
            AsyncTask<String, String, String> postTask =  new SendJsonDataToServer("http://192.168.1.133/saveToDB").execute(String.valueOf(jsonArray));
            try {
                result = postTask.get();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }


    static class SendJsonDataToServer extends AsyncTask<String,String,String> {

        SendJsonDataToServer(String urlAddress)
        {
            this.urlAddress = urlAddress;
        }

        String urlAddress;

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlAddress);
                URL newurl = new URL(url.getProtocol(), url.getHost(), 8080, url.getFile());
                urlConnection = (HttpURLConnection) newurl.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setConnectTimeout(10000);

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);

                writer.close();
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {

                    return null;
                }
                JsonResponse = buffer.toString();

                try {
                    return JsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s)
        {
        }

    }
}
