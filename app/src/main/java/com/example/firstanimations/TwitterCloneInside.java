package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TwitterCloneInside extends AppCompatActivity {
    private static final String TAG = "TwitterCloneInside";
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> allUsersArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_clone_inside);

        setTitle("Welcome " + ParseUser.getCurrentUser().getUsername());

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, allUsersArray);

        listView = (ListView) findViewById(R.id.twitterListView);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);//Sets a check box in each item
        getAllUers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView) view;
                if(ctv.isChecked()){
                    //Now it's checked
                    ParseUser.getCurrentUser().add("isFollowing", allUsersArray.get(position));

                } else {
                    //Now it's unchecked
                    List array = ParseUser.getCurrentUser().getList("isFollowing");
                    array.remove(allUsersArray.get(position));

                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",tempUsers);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });


    }

    private void getAllUers(){

        ParseQuery<ParseUser> query = ParseUser.getQuery(); //Gets the User table/object class

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//where current user is not in list
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                //if e == null -> no errors
                if(e==null){
                    //FIRST: add all users:
                    if(allUsers.size() > 0){
                        for(ParseUser oneUser: allUsers) {
                            allUsersArray.add(oneUser.getUsername());
                        }
                        checkedIfUserIsFollowing();
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void checkedIfUserIsFollowing(){
        List isFollowingArray = ParseUser.getCurrentUser().getList("isFollowing");
        Log.d(TAG, "addIfUserIsFollowing: " + isFollowingArray);
        if(isFollowingArray != null){
            for(String oneUsername: allUsersArray){
                if(isFollowingArray.contains(oneUsername)){
                    //Set the CHECKED BOX for all of these Users to true
                    listView.setItemChecked(allUsersArray.indexOf(oneUsername), true);
                }
            }
        }
    }

    private void mLogout(){
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
    //##############################################################################################################################

    public void dialogPopUp(){
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Enter your tweet:")
                .setView(editText)//BODY
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tweetText = editText.getText().toString();
                        ParseObject tweet = new ParseObject("Tweet");
                        tweet.put("username", ParseUser.getCurrentUser().getUsername());
                        tweet.put("tweet", tweetText);
                        tweet.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex == null) {
                                    Toast.makeText(TwitterCloneInside.this, "Successful tweet!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("Parse Result", "Failed tweet: " + ex.toString());
                                    Toast.makeText(TwitterCloneInside.this, "Failed tweet", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                //OR IF YOU DON'T WANT TO DO ANYTHING, JUST CLOSE THE DIALOG
                //.setNegativeButton("No", null)
                .show();
    }
//##############################################################################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.twitter_clone_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.twitter_tweet:
                dialogPopUp();
                return true;
            case R.id.twitter_feed:

                return true;
            case R.id.twitter_logout:
                mLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //##############################################################################################################################

    //##############################################################################################################################
    //I need this, since Parse should be in a separate file, like "StarterApplication"
    // and added to the Manifest -> main Application android:name=".StarterApplication"
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLogout();
    }
}
