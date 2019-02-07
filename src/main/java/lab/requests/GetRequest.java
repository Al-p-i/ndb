package lab.requests;

public class GetRequest {
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
