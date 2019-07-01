package it.robfrank.testcontainers

import com.orientechnologies.orient.core.db.ODatabaseSession
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import com.orientechnologies.orient.core.record.ODirection
import com.orientechnologies.orient.core.record.OVertex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KotlinOrientContainerTest {

    @Container
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
                .open("openbeer", "admin", "admin")

    }

    @AfterEach
    internal fun tearDown() {
        db.close()
    }


    @Test
    internal fun `should select beers vertexes`() {

        db.query("select from Beer limit 10")
                .use { set ->
                    set.asSequence()
                            .toList()
                            .apply {
                                assertThat(this).hasSize(10)
                            }.map { result ->
                                assertThat(result.isVertex).isTrue()
                                assertThat(result.hasProperty("name")).isTrue()
                                assertThat(result.hasProperty("descript")).isTrue()
                                //map
                                result.vertex.get()
                            }.forEach { vertex: OVertex ->
                                assertThat(vertex.getEdges(ODirection.OUT)).isNotEmpty
                            }


                }
    }

    @Test
    internal fun `should select breweries`() {

        db.query("select from Brewery limit 10")
                .use { set ->
                    set.asSequence()
                            .toList()
                            .apply {
                                assertThat(this).hasSize(10)
                            }.map { result ->
                                assertThat(result.isVertex).isTrue()
                                assertThat(result.hasProperty("name")).isTrue()
                                assertThat(result.hasProperty("descript")).isTrue()
                                //map
                                result.vertex.get()
                            }

                }
    }
}