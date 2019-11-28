package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UberCloneDriver extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = "UberCloneDriver";

    ListView requestListView;
    ArrayList<String> requestsArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    ArrayList<Double> requestLatitudes = new ArrayList<Double>();
    ArrayList<Double> requestLongitudes = new ArrayList<Double>();

    ArrayList<String> usernames = new ArrayList<String>();

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_clone_driver);
        setTitle("Nearby requests");

        requestListView = (ListView) findViewById(R.id.uberRequestsList);

        requestsArrayList.clear();
        requestsArrayList.add("Getting nearby requests...");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requestsArrayList);

        requestListView.setAdapter(arrayAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(UberCloneDriver.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //Driver's location
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //We need these arrays to have at least 1 element.
                    if (requestLatitudes.size() > i && requestLongitudes.size() > i && usernames.size() > i && lastKnownLocation != null) {

                        Intent intent = new Intent(getApplicationContext(), UberCloneDriverMap.class);

                        //Pass Rider's location
                        intent.putExtra("requestLatitude", requestLatitudes.get(i));
                        intent.putExtra("requestLongitude", requestLongitudes.get(i));
                        //Pass Driver's location
                        intent.putExtra("driverLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnownLocation.getLongitude());
                        //Pass Rider's username
                        intent.putExtra("username", usernames.get(i));

                        startActivity(intent);


                    }

                }

            }
        });

        //just setup the Listener.
        listenerSetup();
        //check for permissions, once granted: Execute the listener.
        permissionCheck();

    }
    //PERMISSION REQUEST================================================================================================================
    public void permissionCheck(){

        if (Build.VERSION.SDK_INT < 23) {
            //execute the Listener in listenerSetup
            executeListening();
        } else {
            //If not permission granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else { //if user has given us  permission

                //Get the last known location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null ){
                    Log.d(TAG, "lastKnownLocation: Exists");
                    Toast.makeText(UberCloneDriver.this, "lastKnownLocation: Exists", Toast.LENGTH_SHORT).show();
                    //execute the Listener in listenerSetup
                    executeListening();
                    //BUT still update map using lastKnownLocation
                    updateNearbyRiders(lastKnownLocation);
                } else {
                    Log.d(TAG, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES");
                    Toast.makeText(UberCloneDriver.this, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES", Toast.LENGTH_SHORT).show();
                    //execute the Listener in listenerSetup
                    executeListening();
                }

            }
        }
    }

    //PERMISSION RESULT
    //Once user has granted OR denied: execute this.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case LOCATION_PERMISSION_CODE:
                //IF there's something in the grantResults array, and if the first option is PERMISSION_GRANTED-> Execute your code.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //KIND OF LIKE DOUBLE CHECK
                    executeListening();
                } else {
                    //If not permission granted
                }
                break;
        }

    }
    //===========================================================================================

    //execute the Listener in listenerSetup
    public void executeListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Wait for a while after getting your first location. The location tends to wobble for a few seconds.
            // To determine if the fix is stable use, location.getAccuracy(). Once the accuracy stabilizes, call locationManager.removeUpdates(mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void listenerSetup() {
        //Manages User's location: it get's the user's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Tracks movements
        locationListener = new LocationListener() {
            //Everytime location has changed:
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", "onLocationChanged: " + location.toString());

                //Update the Nearby Riders
                updateNearbyRiders(location);

                //Save Driver's location in the User db
                ParseGeoPoint pgp = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                ParseUser.getCurrentUser().put("location", pgp);
                ParseUser.getCurrentUser().saveInBackground();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }
    //=======================================================

    private void updateNearbyRiders(Location location){
        if (location != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
            //make a ParseGeoPoint (for Parse built-in type) using the Driver's location
            final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            query.whereNear("location", geoPointLocation);//GETs the ParseGeoPoint objects that are near
            query.whereDoesNotExist("driverUsername");//DON'T show if request has already been taken by a Driver
            query.setLimit(10);//only get the 10 closest locations

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        requestsArrayList.clear();
                        requestLongitudes.clear();
                        requestLatitudes.clear();
                        if (objects.size() > 0) {
                            for (ParseObject object : objects) {
                                ParseGeoPoint requestLocation = (ParseGeoPoint) object.get("location");
                                if (requestLocation != null) {
                                    //Distance in miles from Driver(geoPointLocation) to the Rider's request (requestLocation)
                                    Double distanceInMiles = geoPointLocation.distanceInMilesTo(requestLocation);
                                    //Convert the distance to one decimal place.
                                    Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;// if 1.234343 * 10 = 12.34343 rounded -> 12 /10 -> 1.2

                                    //Add Rider's distance
                                    requestsArrayList.add(distanceOneDP.toString() + " miles");
                                    //Add Rider's details
                                    usernames.add(object.getString("username"));
                                    requestLatitudes.add(requestLocation.getLatitude());
                                    requestLongitudes.add(requestLocation.getLongitude());
                                }
                            }
                        } else {
                            requestsArrayList.add("No active requests nearby");
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    //=======================================================

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

        if(locationManager !=null){
            //Stop the locationListener LOOP
            locationManager.removeUpdates(locationListener);
        }

        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(locationManager !=null){
            //Stop the locationListener LOOP
            locationManager.removeUpdates(locationListener);
        }
    }
}
