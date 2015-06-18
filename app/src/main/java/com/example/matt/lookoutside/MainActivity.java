package com.example.matt.lookoutside;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    protected String mLatitude;
    protected String mLongitude;
    protected String mCurrentCity;
    protected int mNumCitiesAdded = 0;
    public boolean mFirstFragment = true;

    private static final int MAX_NUM_PAGES = 5;

    WeatherFragment defaultFragment;
    GPSTracker gps;

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private WeatherOnPageChangeListener mWeatherOnPageChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gps = new GPSTracker(this);
        setLatitude(String.valueOf(gps.getLatitude()));
        setLongitude(String.valueOf(gps.getLongitude()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    public void showPopUpMenu(View aView) {
        PopupMenu popup = new PopupMenu(this, aView);
        popup.setOnMenuItemClickListener(popupListener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
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

    public static class WeatherOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private int currentPage;

        @Override
        public void onPageSelected(int aPosition) {
            currentPage = aPosition;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<WeatherFragment> weatherPages;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            initPages();
        }

        private void initPages() {
            weatherPages = new ArrayList<WeatherFragment>();
            addPage(0);
        }

        public void addPage(int aPositionID)  {
            //TODO: add next fragment to swipe view
        }

        @Override
        public Fragment getItem(int aPosition) {
            //TODO: check the mechanics of this method
            return new WeatherFragment();
        }

        @Override
        public int getCount() {
            return MAX_NUM_PAGES;
        }
    }
}
