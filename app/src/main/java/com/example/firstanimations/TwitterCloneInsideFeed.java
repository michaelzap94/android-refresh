package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterCloneInsideFeed extends AppCompatActivity {
    private static final String TAG = "TwitterCloneInsideFeed";
    private ListView listView;
    //Use a SimpleAdapter for ListViews holding 2 items
    private SimpleAdapter simpleAdapter;
    //List of Maps
    private List<Map<String, String>> tweetDataArray = new ArrayList<>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_clone_inside_feed);
        setTitle("Your Feed");

        //Example of how to add a Map into an ArrayList of Maps
//        Map<String, String> tweetInfo = new HashMap<>();
//        tweetInfo.put("content", "any content");
//        tweetInfo.put("username", "any username");
//        tweetDataArray.add(tweetInfo);

        /**
         * 1) context
         * 2) List holding Maps
         * 3) Type of TextView Layout
         * 4) Array of String containing the Map's KEYs of the VALUES to be displayed
         * 5) Array of Integers containing the Position of the 3) "Type of TextView Layout"
         * e.g -> "content" key in map will be displayed in position 1-> android.R.id.text1 AND "username" in position 2 android.R.id.text2 in the TextView Layout: simple_list_item_2
         */
        simpleAdapter = new SimpleAdapter(this, tweetDataArray, android.R.layout.simple_list_item_2, new String[] {"content", "username"}, new int[] {android.R.id.text1, android.R.id.text2});

        listView = (ListView) findViewById(R.id.twitterFeedListView);
        listView.setAdapter(simpleAdapter);
        getMyFeed();
    }

    private void getMyFeed(){
        ParseQuery<ParseObject> myFeeds = ParseQuery.getQuery("Tweet"); //Gets the TABLE/object class
        myFeeds.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));//Gets only the tweets of the users we follow
        myFeeds.orderByDescending("createdAt");
        myFeeds.setLimit(20);
        myFeeds.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects != null){
                    if(objects.size() > 0){
                        for(ParseObject oneObject: objects){
                            String mUsername = oneObject.getString("username");
                            String mTweet = oneObject.getString("tweet");

                            Map<String, String> tweetInfo = new HashMap<>();
                            tweetInfo.put("content", mUsername);
                            tweetInfo.put("username", mTweet);
                            tweetDataArray.add(tweetInfo);
                        }
                        simpleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
