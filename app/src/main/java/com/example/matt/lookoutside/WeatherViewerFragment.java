package com.example.matt.lookoutside;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.matt.lookoutside.API.WeatherAPI;
import com.example.matt.lookoutside.model.WeatherModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherViewerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String latitude;
    private String longitude;
    private String currentLocation;
    private String activeCity;
    static final int MAX_CITIES = 4;

    private OnFragmentInteractionListener mListener;

    TextView location;
    TextView date;
    TextView description;
    TextView temp;
    TextView sunRiseSet;
    TextView lastUpdated;

    ImageView icon;

    String API = "http://api.openweathermap.org/data/2.5";
    String weatherDescription = "";

    long currentTime;
    long timeUpdated;
    boolean dayTime = false;
    int numAdded = 1;

    SkyconsDrawable drawable;
    RestAdapter restAdapter;
    WeatherAPI weatherapi;
    GPSTracker gps;
    ProgressBar bar;
    Realm realm;
    ArrayList<Place> locations;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherViewer.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherViewerFragment newInstance(String param1, String param2) {
        WeatherViewerFragment fragment = new WeatherViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WeatherViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locations = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).mCurrentCity = currentLocation;
        //((MainActivity) getActivity()).addCityToRealm(currentLocation);

        resetViews();
        retrieveWeather(currentLocation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather_viewer, container, false);
        location = (TextView) rootView.findViewById(R.id.location_textview);
        date = (TextView)  rootView.findViewById(R.id.date_textview);
        description = (TextView) rootView.findViewById(R.id.weather_description_textview);
        temp = (TextView) rootView.findViewById(R.id.temperature_textview);
        sunRiseSet = (TextView) rootView.findViewById(R.id.sun_rise_set_textview);
        lastUpdated = (TextView) rootView.findViewById(R.id.last_updated_textview);
        icon = (ImageView) rootView.findViewById(R.id.weather_icon_imageview);
        bar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        determineLocationName();

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        saveArray();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void determineLocationName() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
        WeatherAPI weatherapi = restAdapter.create(WeatherAPI.class);

        gps = new GPSTracker(getActivity());
        this.setLatitude(String.valueOf(gps.getLatitude()));
        this.setLongitude(String.valueOf(gps.getLongitude()));

        weatherapi.getWeatherByCoord(latitude, longitude, "imperial", new Callback<WeatherModel>() {
            @Override
            public void success(WeatherModel weathermodel, Response response) {
                currentLocation = weathermodel.getName();
                activeCity = currentLocation;
                Place currLoc = new Place();
                currLoc.setName(currentLocation);
                numAdded++;
                locations.add(0, currLoc);
            }

            @Override
            public void failure(RetrofitError e) {
                Log.i("TEST", e.getMessage());
            }
        });
    }

    public void retrieveWeather(String aCity) {
        restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
        weatherapi = restAdapter.create(WeatherAPI.class);

        weatherapi.getWeatherByCity(aCity, "imperial", new Callback<WeatherModel>() {
            @Override
            public void success(WeatherModel weathermodel, Response response) {
                if (weathermodel.getName() != null) {
                    fillViews(weathermodel);
                    if (determineIfNewCity(weathermodel.getName())) {
                        Place newCity = new Place();
                        newCity.setName(weathermodel.getName());
                        if (determineIfNewCity(newCity.getName()) && locations.size() <= MAX_CITIES) {
                            locations.add(newCity);
                        } else {
                            locations.set(4, newCity);
                        }
                        for (Place p : locations)
                            Log.i("TEST", p.getName());
                    }
                    //activeCity = weathermodel.getName();
                } else {
                    bar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Log.i("TEST", "waiting");
                            bar.setVisibility(View.GONE);
                            retrieveWeather(activeCity);
                        }
                    }, 700);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TEST", error.getMessage());
            }
        });
    }

    public boolean determineIfNewCity(String aCity) {
        for (Place p : locations) {
            if (aCity.toLowerCase().equals(p.getName().toLowerCase()))
                return false;
        }
        return true;
    }

    public void saveArray() {
        Context context = getActivity();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();

        String json = gson.toJson(locations);

        editor.putString("LIST", json);
        editor.commit();
    }

    public ArrayList<Place> loadArray() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sp.getString("LIST", null);
        Type type = new TypeToken<ArrayList<Place>>() {}.getType();
        ArrayList<Place> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public ArrayList<Place> getLocations() {
        return locations;
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
        description.setText(weatherDescription);
        drawable = (SkyconsDrawable) IconsUtil.getDrawable(type);
        icon.setImageDrawable(drawable);
        if (drawable != null)
            drawable.start();

        //((MainActivity) getActivity()).addCityToRealm(weathermodel.getName());
    }

    public void determineIfDayTime(long aRise, long aSet) {
        long sunUp = aSet - aRise;
        long timeSinceSunrise = currentTime - aRise;
        if (timeSinceSunrise > 0 && timeSinceSunrise < sunUp)
            dayTime = true;
    }

    public void refresh() {
        resetViews();
        retrieveWeather(activeCity);
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

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String aLatitude) {
        this.latitude = aLatitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String aLongitude) {
        this.longitude = aLongitude;
    }

    public void setActiveCity(String aCity) {
        this.activeCity = aCity;
    }

    public String getActiveCity() {
        return this.activeCity;
    }

}
