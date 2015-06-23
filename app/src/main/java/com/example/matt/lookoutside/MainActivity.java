package com.example.matt.lookoutside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends ActionBarActivity {
    protected String mLatitude;
    protected String mLongitude;
    protected String mCurrentCity;
    protected String mNextCity;
    protected int mNumCitiesAdded = 1;
    protected String mCurrentLocation;
    public boolean mFirstFragment = true;

    private static final int MAX_NUM_PAGES = 5;

    WeatherFragment defaultFragment;
    GPSTracker gps;

    private ArrayList<String> cities;

    private SmartFragmentStatePagerAdapter adapterViewPager;
    ViewPager vPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gps = new GPSTracker(this);
        setLatitude(String.valueOf(gps.getLatitude()));
        setLongitude(String.valueOf(gps.getLongitude()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpPager();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new WeatherFragment())
                .commit();

        cities = new ArrayList<String>();
        cities.add(0, mCurrentCity);
    }

    public void setUpPager() {
        vPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vPager.setAdapter(adapterViewPager);

        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this, "" + cities.get(position), Toast.LENGTH_SHORT);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //
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
        mNextCity = aCity;
        mNumCitiesAdded++;

        Realm realm = Realm.getInstance(this);
        RealmResults<Place> results = realm.where(Place.class)
                                    .contains("city", aCity).findAll();
        if (results == null) {
            realm.beginTransaction();
            Place place = realm.createObject(Place.class);
            place.setName(aCity);
            results = realm.where(Place.class).findAll();
            realm.commitTransaction();

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

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return MAX_NUM_PAGES;
        }

        @Override
        public Fragment getItem(int aPos) {
            //TODO: Return position from arraylist
            return null;
        }

    }

    public static class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SmartFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return null;
        }

        public int getCount() {
            return MAX_NUM_PAGES;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
