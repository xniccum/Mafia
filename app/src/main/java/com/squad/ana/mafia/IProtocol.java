package com.squad.ana.mafia;

/**
 * Created by millerna on 5/6/2016.
 */
public interface IProtocol {

    public enum Headers {
        TYPE,
        SOURCE,
        LOCATION,
        IS_HIDDEN,
        TARGET
    }

    // Current types of protocols
    public static final int PORT = 8888;
    public static final String UPDATE = "UPDATE";
    public static final String ATTACK = "ATTACK";

    public String getType();
    public void setType(String type);
    public String toString();
}
