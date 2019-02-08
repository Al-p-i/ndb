package lab.server;

import lab.requests.PutRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class DBServer {
    private static final String VERSION = "0.1";
    private volatile boolean started = false;

    private final ForkJoinPool pool = new ForkJoinPool(4);

    private final Database database = new Database();

    public void start(int portNumber) {
        ServerSocket serverSocket;
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

    private void putSystemValues() {
        database.put(new PutRequest("ndb_version", VERSION));
    }

    private void handle(Socket clientSocket) {
        pool.execute(new ConnectionHandler(clientSocket, database));
    }

    public boolean isStarted() {
        return started;
    }
}
