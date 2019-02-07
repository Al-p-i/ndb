package lab;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@Disabled
public class DBServerTest {
    @Test
    public void server() throws InterruptedException {
        DBServer server = new DBServer();
        server.start(4444);
        while (true) {
            Thread.sleep(Integer.MAX_VALUE);
        }
    }

    @RepeatedTest(1)
    public void client() {
        DBClient client = new DBClient();
        client.startConnection("127.0.0.1", 4444);
        System.out.println(client.sendMessage("put x 10"));
        System.out.println(client.sendMessage("get x"));
        System.out.println(client.sendMessage("del x"));
        System.out.println(client.sendMessage("get x"));
        client.stopConnection();
    }
}
