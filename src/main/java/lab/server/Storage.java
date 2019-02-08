package lab.server;

import lab.server.requests.PutRequest;
import lab.server.requests.DelRequest;
import lab.server.requests.GetRequest;

import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    public void put(PutRequest putRequest) {
        storage.put(putRequest.getKey(), putRequest.getValue());
    }

    public String get(GetRequest readTask) {
        return storage.get(readTask.getKey());
    }

    public String del(DelRequest deleteTask) {
        return storage.remove(deleteTask.getKey());
    }
}
