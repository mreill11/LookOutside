package com.example.matt.lookoutside.model;

import java.util.ArrayList;

public class WeatherModel {
    private Clouds clouds;

    private Coord coord;

    private long dt;

    private String id;

    public Wind wind;

    private String name;

    public ArrayList<Weather> weather;

    private Rain rain;

    public Main main;

    public Sys sys;

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    @Override
    public String toString() {
        return "ClassPojo [clouds = " + clouds + ", coord = " + coord + ", dt = " + dt + ", id = "
                + id + ", wind = " + wind + ", name = " + name + ", weather = " + weather +
                ", rain = " + rain + ", main = " + main + "]";
    }

    public class Sys {
        private String country;

        private long sunrise;

        private long sunset;

        public void setCountry(String country) { this.country = country; }

        public String getcountry() {
            return country;
        }

        public void setSunrise(long sunrise) {
            this.sunrise = sunrise;
        }

        public long getSunrise() {
            return sunrise;
        }

        public void setSunset(long sunset) {
            this.sunset = sunset;
        }

        public long getSunset() {
            return sunset;
        }

        public String toString() {
            return "ClassPOJO [country = " + country + ", sunrise = " + sunrise + ", sunset = " +
                    sunset + ".";
        }
    }


    public class Weather {

        public String id;

        private String icon;

        private String description;

        private String main;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", icon = " + icon + ", description = " + description
                    + ", main = " + main + "]";
        }
    }

    public class Wind
    {
        private String speed;

        private String deg;

        public String getSpeed ()
        {
            return speed;
        }

        public void setSpeed (String speed)
        {
            this.speed = speed;
        }

        public String getDeg ()
        {
            return deg;
        }

        public void setDeg (String deg)
        {
            this.deg = deg;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [speed = "+speed+", deg = "+deg+"]";
        }
    }

    public class Main
    {
        private String humidity;

        private String pressure;

        private String temp_max;

        private String temp_min;

        private String temp;

        public String getHumidity ()
        {
            return humidity;
        }

        public void setHumidity (String humidity)
        {
            this.humidity = humidity;
        }

        public String getPressure ()
        {
            return pressure;
        }

        public void setPressure (String pressure)
        {
            this.pressure = pressure;
        }

        public String getTemp_max ()
        {
            return temp_max;
        }

        public void setTemp_max (String temp_max)
        {
            this.temp_max = temp_max;
        }

        public String getTemp_min ()
        {
            return temp_min;
        }

        public void setTemp_min (String temp_min)
        {
            this.temp_min = temp_min;
        }

        public String getTemp ()
        {
            return temp;
        }

        public void setTemp (String temp)
        {
            this.temp = temp;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [humidity = "+humidity+", pressure = "+pressure+", temp_max = "+
                    temp_max+", temp_min = "+temp_min+", temp = "+temp+"]";
        }
    }

    public class Coord
    {
        private String lon;

        private String lat;

        public String getLon ()
        {
            return lon;
        }

        public void setLon (String lon)
        {
            this.lon = lon;
        }

        public String getLat ()
        {
            return lat;
        }

        public void setLat (String lat)
        {
            this.lat = lat;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [lon = "+lon+", lat = "+lat+"]";
        }
    }

    public class Clouds
    {
        private String all;

        public String getAll ()
        {
            return all;
        }

        public void setAll (String all)
        {
            this.all = all;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [all = "+all+"]";
        }
    }

    public class Rain
    {
        private String threeh;

        public String getthreeh ()
        {
            return threeh;
        }

        public void setthreeh (String threeh)
        {
            this.threeh = threeh;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [3h = "+threeh+"]";
        }
    }
}