package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MemorablePlaces extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<>();
    //LatLong is an Object for locations.
    static ArrayList<LatLng> locations = new ArrayList<>();
    ArrayList<String> latitudes  = new ArrayList<>();
    ArrayList<String> longitudes = new ArrayList<>();

    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_places);

        ListView listView = (ListView) findViewById(R.id.listPlaces);

        //Clear all arrays.
        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        //DESIRIALIZING AN OBJECT===========================
        //1) Initialize the sharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.firstanimations", Context.MODE_PRIVATE);
        //2) Deserialize the ArrayList Object and then put it in the sharedPreferences
        try {

            //String serializedStringLocations = sharedPreferences.getString("locations", ObjectSerializer.serialize(new ArrayList<LatLng>()));
            //locations = (ArrayList<LatLng>) ObjectSerializer.deserialize(serializedStringLocations);//Desierialize(convert to String) the sharedPreferencesSerializedString String.

            //KEY AND DEFAULT( new ArrayList)
            String serializedStringPlaces = sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>()));
            String serializedStringLatitdes = sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>()));//Serialize(convert to String) the MemorablePlaces.places ArrayList Object.
            String serializedStringLongitudes = sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>()));//Serialize(convert to String) the MemorablePlaces.places ArrayList Object.
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(serializedStringLatitdes);
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(serializedStringLongitudes);
            places = (ArrayList<String>) ObjectSerializer.deserialize(serializedStringPlaces);//Desierialize(convert to String) the sharedPreferencesSerializedString String.

        } catch (Exception e){
            e.printStackTrace();
        }

        //Data exists
        if (places.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for(int i = 0; i < latitudes.size(); i++){
                    LatLng tempLatLong = new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i)));
                    locations.add(tempLatLong);
                }
            }
        } else { //Data first time
            //Add a new place - will be the first Element.
            places.add("Add a new place");
            //Add a default location for Add a new place, as it will be used to add elements;
            locations.add(new LatLng(51.5074,0.1278));
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(getApplicationContext(), MapsActivityMemorablePlaces.class);
                newIntent.putExtra("positionForLocations", position);
                startActivity(newIntent);
            }
        });


    }
}
