package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class EggTimer extends AppCompatActivity {

    TextView eggText;
    ToggleButton toggleButton;
    SeekBar eggSeekBar;
    CountDownTimer cdt;
    int _interval = 1000;//milliseconds
    int _defaultStartTime = 120;//seconds
    int _maxTimeSeekBar = 300;//600 seconds -> 5 min MAX
    int _bufferMilliseconds = 100;// By the time we run the onFinish code, there will be a delay; To avoid this add a buffer.

    public void startTime(int maxSeconds, final int interval){
        int maxMilliseconds = maxSeconds * 1000;
        cdt = new CountDownTimer(maxMilliseconds + _bufferMilliseconds, interval) {
            //Execute this code every "interval", if 1000 every second.
            //millisecondsUntilDone will be counted backwards from MAX.
            public void onTick(long millisecondsUntilDone) {
                Log.i("Seconds Left!", String.valueOf(millisecondsUntilDone / interval));
                int timeLeftInt = (int) millisecondsUntilDone / interval;
                myUpdateTimer(timeLeftInt);
            }

            public void onFinish() {
                Log.i("We're done!", "No more countdown");
                MediaPlayer.create(EggTimer.this, R.raw.airhorn).start();
                myResetTimer();
            }
        }.start();
    }

    public void stopTime(){
        if(cdt!=null){
            cdt.cancel();
            cdt = null;
        }
    }

    public void myUpdateTimer(int secondsLeft){
        int minutes = secondsLeft / 60; // whole number only
        int seconds = secondsLeft - (minutes * 60);
        //e.g
        // progress: 136 seconds |
        // minutes: 136/60 = 2.266666 ~ 2 -> because of "/" we'll only get the whole number
        // seconds = 136 - (2 * 60) = 36

        eggText.setText(String.format("%02d:%02d",minutes, seconds));
    }

    public void myResetTimer(){
        eggSeekBar.setProgress(_defaultStartTime);//default position 120, rather than 0
        //Initialize the textview to 2 min
        eggText.setText(String.format("%02d:%02d",2, 0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_timer);

        eggText = (TextView) findViewById(R.id.textViewEggTimer);
        toggleButton = (ToggleButton) findViewById(R.id.toggleEggTimer);
        eggSeekBar = (SeekBar) findViewById(R.id.seekBarEggTimer);

        eggSeekBar.setMax(_maxTimeSeekBar);

        myResetTimer();

        eggSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int min = 1;//minimum number that can be set through the SeekBar
                int currentNumber;
                if(progress < min){
                    currentNumber = min;
                } else {
                    currentNumber = progress;
                }
                myUpdateTimer(currentNumber);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {// The toggle is enabled
                    int numberSelectedFromSeekBar = eggSeekBar.getProgress();
                    Toast.makeText(EggTimer.this,"Time Started: "+ String.valueOf(numberSelectedFromSeekBar), Toast.LENGTH_SHORT).show();
                    //disable seekbar
                    eggSeekBar.setEnabled(false);
                    startTime(numberSelectedFromSeekBar, _interval);

                } else {// The toggle is disabled
                    Toast.makeText(EggTimer.this,"Time Reset", Toast.LENGTH_SHORT).show();
                    //stop/destroy timer
                    stopTime();
                    //enable seekbar
                    eggSeekBar.setEnabled(true);

                    myResetTimer();
                }
            }
        });

    }
}
