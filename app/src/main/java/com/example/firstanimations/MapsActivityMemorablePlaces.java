package com.example.firstanimations;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivityMemorablePlaces extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivityMemorablePl";
    private GoogleMap mMap;
    int positionForLocations = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_memorable_places);

        Intent fromIntent = getIntent();

        if(fromIntent.hasExtra("positionForLocations")){
            positionForLocations = fromIntent.getIntExtra("positionForLocations", 0);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void showMarkerFirstTime(LatLng latLng){
        Log.i("Location", "showMarkerFirstTime: Latitud" + latLng.latitude);
        Log.i("Location", "showMarkerFirstTime: Longitude" + latLng.longitude);

        String address = getAddress(latLng);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(latLng).title(String.format("%s",address)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public String getAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {

            List<Address> listAdddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (listAdddresses != null && listAdddresses.size() > 0) {
                // OBJECT -> ADDRESS Object: [addressLines=[0:"52 London Rd, London N1 2EJ, UK"],feature=52,admin=England,sub-admin=Greater London,locality=null,thoroughfare=London Rd,postalCode=N1 2EJ,countryCode=GB,countryName=UK,hasLatitude=true,latitude=XXXX,hasLongitude=true,longitude=XXXX,phone=null,url=null,extras=null]
                if (listAdddresses.get(0).getThoroughfare() != null) {
                    if (listAdddresses.get(0).getSubThoroughfare() != null) {
                        address += listAdddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAdddresses.get(0).getThoroughfare();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (address.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address += sdf.format(new Date());
        }

        return address;

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

        //Get the LatLong Object from the static locations ArrayList in MemorablePlaces
        showMarkerFirstTime(MemorablePlaces.locations.get(positionForLocations));
    }


    // Sets a Google onlongpressed listener since we are IMPLEMENTING GoogleMap.OnMapLongClickListener
    @Override
    public void onMapLongClick(LatLng location) {
        Log.d(TAG, "onMapLongClick: pressed");
        String address = getAddress(location);

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        MemorablePlaces.places.add(location.toString());
        MemorablePlaces.locations.add(location);

        //updates the arrayAdapter
        MemorablePlaces.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(),
                "New marker added at " + location.toString(), Toast.LENGTH_LONG)
                .show();
    }


}
