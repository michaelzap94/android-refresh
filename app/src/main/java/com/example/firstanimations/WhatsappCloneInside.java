package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class WhatsappCloneInside extends AppCompatActivity {

    private static final String TAG = "WhatsappCloneInside";

    ArrayAdapter<String> arrayAdapter;
    ListView whatsappUserListView;
    ArrayList<String> usernames;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_clone_inside);
        //SET TITLE=========================================================================
        setTitle("Welcome "+ParseUser.getCurrentUser().getUsername() );
        //==================================================================================

        usernames = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);
        whatsappUserListView = (ListView) findViewById(R.id.whatsappUsersListView);

        //This 2 lines will not work: as query.findInBackground is executing in the back thread,
        // while : whatsappUserListView.setAdapter(arrayAdapter);, will execute in the main thread
//        getUserList();
//        whatsappUserListView.setAdapter(arrayAdapter);

        getUserList();

        whatsappUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position: "+position);
                Intent intent = new Intent(getApplicationContext(), WhatsappCloneInsideChat.class);
                intent.putExtra("usernameSelected", usernames.get(position));
                startActivity(intent);
            }
        });
    }

    //==================================================

    private void getUserList(){
        ParseQuery<ParseUser> query = ParseUser.getQuery(); //Gets the TABLE/object class

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//where current user is not in list
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                //if e == null -> no errors
                if(e==null){
                    if(allUsers.size() > 0){
                        for(ParseUser oneUser: allUsers){
                            usernames.add(oneUser.getUsername());
                        }
                        //after adding all users to the usernames array set the adapter
                        whatsappUserListView.setAdapter(arrayAdapter);
                        //YOU could have also set it in the "onCreate" and call arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //I need this, since Parse should be in a separate file, like "StarterApplication"
    // and added to the Manifest -> main Application android:name=".StarterApplication"
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //CUSTOM logout
        //ParseUser.getCurrentUser()//GETS logged in user INFO
        if(ParseUser.getCurrentUser() != null){
            Log.d(TAG, "Signed in user info: " + ParseUser.getCurrentUser().getUsername());
            //LOG user out
            ParseUser.logOut();
        } else {
            Log.d(TAG, "No user is Logged in");
        }
        Parse.destroy();
        finish();
    }
}
