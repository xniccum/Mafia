package com.squad.ana.mafia;

import android.location.Location;

import java.util.List;

/**
 * Created by xniccum on 5/17/16.
 */
public class Entity {
    private Location location;
    private String macAddress;
    private boolean hiding;
    private final int RANGE = 30;
    private final int KILLRANGE = 5;
    private boolean dead;
    private int score;
    private boolean sighted;

    public Entity(Location location, String macAddress) {
        this.location = location;
        this.hiding = true;
        this.dead = false;
        this.score = 0;
        this.macAddress = macAddress;
    }

    public String getMacAddress() { return macAddress; }
    public boolean isHiding() { return hiding;}
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() { return location;}
    public int getScore() {return score;}
    public boolean isTarget() { return sighted; }

    public void toggleHiding() { hiding = !hiding;}

    public boolean inRange(Location...targets) {
        return inRange(targets);
    }

    public boolean inRange(List<Location> locations) {
        for(Location loc: locations) {
            if(this.location.distanceTo(loc) <= RANGE)
                return true;
        }
        return false;
    }

    public void attack(List<Entity> players) {
        float dist = RANGE;
        Entity target = null;

        for(Entity p: players) {
            // loops through the list of known players to find the nearest one
            if (this.location.distanceTo(p.getLocation()) < dist) {
                dist = this.location.distanceTo(p.getLocation());
                target = p;
            }
        }

        if (dist < KILLRANGE && !target.hiding) {
            // target is set to nearest player, they are in the kill range and they are not hiding
                // this is where we set the target as the MAC address of the closest player,
                //      assembling the "attack message" to be broadcast
            target.die(); // this line will likely change, but I think it conveys the logic well,  the attacker is telling the target to die
            this.score++;
        }
    }

    public void die() {
        // this is where we would handle our own death, as the target.
            // In other words, our MAC address was received in the "attack message" broadcast
        this.dead = true;
        // Here is where we implement what happens when we die
    }




}
