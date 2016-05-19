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

    private AsyncTask update;

    public LocationCountDownTimer(long startTime, long interval, RadarActivity rActivity, AsyncTask update) {
        super(startTime, interval);
        this.rActivity = rActivity;
        this.update = update;
    }

    @Override
    public void onFinish() {
        rActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(rActivity, "Countdown finished", Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            update.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            update.execute();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long time = millisUntilFinished / 1000;
    }
}
