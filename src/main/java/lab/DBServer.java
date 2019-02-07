package lab;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class DBServer {
    private volatile boolean started = false;

    private ForkJoinPool pool = new ForkJoinPool(4);

    private final Database database = new Database();

    public void start(int portNumber) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
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

    private void handle(Socket clientSocket) {
        pool.execute(new ConnectionHandler(clientSocket, database));
    }

    public boolean isStarted() {
        return started;
    }
}
