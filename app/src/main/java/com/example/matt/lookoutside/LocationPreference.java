package com.example.matt.lookoutside;

import android.app.Activity;
import android.content.SharedPreferences;

public class LocationPreference {
    SharedPreferences prefs;
    public LocationPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity() {
        return prefs.getString("city", "Chicago");
    }

    void setCity(String city) {
        prefs.edit().putString("city", city).commit();
    }
}