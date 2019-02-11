package lab;

import lab.client.DBClient;
import lab.server.DBServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

@Disabled
class DBServerTest {
    private DBServer startServer() {
        DBServer server = new DBServer();
        Executors.newSingleThreadExecutor().execute(() -> server.start(4444));
        while (!server.isStarted()) {
        }
        return server;
    }

    @Test
    void crud() {
        DBServer dbServer = startServer();
        try (DBClient client = new DBClient()) {
            client.startConnection("127.0.0.1", 4444);
            Assertions.assertEquals("null", client.sendMessage("get x"));
            Assertions.assertEquals("x <= 10", client.sendMessage("put x 10"));
            Assertions.assertEquals("10", client.sendMessage("get x"));
            Assertions.assertEquals("x <= 20", client.sendMessage("put x 20"));
            Assertions.assertEquals("20", client.sendMessage("get x"));
            Assertions.assertEquals("deleted x", client.sendMessage("del x"));
            Assertions.assertEquals("null", client.sendMessage("get x"));
        } finally {
            dbServer.stop();
        }
    }

    @Test
    void doubleBegin() {
        DBServer dbServer = startServer();
        try (DBClient client1 = new DBClient()) {
            client1.startConnection("127.0.0.1", 4444);

            Assertions.assertEquals("transaction began", client1.sendMessage("begin"));
            Assertions.assertEquals("transaction already began - abort transaction", client1.sendMessage("begin"));
        } finally {
            dbServer.stop();
        }
    }

    @Test
    void commitTransactionThatDidNotBegin() {
        DBServer dbServer = startServer();
        try (DBClient client1 = new DBClient()) {
            client1.startConnection("127.0.0.1", 4444);
            Assertions.assertEquals("no transaction found", client1.sendMessage("commit"));
        } finally {
            dbServer.stop();
        }
    }

    @Test
    void syntaxErrorDuringTransaction() {
        DBServer dbServer = startServer();
        try (DBClient client1 = new DBClient()) {
            client1.startConnection("127.0.0.1", 4444);

            Assertions.assertEquals("transaction began", client1.sendMessage("begin"));
            Assertions.assertEquals("syntactic error - abort transaction", client1.sendMessage("no such command"));
        } finally {
            dbServer.stop();
        }
    }

    @Test
    void transactionIsolationAndAtomicity() {
        DBServer dbServer = startServer();
        try (DBClient client1 = new DBClient(); DBClient client2 = new DBClient()) {
            client1.startConnection("127.0.0.1", 4444);
            client2.startConnection("127.0.0.1", 4444);

            Assertions.assertEquals("transaction began", client1.sendMessage("begin"));
            Assertions.assertEquals("t", client1.sendMessage("put x 1"));

            //transaction2 begin
            Assertions.assertEquals("null", client2.sendMessage("get x"));
            Assertions.assertEquals("transaction began", client2.sendMessage("begin"));
            Assertions.assertEquals("t", client2.sendMessage("get x"));
            Assertions.assertEquals("t", client2.sendMessage("put x 10"));
            Assertions.assertEquals("t", client2.sendMessage("put x 20"));
            Assertions.assertEquals("t", client2.sendMessage("get x"));
            Assertions.assertEquals("[x=null; x <= 10; x <= 20; x=20; ]", client2.sendMessage("commit"));
            //transaction2 commit


            Assertions.assertEquals("t", client1.sendMessage("put x 2"));
            Assertions.assertEquals("t", client1.sendMessage("get x"));
            Assertions.assertEquals("[x <= 1; x <= 2; x=2; ]", client1.sendMessage("commit"));
        } finally {
            dbServer.stop();
        }
    }
}
