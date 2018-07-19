package io.github.robfrank.testcontainers;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaSimpleQueryTestTest {

    @ClassRule
    public static PostgreSQLContainer container =
            new PostgreSQLContainer("postgres:9.6.9");

    @Test
    public void shouldTestSimpleQuery() throws SQLException {

        System.out.println("container.getJdbcUrl() = " + container.getJdbcUrl());
        Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");

        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();

        assertThat(resultSet.getInt(1)).isEqualTo(1);
    }
}
