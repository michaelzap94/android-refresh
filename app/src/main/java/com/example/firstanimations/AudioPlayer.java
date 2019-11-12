package com.example.firstanimations;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class AudioPlayer extends AppCompatActivity {

    private static final String TAG = "AudioPlayer";
    Button play;
    Button pause;
    SeekBar volumeControl;
    SeekBar audioProgressSeekBar;
    MediaPlayer mediaPlayer;//This will get the actual Audio file
    AudioManager audioManager;//Allows us to manage the audio

    Timer myAudioTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play = (Button) findViewById(R.id.buttonPlay);
        pause = (Button) findViewById(R.id.buttonPause);
        volumeControl = (SeekBar) findViewById(R.id.volumeSeekBar);
        audioProgressSeekBar = (SeekBar) findViewById(R.id.audioProgressSeekBar);

        mediaPlayer = MediaPlayer.create(this, R.raw.horse);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);//import the AUDIO_SERVICE, which will be of type AudioManager

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.start();

                //TIMER START----------------------
                /** THIS WILL RUN FOR EVER, UNLESS WE STOP.Therefore
                 * -> 1) make sure you define when to START
                 * -> 2) make sure you define when to STOP
                 *
                 * Parameters:
                 * 1st: pass a TimerTask object,
                 * 2nd: delay: when to start; e.g; if 0, start now
                 * 3th period: miliseconds = how often do we want it to run. if 1000, run every second.
                 */
                myAudioTimer = new Timer();//This will create a Timer, that will run FOREVER, unless we destroy it.
                myAudioTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Timer running: ");
                        //GETS THE CURRENT POSITION - mediaPlayer.getCurrentPosition()
                        //int currentAudioPosition = mediaPlayer.getCurrentPosition(); - WE DON'T want to waste memory, so assign it directly
                        //.setProgres method of a SeekBar will call the onProgressChanged() method and update the progress bar
                        audioProgressSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }, 0, 500);
                //TIMER END----------------------

            }
        });

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaPlayer.pause();

                //STOP THE TIMER and kill/destroy the Timer
                //We also need to kill the timer when the audio stops, see below line 96
                if(myAudioTimer!=null){
                    myAudioTimer.cancel();
                    myAudioTimer = null;
                }
            }
        });

        //add a Completion listener to be activated when the sound finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion Audio: called");
                if(myAudioTimer!=null){
                    myAudioTimer.cancel();
                    myAudioTimer = null;
                }
            }
        });

        //VOLUME==========================================================================================

        //GETS THE MAX volume - you can get different MAX volumes. e.g: STREAM_ALARM, STREAM_RING, STREAM_SYSTEM, etc.
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //GETs the current volume.
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //set the MAX number in the SeekBar(100 by default) to be the maxVolume from AudioManager
        volumeControl.setMax(maxVolume);

        //sets the current Position of the progress in SeekBar
        volumeControl.setProgress(currentVolume);

        //THIS IS THE SEEKBAR LISTENER.
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //called everytime the seekbar is moved. DEFAULT: Progress will be 0 to 100 (position)
            // BUT in this case, we changed the MAX volumeControl.setMax(maxVolume); so it will be
            // 0 to maxVolume
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, String.format("onProgressChanged: changed position: 0 to 100: %s", progress));

                //Requires an integer, NOT 0 to 100.
                // so we need to define maxVolume and set it to the MAX number in SeekBar
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: event");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: event");

            }
        });
        //VOLUME END==========================================================================================
        //AUDIO PROGRESS START==========================================================================================

        //GET'S the duration of the AUDIO
        int maxDuration = mediaPlayer.getDuration();
        //set the MAX number in the SeekBar(100 by default) to be the maxDuration from MediaPlayer
        audioProgressSeekBar.setMax(maxDuration);


        //THIS IS THE SEEKBAR for audio progress LISTENER.
        audioProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, String.format("audioProgressSeekBar onProgressChanged: changed position: 0 to 100: %s", progress));

                //if the SeekBar was changed by the user, then do this.
                //This is because our Timer is also updating the SeekBar
                if(fromUser){
                    //This will set the Audio Progress when we move the SeekBar.
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "audioProgressSeekBar onStartTrackingTouch: event");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "audioProgressSeekBar onStopTrackingTouch: event");

            }
        });

        //AUDIO PROGRESS END==========================================================================================


    }

}
