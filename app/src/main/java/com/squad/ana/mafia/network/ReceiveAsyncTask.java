package com.squad.ana.mafia.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.squad.ana.mafia.engine.Engine;
import com.squad.ana.mafia.message.AttackMessage;
import com.squad.ana.mafia.message.IProtocol;
import com.squad.ana.mafia.message.UpdateMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class ReceiveAsyncTask extends AsyncTask<URL, Integer, Long> {

    private Context context;

    public ReceiveAsyncTask(Context context) {
        this.context = context;
    }

    /**
     * IF mob, start activity that can handle shooting
     */
    @Override
    protected void onPostExecute(Long result) {
        // Do nothing
    }

    @Override
    protected Long doInBackground(URL... urls) {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            DatagramSocket socket = new DatagramSocket(IProtocol.PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                final String data = new String(packet.getData()).trim();
                IProtocol request = parseRequestString(data);

                if (request.getType().equals(IProtocol.UPDATE)) {
                    if(!((UpdateMessage)request).getSrc().toLowerCase().equals(Engine.getMacAddress().toLowerCase())) {
                        Engine.updatePlayers((UpdateMessage)request);
                    }
                }

                // Send the packet data back to the UI thread
//                Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
//                        // Puts the data into the Intent
//                        .putExtra(Constants.EXTENDED_DATA_STATUS, data);
//                // Broadcasts the Intent to receivers in this app.
//                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        } catch (IOException ex) {
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }

    public IProtocol parseRequestString(String requestString) {
        String[] fields = requestString.split("\n");
        System.out.println(fields[0]);
        IProtocol protocol = null;
        String[][] fieldPairs = new String[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            fieldPairs[i] = fields[i].split(";");
        }

        String fieldHeader = fieldPairs[0][0].trim();
        String fieldProperty = fieldPairs[0][1].trim();
        if (fieldHeader.equals(IProtocol.Headers.TYPE.toString())) {
            if (fieldProperty.equals(IProtocol.ATTACK)) {
                protocol = new AttackMessage();;
                ((AttackMessage) protocol).setTarget(fieldPairs[1][1]);
            } else {
                protocol = new UpdateMessage();
                ((UpdateMessage) protocol).setSrc(fieldPairs[1][1]);
                String[] location = fieldPairs[2][1].split(",");
                double latitude = Double.parseDouble(location[0]);
                double longitude = Double.parseDouble(location[1]);
                ((UpdateMessage) protocol).setLocation(new double[]{latitude, longitude});
                String hidden = fieldPairs[3][1];
                ((UpdateMessage) protocol).setHidden(Boolean.parseBoolean(hidden.trim()));
            }
        }
        return protocol;
    }
}
