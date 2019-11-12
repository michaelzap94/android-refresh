package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BasicFrenchPhrases extends AppCompatActivity {

    MediaPlayer mediaPlayer;//This will get the actual Audio file


    public void playFrench(View v){
        Button buttonClicked = (Button) v;


        //YOU COULD HAVE ALSO IMPLEMENTED THIS:
        // int resource_ID = getResources().getIdentifier(NAME_OF_FILE_HERE, "raw", getPackageName());
        //MediaPlayer.create(this, resource_ID).start()

        switch (buttonClicked.getTag().toString()){
            case "speakEnglish": MediaPlayer.create(this, R.raw.doyouspeakenglish).start();
                break;
            case "goodEvening": MediaPlayer.create(this, R.raw.goodevening).start();
                break;
            case "hello": MediaPlayer.create(this, R.raw.hello).start();
                break;
            case "howAreYou": MediaPlayer.create(this, R.raw.howareyou).start();
                break;
            case "liveIn": MediaPlayer.create(this, R.raw.ilivein).start();
                break;
            case "nameIs": MediaPlayer.create(this, R.raw.mynameis).start();
                break;
            case "please": MediaPlayer.create(this, R.raw.please).start();
                break;
            case "welcome": MediaPlayer.create(this, R.raw.welcome).start();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_french_phrases);
    }
}
