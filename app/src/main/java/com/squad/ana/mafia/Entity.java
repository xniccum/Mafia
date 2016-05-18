package com.squad.ana.mafia;

import android.location.Location;
import android.location.LocationListener;

import java.util.List;

/**
 * Created by xniccum on 5/17/16.
 */
public class Entity {
    private Location location;
    private boolean hiding;
    private final int RANGE = 30;

    public Entity(Location location) {
        this.location = location;
        this.hiding = true;
    }

    public boolean isHiding() { return hiding;}
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() { return location;}

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




}
