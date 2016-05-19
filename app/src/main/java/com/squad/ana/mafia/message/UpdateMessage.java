package com.squad.ana.mafia.message;

/**
 * Created by millerna on 5/6/2016.
 */
public class UpdateMessage implements IProtocol {

    private String type;
    private boolean isHidden;
    private String src;
    private double[] loc;

    public UpdateMessage() {
        this.type = IProtocol.UPDATE;
    }

    public boolean isHidden() {return isHidden; }

    public void setHidden(boolean isHidden) { this.isHidden = isHidden; }

    public String getSrc() { return src; }

    public void setSrc(String src) { this.src = src; }

    @Override
    public String getType() {
        return type;
    }

    public double[] getLocation() { return this.loc; }

    public void setLocation(double[] location) { this.loc = location; }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Headers.TYPE.toString() + ": " + this.type);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.SOURCE.toString() + ": " + this.src);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.LOCATION.toString() + ": " + loc[0] + "," + loc[1]);
        buffer.append(System.getProperty("line.separator"));
        buffer.append(Headers.IS_HIDDEN.toString() + ": " + this.isHidden);
        buffer.append(System.getProperty("line.separator"));
        return buffer.toString();
    }
}
