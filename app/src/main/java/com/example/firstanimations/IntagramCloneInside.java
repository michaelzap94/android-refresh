package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class IntagramCloneInside extends AppCompatActivity {
    private static final String TAG = "IntagramCloneInside";

    ArrayAdapter<String> arrayAdapter;
    ListView instaUserListView;
    ArrayList<String> usernames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intagram_clone_inside);



        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);

        instaUserListView = (ListView) findViewById(R.id.instaUsersListView);
        //This 2 lines will not work: as query.findInBackground is executing in the back thread,
        // while : instaUserListView.setAdapter(arrayAdapter);, will execute in the main thread
        getUserList();
        instaUserListView.setAdapter(arrayAdapter);
        instaUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position: "+position);
            }
        });


    }

    private void getUserList(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("User"); //Gets the TABLE/object class

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
                    }

                }
            }
        });
    }

    //##############################################################################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.instagram_clone_inside, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.instaShare:
                Log.d(TAG, "instaShare selected");
                return true;
            case R.id.instaLogout:
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //##############################################################################################################################
}
