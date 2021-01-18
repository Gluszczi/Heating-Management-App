package com.heatingManagement;

import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;

import javax.persistence.*;
import java.util.List;

@Entity
public class Room
{
    @Id
    private int id;
    private String name;
    private int temp;

    private int groupId;
    private String groupName;

    boolean hasSchedule;
    boolean hasActiveSchedule;
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<TimeFrameHandler> timeFrameHandlerList;

    public Room() {}

    public Room(int id)
    {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTemp() {
        return temp;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
}
