package io.github.robfrank.testcontainers

import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait


internal class KotlinOrientContainerTest {

    private val container: GenericContainer<Nothing> = GenericContainer<Nothing>("robfrank/orientdb")
            .apply {
                withExposedPorts(2424, 2480)
                withEnv("ORIENTDB_ROOT_PASSWORD", "rootpassword")
                waitingFor(Wait.forListeningPort())
                start()
            }

    private lateinit var db: ODatabaseSession

    @BeforeEach
    internal fun setUp() {
        db = OrientDB("remote:${container.containerIpAddress}:${container.firstMappedPort}", OrientDBConfig.defaultConfig())
                .open("OpenBeer", "admin", "admin")

    }


    @Test
    internal fun `should be consistent`() {

        db.query("select expand(classes) from metadata:schema where name = 'Beer'").asSequence().forEach { println("it = ${it}") }
    }

    @Test
    internal fun `should select beers`() {

        db.query("select from beer limit 10").asSequence()
                .toList()
                .apply {

                    assertThat(this).hasSize(10)

                }


    }
}