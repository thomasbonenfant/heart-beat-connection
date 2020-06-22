package it.btom.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler implements Runnable{
    private final Socket socket;
    private ScheduledExecutorService scheduler;
    private BufferedWriter out;
    private BufferedReader in;

    //constants
    private final int pingDelay = 2;
    private final int timeout = 5 * 1000;


    public ConnectionHandler(Socket socket) {
        this.socket = socket;

    }



    @Override
    public void run() {

        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        Runnable pingSender = () -> {
            try {

                out.write("ping");
                out.newLine();
                out.flush();
                System.out.println("ping");

            } catch (IOException ignored) {

            }
        };

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(pingSender, pingDelay, TimeUnit.SECONDS);
        String message = null;

        while(true) {
            try {
                if ((message = in.readLine()) == null) {
                    System.out.println("message = null");
                    break;
                };
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(message != null) {
                if ("pong".equals(message)) {
                    System.out.println("pong");
                    scheduler.schedule(pingSender, pingDelay, TimeUnit.SECONDS);
                } else {
                    System.out.println(message);
                }
            }

        }

        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
