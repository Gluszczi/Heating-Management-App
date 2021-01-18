package com.heatingManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class RoomResource {

    private static long hashCode;

    @Autowired
    RoomRepository repo;

    @Autowired
    TimeFrameHandlerRepository timeRepo;


    @GetMapping("/home/rooms/get")
    public List<Room> RoomsPage(Model model) {
        List<Room> rooms = (List<Room>) repo.findAll();

        return rooms;
    }

    @RequestMapping(value = "/saveToDBNewRoom", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String saveToDBNewRoom( @RequestBody String str)
    {
        String response = "";

        try
        {
            Room r = new Room();
            JSONObject jsonObject = new JSONObject(str);
            int id = jsonObject.getInt("id");
            r.setId(id);
            String name = jsonObject.getString("name");
            r.setName(name);
            int temp = jsonObject.getInt("temp");
            r.setTemp(temp);
            repo.save(r);
            response = "Data successfully saved to database";

        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping(value = "/saveToDB", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    String saveToDB( @RequestBody String str )
    {
        String response = "";

        try {
            List<TimeFrameHandler> allTimeFrames = (List<TimeFrameHandler>) timeRepo.findAll();

            JSONArray jsonArray = new JSONArray(str);
            for(int i=0; i<jsonArray.length(); i++)
            {
                Room r = new Room();
                List<TimeFrameHandler> currentTimeFrames = new LinkedList<>();
                List<TimeFrameHandler> timeFrameHandlersToVerify = new LinkedList<>();
                List<TimeFrameHandler> timeFramesToRemove = new LinkedList<>();
                boolean booleanSchedule = false;

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                r.setId(id);
                if (!jsonObject.isNull("name")) {
                    String name = jsonObject.getString("name");
                    r.setName(name);
                }
                if (!jsonObject.isNull("temp")) {
                    int temp = jsonObject.getInt("temp");
                    r.setTemp(temp);
                }
                if (!jsonObject.isNull("groupId")) {
                    int groupId = jsonObject.getInt("groupId");
                    r.setGroupId(groupId);
                }
                if (!jsonObject.isNull("groupName")) {
                    String groupName = jsonObject.getString("groupName");
                    r.setGroupName(groupName);
                } else
                    {
                        r.setGroupName(null);
                    }
                if (!jsonObject.isNull("booleanSchedule")) {
                    booleanSchedule = jsonObject.getBoolean("booleanSchedule");
                    r.setBooleanSchedule(booleanSchedule);
                }
                if (!jsonObject.isNull("booleanActiveSchedule")) {
                    boolean booleanActiveSchedule = jsonObject.getBoolean("booleanActiveSchedule");
                    r.setBooleanActiveSchedule(booleanActiveSchedule);
                }

                if (booleanSchedule) {

                    for (TimeFrameHandler timeFrameHandler : allTimeFrames) {
                        if (timeFrameHandler.getRoom().getId() == r.getId()) {
                            currentTimeFrames.add(timeFrameHandler);
                        }
                    }

                    if (r.getTimeFrameHandlerList() == null) {
                        r.setTimeFrameHandlerList(new LinkedList<TimeFrameHandler>());
                    }

                    JSONArray jarray = jsonObject.getJSONArray("timeFrames");
                    for (int j = 0; j < jarray.length(); j++) {
                        JSONObject jObject = jarray.getJSONObject(j);
                        TimeFrameHandler timeFrame = new TimeFrameHandler();
                        timeFrame.setDay(Long.parseLong((String) jObject.get("day")));
                        timeFrame.setStartTime(Long.parseLong((String) jObject.get("startTime")));
                        timeFrame.setEndTime(Long.parseLong((String) jObject.get("endTime")));
                        timeFrame.setTempValue(Integer.parseInt((String) jObject.get("tempValue")));
                        timeFrame.setRoom(r);
                        timeFrameHandlersToVerify.add(timeFrame);
                        if (!currentTimeFrames.contains(timeFrame)) {
                            currentTimeFrames.add(timeFrame);
                        }
                    }

                    for (TimeFrameHandler timeFrame : currentTimeFrames) {
                        if (!timeFrameHandlersToVerify.contains(timeFrame)) {
                            timeFramesToRemove.add(timeFrame);
                            timeRepo.delete(timeFrame);
                        }
                    }

                    currentTimeFrames.removeAll(timeFramesToRemove);

                    Collections.sort(currentTimeFrames, new TimeFrameHandlerComparator());

                    if (checkTimeFrames(currentTimeFrames)) {
                        for (TimeFrameHandler timeFrameHandler : currentTimeFrames) {
                            if (!allTimeFrames.contains(timeFrameHandler)) {
                                r.getTimeFrameHandlerList().add(timeFrameHandler);
                            }
                        }
                        response = "Data successfully saved to database";
                    }
                } else {
                    for (TimeFrameHandler timeFrame : allTimeFrames) {
                        if (timeFrame.getRoom().getId() == r.getId()) {
                            timeRepo.delete(timeFrame);
                        }
                    }
                }
                repo.save(r);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            response = "error occurred";
            return response;
        }
        return response;
    }

    public boolean checkTimeFrames(List<TimeFrameHandler> currentTimeFrames)
    {
        for(TimeFrameHandler current:currentTimeFrames)
        {
            if(current.getStartTime() >= current.getEndTime())
            {
                return false;
            }
        }

        for(int i=0; i<currentTimeFrames.size()-1; i++)
        {
            if(currentTimeFrames.get(i).getDay() == currentTimeFrames.get(i+1).getDay())
            {
                if(currentTimeFrames.get(i).getEndTime() > currentTimeFrames.get(i+1).getStartTime())
                {
                    return false;
                }
            }
        }
        return true;
    }
}