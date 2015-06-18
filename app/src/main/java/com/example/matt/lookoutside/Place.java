package com.example.matt.lookoutside;

import io.realm.RealmObject;

public class Place extends RealmObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}