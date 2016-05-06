package com.squad.ana.mafia;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

        // IF attacker, setup an event listener for a "shoot" button. Send "update position"
        // Otherwise, send "update position"
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(new WifiP2pInfo().groupOwnerAddress, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(buf, 0, buf.length);
            outputStream.close();

        } catch (FileNotFoundException e) {
            final String exc = e.toString();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, exc, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            final String exc = e.toString();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, exc, Toast.LENGTH_SHORT).show();
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
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return null;
    }
}
