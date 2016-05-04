package com.squad.ana.mafia;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
        System.out.println("Starting server background task");
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
            String request = readInput(inputstream);
            System.out.println("Request: " + request);
            Toast.makeText(context, request,
                    Toast.LENGTH_LONG).show();
            serverSocket.close();
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
}
