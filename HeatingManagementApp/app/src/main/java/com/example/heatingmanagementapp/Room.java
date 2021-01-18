package com.example.heatingmanagementapp;


import java.util.List;
import java.util.Objects;

public class Room
{
    private int Id;
    private String name;
    private int temp;

    private int groupId;
    private String groupName;

    private boolean hasSchedule;
    private boolean hasActiveSchedule;
    private List<TimeFrameHandler> timeFrameHandlerList;


    public int getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public int getTemp() {
        return temp;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean getBooleanSchedule() {
        return hasSchedule;
    }

    public void setBooleanSchedule(boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public boolean getBooleanActiveSchedule() {
        return hasActiveSchedule;
    }

    public void setBooleanActiveSchedule(boolean hasActiveSchedule) {
        this.hasActiveSchedule = hasActiveSchedule;
    }

    public List<TimeFrameHandler> getTimeFrameHandlerList() {
        return timeFrameHandlerList;
    }

    public void setTimeFrameHandlerList(List<TimeFrameHandler> timeFrameHandlerList) {
        this.timeFrameHandlerList = timeFrameHandlerList;
    }

    public void downTempValue()
    {
        this.temp -= 1;
    }

    public void upTempValue()
    {
        this.temp += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
