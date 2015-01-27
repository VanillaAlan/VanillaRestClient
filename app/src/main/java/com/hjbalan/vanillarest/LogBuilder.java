package com.nut.vanilla;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by alan on 15/1/27.
 */
public class LogBuilder {

    private static DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    
    private StringBuilder mStringBuilder;
    
    public LogBuilder() {
        mStringBuilder = new StringBuilder();
    }
    
    public LogBuilder append(String eventId, String labelId) {
        mStringBuilder.append(new Event(eventId, labelId).toString());
        mStringBuilder.append("\n");
        return this;
    }
    
    public void build() {
        Timber.i(mStringBuilder.toString());
        mStringBuilder.setLength(0);
    }
    
    public static class Event {
        String timeStamp;
        String eventId;
        String labelId;
        
        public Event(String eventId, String labelId) {
            timeStamp = sDateFormat.format(new Date());
            this.eventId = eventId;
            this.labelId = labelId;
        }

        @Override
        public String toString() {
            return timeStamp + " " + eventId + " " + labelId;
        }
    }
}
