package it.robfrank.testcontainers;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaJdbcUrlParametrizedTest {
    
    
    @ParameterizedTest(name = "PGSQL version:: {arguments}")
    @ValueSource(strings = {"9", "10", "11"})
    public void shouldSelectFromBar(String version) throws SQLException {
        String jdbcUrl = "jdbc:tc:postgresql:"
                + version
                + "://hostname/databasename?&TC_INITFUNCTION=it.robfrank.testcontainers.JavaJdbcUrlTest::sampleInitFunction";
        
        var conn = DriverManager.getConnection(jdbcUrl);
        
        var stmt = conn.createStatement();
        stmt.execute("SELECT * FROM bar");
        
        var resultSet = stmt.getResultSet();
        resultSet.next();
        
        assertThat(resultSet.getString("foo")).isEqualTo("hello world");
        
    }
}
