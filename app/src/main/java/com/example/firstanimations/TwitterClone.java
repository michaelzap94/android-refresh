package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class TwitterClone extends AppCompatActivity {

    private static final String TAG = "TwitterClone";
    Button signupLogin;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_clone);

        username = (EditText) findViewById(R.id.twitterUsernameEditText);
        password = (EditText) findViewById(R.id.twitterPasswordEditText);
        signupLogin = (Button) findViewById(R.id.twitterSignupButton);
        signupLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String mUsername = username.getText().toString();
                final String mPassword = password.getText().toString();
                //LOGIN
                ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e==null && user != null){ //user exists
                            Log.d(TAG, "User Logged in");
                            Toast.makeText(TwitterClone.this, "User Logged in", Toast.LENGTH_SHORT).show();
                            moveInside();
                        } else {
                            //SIGNUP
                            ParseUser newUser = new ParseUser();
                            newUser.setUsername(mUsername);
                            newUser.setPassword(mPassword);
                            newUser.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException signUpError) {
                                    if(signUpError==null){
                                        Log.d(TAG, "Sign up OK!");
                                        Toast.makeText(TwitterClone.this, "Sign up OK!", Toast.LENGTH_SHORT).show();
                                        moveInside();
                                    } else {
                                        Toast.makeText(TwitterClone.this, "Sign up Error: "+ signUpError.getMessage().substring(signUpError.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                                        signUpError.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
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

        //If user is already logged in, move him in.
        if(ParseUser.getCurrentUser() != null){
            Log.d(TAG, "Signed in user info: " + ParseUser.getCurrentUser().getUsername());
            //Move user inside
            moveInside();
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

    public void moveInside(){
        Intent ni = new Intent(TwitterClone.this, TwitterCloneInside.class);
        startActivity(ni);
        finish();
    }


}
