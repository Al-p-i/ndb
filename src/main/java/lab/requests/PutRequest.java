package lab.requests;

public class PutRequest {
    private final String key;
    private final String value;

    public PutRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
