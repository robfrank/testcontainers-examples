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
import org.testcontainers.containers.OrientDBContainer;
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
    public static OrientDBContainer container = new OrientDBContainer("robfrank/orientdb");
    
    @BeforeEach
    public void setUp(TestInfo info) throws Exception {
        var logConsumer = new Slf4jLogConsumer(LOGGER);
        container.followOutput(logConsumer);
        
        var serverUrl = container.getServerUrl();
        
        //one db per test!
        var testName = info.getTestMethod().get().getName();
        
        var databaseUrl = createDatabase(serverUrl, testName);
        createSchemaAndSampleData(databaseUrl);
    }
    
    private ODatabaseSession createDatabase(String serverUrl, String dbname) throws IOException {
        
        var orientDB = new OrientDB(serverUrl, "root", "root", OrientDBConfig.defaultConfig());
        
        if (orientDB.exists(dbname)) {
            orientDB.drop(dbname);
        }
        
        orientDB.create(dbname, ODatabaseType.PLOCAL);
        
        return orientDB.open(dbname, "admin", "admin");
    }
    
    private void createSchemaAndSampleData(ODatabaseSession db) {
        
        db.execute("sql", """
                create class Person extends V;
                create property Person.name string;
                create index Person.name on Person(name) unique;
                
                create class FriendOf extends E;
                create property FriendOf.kind string;
                """);
        
        db.execute("sql", """
                insert into Person set name='rob';
                insert into Person set name='frank';
                insert into Person set name='john';
                insert into Person set name='jane';
                
                CREATE EDGE FriendOf \
                FROM (SELECT FROM Person WHERE name = 'rob') \
                TO (SELECT FROM Person WHERE name = 'frank') \
                set kind = 'fraternal';
                
                CREATE EDGE FriendOf \
                FROM (SELECT FROM Person WHERE name = 'john') \
                TO (SELECT FROM Person  WHERE name = 'jane') \
                set kind = 'fraternal';
                """);
        
        db.close();
    }
    
    @Test
    @Order(1)
    public void shouldCountPersons(TestInfo info) {
        var testName = info.getTestMethod().get().getName();
        
        var dbUrl = container.getServerUrl();
        
        var orientDB = new OrientDB(dbUrl, OrientDBConfig.defaultConfig());
        
        var db = orientDB.open(testName, "admin", "admin");
        
        var persons = db.query("SELECT FROM Person").stream().count();
        
        assertThat(persons).isEqualTo(4);
    }
    
    @Test
    @Order(2)
    public void shouldCountPersonsOnAnotherdb(TestInfo info) {
        var testName = info.getTestMethod().get().getName();
        
        var dbUrl = container.getServerUrl();
        
        var orientDB = new OrientDB(dbUrl, OrientDBConfig.defaultConfig());
        
        var db = orientDB.open(testName, "admin", "admin");
        
        var persons = db.query("SELECT FROM Person").stream().count();
        
        assertThat(persons).isEqualTo(4);
    }
    
    @Test
    @Order(3)
    void shouldHaveThreeTestDatabases() {
        var dbUrl = container.getServerUrl();
        
        var orientDB = new OrientDB(dbUrl, "root", "root", OrientDBConfig.defaultConfig());
        
        assertThat(orientDB.list()).hasSize(4);
    }
}
