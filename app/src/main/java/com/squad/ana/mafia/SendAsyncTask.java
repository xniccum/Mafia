package com.squad.ana.mafia;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by millerna on 5/3/2016.
 */
public class SendAsyncTask extends AsyncTask<URL, Integer, Long> {

    String host;
    int port;
    Socket socket = new Socket();
    byte buf[];
    Context context;

    public SendAsyncTask(Context context, String host, int port, String content) {
        this.host = host;
        this.port = port;
        this.buf = content.getBytes(Charset.forName("UTF-8"));;
        this.context = context;
    }

    public InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    @Override
    protected Long doInBackground(URL... params) {

        DatagramSocket socket = null;
        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Toast.makeText(context, "Starting async task: Group owner: " + getBroadcastAddress().toString() + "", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "Get broadcast address exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            //Open a random port to send the package
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, getBroadcastAddress(), port);
            socket.send(sendPacket);

            final String address = getBroadcastAddress().getHostAddress();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Broadcast packet sent to: " + address, Toast.LENGTH_SHORT).show();
                }
            });
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
        return null;
    }
}
