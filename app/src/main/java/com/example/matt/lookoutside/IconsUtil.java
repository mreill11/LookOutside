package com.example.matt.lookoutside;

import android.graphics.drawable.Drawable;

public class IconsUtil {

    public static Drawable getDrawable(String aType) {

        if (aType.equals("clear-night")) {
            return new SkyconsDrawable.ClearNight();
        } else if (aType.equals("rain")) {
            return new SkyconsDrawable.Rain();
        } else if (aType.equals("clear-day")) {
            return new SkyconsDrawable.ClearDay();
        } else if (aType.equals("snow")) {
            return new SkyconsDrawable.Snow();
        } else if (aType.equals("sleet")) {
            return new SkyconsDrawable.Sleet();
        } else if (aType.equals("fog")) {
            return new SkyconsDrawable.Fog();
        } else if (aType.equals("cloudy")) {
            return new SkyconsDrawable.Cloudy();
        } else if (aType.equals("partly-cloudy-day")) {
            return new SkyconsDrawable.PartlyCloudy();
        } else if (aType.equals("partly-cloudy-night")) {
            return new SkyconsDrawable.PartlyCloudyNight();
        } else {
            return null;
        }
    }
}