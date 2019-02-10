package lab.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBClient implements AutoCloseable {
    private static Pattern EXIT_PATTERN = Pattern.compile("^\\s*(exit)\\s*$");

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = null;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public static void repl(String ip, int port) {
        DBClient dbClient = new DBClient();
        dbClient.startConnection(ip, port);
        Scanner stdin = new Scanner(System.in);
        requestVersion(dbClient);
        System.out.print("> ");
        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            if (isExit(line)) {
                return;
            }
            System.out.println(dbClient.sendMessage(line));
            System.out.print("> ");
        }
    }

    private static boolean isExit(String line) {
        Matcher matcher = EXIT_PATTERN.matcher(line);
        return matcher.find();
    }

    @Override
    public void close() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void requestVersion(DBClient dbClient) {
        String ndbVersionRequest = "get ndb_version";
        System.out.println(ndbVersionRequest);
        System.out.println(dbClient.sendMessage(ndbVersionRequest));
    }

    public static void main(String[] args) {
        String ip;
        int port;
        if (args.length == 2) {
            ip = args[0];
            port = Integer.valueOf(args[1]);
        } else {
            ip = "127.0.0.1";
            port = 4444;
        }
        System.out.println("=== ndb client ===");
        System.out.println("Connecting to " + ip + ":" + port);
        repl(ip, port);
    }
}