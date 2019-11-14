package com.example.firstanimations;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class SharedPreferencesBasic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preferences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ACCESS SP
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.firstanimations", Context.MODE_PRIVATE);
        //PUT
        sharedPreferences.edit().putString("username", "michaelzap20").apply();//.apply() saves it//PUTS info into SharedPreferences
        //GET
        String username = sharedPreferences.getString("username","unknown");


        //SERIALIZING AN OBJECT.==========================================================================================
        ArrayList<String> friends = new ArrayList<>(Arrays.asList("mike","fido","homie"));

        try {
            String serializedString = ObjectSerializer.serialize(friends);//Serialize(convert to String) the friends ArrayList Object.
            sharedPreferences.edit().putString("friends", serializedString).apply();//Store the Serialized version in my SharedPreferences file.
            Log.d("Serialized friends", serializedString);
        } catch (Exception e){
            e.printStackTrace();
        }

        //DESIRIALIZING AN OBJECT===========================
        ArrayList<String> newFriends;

        try {
            String sharedPreferencesSerializedString = sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>()));
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferencesSerializedString);//Desierialize(convert to String) the sharedPreferencesSerializedString String.
            Log.d("Desirialized newFriends", newFriends.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        //=================================================================================================================
    }

}
