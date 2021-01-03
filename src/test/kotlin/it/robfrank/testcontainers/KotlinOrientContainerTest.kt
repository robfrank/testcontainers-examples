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
import org.testcontainers.containers.OrientDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


/**
 * Test showing the use of a single instance for class of a container
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KotlinOrientContainerTest {

    private val container = OrientDBContainer( DockerImageName.parse("robfrank/orientdb").asCompatibleSubstituteFor("orientdb"))
        .apply {
            withDatabaseName("openbeer")
            start()
        }

    private lateinit var db: ODatabaseSession

    @BeforeEach
    internal fun setUp() {
        db = container.session
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
