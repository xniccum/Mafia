package com.squad.ana.mafia;

import android.location.Location;

/**
 * Created by millerna on 5/6/2016.
 */
public class UpdateMessage implements IProtocol {

    private Location location;
    private String type;
    private boolean isHidden;
    private String src;

    public boolean getIsHidden() {return isHidden; }

    public void setIsHidden(boolean isHidden) { this.isHidden = isHidden; }

    public String getSrc() { return src; }

    public void setSrc(String src) { this.src = src; }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Headers.TYPE.toString() + ": " + this.type);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.SOURCE.toString() + ": " + this.src);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.LOCATION.toString() + ": " + this.location);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.IS_HIDDEN.toString() + ": " + this.isHidden);
        buffer.append(System.getProperty("line.separator"));
        return buffer.toString();
    }
}
