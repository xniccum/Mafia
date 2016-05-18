package com.squad.ana.mafia;

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
import android.os.CountDownTimer;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private RadarActivity rActivity;
    private List<WifiP2pDevice> peers = new ArrayList();
    private Entity entity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, RadarActivity activity, Entity entity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.rActivity = activity;
        this.entity = entity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        // TODO: Refactor using strategy pattern
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("WiFi P2P is enabled");

                new ReceiveAsyncTask(rActivity).execute();
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

            ((Activity) rActivity).runOnUiThread(new Runnable() {
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

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            System.out.println("Printing peers: ");

            final long startTime = 10;
            final long interval = 10;

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
            Location loc = entity.getLocation();
            String macAddress = entity.getMacAddress();
            System.out.println("TEST1");
            AsyncTask<Object, Integer, Long> sendTask = new SendAsyncTask(rActivity, loc, macAddress);
            CountDownTimer cDownTimer = new LocationCountDownTimer(startTime, interval, rActivity, sendTask);
            cDownTimer.start();

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
}