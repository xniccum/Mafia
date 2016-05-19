package com.squad.ana.mafia.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squad.ana.mafia.Timers.LocationUpdateTimer;
import com.squad.ana.mafia.engine.Engine;
import com.squad.ana.mafia.message.AttackMessage;
import com.squad.ana.mafia.R;
import com.squad.ana.mafia.message.UpdateMessage;
import com.squad.ana.mafia.network.ReceiveAsyncTask;
import com.squad.ana.mafia.network.SendAsyncTask;
import com.squad.ana.mafia.network.WiFiDirectBroadcastReceiver;

import java.util.Collection;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class RadarActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationSource.OnLocationChangedListener mapListener = null;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        //Initialized
        Engine.init(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get buttons
        Button fireBT = (Button) findViewById(R.id.fireBT);
        Button hideBT = (Button) findViewById(R.id.hideBT);

        //add button listeners
        fireBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = Engine.attack();
                if(address != null) {
                    // Create update message
                    AttackMessage message = new AttackMessage();
                    message.setTarget(address);

                    new SendAsyncTask(RadarActivity.this, message).execute();
                }
            }
        });
        hideBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Engine.toggleHiding();
                Log.d("Mafia","Hiding = "+Engine.isHiding());
            }
        });

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        new ReceiveAsyncTask(this).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mapListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {
               mapListener = null;
            }
        });

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setLocation(location);
                mapListener.onLocationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        beginLocationUpdates(locationListener);
    }

    private void beginLocationUpdates(LocationListener listener) {
        mMap.setMyLocationEnabled(true);

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

        new LocationUpdateTimer(100, 50,this).start();
    }

    private void setLocation(Location loc) {
        Engine.setLocation(loc);
        LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
//        if(Engine.isTarget()) {
//            mMap.addMarker(new MarkerOptions().
//                    position(current).
//                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//        } else {
//            mMap.addMarker(new MarkerOptions().position(current));
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel()));
    }

    public void updatePlayers() {
        mMap.clear();
        Collection<UpdateMessage> messages = Engine.getPlayers().values();
        for(UpdateMessage update: messages) {
            LatLng current = new LatLng(update.getLocation()[0], update.getLocation()[1]);
            mMap.addMarker(new MarkerOptions().position(current));
        }
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
