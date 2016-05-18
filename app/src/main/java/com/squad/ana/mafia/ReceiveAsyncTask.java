package com.squad.ana.mafia;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Ready to receive broadcast packets!", Toast.LENGTH_SHORT).show();
                    }
                });

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Packet received from: " + packet.getAddress().getHostAddress(), Toast.LENGTH_SHORT).show();
                    }
                });
                final String data = new String(packet.getData()).trim();
                IProtocol request = parseRequestString(data);
                if (request.getType() == IProtocol.UPDATE) {
                    // TODO: Handle update request
                } else {
                    // TODO: Handle attack request
                }


                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Packet received; data: " + data, Toast.LENGTH_SHORT).show();
                    }
                });

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

    private String readInput(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    public IProtocol parseRequestString(String requestString) {
        String[] fields = requestString.split("\n");
        IProtocol protocol = new UpdateMessage();
        String[][] fieldPairs = new String[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            fieldPairs[i] = fields[i].split(":");
        }

        String fieldHeader = fieldPairs[0][0].trim();
        String fieldProperty = fieldPairs[0][1].trim();
        if (fieldHeader == IProtocol.Headers.TYPE.toString()) {
            if (fieldProperty == IProtocol.ATTACK) {
                protocol.setType(IProtocol.ATTACK);
                ((AttackMessage) protocol).setTarget(fieldPairs[1][1]);
            } else {
                protocol.setType(IProtocol.UPDATE);
                ((UpdateMessage) protocol).setSrc(fieldPairs[1][1]);
                String[] location = fieldPairs[2][1].split(",");
                double latitude = Double.parseDouble(location[0]);
                double longitude = Double.parseDouble(location[1]);
                ((UpdateMessage) protocol).setLocation(new Double[]{latitude, longitude});
                ((UpdateMessage) protocol).setIsHidden(Boolean.parseBoolean(fieldPairs[3][1]));
            }
        }
        return protocol;
    }
}
