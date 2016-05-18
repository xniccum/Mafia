package com.squad.ana.mafia;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class ReceiveAsyncTask extends AsyncTask<URL, Integer, Long> {

    private Context context;
    private int port;

    public ReceiveAsyncTask(Context context, int port) {
        this.context = context;
        this.port = port;
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
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            InputStream inputstream = client.getInputStream();
            final String requestString = readInput(inputstream);

            // Parse the request and return the protocol
//            IProtocol protocol = parseRequestString(requestString);

            System.out.println("Receiving Request: " + requestString);

//            System.out.println("Location: " + protocol.getLocation());

            serverSocket.close();
            System.out.println("Closing receiving socket");
            return null;
        } catch (IOException e) {
            System.out.println("ReceiveAsyncTask.doInBackground Exception: " + e);
            return null;
        }
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
        IProtocol protocol = new Protocol();
        for(String field : fields) {
            String[] fieldPair = field.split(":");
            String fieldHeader = fieldPair[0].trim();
            String fieldProperty = fieldPair[1].trim();
            if (fieldHeader == IProtocol.Headers.TYPE.toString()) {
                if (fieldProperty == IProtocol.ATTACK) {
                    protocol.setType(IProtocol.ATTACK);
                } else {
                    protocol.setType(IProtocol.LOCATION);
                }
            }
            if (fieldHeader == IProtocol.Headers.LOCATION.toString()) {
                protocol.setLocation(fieldProperty);
            }
        }
        return protocol;
    }
}
