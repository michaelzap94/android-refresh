package com.example.firstanimations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

/**
 * Created by timbuchalka on 22/07/2016.
 */

public class StandaloneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);

        Button btnPlayVideo = (Button) findViewById(R.id.btnPlayVideo);
        Button btnPlaylist = (Button) findViewById(R.id.btnPlayList);

        // 1) THIS SYNTAX WILL NEED YOU TO IMPLEMENT THE View.OnClickListener interface
//        btnPlayVideo.setOnClickListener(this);
//        btnPlaylist.setOnClickListener(this);

        //With this syntax you define a button listener and then attach it to a set of buttons.
        View.OnClickListener ourListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;

                switch(view.getId()) {
                    case R.id.btnPlayVideo:
                        intent = YouTubeStandalonePlayer.createVideoIntent(StandaloneActivity.this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_VIDEO_ID, 0, true, false);
                        break;

                    case R.id.btnPlayList:
                        intent = YouTubeStandalonePlayer.createPlaylistIntent(StandaloneActivity.this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_PLAYLIST, 0, 0, true, true);
                        break;

                    default:

                }

                if(intent != null) {
                    startActivity(intent);
                }
            }
        };

        btnPlayVideo.setOnClickListener(ourListener);
        btnPlaylist.setOnClickListener(ourListener);

        // different syntax to add a listener to button directly
//        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    // 1) THIS SYNTAX WILL NEED YOU TO IMPLEMENT THE View.OnClickListener interface
//    @Override
//    public void onClick(View view) {
//        Intent intent = null;
//
//        switch(view.getId()) {
//            case R.id.btnPlayVideo:
//                intent = YouTubeStandalonePlayer.createVideoIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_VIDEO_ID, 0, true, false);
//                break;
//
//            case R.id.btnPlayList:
//                intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_PLAYLIST, 0, 0, true, true);
//                break;
//
//            default:
//
//        }
//
//        if(intent != null) {
//            startActivity(intent);
//        }
//
//
//    }
}