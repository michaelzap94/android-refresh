package com.example.firstanimations;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class PrefsActBarDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preferences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //##############################################################################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingsBasicActionBar:
                Toast.makeText(PrefsActBarDialog.this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.helpBasicActionBar:
//                Toast.makeText(PrefsActBarDialog.this, "Help selected", Toast.LENGTH_SHORT).show();
                dialogPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //##############################################################################################################################
    
    //##############################################################################################################################

    public void dialogPopUp(){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure")// Title
                .setMessage("Do you definitely want to do this?") // Body
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PrefsActBarDialog.this, "It's done", Toast.LENGTH_SHORT).show();
                    }
                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
                //OR IF YOU DON'T WANT TO DO ANYTHING, JUST CLOSE THE DIALOG
                .setNegativeButton("No", null)
                .show();
    }

    //##############################################################################################################################


    public void SerializeDesirialize(){
        //##############################################################################################################################
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
        //##############################################################################################################################
    }

}
