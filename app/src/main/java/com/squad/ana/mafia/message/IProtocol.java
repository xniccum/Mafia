package com.squad.ana.mafia.message;

/**
 * Created by millerna on 5/6/2016.
 */
public interface IProtocol {

    enum Headers {
        TYPE,
        SOURCE,
        LOCATION,
        IS_HIDDEN,
        TARGET
    }

    // Current types of protocols
    int PORT = 8888;
    String UPDATE = "UPDATE";
    String ATTACK = "ATTACK";

    String getType();
    String toString();
}
