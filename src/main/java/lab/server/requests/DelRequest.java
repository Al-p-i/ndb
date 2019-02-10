package lab.server.requests;

public class DelRequest implements Request {
    private final String key;

    public DelRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
