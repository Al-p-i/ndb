package lab.server;

import lab.server.requests.PutRequest;
import lab.server.requests.DelRequest;
import lab.server.requests.GetRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Storage {
    private final Lock lock = new ReentrantLock();
    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

    public void begin() {
        lock.lock();
    }

    public void commit() {
        lock.unlock();
    }

    public void put(PutRequest putRequest) {
        try {
            lock.lock();
            storage.put(putRequest.getKey(), putRequest.getValue());
        } finally {
            lock.unlock();
        }
    }

    public String get(GetRequest readTask) {
        try {
            lock.lock();
            return storage.get(readTask.getKey());
        } finally {
            lock.unlock();
        }
    }

    public String del(DelRequest deleteTask) {
        try {
            lock.lock();
            return storage.remove(deleteTask.getKey());
        } finally {
            lock.unlock();
        }
    }
}
