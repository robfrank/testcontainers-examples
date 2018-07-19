package io.github.robfrank.testcontainers

import org.assertj.core.api.Assertions
import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.sql.DriverManager

class KotlinSimpleQueryTest {

    val container: PostgreSQLContainer<Nothing>

    init {
        container = PostgreSQLContainer<Nothing>().apply {
            start()
        }
    }

    @Test
    fun `should perform simple query`() {

        val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
        val stmt = conn.createStatement()
        stmt.execute("SELECT 1")

        val resultSet = stmt.resultSet
        resultSet.next()

        Assertions.assertThat(resultSet.getInt(1)).isEqualTo(1)
    }


}