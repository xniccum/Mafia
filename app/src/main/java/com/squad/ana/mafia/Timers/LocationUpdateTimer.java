package com.squad.ana.mafia.Timers;

import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.squad.ana.mafia.activity.RadarActivity;

/**
 * Created by xniccum on 5/19/16.
 */
public class LocationUpdateTimer extends CountDownTimer {

    private RadarActivity rActivity;

    public LocationUpdateTimer(long startTime, long interval, RadarActivity rActivity) {
        super(startTime, interval);
        this.rActivity = rActivity;
    }

    @Override
    public void onFinish() {
        rActivity.updatePlayers();
        this.start();
    }

    @Override
    public void onTick(long millisUntilFinished) {}
}
