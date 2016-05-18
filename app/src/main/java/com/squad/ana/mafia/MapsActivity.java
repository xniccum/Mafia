package com.squad.ana.mafia;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Entity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.entity = new Entity(new Location(LocationManager.GPS_PROVIDER));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        beginLocationUpdates(locationListener);
    }

    private void beginLocationUpdates(LocationListener listener) {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Begin Location Updates If given permission by user
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        getResources().getInteger(R.integer.min_time),
                        getResources().getInteger(R.integer.min_distance),
                        listener);
            }
            else {
                Log.d("Mafia-location","GPS Provider not enabled");
            }
        }
        else {
            Log.d("Mafia-location","Permission Not Granted");
        }
    }

    private void setLocation(Location loc) {
        entity.setLocation(loc);
        LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(current));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }
}
