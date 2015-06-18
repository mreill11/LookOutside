package com.example.matt.lookoutside.API;

import com.example.matt.lookoutside.model.WeatherModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface WeatherAPI {

    @GET("/weather")
    void getWeatherByCity(@Query("q") String city, @Query("units") String
            unitType, Callback<WeatherModel> callback);

    @GET("/weather")
    void getWeatherByCoord(@Query("lat") String lat, @Query("lon") String
            lon, @Query("units") String units, Callback<WeatherModel> callback);
}
