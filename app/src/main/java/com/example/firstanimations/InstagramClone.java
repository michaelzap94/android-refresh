package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class InstagramClone extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{
    private static final String TAG = "InstagramClone";

    boolean isSignUpMode = true;
    TextView loginSignupTextView;
    Button instaSignUpButton;
    EditText username;
    EditText password;

    @Override
    public boolean onKey(View v, int i, KeyEvent keyEvent){

        //i == keyEvent.KEYCODE_ENTER -> WHEN key "enter"
        //keyEvent.getAction() == KeyEvent.ACTION_DOWN -> WHEN Action is "pressed"
        if(i==keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            //Apply the login/signup
            signUpOrLogIn(v);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.instaLoginTextView){
            if(isSignUpMode) {
                loginSignupTextView.setText("or, Sign up");
                instaSignUpButton.setText("Login");
                isSignUpMode = false;
            } else {
                loginSignupTextView.setText("or, Login");
                instaSignUpButton.setText("Sign Up");
                isSignUpMode = true;
            }
            //IF logo or background layout was clicked
        } else if(v.getId() == R.id.instaImageView || v.getId() == R.id.instaRelLayout){
            //if keyboard is shown, hide it
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void signUpOrLogIn(View v){

        if(username.getText().toString().matches("") || password.getText().toString().matches("") ) {
            Toast.makeText(this, "A username and password are needed.", Toast.LENGTH_SHORT).show();
        } else {
            if(isSignUpMode){
                //CUSTOM USER CREATION/sign up
                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Log.d(TAG, "Sign up OK!");
                            Toast.makeText(InstagramClone.this, "Sign up OK!", Toast.LENGTH_SHORT).show();
                            moveInside();
                        } else {
                            Toast.makeText(InstagramClone.this, "Sign up Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e==null && user != null){ //user exists
                            Log.d(TAG, "User Logged in");
                            Toast.makeText(InstagramClone.this, "User Logged in", Toast.LENGTH_SHORT).show();
                            moveInside();
                        } else {
                            //Password or user is wrong/does not exist
                            Toast.makeText(InstagramClone.this, "Login Error:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_clone);

        //If user is already logged in, move him in.
        if(ParseUser.getCurrentUser() != null){
            Log.d(TAG, "Signed in user info: " + ParseUser.getCurrentUser().getUsername());
            //Move user inside
            moveInside();
        }

        ImageView logoImageView = (ImageView) findViewById(R.id.instaImageView);
        RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.instaRelLayout);
        username = (EditText) findViewById(R.id.instaUsernameEditText);
        password = (EditText) findViewById(R.id.instaPasswordEditText);
        loginSignupTextView = (TextView) findViewById(R.id.instaLoginTextView);
        instaSignUpButton = (Button) findViewById(R.id.instaSignUpButton);

        //Attach a onClick listener to the textview (implemented:  implements View.OnClickListener)
        loginSignupTextView.setOnClickListener(this);
        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);

        //Attach a onKey listener to the password EditText (implemented:  implements  View.OnKeyListener)
        password.setOnKeyListener(this);

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

        //###########################################################################################################
        //Tracks the Activity OR Application usage
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        //####################################################################################################
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        //CUSTOM logout
//        //ParseUser.getCurrentUser()//GETS logged in user INFO
//        if(ParseUser.getCurrentUser() != null){
//            Log.d(TAG, "Signed in user info: " + ParseUser.getCurrentUser().getUsername());
//            //LOG user out
//            ParseUser.logOut();
//        } else {
//            Log.d(TAG, "No user is Logged in");
//        }
//        Parse.destroy();
//        finish();
//    }

    public void moveInside(){
        Intent ni = new Intent(InstagramClone.this, IntagramCloneInside.class);
        startActivity(ni);
    }
}
