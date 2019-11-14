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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_CODE = 1;

    private GoogleMap mMap;

    //Getting user's location=================================================================================
    LocationManager locationManager;
    LocationListener locationListener;

    public void locationSetup() {
        //Manages User's location: it get's the user's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Tracks movements
        locationListener = new LocationListener() {
            //Everytime location has changed:
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", "onLocationChanged: " + location.toString());
                Log.i("Location", "onLocationChanged: Longitude" + location.getLongitude());
                Log.i("Location", "onLocationChanged: Latitud" + location.getLatitude());

                //if location has changed, you should Clear the previous location
                mMap.clear();

                // Add a marker in Sydney and move the camera
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(everest).title("Biggest in the world"));//Normal GPS view Button
                //Make the icon Pointer Yellow: you can create your own.
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(everest));//Normal view
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));//Normal view// 20 is the max.

                //TURN latlong into an address -> Locale.getDefault()-> Locale is a Country. getDefault() Will return the current's user Locale information.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    //The last parameter: maxResults -> it may give multiple locations, by passing 1 you get the best match.
                    List<Address> addressesList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addressesList != null && addressesList.size() > 0){
                        String addressFromParts = "";
                        String firstAddressInListObject = addressesList.get(0).toString();
                        Log.d(TAG, "onLocationChanged - PlaceInfo: "+firstAddressInListObject);
                        // OBJECT -> ADDRESS Object: [addressLines=[0:"52 London Rd, London N1 2EJ, UK"],feature=52,admin=England,sub-admin=Greater London,locality=null,thoroughfare=London Rd,postalCode=N1 2EJ,countryCode=GB,countryName=UK,hasLatitude=true,latitude=XXXX,hasLongitude=true,longitude=XXXX,phone=null,url=null,extras=null]

                        String firstAddress = addressesList.get(0).getAddressLine(0);
                        Log.d(TAG, "onLocationChanged - PlaceInfo: "+firstAddress); // String -> Your line addresss:  52 London Rd, London N1 2EJ, UK
                        //or
                        if (addressesList.get(0).getThoroughfare() != null) {
                            addressFromParts += addressesList.get(0).getThoroughfare() + " ";
                        }

                        if (addressesList.get(0).getLocality() != null) {
                            addressFromParts += addressesList.get(0).getLocality() + " ";
                        }

                        if (addressesList.get(0).getPostalCode() != null) {
                            addressFromParts += addressesList.get(0).getPostalCode() + " ";
                        }

                        if (addressesList.get(0).getAdminArea() != null) {
                            addressFromParts += addressesList.get(0).getAdminArea();
                        }

                        Toast.makeText(MapsActivity.this, addressFromParts, Toast.LENGTH_SHORT).show();


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    //=================================================================================
    //PERMISSION REQUEST================================================================================================================
    public void permissionCheck(){

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            //If not permission granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else { //if user has given us  permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);

                //Get the last known location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // Add a marker in Sydney and move the camera
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(everest).title("Biggest in the world"));//Normal GPS view Button
                //Make the icon Pointer Yellow: you can create your own.
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(everest));//Normal view
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));//Normal view// 20 is the max.
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
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    //gps_provider | how often in time do we want to update location in: Minutes| Distance| LocationListener
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
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
    //===========================================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //Gets/Start the Map and Execute the OnMapReadyCallback once it's ready(Where you set the Map to a variable mMap).
        mapFragment.getMapAsync(this);

        //Setup the location Manager and Listener
        locationSetup();

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
        //Set the Created Map to the mMap variable.
        mMap = googleMap;

        //Once map is ready, execute the permission check.
        permissionCheck();

//        //Add this line to have satellite view
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//        // Add a marker in Sydney and move the camera
//        LatLng everest = new LatLng(28.0249447, 86.8869557);
//        //mMap.addMarker(new MarkerOptions().position(everest).title("Biggest in the world"));//Normal GPS view Button
//        //Make the icon Pointer Yellow: you can create your own.
//        mMap.addMarker(new MarkerOptions().position(everest).title("Biggest in the world").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//        //mMap.moveCamera(CameraUpdateFactory.newLatLng(everest));//Normal view
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(everest,5));//Normal view// 20 is the max.

    }
}
