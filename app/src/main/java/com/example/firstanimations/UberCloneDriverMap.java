package com.example.firstanimations;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UberCloneDriverMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Double requestLatitude = null;
    Double requestLongitude = null;
    Double driverLatitude = null;
    Double driverLongitude = null;
    String riderUsername = null;

    public void acceptRequest(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", riderUsername);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size() > 0){
                        for (ParseObject object: objects){
                            //Add the name of this Driver that accepted the request;
                            object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        //SIMPLY LAUNCH GOOGLE MAPS WITH BOTH MARKERS
                                        String mUri = "http://maps.google.com/maps?saddr=" + driverLatitude + "," + driverLongitude + "&daddr=" + requestLatitude + "," + requestLongitude;
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri));
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_clone_driver_map);

        Intent fromDriverIntent = getIntent();
        if (fromDriverIntent.hasExtra("requestLatitude") && fromDriverIntent.hasExtra("requestLongitude") && fromDriverIntent.hasExtra("driverLatitude")
                && fromDriverIntent.hasExtra("driverLongitude") && fromDriverIntent.hasExtra("username")) {
            requestLatitude = fromDriverIntent.getDoubleExtra("requestLatitude", 0);
            requestLongitude = fromDriverIntent.getDoubleExtra("requestLongitude", 0);
            driverLatitude = fromDriverIntent.getDoubleExtra("driverLatitude", 0);
            driverLongitude = fromDriverIntent.getDoubleExtra("driverLongitude", 0);
            riderUsername = fromDriverIntent.getStringExtra("username");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //We need this because the code we are using to show 2 marker, may throw an error, saying that we are updating the map before it is defined.
        ConstraintLayout mapLayout = (ConstraintLayout)findViewById(R.id.uberDriverConstraintLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialPositionRiderAndDriver();
            }
        });

    }

    private void initialPositionRiderAndDriver(){

        LatLng driverLocation = new LatLng(driverLatitude, driverLongitude);
        LatLng riderLocation = new LatLng(requestLatitude, requestLongitude);

        //YOU NEED TO PUT BOTH MARKERS IN AN ARRAYLIST
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));//Driver's location
        markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Request location")));//Rider's location
        //USE THIS CODE TO SHOW BOTH MARKERS ON THE MAP:
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();//holds both Markers(with lat and long each)
        int padding = 30;//offset from edges of the map in pixels//If this is too SMALL -> error
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);

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
        //initializing it in the onCreate/ onGlobalLayout
    }
}
