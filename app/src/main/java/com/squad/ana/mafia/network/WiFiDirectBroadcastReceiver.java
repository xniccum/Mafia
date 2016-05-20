package com.squad.ana.mafia.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.squad.ana.mafia.engine.Engine;
import com.squad.ana.mafia.message.UpdateMessage;
import com.squad.ana.mafia.activity.RadarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private RadarActivity rActivity;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private CountDownTimer updateTimer;
    private Handler updateHandler = new Handler();
    private int updateInterval = 5000;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, RadarActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.rActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("WiFi P2P is enabled");

                //  new ReceiveAsyncTask(rActivity).execute();
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Succeeded discovering devices.");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        System.out.println("Failed discovering devices.");
                    }
                });
            } else {
                System.out.println("WiFi P2P is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            System.out.println("Requesting peers");
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            rActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(rActivity, "Requesting peers", Toast.LENGTH_SHORT).show();
                }
            });

            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            System.out.println("WIFI_P2P_CONNECTION_CHANGED_ACTION");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            System.out.println("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(Engine.isInitilized()) {
                // Out with the old, in with the new.
                stopUpdatingTask();
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                System.out.println("Printing peers: ");

                for (WifiP2pDevice device : peers) {
                    System.out.println("Peer: " + device.toString());

                    final WifiP2pConfig config = new WifiP2pConfig();
                    config.wps.setup = WpsInfo.PBC;
                    config.deviceAddress = device.deviceAddress;

                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            ((Activity) rActivity).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(rActivity, "Connect Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //and stop timer using
                            // cDownTimer.cancel();
                            // something like that
                        }

                        @Override
                        public void onFailure(int reason) {
                            System.out.println("Connect Failure: Reason, " + reason);
                        }
                    });
                }
                startUpdatingTask();
            }

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
//            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
//            if (peers.size() == 0) {
//                Log.d(WiFiDirectActivity.TAG, "No devices found");
//                return;
//            }
        }
    };

    Runnable mUpdateRunner = new Runnable() {
        @Override
        public void run() {
            try {
                Location location = Engine.getLocation();
                String macAddress = Engine.getMacAddress();
                System.out.println("UPDATING TASK");
                if (location != null) {

                    // Create update message
                    UpdateMessage message = new UpdateMessage();
                    message.setHidden(Engine.isHiding());
                    message.setLocation(new double[]{location.getLatitude(), location.getLongitude()});
                    message.setSrc(macAddress);

                    AsyncTask<Object, Integer, Long> updateTask = new SendAsyncTask(rActivity, message);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    else
                        updateTask.execute();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                updateHandler.postDelayed(mUpdateRunner, updateInterval);
            }
        }
    };

    void startUpdatingTask() {
        mUpdateRunner.run();
    }

    void stopUpdatingTask() {
        updateHandler.removeCallbacks(mUpdateRunner);
    }
}