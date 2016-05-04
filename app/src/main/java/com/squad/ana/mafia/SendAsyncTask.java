package com.squad.ana.mafia;

import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
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

    public SendAsyncTask(String host, int port, String content) {
        this.host = host;
        this.port = port;
        this.buf = content.getBytes(Charset.forName("UTF-8"));;
    }

    @Override
    protected Long doInBackground(URL... params) {

        // IF attacker, setup an event listener for a "shoot" button. Send "update position"
        // Otherwise, send "update position"
        System.out.println("Starting sending task");
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);
            System.out.println("Socket connetion established");

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(buf, 0, buf.length);
            outputStream.close();
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
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
