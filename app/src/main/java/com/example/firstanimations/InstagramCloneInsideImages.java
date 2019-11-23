package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class InstagramCloneInsideImages extends AppCompatActivity {

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_clone_inside_images);

        linearLayout = findViewById(R.id.instaLinearLayout);

        String usernameSelected;
        Intent fromIntent = getIntent();
        if(fromIntent.hasExtra("usernameSelected")){
            usernameSelected = fromIntent.getStringExtra("usernameSelected");
            queryImages(usernameSelected);
        } else {

            //SHOW A STANDARD INSTAGRAM PICTURE.
            //PROGRAMATICALLY ADD ImageViews.
            ImageView imageView = new ImageView(getApplicationContext());
            //set Layout params, width, height. as setLayoutParams needs to know the SIZE
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            //set the image
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //>= API 21
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.instagram, getApplicationContext().getTheme()));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.instagram));
            }
            //FINALLY: add image to the linear layout
            linearLayout.addView(imageView);
        }



    }

    private void queryImages(String usernameSelected){
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Image");
        parseQuery.whereEqualTo("username", usernameSelected);
        parseQuery.orderByDescending("createdAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for (ParseObject oneObject: objects){
                            //This is the imageFile object in ParseFile format
                            ParseFile imageFile = (ParseFile) oneObject.get("image");
                            //now we need to download the image, Parse will provide the functions for me.
                            imageFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e==null && data != null){
                                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        addImageToLayout(bitmapImage);
                                    }

                                }
                            });

                        }
                    }
                }
            }
        });
    }

    private void addImageToLayout(Bitmap bitmapImage){
        //PROGRAMATICALLY ADD ImageViews.
        ImageView imageView = new ImageView(getApplicationContext());
        //set Layout params, width, height. as setLayoutParams needs to know the SIZE
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        //set the image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //>= API 21
            //imageView.setImageDrawable(getResources().getDrawable(R.drawable.instagram, getApplicationContext().getTheme()));
            imageView.setImageBitmap(bitmapImage);
        } else {
            //imageView.setImageDrawable(getResources().getDrawable(R.drawable.instagram));
            imageView.setImageBitmap(bitmapImage);
        }

        //FINALLY: add image to the linear layout
        linearLayout.addView(imageView);
    }
}
