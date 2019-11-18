package com.example.firstanimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HikersWatch extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = "HikersWatch";

    //Getting user's location=================================================================================
    LocationManager locationManager;
    LocationListener locationListener;

    Boolean stable = false;
    int isStableAt = 5;
    ArrayList<Float> accuracyFrecuency = new ArrayList<>();

    public void listenerSetup() {
        //Manages User's location: it get's the user's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Tracks movements
        locationListener = new LocationListener() {
            //Everytime location has changed:
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", "onLocationChanged: " + location.toString());
                updateLocationInfo(location);

                //add the accuracy to the set of accuracyFrecuency
                accuracyFrecuency.add(location.getAccuracy());

                //check if the accuracy is stable. If it is -> end the listener.
                checkAccuracyStable();
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

    public void updateLocationInfo(Location location) {

        Log.i("updateLocationInfo", location.toString());

        TextView latTextView = (TextView) findViewById(R.id.hikersLatutude);

        TextView lonTextView = (TextView) findViewById(R.id.hikersLongitude);

        TextView altTextView = (TextView) findViewById(R.id.hikersAltitude);

        TextView accTextView = (TextView) findViewById(R.id.hikersAccuracy);

        TextView addressTextView = (TextView) findViewById(R.id.hikersAddress);

        latTextView.setText(String.format("Latitude: %.2f" , location.getLatitude()));

        lonTextView.setText(String.format("Longitude: %.2f" , location.getLongitude()));

        altTextView.setText(String.format("Altitude: %.2f" , location.getAltitude()));

        accTextView.setText("Accuracy: " + location.getAccuracy());

        //TURN latlong into an address -> Locale.getDefault()-> Locale is a Country. getDefault() Will return the current's user Locale information.
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            String finalAddress = "Could not find address";

            //The last parameter: maxResults -> it may give multiple locations, by passing 1 you get the best match.
            List<Address> addressesList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addressesList != null && addressesList.size() > 0){

                String firstAddressInListObject = addressesList.get(0).toString();
                Log.d(TAG, "onLocationChanged - PlaceInfo: "+firstAddressInListObject);
                // OBJECT -> ADDRESS Object: [addressLines=[0:"52 London Rd, London N1 2EJ, UK"],feature=52,admin=England,sub-admin=Greater London,locality=null,thoroughfare=London Rd,postalCode=N1 2EJ,countryCode=GB,countryName=UK,hasLatitude=true,latitude=XXXX,hasLongitude=true,longitude=XXXX,phone=null,url=null,extras=null]

                String firstAddress = addressesList.get(0).getAddressLine(0);
                finalAddress = firstAddress;
                Log.d(TAG, "onLocationChanged - PlaceInfo: "+finalAddress); // String -> Your line addresss:  52 London Rd, London N1 2EJ, UK

                //or///////////////////////////////
//                String addressFromParts = "";
//                if (addressesList.get(0).getThoroughfare() != null) {
//                    addressFromParts += addressesList.get(0).getThoroughfare() + " ";
//                }
//
//                if (addressesList.get(0).getLocality() != null) {
//                    addressFromParts += addressesList.get(0).getLocality() + " ";
//                }
//
//                if (addressesList.get(0).getPostalCode() != null) {
//                    addressFromParts += addressesList.get(0).getPostalCode() + " ";
//                }
//
//                if (addressesList.get(0).getAdminArea() != null) {
//                    addressFromParts += addressesList.get(0).getAdminArea();
//                }
//                finalAddress = addressFromParts;
                ////////////////////////////////////////
            }

            addressTextView.setText(finalAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    Toast.makeText(HikersWatch.this, "lastKnownLocation: Exists", Toast.LENGTH_SHORT).show();
                    updateLocationInfo(lastKnownLocation);
                } else {
                    Log.d(TAG, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES");
                    Toast.makeText(HikersWatch.this, "lastKnownLocation: DOES NOT EXIST, GET NEW LOCATION UPDATES", Toast.LENGTH_SHORT).show();
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


    //execute the Listener in listenerSetup
    public void executeListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Wait for a while after getting your first location. The location tends to wobble for a few seconds.
            // To determine if the fix is stable use, location.getAccuracy(). Once the accuracy stabilizes, call locationManager.removeUpdates(mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    //execute the Listener in listenerSetup
    public void checkAccuracyStable() {
        Set<Float> accuracySet = new HashSet<Float>(accuracyFrecuency);
        for(float s: accuracyFrecuency){
            Log.d(TAG, "checkAccuracyStable: accuracy:" + s);
            int frecuency = Collections.frequency(accuracyFrecuency,s);
            Log.d(TAG, "checkAccuracyStable: frecuency:" + frecuency);

            if(frecuency >= isStableAt) {
                stable = true;
                break;
            }
        }
        Log.d(TAG, "checkAccuracyStable: " + stable);
        if(locationManager !=null){
            if(stable){
                //Stop the locationListener LOOP
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikers_watch);
        //just setup the Listener.
        listenerSetup();
        //check for permissions, once granted: Execute the listener.
        permissionCheck();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(locationManager !=null){
            //Stop the locationListener LOOP
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
