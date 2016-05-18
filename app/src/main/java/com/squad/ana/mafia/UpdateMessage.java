package com.squad.ana.mafia;

import android.location.Location;

/**
 * Created by millerna on 5/6/2016.
 */
public class UpdateMessage implements IProtocol {

    private String type;
    private boolean isHidden;
    private String src;
    private Double[] coords;

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

    public Double[] getLocation() { return this.coords; }

    public void setLocation(Double[] coords) { this.coords = coords; }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Headers.TYPE.toString() + ": " + this.type);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.SOURCE.toString() + ": " + this.src);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.LOCATION.toString() + ": " + this.coords[0] + "," + this.coords[1]);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.IS_HIDDEN.toString() + ": " + this.isHidden);
        buffer.append(System.getProperty("line.separator"));
        return buffer.toString();
    }
}
