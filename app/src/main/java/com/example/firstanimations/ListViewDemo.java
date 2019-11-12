package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class ListViewDemo extends AppCompatActivity {

    private ArrayList<String> myFamily = new ArrayList<>(asList("Richard","Veronica","Ricardo"));
    List<String> places = asList("Quito", "Granada", "London", "Paris", "Barcelona", "Madrid", "Malaga", "Quito", "Granada", "London", "Paris", "Barcelona", "Madrid", "Malaga", "Quito", "Granada", "London", "Paris", "Barcelona", "Madrid", "Malaga");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_demo);

        ListView myListViewDemo = findViewById(R.id.myListViewDemo);

        myFamily.add("Rene");

        //ArrayAdapter, takes the information from an array and PUT it IN the ListView.
        /**
         * Parameters:
         * 1) Context
         * 2) Layout that we want to use to display the DATA inside ONE element of the ListView. iow: a row layout in the list.
         * 2.1) You can build your own, and put it under "res/layout" ( IT MUST BE A TextView)
         * 2.2) Your can use one of the built-in types. e.g: android.R.layout.simple_list_item_1
         *
         */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);

        myListViewDemo.setAdapter(arrayAdapter);

        myListViewDemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //view is the TextView
            //position is the position of the element(view) in Array
            //id is the id of the TextView, auto-generated when populating the array in the ListView.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setVisibility(View.GONE);//MAKE THE ITEM SELECTED DISSAPEAR
                Toast.makeText(getApplicationContext(),String.format("Place Selected is: %s",places.get(position)), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
