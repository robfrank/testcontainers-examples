package io.github.robfrank.testcontainers;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaCustomContainerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCustomContainerTest.class);

    public static GenericContainer container = new GenericContainer("robfrank/orientdb")
            .withExposedPorts(2424, 2480)
            .withEnv("ORIENTDB_ROOT_PASSWORD", "rootpassword")
            .waitingFor(Wait.forListeningPort());


    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    public void setUp(TestInfo info) throws Exception {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);
        container.followOutput(logConsumer);

        String serverUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424);

        final String testName = info.getTestMethod().get().getName();
        final String databaseUrl = createDatabase(serverUrl, testName);
        populateDatabase(databaseUrl);
    }

    private String createDatabase(String serverUrl, String dbname) throws IOException {

        OServerAdmin serverAdmin = new OServerAdmin(serverUrl);
        serverAdmin.connect("root", "rootpassword");
        serverAdmin.createDatabase(dbname, "graph", "plocal");
        serverAdmin.close();

        return serverUrl + "/" + dbname;


    }


    private void populateDatabase(String database) {

        ODatabaseDocumentTx db = new ODatabaseDocumentTx(database);

        db.open("admin", "admin");

        db.command(new OCommandSQL("create class Person extends V")).execute();
        db.command(new OCommandSQL("create property Person.name string")).execute();
        db.command(new OCommandSQL("create index Person.name on Person(name) unique")).execute();

        db.command(new OCommandSQL("create class FriendOf extends E")).execute();
        db.command(new OCommandSQL("create property FriendOf.kind string")).execute();

        db.command(new OCommandSQL("insert into Person set name='rob'")).execute();
        db.command(new OCommandSQL("insert into Person set name='frank'")).execute();
        db.command(new OCommandSQL("insert into Person set name='john'")).execute();
        db.command(new OCommandSQL("insert into Person set name='jane'")).execute();

        db.command(new OCommandSQL("CREATE EDGE FriendOf FROM (SELECT FROM Person WHERE name = 'rob') TO (SELECT FROM Person WHERE name = 'frank')set kind = 'fraternal' ")).execute();
        db.command(new OCommandSQL("CREATE EDGE FriendOf FROM (SELECT FROM Person WHERE name = 'john') TO        (SELECT FROM Person WHERE name = 'jane')set kind = 'fraternal' ")).execute();


        db.close();

    }

    @Test
    public void shouldQuery(TestInfo info) {
        final String testName = info.getTestMethod().get().getName();

        System.out.println("info = " + testName);
        String dbUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424) + "/" + testName;
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbUrl);

        db.open("admin", "admin");

        final int person = db.query(new OSQLSynchQuery<>("SELECT FROM Person")).size();

        assertThat(person).isEqualTo(4);
    }
}
