package com.squad.ana.mafia;

/**
 * Created by millerna on 5/6/2016.
 */
public class Protocol implements IProtocol {

    private String location;
    private String type;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Headers.TYPE.toString() + ": " + this.type);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.LOCATION.toString() + ": " + this.location);
        buffer.append(System.getProperty("line.separator"));
        return buffer.toString();
    }
}
