package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class BasicNoteEditor extends AppCompatActivity {

    final static String SAVED_INSTANCE_NOTE = "savedInstanceEditText";
    final static String SAVED_INSTANCE_POSITION = "savedInstancePosition";

    EditText note;

    int _position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_note_editor);

        note = (EditText) findViewById(R.id.noteEditorEditText);

        Intent fromBasicNote = getIntent();

        if(fromBasicNote.hasExtra("noteId")){
            _position = fromBasicNote.getIntExtra("noteId", -1);
        }

        if(_position != -1){
            note.setText(BasicNotes.notes.get(_position));
        } else {
            // It is a new NOTE, so add a new Element "" in the ArrayList and get its position.
            BasicNotes.notes.add("");
            _position = BasicNotes.notes.size() - 1;
        }

        //EditText watcher
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //run with every change
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveText(_position, s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    //If user leaves or back-press
//    @Override
//    protected void onStop() {
//        super.onStop();
//        saveText(_position);
//    }

    //SAVED INSTANCE( ON phone rotated)================================
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(SAVED_INSTANCE_NOTE, note.getText().toString());
        //outState.putInt(SAVED_INSTANCE_POSITION, _position);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SAVED_INSTANCE_NOTE)){
                note.setText(savedInstanceState.getString(SAVED_INSTANCE_NOTE));
                //_position = savedInstanceState.getInt(SAVED_INSTANCE_POSITION);
            }
        }
    }

    //SAVE text on SharedPreferences================================

    public void saveText(int position, CharSequence textChar){

        if(position == -1){
            //BasicNotes.notes.add(note.getText().toString());
            BasicNotes.notes.add(String.valueOf(textChar));
        } else {
            //BasicNotes.notes.set(position, note.getText().toString());
            BasicNotes.notes.set(position, String.valueOf(textChar));
        }

        BasicNotes.arrayAdapter.notifyDataSetChanged();

        //same as the code below, but encapsulated in a static method in BasicNotes
        BasicNotes.updateSharedPreferences(getApplicationContext());
//        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.firstanimations", Context.MODE_PRIVATE);
//
//        try {
//
//            String serializedStringNotes = ObjectSerializer.serialize(BasicNotes.notes);//Serialize(convert to String) the MemorablePlaces.places ArrayList Object.
//            sharedPreferences.edit().putString("BasicNotes", serializedStringNotes).apply();//Store the Serialized version in my SharedPreferences file.
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
