package com.heatingManagement;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class TimeFrameHandler
{
    @Id
    @JsonIgnore
    @GeneratedValue( strategy = GenerationType.AUTO)
    private int TimeFrameHandlerId;
    private long day;
    private long startTime;
    private long endTime;
    private int tempValue;

    @JsonIgnore
    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Room room;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTempValue() {
        return tempValue;
    }

    public void setTempValue(int tempValue) {
        this.tempValue = tempValue;
    }

    public int getTimeFrameHandlerId() {
        return TimeFrameHandlerId;
    }

    public void setTimeFrameHandlerId(int timeFrameHandlerId) {
        TimeFrameHandlerId = timeFrameHandlerId;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof TimeFrameHandler)) {
            return false;
        }

        TimeFrameHandler tfh = (TimeFrameHandler) o;

        return Long.compare(day, tfh.getDay()) == 0
                && Long.compare(startTime, tfh.getStartTime()) == 0 && Long.compare(endTime, tfh.getEndTime()) == 0 && Integer.compare(tempValue, tfh.getTempValue()) == 0 && Integer.compare(room.getId(), tfh.getRoom().getId()) == 0;
    }

    @Override
    public int hashCode()
    {
        long result;
        result = day + startTime + endTime + tempValue + room.getId();
        return Long.valueOf(result).hashCode();
    }
}
