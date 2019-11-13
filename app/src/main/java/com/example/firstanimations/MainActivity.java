package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    //INTENTS=======================================================
    public void intentConnect3Game(View view){
        String referenceConnectGame = "connectGameStarter";
        Intent intent = new Intent(MainActivity.this, ConnectGame.class);
        intent.putExtra(referenceConnectGame, '3');
        startActivity(intent);
    }

    public void intentAnimations(View view){
        Intent intent = new Intent(MainActivity.this, Animations.class);
        startActivity(intent);
    }

    public void intentVideo(View view){
        //String referenceConnectGame = "videoStarter";
        Intent intent = new Intent(MainActivity.this, Video.class);
        //intent.putExtra(referenceConnectGame, '3');
        startActivity(intent);
    }

    public void intentAudio(View view){
        Intent intent = new Intent(MainActivity.this, AudioPlayer.class);
        startActivity(intent);
    }

    public void intentYoutube(View view){
        Intent intent = new Intent(MainActivity.this, YoutubeAPI.class);
        startActivity(intent);
    }

    public void intentCalculator(View view){
        Intent intent = new Intent(MainActivity.this, BasicCalculator.class);
        startActivity(intent);
    }

    public void intentFrenchPhrases(View view){
        Intent intent = new Intent(MainActivity.this, BasicFrenchPhrases.class);
        startActivity(intent);
    }

    public void intentListViewDemo(View view){
        Intent intent = new Intent(MainActivity.this, ListViewDemo.class);
        startActivity(intent);
    }

    public void intentEggTimer(View view){
        Intent intent = new Intent(MainActivity.this, EggTimer.class);
        startActivity(intent);
    }

    public void intentWebDownload(View view){
        Intent intent = new Intent(MainActivity.this, WebDownload.class);
        startActivity(intent);
    }
    //END INTENTS=======================================================

    MediaPlayer mediaPlayer;
    Button playAudioButton;

    public void toggleAudio(View view){
        //NEED TO BE OUTSIDE, SO ONLY ONE INSTANCE OF THE METHOD IS CREATED AT THE START OF THE ACTIVITY.
        //MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.horse);
        //Button playAudioButton = (Button) view;
        //playAudioButton = (Button) view;

        //Check if audio is paused.
        Boolean isPaused = !mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() > 1;

        Log.d(TAG, String.format("toggleAudio: isPaused: %s", isPaused));
        //Check if audio is playing
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playAudioButton.setText("Paused");
        } else if(isPaused) {
            mediaPlayer.start();
            playAudioButton.setText("Playing...");
        } else{
            mediaPlayer.start();
            playAudioButton.setText("Playing...");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Audio player========================================================
        //Add Audio PLayer
        mediaPlayer = MediaPlayer.create(this, R.raw.horse);
        playAudioButton = (Button) findViewById(R.id.playAudio);
        //add a Completion listener to be activated when the sound finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: called");
                playAudioButton.setText("Play Audio");
            }
        });
        //Audio player END========================================================

    }
}
