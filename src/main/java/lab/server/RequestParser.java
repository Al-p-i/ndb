package lab.server;

import lab.server.requests.*;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {
    private static Pattern READ_PATTERN = Pattern.compile("^\\s*get\\s(\\w+)\\s*$");
    private static Pattern CREATE_PATTERN = Pattern.compile("^\\s*put\\s+(\\w+)\\s+(\\w+)\\s*$");
    private static Pattern DELETE_PATTERN = Pattern.compile("^\\s*del\\s+(\\w+)\\s*$");
    private static Pattern UNKNOWN_COMMAND_PATTERN = Pattern.compile("^\\s*(\\w+)\\s*$");
    private static Pattern BEGIN_PATTERN = Pattern.compile("^\\s*begin\\s*$");
    private static Pattern COMMIT_PATTERN = Pattern.compile("^\\s*commit\\s*$");


    Request parseLine(String line) {
        GetRequest getRequest = parseRead(line);
        if (getRequest != null) {
            return getRequest;
        }
        PutRequest putRequest = parseCreate(line);
        if (putRequest != null) {
            return putRequest;
        }
        DelRequest delRequest = parseDelete(line);
        if (delRequest != null) {
            return delRequest;
        }
        BeginRequest beginRequest = parseBegin(line);
        if (beginRequest != null) {
            return beginRequest;
        }
        CommitRequest commitRequest = parseCommit(line);
        if (commitRequest != null) {
            return commitRequest;
        }
        UnknownRequest unknownRequest = parseUnknown(line);
        if (unknownRequest != null) {
            return unknownRequest;
        }
        return null;
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

    private BeginRequest parseBegin(String request) {
        Matcher matcher = BEGIN_PATTERN.matcher(request);
        if (matcher.find()) {
            return new BeginRequest();
        }
        return null;
    }

    private CommitRequest parseCommit(String request) {
        Matcher matcher = COMMIT_PATTERN.matcher(request);
        if (matcher.find()) {
            return new CommitRequest();
        }
        return null;
    }

}