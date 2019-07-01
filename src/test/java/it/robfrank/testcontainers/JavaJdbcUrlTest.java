package it.robfrank.testcontainers;


import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaJdbcUrlTest {

    public static void sampleInitFunction(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE bar (\n" +
                "  foo VARCHAR(255)\n" +
                ");");
        connection.createStatement().execute("INSERT INTO bar (foo) VALUES ('hello world');");
        connection.createStatement().execute("CREATE TABLE my_counter (\n" +
                "  n INT\n" +
                ");");
    }

    @Test
    public void shouldSelectFromBar() throws SQLException {
        String jdbcUrl = "jdbc:tc:postgresql:11://hostname/databasename?&TC_INITFUNCTION=it.robfrank.testcontainers.JavaJdbcUrlTest::sampleInitFunction";

        Connection conn = DriverManager.getConnection(jdbcUrl);

        Statement stmt = conn.createStatement();
        stmt.execute("SELECT * FROM bar");

        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();

        assertThat(resultSet.getString("foo")).isEqualTo("hello world");

    }
}
