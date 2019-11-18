package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MemorablePlaces extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<String>();
    //LatLong is an Object for locations.
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_places);

        ListView listView = (ListView) findViewById(R.id.listPlaces);

        //Add a new place - will be the first Element.
        places.add("Add a new place");
        //Add a default location for Add a new place, as it will be used to add elements;
        locations.add(new LatLng(0,0));

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(getApplicationContext(), MapsActivity.class);
                newIntent.putExtra("positionForLocations", position);
                startActivity(newIntent);
            }
        });


    }
}
