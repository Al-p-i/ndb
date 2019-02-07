package lab;

import lab.requests.PutRequest;
import lab.requests.DelRequest;
import lab.requests.GetRequest;

import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    public void create(PutRequest putRequest) {
        storage.put(putRequest.getKey(), putRequest.getValue());
    }

    public String read(GetRequest readTask) {
        return storage.get(readTask.getKey());
    }

    public String delete(DelRequest deleteTask) {
        return storage.remove(deleteTask.getKey());
    }
}
