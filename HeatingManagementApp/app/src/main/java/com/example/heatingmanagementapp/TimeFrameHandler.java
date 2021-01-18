package com.example.heatingmanagementapp;

public class TimeFrameHandler {

    private long day;
    private long startTime;
    private long endTime;
    private int tempValue;

    public TimeFrameHandler(long day, long startTime, long endTime, int tempValue)
    {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tempValue = tempValue;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

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
                && Long.compare(startTime, tfh.getStartTime()) == 0 && Long.compare(endTime, tfh.getEndTime()) == 0 && Integer.compare(tempValue, tfh.getTempValue()) == 0;
    }

    @Override
    public int hashCode()
    {
        long result;
        result = day + startTime + endTime + tempValue;
        return Long.valueOf(result).hashCode();
    }
}
