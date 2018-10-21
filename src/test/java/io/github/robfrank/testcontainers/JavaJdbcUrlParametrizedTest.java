package io.github.robfrank.testcontainers;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaJdbcUrlParametrizedTest {

    public static void sampleInitFunction(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE TABLE bar (\n" +
                "  foo VARCHAR(255)\n" +
                ");");
        connection.createStatement().execute("INSERT INTO bar (foo) VALUES ('hello world');");
        connection.createStatement().execute("CREATE TABLE my_counter (\n" +
                "  n INT\n" +
                ");");
    }

    @ParameterizedTest(name = "PGSQL version:: {arguments}")
    @ValueSource(strings = {"9.6.6", "9.6.7", "9.6.8"})
    public void shouldSelectFromBar(String version) throws SQLException {
        String jdbcUrl = "jdbc:tc:postgresql:" + version + "://hostname/databasename?&TC_INITFUNCTION=io.github.robfrank.testcontainers.JavaJdbcUrlTest::sampleInitFunction";

        Connection conn = DriverManager.getConnection(jdbcUrl);

        Statement stmt = conn.createStatement();
        stmt.execute("SELECT * FROM bar");

        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();

        assertThat(resultSet.getString("foo")).isEqualTo("hello world");

    }
}
