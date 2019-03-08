package it.robfrank.testcontainers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.DriverManager


@Testcontainers
internal class KotlinPostgresDvdRentalTest {


    @Container
    val container = PostgreSQLContainer<Nothing>("robfrank/postgres-dvdrental")
            .apply {
                withUsername("postgres")
                withPassword("postgres")
            }

    @Test
    internal fun `should query movies`() {
        container.withDatabaseName("dvdrental")

        val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

        val statement = connection.createStatement()

        val resultSet = statement.executeQuery("SELECT count(*) AS movies from Film")

        resultSet.next()

        Assertions.assertThat(resultSet.getInt("movies")).isEqualTo(1000)

    }
}