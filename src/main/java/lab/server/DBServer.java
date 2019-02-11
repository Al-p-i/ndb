package lab.server;

import lab.server.requests.PutRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class DBServer {
    private static final String VERSION = "0.1";
    private volatile boolean started = false;

    private final ForkJoinPool pool = new ForkJoinPool(4);

    private final Storage storage = new Storage();

    private volatile ServerSocket serverSocket;

    public void start(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        putSystemValues();
        started = true;
        System.out.println(DBServer.class.getName() + " started");
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            handle(clientSocket);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putSystemValues() {
        storage.put(new PutRequest("ndb_version", VERSION));
    }

    private void handle(Socket clientSocket) {
        pool.execute(new ConnectionHandler(clientSocket, storage));
    }

    public boolean isStarted() {
        return started;
    }

    public static void main(String[] args) {
        int port;
        if (args.length == 2) {
            port = Integer.valueOf(args[1]);
        } else {
            port = 4444;
        }
        System.out.println("=== ndb server ===");
        System.out.println("listening at " + port);
        DBServer server = new DBServer();
        server.start(port);
    }
}
