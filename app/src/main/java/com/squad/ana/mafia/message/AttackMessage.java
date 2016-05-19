package com.squad.ana.mafia.message;

/**
 * Created by millerna on 5/18/2016.
 */
public class AttackMessage implements IProtocol {

    private String type;
    private String target;

    public AttackMessage() {
        this.type = IProtocol.ATTACK;
    }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(IProtocol.Headers.TYPE.toString() + ": " + this.type);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(IProtocol.Headers.TARGET.toString() + ": " + this.target);
        buffer.append(System.getProperty("line.separator"));
        return buffer.toString();
    }
}
