package com.example.firstanimations;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicNotes extends AppCompatActivity {

    private static final String TAG = "BasicNotes";
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> notes = new ArrayList<>();//make an empty list: []

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //clear anyting stored in the Notes array
        notes.clear();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.firstanimations", Context.MODE_PRIVATE);

        try {
            HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("BasicNotes", null);
            Log.d(TAG, "onCreate: " + set.toString());
            if(set != null){
                notes = new ArrayList(set);//convert the Set into an ArrayList;
            } else {
                notes.add("Example Note");//default note if empty
            }
            //String serializedStringNotes = sharedPreferences.getString("BasicNotes", ObjectSerializer.serialize(new ArrayList<String>()));//Serialize(convert to String) the notes ArrayList Object.
            //notes = (ArrayList<String>) ObjectSerializer.deserialize(serializedStringNotes);
        } catch (Exception e){
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notes);

        lv = (ListView) findViewById(R.id.listBasicNotes);
        lv.setAdapter(arrayAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(BasicNotes.this, BasicNoteEditor.class);
                newIntent.putExtra("noteId", position);
                startActivity(newIntent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return dialogPopUp(position);
            }
        });

    }

    //MENU##############################################################################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.basic_notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addABasicNoteMenu:
                Toast.makeText(BasicNotes.this, "Add a note selected", Toast.LENGTH_SHORT).show();
                Intent ni = new Intent(BasicNotes.this, BasicNoteEditor.class);
                ni.putExtra("noteId", -1);
                startActivity(ni);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //DIALOG##############################################################################################################################

    public Boolean dialogPopUp(final int _position){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")// Title
                .setMessage("Do you want to delete this note?") // Body
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean result = deleteNote(_position);
                        if(result){
                            arrayAdapter.notifyDataSetChanged();
                            updateSharedPreferences(getApplicationContext());
                            Toast.makeText(BasicNotes.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BasicNotes.this, "Note NOT deleted.", Toast.LENGTH_SHORT).show();
                        }
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
        return true;
    }

    //#####################################################################################################################################

    public boolean deleteNote(int position){

        try {
            notes.remove(position);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    static void updateSharedPreferences(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.firstanimations", Context.MODE_PRIVATE);

        try {

            //String serializedStringNotes = ObjectSerializer.serialize(BasicNotes.notes);//Serialize(convert to String) the MemorablePlaces.places ArrayList Object.

            //We can SAVE  set in the SharedPreferences, WITHOUT SERIALIZING IT.
            HashSet<String> set = new HashSet<>(notes);//Convert ArrayList into HashSet
            sharedPreferences.edit().putStringSet("BasicNotes", set).apply();//Store the Serialized version in my SharedPreferences file.

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
