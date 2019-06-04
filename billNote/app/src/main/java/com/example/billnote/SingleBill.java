package com.example.billnote;
import android.location.LocationManager;

import java.io.Serializable;
import java.util.LinkedList;
import  java.util.Date;

public class SingleBill implements Serializable {
    private Date date;
    private String locationStr;
    private float money;
    private String event;
    public LinkedList<String> picNameList;

    public SingleBill() {
        date = new Date();
        locationStr = "";
        money = 0;
        event = "";
        picNameList = new LinkedList<String>();
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date newDate) {
        date = newDate;
    }

    public String getLocation() { return locationStr; }
    public void setLocation(String newLocationStr) { locationStr = newLocationStr; }

    public float getMoney() {
        return money;
    }
    public void setMoney(float newMoney) { money = newMoney; }

    public String getEvent() { return event; }
    public void setEvent(String newEvent) {event = newEvent; }
}
