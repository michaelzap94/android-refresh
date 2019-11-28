package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class UberClone extends AppCompatActivity {

    private static final String TAG = "UberClone";

    private Switch mSwitch;
    private TextView mTextView;

    public void getStarted(View view) {
        final String userType;
        if(mSwitch.isChecked()){
            userType = "driver";
            ParseUser.getCurrentUser().put("riderOrDriver", userType);
        } else {
            userType = "rider";
            ParseUser.getCurrentUser().put("riderOrDriver", userType);
        }
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                moveInside(userType);
            }
        });//YOU DON'T SAVE IT, you will not see it in the DB
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_clone);
        getSupportActionBar().hide();//hide bar

        mTextView = findViewById(R.id.uberTextView);

        mSwitch = findViewById(R.id.uberTypeSwitch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position (Driver)
                if(isChecked){
                    mTextView.setText("Driver Selected");
                } else {
                    mTextView.setText("Rider Selected");
                }
            }
        });


        //=============================================================================================
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Add your initialization code here - get this from Putty, OR connection to Parse Server
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(BuildConfig.PARSER_APP_ID)//appId
                .clientKey(BuildConfig.PARSER_CLIENT_ID)//masterKey
                .server("http://3.17.4.96:80/parse/")//serverURL - add a slash at the end .../parse/
                .build()
        );

        //ANONYMOUS USER: creates an anonymous user in our Local Storage and also registered in Server to have a local session,
        //                UNTIL the user logs out, and we destroy all of the saved data
        if(ParseUser.getCurrentUser() == null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e==null){
                        Log.d(TAG, "done: Anonymous login successful");
                    } else {
                        Log.d(TAG, "done: Anonymous login failed");
                    }
                }
            });
        } else {
            if(ParseUser.getCurrentUser().get("riderOrDriver")!=null){
                moveInside(ParseUser.getCurrentUser().get("riderOrDriver").toString());
            }
        }
        //=============================================================================================
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

    public void moveInside(String userType){
        Class className = (userType == "rider") ? UberCloneRider.class : UberCloneDriver.class;

        Intent ni = new Intent(UberClone.this, className);
        startActivity(ni);
        finish();
    }
}
