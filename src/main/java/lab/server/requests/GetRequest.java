package lab.server.requests;

public class GetRequest implements Request {
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
