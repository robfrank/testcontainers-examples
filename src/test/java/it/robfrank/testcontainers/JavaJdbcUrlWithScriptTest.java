package it.robfrank.testcontainers;

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
        
        var jdbcUrl = "jdbc:tc:postgresql:11://hostname/databasename?&TC_INITSCRIPT=initdb.sql";
        
        var conn = DriverManager.getConnection(jdbcUrl);
        
        var stmt = conn.createStatement();
        stmt.execute("SELECT * FROM bar");
        
        var resultSet = stmt.getResultSet();
        resultSet.next();
        
        assertThat(resultSet.getString("foo")).isEqualTo("hello world");
        
    }
}
