package com.squad.ana.mafia.engine;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;

import com.squad.ana.mafia.message.AttackMessage;
import com.squad.ana.mafia.message.IProtocol;
import com.squad.ana.mafia.message.UpdateMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xniccum on 5/17/16.
 */
public class Engine {
    private static final int RANGE = 30;

    private static Location location;
    private static String macAddress;
    private static Map<String,UpdateMessage> players;
    private static boolean hiding;
    private static boolean dead;
    private static int score;
    private static boolean sighted;
    private static boolean initilized = false;

    public static void init(Context context) {
        macAddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .getConnectionInfo()
                .getMacAddress();
        players = new HashMap<>();
        location = null;
        hiding = true;
        dead = false;
        score = 0;
        initilized = true;
    }

    public static boolean isInitilized() {
        return initilized;
    }

    public static String getMacAddress() { return macAddress; }
    public static boolean isHiding() { return hiding;}
    public static void setLocation(Location location) {
        Engine.location = location;
    }
    public static Location getLocation() { return location;}
    public static int getScore() {return score;}
    public static boolean isTarget() { return sighted; }

    public static void toggleHiding() { hiding = !hiding;}

    public static void updatePlayers(UpdateMessage message) {
        if (!players.containsKey(message.getSrc()))
            players.remove(message.getSrc());
        players.put(message.getSrc(), message);
    }
    public static Map<String, UpdateMessage> getPlayers() {
        return players;
    }

    public static String attack() {
        final int KILLRANGE = 5;
        float dist = RANGE;
        UpdateMessage target = null;

        float playerDistance;
        UpdateMessage tempPlayer;
        for(String p: players.keySet()) {
            tempPlayer = players.get(p);
            playerDistance = distanceTo(tempPlayer.getLocation());
            // loops through the list of known players to find the nearest one
            if (tempPlayer.isHidden() && playerDistance < dist) {
                dist = playerDistance;
                target = tempPlayer;
            }
        }

        if (dist < KILLRANGE) {
            Engine.score++;
            return target.getSrc();
        }
        return null;
    }

    public static void die() {
        // this is where we would handle our own death, as the target.
            // In other words, our MAC address was received in the "attack message" broadcast
        dead = true;
        score = 0;
        // Here is where we implement what happens when we die
    }

    private static float distanceTo(double[] latlong) {
        //may have troubles due of intricacies of distanceBetween
        float[] temp = new float[3];
        Location.distanceBetween(
                Engine.location.getLatitude(),Engine.location.getLongitude(),
                latlong[0],latlong[1],
                temp
                );
        return temp[0];
    }




}
