package io.github.robfrank.testcontainers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager

class KotlinSimpleQuerySpec {

    private val container: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>()
            .apply {
                start()
            }

    @Test
    fun `should perform simple query`() {

        val conn = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
        val stmt = conn.createStatement()
        stmt.execute("SELECT 1")

        val resultSet = stmt.resultSet
        resultSet.next()

        assertThat(resultSet.getInt(1)).isEqualTo(1)
    }


}