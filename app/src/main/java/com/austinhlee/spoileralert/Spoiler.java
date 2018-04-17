package com.austinhlee.spoileralert;

import java.util.List;

/**
 * Created by Austin Lee on 4/15/2018.
 */

public class Spoiler {

    private String title;
    private String filterWords;
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
}
