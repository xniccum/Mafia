package com.squad.ana.mafia;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.net.URL;

public class LocationCountDownTimer extends CountDownTimer {

    private final MainActivity mActivity;
    private final WifiP2pConfig config;
    private final int port;

    public LocationCountDownTimer(long startTime, long interval, MainActivity mActivity, WifiP2pConfig config, int port) {
        super(startTime, interval);
        this.mActivity = mActivity;
        this.config = config;
        this.port = port;
    }

    @Override
    public void onFinish() {
        ((Activity)mActivity).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mActivity, "Countdown finished", Toast.LENGTH_SHORT).show();
            }
        });
        AsyncTask<URL, Integer, Long> sendTask = new SendAsyncTask(mActivity, config.deviceAddress, port, "My Location");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            sendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            sendTask.execute();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long time = millisUntilFinished / 1000;
    }
}