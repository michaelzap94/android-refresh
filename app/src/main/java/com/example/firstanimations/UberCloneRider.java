package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UberCloneRider extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "UberCloneRider";
    private static final int LOCATION_PERMISSION_CODE = 1;

    Button requestOrCancelUberButton;
    Handler handler = new Handler();
    TextView updatesTextView;

    private GoogleMap mMap;
    //Getting user's location=================================================================================
    LocationManager locationManager;
    LocationListener locationListener;

    public void listenerSetup() {
        //Manages User's location: it get's the user's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Tracks movements
        locationListener = new LocationListener() {
            //Everytime location has changed:
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", "onLocationChanged: " + location.toString());
                updateLocationMap(location);
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

    private boolean requestActive = false;
    private boolean driverOnItsWay = false;

    //CHECK FOR UPDATES--------------------------------------------------------------------------------------------

    private void checkForDriverAcceptedAndPosition(){

        Log.d(TAG, "checkForDriverAcceptedAndPosition: CALLED");
        //Get from Requests WHERE Rider made a Request AND a Driver has accepted.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereExists("driverUsername");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {

                    driverOnItsWay = true;

                    updatesTextView.setText("Your Driver is on its way");
                    requestOrCancelUberButton.setVisibility(View.GONE);
                    String driverUsername = objects.get(0).getString("driverUsername");
//                    ParseGeoPoint riderLocation = objects.get(0).getParseGeoPoint("location");
                    findDriverDistance(driverUsername);
                } else {
                    //CALL SAME "checkForDriverAcceptedAndPosition" METHOD EVERY 2 SECONDS (RECURSIVELY)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkForDriverAcceptedAndPosition();
                        }
                    }, 2000);
                }
            }
        });
    }

    private void findDriverDistance(String driverUsername){
        Log.d(TAG, "findDriverDistance: CALLED");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", driverUsername);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("location");
                    Location lastKnownLocationLive = myGetLastKnownLocationLive();
                    if (driverLocation != null && lastKnownLocationLive != null) {
                        ParseGeoPoint riderLocation = new ParseGeoPoint(lastKnownLocationLive.getLatitude(), lastKnownLocationLive.getLongitude());
                        //Distance in miles from Driver(geoPointLocation) to the Rider's request (requestLocation)
                        Double distanceInMiles = driverLocation.distanceInMilesTo(riderLocation);
                        //Convert the distance to one decimal place.
                        Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;// if 1.234343 * 10 = 12.34343 rounded -> 12 /10 -> 1.2

                        if (distanceOneDP < 0.01) {
                            //IF DRIVER is HERE:
                            updatesTextView.setText("Your Driver is HERE!");

                            //Give a few seconds before updating so a new Uber can be requested.
                            //OR use a dialog or something
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    driverOnItsWay = false;
                                    requestActive = false;
                                    requestOrCancelUberButton.setVisibility(View.VISIBLE);
                                    requestOrCancelUberButton.setText("Call an Uber");
                                    updatesTextView.setText("");
                                }
                            }, 4000);


                        } else {
                            //Add Driver's distance to TextView
                            updatesTextView.setText("Your Driver is " + distanceOneDP.toString() + " miles away");
                            //Show BOTH Rider's and Driver's location on the map
                            showPositionRiderAndDriver(driverLocation,riderLocation);

                            //CALL SAME "checkForDriverAcceptedAndPosition" METHOD EVERY 2 SECONDS (RECURSIVELY)
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkForDriverAcceptedAndPosition();
                                }
                            }, 2000);
                        }
                    }
                }
            }
        });
    }

    private void showPositionRiderAndDriver(ParseGeoPoint driver, ParseGeoPoint rider){
        Log.d(TAG, "showPositionRiderAndDriver: CALLED");

        mMap.clear();//Clear previous Markers

        LatLng driverLocation = new LatLng(driver.getLatitude(), driver.getLongitude());
        LatLng riderLocation = new LatLng(rider.getLatitude(), rider.getLongitude());
        //YOU NEED TO PUT BOTH MARKERS IN AN ARRAYLIST
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver's location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));//Driver's location
        markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Your location")));//Rider's location
        //USE THIS CODE TO SHOW BOTH MARKERS ON THE MAP:
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();//holds both Markers(with lat and long each)
        int padding = 60;//offset from edges of the map in pixels//If this is too SMALL -> error
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);

    }

    //END CHECK FOR UPDATES--------------------------------------------------------------------------------------------

    private void initialRequestActive(){
        ParseQuery<ParseObject> request = new ParseQuery<ParseObject>("Request");
        request.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        request.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        requestActive = true;
                        requestOrCancelUberButton.setText("Cancel Uber");
                        checkForDriverAcceptedAndPosition();
                    }
                }
            }
        });
    }

    public void requestOrCancelUber(View view){
        if(requestActive){
            removeGeoPointDB();
        } else {
            addGeoPointToDB();
        }
    }

    public void addGeoPointToDB(){
        Location lastKnownLocationLive = myGetLastKnownLocationLive();

        if (lastKnownLocationLive != null) {

            ParseObject request = new ParseObject("Request");
            request.put("username", ParseUser.getCurrentUser().getUsername());
            //Built-in Parse type ParseGeoPoint
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocationLive.getLatitude(), lastKnownLocationLive.getLongitude());
            request.put("location", parseGeoPoint);


            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        requestOrCancelUberButton.setText("Cancel Uber");
                        requestActive = true;
                        checkForDriverAcceptedAndPosition();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Error while finding location. Please try again later.", Toast.LENGTH_SHORT).show();
        }

    }

    public void removeGeoPointDB(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        requestOrCancelUberButton.setText("Call an Uber");
                                        requestActive = false;
                                    } else {
                                        Log.d(TAG, "deleteInBackground done: Error - " + e.getMessage());
                                    }
                                }
                            });
                        }

                    }
                }
            }
        });

    }

    private Location myGetLastKnownLocationLive(){
        Location lastKnownLocation = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return lastKnownLocation;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_clone_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        updatesTextView = (TextView) findViewById(R.id.uberInfoTextView);
        requestOrCancelUberButton = (Button) findViewById(R.id.requestOrCancelUberButton);

        //setup the listener and check for permissions, When map is ready -> onMapReady

        //make the initial request to check if there's an active Uber request
        initialRequestActive();
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
                    Toast.makeText(UberCloneRider.this, "lastKnownLocation: Exists", Toast.LENGTH_SHORT).show();
                    //execute the Listener in listenerSetup
                    executeListening();
                    //BUT still update map using lastKnownLocation
                    updateLocationMap(lastKnownLocation);
                } else {
                    Log.d(TAG, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES");
                    Toast.makeText(UberCloneRider.this, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES", Toast.LENGTH_SHORT).show();
                    //execute the Listener in listenerSetup
                    executeListening();
                }

            }
        }
    }

    //PERMISSION RESULT
    //Once user has granted OR denied: execute this.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    //==========================================================================================

    private void updateLocationMap(Location location) {

        if(!driverOnItsWay){
            //if location has changed, you should Clear the previous location
            mMap.clear();

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(everest).title("Biggest in the world"));//Normal GPS view Button
            //Make the icon Pointer Yellow: you can create your own.
            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(everest));//Normal view
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));//Normal view// 20 is the max.

        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //just setup the Listener.
        listenerSetup();
        //Once map is ready, execute the permission check.
        permissionCheck();

    }

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

    @Override
    public void onStop(){
        super.onStop();
        if(locationManager !=null){
            //Stop the locationListener LOOP
            locationManager.removeUpdates(locationListener);
        }
    }

}
