package com.austinhlee.spoileralert;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * Created by Austin Lee on 4/15/2018.
 */

public class Spoiler {

    private String title;
    private String filterWords;
    private long reminderTime;
    private String uid;

    public Spoiler(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilterWords() {
        return filterWords;
    }

    public void setFilterWords(String filterWords) {
        this.filterWords = filterWords;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String serialize(){
        return new Gson().toJson(this);
    }

    public static Spoiler deserialize(String jsonString){
        return new Gson().fromJson(jsonString, Spoiler.class);
    }
}
