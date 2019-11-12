package com.example.firstanimations;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Animations extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    Boolean bartIsShowing = true;
    Boolean bartDefaultSize = true;
    Boolean bartDefaultPosition = true;
    //Boolean bartDefaultSize, bartDefaultPosition = true;
    //Boolean homerDefaultSize, homerDefaultPosition = true;
    Boolean homerDefaultSize = true;
    Boolean homerDefaultPosition = true;
    ImageView bart;
    ImageView homer;

    public void bartHomerToggle(View view){
        Log.d(TAG, "bartHomerToggle: touched");


        //.alpha(int) DECIDES HOW SOLID A PICTURE APPEARS, .seDuration(miliseconds)
        //.rotation(180) media vuelta, .rotation(360) una vuelta, .rotation(720) dos vueltas, etc.
        //ONCE a view has been rotated, you would have to Rotate it back -degree, to see the rotation.
        //OR add more degrees on TOP of the ones you used for the first rotation



        if(bartIsShowing){
            bart.animate().rotation(1800).alpha(0).setDuration(2000);
            homer.animate().rotation(1800).alpha(1).setDuration(2000);
            bartIsShowing = false;
        } else {
            homer.animate().rotation(-1800).alpha(0).setDuration(2000);
            bart.animate().rotation(-1800).alpha(1).setDuration(2000); // .alpha(int) DECIDES HOW SOLID A PICTURE APPEARS, .seDuration(miliseconds)
            bartIsShowing = true;
        }

    }

    public void toggleSmallBig(View view){
        Log.d(TAG, "toggleSmallBig: touched");

        if(bartIsShowing){
            if(bartDefaultSize){
                bart.animate().scaleX(0.5f).scaleY(0.5f).setDuration(1000);
                bartDefaultSize = false;
            } else {
                bart.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000);
                bartDefaultSize = true;
            }
        } else {
            if(homerDefaultSize){
                homer.animate().scaleX(0.5f).scaleY(0.5f).setDuration(1000);
                homerDefaultSize = false;
            } else {
                homer.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000);
                homerDefaultSize = true;
            }
        }

    }

    public void goComeLeft(View view){
        Log.d(TAG, "goComeLeft: touched");


        if(bartIsShowing){
            if(bartDefaultPosition){
                bart.animate().translationXBy(-1000).rotation(-1800).setDuration(1000);
                bartDefaultPosition = false;
            } else {
                bart.animate().translationXBy(1000).rotation(1800).setDuration(1000);
                bartDefaultPosition = true;
            }
        } else {
            if(homerDefaultPosition){
                homer.animate().translationXBy(-1000).rotation(-1800).setDuration(1000);
                homerDefaultPosition = false;
            } else {
                homer.animate().translationXBy(1000).rotation(1800).setDuration(1000);
                homerDefaultPosition = true;
            }
        }

    }

    public void onlyComeLeft(View view){
        Log.d(TAG, "goComeLeft: touched");


        if(bartIsShowing){
            if(bartDefaultPosition){
                bart.setX(-1000);
                bartDefaultPosition = false;
            } else {
                bartDefaultPosition = true;
            }
            bart.animate().translationXBy(1000).rotation(1800).setDuration(1000);

        } else {
            if(homerDefaultPosition){
                homer.setX(-1000);
                homerDefaultPosition = false;
            } else {
                homerDefaultPosition = true;
            }
            homer.animate().translationXBy(1000).rotation(1800).setDuration(1000);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animations);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bart = (ImageView) findViewById(R.id.bartImageView);
        homer = (ImageView) findViewById(R.id.homerImageView);
    }

}
