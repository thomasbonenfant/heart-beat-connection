package it.btom.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private String ip;
    private int port;
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private final int timeout = 5 * 1000;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(ip, port), timeout);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String message = null;
        while(true) {
            try {
                if ((message = in.readLine()) == null) {
                    System.out.println("message = null");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if(message.equals("ping")) {
                try {
                    out.write("pong");
                    out.newLine();
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
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

    public static void main(String[] args) {
        Client client = new Client("localhost", 2034);
        client.start();
    }

}
