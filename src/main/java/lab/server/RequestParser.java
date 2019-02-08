package lab.server;

import lab.requests.DelRequest;
import lab.requests.GetRequest;
import lab.requests.PutRequest;
import lab.requests.UnknownRequest;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {
    private static Pattern READ_PATTERN = Pattern.compile("^\\s*get\\s(\\w+)\\s*$");
    private static Pattern CREATE_PATTERN = Pattern.compile("^\\s*put\\s+(\\w+)\\s+(\\w+)\\s*$");
    private static Pattern DELETE_PATTERN = Pattern.compile("^\\s*del\\s+(\\w+)\\s*$");
    private static Pattern UNKNOWN_COMMAND_PATTERN = Pattern.compile("^\\s*(\\w+)\\s.*$");
    private final Database database;

    public RequestParser(Database database) {
        this.database = database;
    }

    String parseLine(PrintWriter out, String line) {
        GetRequest getRequest = parseRead(line);
        if (getRequest != null) {
            return database.get(getRequest);
        }
        PutRequest putRequest = parseCreate(line);
        if (putRequest != null) {
            database.put(putRequest);
            return putRequest.getKey() + " <= " + putRequest.getValue();
        }
        DelRequest delRequest = parseDelete(line);
        if (delRequest != null) {
            String deleted = database.del(delRequest);
            if (deleted == null) {
                out.println("no such key " + delRequest.getKey());
            }
            return "deleted " + delRequest.getKey();
        }
        UnknownRequest unknownRequest = parseUnknown(line);
        if (unknownRequest != null) {
            return "wrong command " + unknownRequest.getCommand();
        }
        return "syntactic error";
    }

    private DelRequest parseDelete(String request) {
        Matcher matcher = DELETE_PATTERN.matcher(request);
        if (matcher.find()) {
            return new DelRequest(matcher.group(1));
        }
        return null;
    }

    private GetRequest parseRead(String request) {
        Matcher matcher = READ_PATTERN.matcher(request);
        if (matcher.find()) {
            return new GetRequest(matcher.group(1));
        }
        return null;
    }

    private PutRequest parseCreate(String request) {
        Matcher matcher = CREATE_PATTERN.matcher(request);
        if (matcher.find()) {
            return new PutRequest(matcher.group(1), matcher.group(2));
        }
        return null;
    }

    private UnknownRequest parseUnknown(String request) {
        Matcher matcher = UNKNOWN_COMMAND_PATTERN.matcher(request);
        if (matcher.find()) {
            return new UnknownRequest(matcher.group(1));
        }
        return null;
    }
}