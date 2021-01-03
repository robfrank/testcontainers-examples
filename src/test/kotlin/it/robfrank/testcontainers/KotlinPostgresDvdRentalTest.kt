package it.robfrank.testcontainers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager


/**
 * Test that show hoe to use a custom container with preloaded database
 *
 * Script to create image is in src/main/docker/postgres-dvdrental
 *
 */
@Testcontainers
internal class KotlinPostgresDvdRentalTest {


    @Container
    val container = PostgreSQLContainer<Nothing>(DockerImageName.parse("robfrank/postgres-dvdrental").asCompatibleSubstituteFor("postgres"))
        .apply {
            withUsername("postgres")
            withPassword("postgres")
        }

    @Test
    internal fun `should query movies`() {
        container.withDatabaseName("dvdrental")

        DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
            .use { conn ->

                conn.createStatement().use { stmt ->

                    stmt.executeQuery("SELECT count(*) AS movies from Film").use { resultSet ->

                        resultSet.next()

                        Assertions.assertThat(resultSet.getInt("movies")).isEqualTo(1000)

                    }

                }

            }

    }
}
