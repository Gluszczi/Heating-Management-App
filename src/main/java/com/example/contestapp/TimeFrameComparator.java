package com.example.contestapp;

import java.util.Comparator;

public class TimeFrameComparator implements Comparator<TimeFrameHandler>
{
    @Override
    public int compare(TimeFrameHandler tf1, TimeFrameHandler tf2)
    {
        int result = Long.compare(tf1.getDay(), tf2.getDay());
        if (result != 0) {
            return result;
        }
        return Long.compare(tf1.getStartTime(), tf2.getStartTime());
    }
}
