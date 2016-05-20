package com.squad.ana.mafia.network;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squad.ana.mafia.engine.Engine;
import com.squad.ana.mafia.message.IProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by millerna on 5/3/2016.
 */
public class SendAsyncTask extends AsyncTask<Object, Integer, Long> {

    Context context;
    private IProtocol message;

    public SendAsyncTask(Context context, IProtocol message) {
        this.context = context;
        this.message = message;
    }

    @Override
    protected Long doInBackground(Object... params) {


        Map<String, String> addresses = new HashMap<String, String>();
        addresses.put("f8:e0:79:3c:3e:d6","137.112.221.210");
        addresses.put("78:24:af:14:e6:38","137.112.237.16");
        addresses.put("78:4b:87:4f:DC:DC","137.112.231.148");

        for (String mac : addresses.keySet()) {
            if (!Engine.getMacAddress().toLowerCase().equals(mac.toLowerCase())) {
                sendMessage(addresses.get(mac));
            }
        }
        return null;
    }

    private void sendMessage(String address) {
        DatagramSocket socket = null;
        try {
            final InetAddress broadcastAddress = InetAddress.getByName(address);
            if (broadcastAddress != null) {
                byte[] buf = message.toString().getBytes(Charset.forName("UTF-8"));

                //Open a random port to send the package
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, broadcastAddress, IProtocol.PORT);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            final String exc = e.toString();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Send Exception: " + exc, Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    socket.close();
                }
            }
        }
    }
}
