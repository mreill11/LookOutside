package com.example.matt.lookoutside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.matt.lookoutside.API.WeatherAPI;
import com.example.matt.lookoutside.model.WeatherModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {
    protected String mLatitude;
    protected String mLongitude;
    protected String mCurrentCity;
    protected String mNextCity;
    protected int mNumCitiesAdded = 1;
    protected String mCurrentLocation;
    String API = "http://api.openweathermap.org/data/2.5";

    private static final int MAX_NUM_PAGES = 5;
    private ArrayList<String> cities;

    GPSTracker gps;
    ViewPager vPager;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gps = new GPSTracker(this);
        setLatitude(String.valueOf(gps.getLatitude()));
        setLongitude(String.valueOf(gps.getLongitude()));
        determineLocationName();
        mCurrentCity = mCurrentLocation;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new WeatherFragment())
                .commit();

        refreshInfo();

        setUpPager();

        cities = new ArrayList<>();
        cities.add(0, mCurrentCity);
    }

    public void setUpPager() {
        vPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapterViewPager = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        vPager.setAdapter(adapterViewPager);
        vPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    public void determineLocationName() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API).build();
        WeatherAPI weatherapi = restAdapter.create(WeatherAPI.class);

        weatherapi.getWeatherByCoord(mLatitude, mLongitude, "imperial", new Callback<WeatherModel>() {
            @Override
            public void success(WeatherModel weathermodel, Response response) {
                mCurrentLocation = weathermodel.getName();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TEST", error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshInfo();
            return true;
        } else if (id == R.id.action_add_city) {
            displayAddCityDialog();
            return true;
        } else if (id == R.id.action_view_menu) {
            showPopUpMenu(findViewById(R.id.action_bar));
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshInfo() {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city", mCurrentCity);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "HI")
                .commit();
    }

    public void displayAddCityDialog() {
        AlertDialog.Builder newCity = new AlertDialog.Builder(MainActivity.this);
        newCity.setTitle("Enter a city name:");

        final EditText input = new EditText(MainActivity.this);

        newCity.setView(input);
        newCity.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().popBackStack();
                String newCity = input.getText().toString();
                addCity(newCity);
            }
        });
        newCity.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        newCity.show();
    }

    public void addCity(String aCity) {
        //TODO: Figure out how the hell to do this

        mNumCitiesAdded++;
        RealmResults<Place> results;

        try {
            realm = Realm.getInstance(this);
            results = realm.where(Place.class)
                    .contains("city", aCity).findAll();
        } catch (IllegalArgumentException e) {
            Log.i("TEST", e.toString());
            realm.beginTransaction();
            Place place = realm.createObject(Place.class);
            place.setName(aCity);
            results = realm.where(Place.class).findAll();
            realm.commitTransaction();

            mNextCity = aCity;

            for (Place p : results) {
                cities.add(p.getName());
            }
        }

    }

    public void showPopUpMenu(View aView) {
        PopupMenu popup = new PopupMenu(this, aView);
        popup.setOnMenuItemClickListener(popupListener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
    }

    public String getCity(int position) {
        return cities.get(position);
    }

    public String getCurrentCity() {
        return this.mCurrentCity;
    }

    public void setCurrentCity(String aCity) {
        this.mCurrentCity = aCity;
    }

    public String getLatitude() {
        return this.mLatitude;
    }

    public void setLatitude(String aLatitude) {
        this.mLatitude = aLatitude;
    }

    public String getLongitude() {
        return this.mLongitude;
    }

    public void setLongitude(String aLongitude) {
        this.mLongitude = aLongitude;
    }

    private PopupMenu.OnMenuItemClickListener popupListener = new
            PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem aItem) {
                    switch (aItem.getItemId()) {
                        case R.id.settings:
                            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                            return true;
                        case R.id.my_cities:
                            Intent viewCitiesIntent = new Intent(MainActivity.this, ViewCitiesActivity.class);
                            startActivity(viewCitiesIntent);
                            return true;
                        default:
                            return false;
                    }
                }
            };

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return WeatherFragment.newInstance(mCurrentLocation);
                case 1: return WeatherFragment.newInstance("Reno");
                default:
                    return WeatherFragment.newInstance("Las Vegas");
            }
        }

        @Override
        public int getCount() {
            return MAX_NUM_PAGES;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else {
                view.setAlpha(0);
            }
            Log.i("TEST", "Animating some shit");
        }
    }
}
