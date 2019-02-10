package lab.server.requests;

public class UnknownRequest implements Request {
    private final String command;

    public UnknownRequest(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
