package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class IntagramCloneInside extends AppCompatActivity {
    private static final String TAG = "IntagramCloneInside";
    private static final int MEDIA_ACCESS_PERMISSION_CODE = 1;


    ArrayAdapter<String> arrayAdapter;
    ListView instaUserListView;
    ArrayList<String> usernames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intagram_clone_inside);

        usernames = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);
        instaUserListView = (ListView) findViewById(R.id.instaUsersListView);

        //This 2 lines will not work: as query.findInBackground is executing in the back thread,
        // while : instaUserListView.setAdapter(arrayAdapter);, will execute in the main thread
//        getUserList();
//        instaUserListView.setAdapter(arrayAdapter);

        getUserList();

        instaUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position: "+position);
            }
        });


    }

    //PERMISSION REQUEST================================================================================================================
    public void permissionCheck(){

        if (Build.VERSION.SDK_INT < 23) {
            //execute the Listener in listenerSetup
            getPhoto();
        } else {
            //If not permission granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_ACCESS_PERMISSION_CODE);
            } else { //if user has given us  permission
                getPhoto();
            }
        }
    }

    //PERMISSION RESULT
    //Once user has granted OR denied: execute this.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MEDIA_ACCESS_PERMISSION_CODE:
                //IF there's something in the grantResults array, and if the first option is PERMISSION_GRANTED-> Execute your code.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //KIND OF LIKE DOUBLE CHECK first
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        getPhoto();
                    }
                } else {
                    //If not permission granted
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG, "onRequestPermissionsResult: Permission not granted FINAL");
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: Permission granted WEIRD");
                    }
                }
                break;
        }

    }

    //===========================================================================================
    //execute the photo upload
    private void getPhoto() {
        Intent getPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getPhotoIntent, MEDIA_ACCESS_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //data will hold the Uri
        Uri selectedImageUri = data.getData();//get the DEVICE uri location of the image
        Log.d(TAG, "onActivityResult: Image URI: "+selectedImageUri);

        if(requestCode == MEDIA_ACCESS_PERMISSION_CODE && resultCode == RESULT_OK && data != null){
            try{
                Bitmap bitmapImageFromSelectedImageUri = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                //YOU COULD ASSIGN IT TO AN ImageView and display it
                //ImageView imageView = findViewById(R.id.imageView);
                //imageView.setImageBitmap(bitmapImageFromSelectedImageUri);
                //BUT, instead Parse the image so you can store it

                //
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //Compress the Bitmap into a PNG, with quality 100, USING ByteArrayOutputStream stream
                bitmapImageFromSelectedImageUri.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //Array of bytes

                //convert the Stream to a byte array
                byte byteArray[] = stream.toByteArray();

                //Create a file object, able to hold a byteArray that when decompressed, will be a PNG
                ParseFile fileHoldingByteArrayWithBitmapCompressed = new ParseFile("image.png", byteArray);
                //Create a Parse object
                addPhoto(fileHoldingByteArrayWithBitmapCompressed);

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    //===========================================================================================

    private void addPhoto(ParseFile fileHoldingByteArrayWithBitmapCompressed){
        //Create a Class Object/Table/Schema - e.g: Score.============================
        ParseObject images = new ParseObject("Image");
        images.put("username", ParseUser.getCurrentUser().getUsername());//String
        images.put("image", fileHoldingByteArrayWithBitmapCompressed);//Bitmap

        //score.save();//WILL PAUSE YOUR APP, it RUNs ON MAIN THREAD

        images.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) { //ONCE done
                if (ex == null) {
                    Log.i("Parse Result", "Successful!");
                    Toast.makeText(IntagramCloneInside.this, "Image has been shared", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Parse Result", "Failed" + ex.toString());
                    Toast.makeText(IntagramCloneInside.this, "Image could not be shared", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //==================================================

    private void getUserList(){
        ParseQuery<ParseUser> query = ParseUser.getQuery(); //Gets the TABLE/object class

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//where current user is not in list
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                //if e == null -> no errors
                if(e==null){
                    if(allUsers.size() > 0){
                        for(ParseUser oneUser: allUsers){
                            usernames.add(oneUser.getUsername());
                        }
                        //after adding all users to the usernames array set the adapter
                        instaUserListView.setAdapter(arrayAdapter);
                    }
                }
            }
        });
    }

    //##############################################################################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.instagram_clone_inside, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.instaShare:
                Log.d(TAG, "instaShare selected");
                permissionCheck();
                return true;
            case R.id.instaLogout:
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //##############################################################################################################################

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
}
