package it.btom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final int maxNumberOfThreads = 5;

    public Server(int port) {
        this.port = port;

    }

    public void startServer() {
        ExecutorService executor = Executors.newFixedThreadPool(maxNumberOfThreads);
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);


            System.out.println("Server ready");
            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("Connected to " + socket.getRemoteSocketAddress());

                executor.execute(new ConnectionHandler(socket));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(2034);
        server.startServer();
    }
}
