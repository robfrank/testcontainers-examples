package io.github.robfrank.testcontainers;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaJdbcUrlWithScriptTest {


    @Test
    public void shouldSelectFromBar() throws SQLException {

        String jdbcUrl = "jdbc:tc:postgresql:9.6.8://hostname/databasename?&TC_INITSCRIPT=initdb.sql";

        Connection conn = DriverManager.getConnection(jdbcUrl);

        Statement stmt = conn.createStatement();
        stmt.execute("SELECT * FROM bar");

        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();

        assertThat(resultSet.getString("foo")).isEqualTo("hello world");

    }
}
