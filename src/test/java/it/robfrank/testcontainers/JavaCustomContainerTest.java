package it.robfrank.testcontainers;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JavaCustomContainerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCustomContainerTest.class);

    @Container
    public static GenericContainer container = new GenericContainer("robfrank/orientdb")
            .withExposedPorts(2424, 2480)
            .withEnv("ORIENTDB_ROOT_PASSWORD", "rootpassword")
            .waitingFor(Wait.forListeningPort());


    @BeforeEach
    public void setUp(TestInfo info) throws Exception {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);
        container.followOutput(logConsumer);

        String serverUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424);

        //one db per test!
        final String testName = info.getTestMethod().get().getName();

        final ODatabaseSession databaseUrl = createDatabase(serverUrl, testName);
        populateDatabase(databaseUrl);
    }

    private ODatabaseSession createDatabase(String serverUrl, String dbname) throws IOException {


        OrientDB orientDB = new OrientDB(serverUrl, "root", "rootpassword", OrientDBConfig.defaultConfig());

        if (orientDB.exists(dbname))
            orientDB.drop(dbname);

        orientDB.create(dbname, ODatabaseType.PLOCAL);

        return orientDB.open(dbname, "admin", "admin");


    }


    private void populateDatabase(ODatabaseSession db) {


        db.command("create class Person extends V");
        db.command("create property Person.name string");
        db.command("create index Person.name on Person(name) unique");

        db.command("create class FriendOf extends E");
        db.command("create property FriendOf.kind string");

        db.command("insert into Person set name='rob'");
        db.command("insert into Person set name='frank'");
        db.command("insert into Person set name='john'");
        db.command("insert into Person set name='jane'");

        db.command("CREATE EDGE FriendOf FROM (SELECT FROM Person WHERE name = 'rob') TO (SELECT FROM Person WHERE name = 'frank')set kind = 'fraternal' ");
        db.command("CREATE EDGE FriendOf FROM (SELECT FROM Person WHERE name = 'john') TO (SELECT FROM Person WHERE name = 'jane')set kind = 'fraternal' ");


        db.close();

    }

    @Test
    @Order(1)
    public void shouldCountPersons(TestInfo info) {
        final String testName = info.getTestMethod().get().getName();

        String dbUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424);

        OrientDB orientDB = new OrientDB(dbUrl, OrientDBConfig.defaultConfig());

        final ODatabaseSession db = orientDB.open(testName, "admin", "admin");

        final long persons = db.query("SELECT FROM Person").stream().count();

        assertThat(persons).isEqualTo(4);
    }

    @Test
    @Order(2)
    public void shouldCountPersonsOnAnotherdb(TestInfo info) {
        final String testName = info.getTestMethod().get().getName();

        String dbUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424);

        OrientDB orientDB = new OrientDB(dbUrl, OrientDBConfig.defaultConfig());

        final ODatabaseSession db = orientDB.open(testName, "admin", "admin");

        final long persons = db.query("SELECT FROM Person").stream().count();

        assertThat(persons).isEqualTo(4);
    }


    @Test
    @Order(3)
    void shouldHaveThreeTestDatabases() {
        String dbUrl = "remote:" + container.getContainerIpAddress() + ":" + container.getMappedPort(2424);

        OrientDB orientDB = new OrientDB(dbUrl, "root", "rootpassword", OrientDBConfig.defaultConfig());

        assertThat(orientDB.list()).hasSize(4);

    }
}
