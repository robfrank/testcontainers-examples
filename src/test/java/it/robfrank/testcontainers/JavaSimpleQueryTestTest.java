package it.robfrank.testcontainers;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class JavaSimpleQueryTestTest {

    @Container
    public static PostgreSQLContainer container =
            new PostgreSQLContainer("postgres:11");


    @Test
    public void shouldTestSimpleQuery() throws SQLException {

        Connection conn = DriverManager.getConnection(container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword());

        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");

        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();

        assertThat(resultSet.getInt(1)).isEqualTo(1);
    }
}
