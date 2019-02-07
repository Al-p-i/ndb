package lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final RequestParser requestParser;

    public ConnectionHandler(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        requestParser = new RequestParser(database);
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                out.println(requestParser.parseLine(out, line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String parseLine(PrintWriter out, String line) {
        return requestParser.parseLine(out, line);
    }
}
