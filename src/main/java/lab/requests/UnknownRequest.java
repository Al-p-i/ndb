package lab.requests;

public class UnknownRequest {
    private final String command;

    public UnknownRequest(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
