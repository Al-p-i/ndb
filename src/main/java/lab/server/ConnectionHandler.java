package lab.server;

import lab.server.requests.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final Storage storage;
    private final RequestParser requestParser;
    private ConcurrentLinkedQueue<Request> currentTransaction;

    public ConnectionHandler(Socket clientSocket, Storage storage) {
        this.clientSocket = clientSocket;
        this.storage = storage;
        requestParser = new RequestParser();
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                Request request = requestParser.parseLine(line);
                handleRequest(request, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                currentTransaction = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(Request request, PrintWriter out) {
        if (request == null) {

            if (currentTransaction != null) {
                this.currentTransaction = null;
                out.println("syntactic error - abort transaction");
                return;
            }
            out.println("syntactic error");
        } else if (request instanceof UnknownRequest) {
            out.println("wrong command " + ((UnknownRequest) request).getCommand());
            if (currentTransaction != null) {
                this.currentTransaction = null;
                out.println("abort transaction");
                return;
            }
        } else if (this.currentTransaction != null) {
            if (request instanceof BeginRequest) {
                currentTransaction = null;
                out.println("transaction already began - abort transaction");
            } else if (request instanceof CommitRequest) {
                currentTransaction.add(request);
                if (!commitTransaction(out)) {
                    rollbackTransaction();
                }
                currentTransaction = null;
            } else {
                this.currentTransaction.add(request);
                out.println("t");
            }
        } else {
            if (request instanceof GetRequest) {
                out.println(storage.get((GetRequest) request));
            }
            if (request instanceof PutRequest) {
                PutRequest putRequest = (PutRequest) request;
                storage.put(putRequest);
                out.println(putRequest.getKey() + " <= " + putRequest.getValue());
            }
            if (request instanceof DelRequest) {
                DelRequest delRequest = (DelRequest) request;
                String deleted = storage.del(delRequest);
                if (deleted == null) {
                    out.println("no such key " + delRequest.getKey());
                } else {
                    out.println("deleted " + delRequest.getKey());
                }
            }
            if (request instanceof BeginRequest) {
                currentTransaction = new ConcurrentLinkedQueue<>();
                currentTransaction.add(request);
                out.println("transaction began");
            }
            if (request instanceof CommitRequest) {
                out.println("no transaction found");
            }
        }
    }

    private void rollbackTransaction() {

    }

    private boolean commitTransaction(PrintWriter out) {
        for (Request request : currentTransaction) {
            if (request instanceof BeginRequest) {
                storage.begin();
                out.print("[");
            }
            if (request instanceof GetRequest) {
                GetRequest getRequest = (GetRequest) request;
                out.print(getRequest.getKey() + "=" + storage.get(getRequest) + "; ");
            }
            if (request instanceof PutRequest) {
                PutRequest putRequest = (PutRequest) request;
                storage.put(putRequest);
                out.print(putRequest.getKey() + " <= " + putRequest.getValue() + "; ");
            }
            if (request instanceof DelRequest) {
                DelRequest delRequest = (DelRequest) request;
                String deleted = storage.del(delRequest);
                if (deleted == null) {
                    out.print("no such key " + delRequest.getKey() + "; ");
                } else {
                    out.print("deleted " + delRequest.getKey() + "; ");
                }
            }
            if (request instanceof CommitRequest) {
                storage.commit();
                out.println("]");
            }
        }
        return true;
    }
}
