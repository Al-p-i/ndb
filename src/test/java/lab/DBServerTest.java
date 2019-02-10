package lab;

import lab.client.DBClient;
import lab.server.DBServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class DBServerTest {
    @Test
    void server() throws InterruptedException {
        DBServer server = new DBServer();
        server.start(4444);
        while (true) {
            Thread.sleep(Integer.MAX_VALUE);
        }
    }

    @Test
    void crud() {
        try (DBClient client = new DBClient()) {
            client.startConnection("127.0.0.1", 4444);
            Assertions.assertEquals("x <= 10", client.sendMessage("put x 10"));
            Assertions.assertEquals("10", client.sendMessage("get x"));
            Assertions.assertEquals("x <= 20", client.sendMessage("put x 20"));
            Assertions.assertEquals("20", client.sendMessage("get x"));
            Assertions.assertEquals("deleted x", client.sendMessage("del x"));
            Assertions.assertEquals("null", client.sendMessage("get x"));
        }
    }
}
