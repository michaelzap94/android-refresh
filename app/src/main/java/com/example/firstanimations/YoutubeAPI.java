package com.example.firstanimations;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class YoutubeAPI extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_api);

        Button btnSingle = (Button) findViewById(R.id.btnPlaySingle);
        Button btnStandalone = (Button) findViewById(R.id.btnStandalone);
        btnSingle.setOnClickListener(this);
        btnStandalone.setOnClickListener(this);
    }

    //THIS SYNTAX WILL NEED YOU TO IMPLEMENT THE View.OnClickListener interface
    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch(view.getId()) {
            case R.id.btnPlaySingle:
                intent = new Intent(this, YoutubeActivity.class);
                break;

            case R.id.btnStandalone:
                intent = new Intent(this, StandaloneActivity.class);
                break;

            default:
        }

        if(intent != null) {
            startActivity(intent);
        }
    }

}
