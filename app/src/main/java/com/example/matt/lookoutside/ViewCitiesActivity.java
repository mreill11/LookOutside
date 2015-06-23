package com.example.matt.lookoutside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class ViewCitiesActivity extends ActionBarActivity {

    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cities);
        displayList();
    }

    public void displayList() {
        listView = (ListView) findViewById(R.id.my_locations_listview);
        locationList = new ArrayList();

        Realm realm = Realm.getInstance(this);
        RealmQuery<Place> query = realm.where(Place.class);
        RealmResults<Place> results = query.findAll();

        for (Place p : results) {
            locationList.add(p.getName());
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.row, locationList);
        listView.setAdapter(listAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                displayDeleteDialog(position);
                return true;
            }
        });
    }

    public void displayDeleteDialog(final int aPos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Delete " + locationList.get(aPos) + "?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(aPos);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void deleteItem(int pos) {
        Realm realm = Realm.getInstance(this);
        RealmQuery<Place> query = realm.where(Place.class);
        RealmResults<Place> results = query.findAll();
        realm.beginTransaction();
        results.remove(pos);
        realm.commitTransaction();
        displayList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_cities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
