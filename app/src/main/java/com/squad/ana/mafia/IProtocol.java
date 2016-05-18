package com.squad.ana.mafia;

/**
 * Created by millerna on 5/6/2016.
 */
public interface IProtocol {

    public enum Headers {
        TYPE,
        LOCATION
    }

    public static final String LOCATION = "LOCATION";
    public static final String ATTACK = "ATTACK";

    public String getType();
    public void setType(String type);
    public String getLocation();
    public void setLocation(String location);
    public String toString();
}
