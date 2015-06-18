package com.example.matt.lookoutside;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matt.lookoutside.API.WeatherAPI;
import com.example.matt.lookoutside.model.WeatherModel;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WeatherFragment extends Fragment {

    TextView location;
    TextView date;
    TextView description;
    TextView temp;
    TextView sunRiseSet;
    TextView lastUpdated;

    ImageView icon;

    String API = "http://api.openweathermap.org/data/2.5";
    String defaultCity = "";
    String weatherDescription = "";
    String currentLocation = "";

    long currentTime;
    long timeUpdated;
    boolean dayTime = false;

    SkyconsDrawable drawable;
    RestAdapter restAdapter;
    WeatherAPI weatherapi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
        weatherapi = restAdapter.create(WeatherAPI.class);
    }

    public void determineLocationName() {
        String lat = ((MainActivity) getActivity()).getLatitude();
        String lon = ((MainActivity) getActivity()).getLongitude();

        weatherapi.getWeatherByCoord(lat, lon, "imperial", new Callback<WeatherModel>() {
            @Override
            public void success(WeatherModel weathermodel, Response response) {
                currentLocation = weathermodel.getName();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.i("TEST", error.getMessage());
            }
        });
    }

    public void retrieveWeather(String aCity) {
        weatherapi.getWeatherByCity(aCity, "imperial", new Callback<WeatherModel>() {
            @Override
            public void success(WeatherModel weathermodel, Response response) {
                fillViews(weathermodel);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TEST", error.getMessage());
            }
        });
    }

    public void fillViews(WeatherModel weathermodel) {
        long rise = weathermodel.sys.getSunrise() * 1000;
        long set = weathermodel.sys.getSunset() * 1000;
        currentTime = System.currentTimeMillis();
        timeUpdated = weathermodel.getDt() * 1000;
        location.setText(weathermodel.getName());
        date.setText(monthNameFormatter(currentTime));
        temp.setText(weathermodel.main.getTemp().substring(0, 2) + "°F");
        lastUpdated.setText("Last Updated: " + hoursMinutesFormatter(convertTime(timeUpdated)));

        determineIfDayTime(rise, set);

        if (dayTime)
            sunRiseSet.setText("Sunset: " + hoursMinutesFormatter(convertTime(set)));
        else
            sunRiseSet.setText("Sunrise: " + hoursMinutesFormatter(convertTime(rise)));

        String id = weathermodel.weather.get(0).id;
        int weatherType = Integer.parseInt(id);
        String type = setWeatherType(weatherType);
        description.setText(weatherType);
        drawable = (SkyconsDrawable) IconsUtil.getDrawable(type);
        icon.setImageDrawable(drawable);
        if (drawable != null)
            drawable.start();
    }

    public void determineIfDayTime(long aRise, long aSet) {
        long sunUp = aSet - aRise;
        long timeSinceSunrise = currentTime - aRise;
        if (timeSinceSunrise > 0 && timeSinceSunrise < sunUp)
            dayTime = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        location = (TextView) rootView.findViewById(R.id.location_textview);
        date = (TextView) rootView.findViewById(R.id.date_textview);
        description = (TextView) rootView.findViewById(R.id.weather_description_textview);
        temp = (TextView) rootView.findViewById(R.id.temperature_textview);
        sunRiseSet = (TextView) rootView.findViewById(R.id.sun_rise_set_textview);
        lastUpdated = (TextView) rootView.findViewById(R.id.last_updated_textview);
        icon = (ImageView) rootView.findViewById(R.id.weather_icon_imageview);

        if (((MainActivity) getActivity()).mNumCitiesAdded == 0) {
            determineLocationName();
            retrieveWeather(currentLocation);
        } else {
            //TODO: retrieveWeather(city from bundle);
        }

        return rootView;
    }

    public void resetViews() {
        location.setText("");
        date.setText("");
        description.setText("");
        temp.setText("");
        sunRiseSet.setText("");
        lastUpdated.setText("");
        icon.setImageDrawable(null);
    }

    public String setWeatherType(int id) {
        String type = "";
        int generalID = id / 100;

        switch (generalID) {

            case 2:
                type = "rain";
                weatherDescription = "Thunderstorm";
                break;
            case 3:
                type = "rain";
                weatherDescription = "Drizzling";
                break;
            case 5:
                type = "rain";
                weatherDescription = "Raining";
                break;
            case 6:
                switch (id) {
                    case 611:
                    case 612:
                        type = "sleet";
                        weatherDescription = "sleet";
                        break;
                    default:
                        type = "snow";
                        weatherDescription = "Snowing";
                        break;
                }
                break;
            case 7:
                type = "fog";
                weatherDescription = "Foggy";
                break;
            case 8:
                switch (id) {
                    case 801:
                    case 802:
                    case 803:
                        if (dayTime) {
                            type = "partly-cloudy-day";
                            weatherDescription = "Partly Cloudy"; }
                        else {
                            type = "partly-cloudy-night";
                            weatherDescription = "Partly Cloudy"; }
                        break;
                    case 804:
                        type = "cloudy";
                        weatherDescription = "Cloudy";
                        break;
                    default:
                        if (dayTime) {
                            type = "clear-day";
                            weatherDescription = "Clear Day"; }
                        else {
                            type = "clear-night";
                            weatherDescription = "Clear Night"; }
                        break;
                }
                break;
            default:
                weatherDescription = "Error.";
                break;
        }
        return type;
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return format.format(date);
    }

    public String hoursMinutesFormatter(String date) {
        int hr = Integer.parseInt(date.substring(11,13));
        int min = Integer.parseInt(date.substring(14,16));
        return (((hr % 12 ==0) ? "12" : hr % 12) + ":" + ((min < 10) ? "0" : "")
                + min + " " + ((hr >= 12) ? "PM" : "AM"));
    }

    public String monthNameFormatter(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
        return format.format(date);
    }
}
