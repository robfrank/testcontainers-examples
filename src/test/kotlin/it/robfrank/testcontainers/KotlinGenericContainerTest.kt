package it.robfrank.testcontainers

import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper
import org.assertj.core.api.Assertions.assertThat
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
internal class KotlinGenericContainerTest {

    @Container
    val container: KGenericContainer = KGenericContainer("janusgraph/janusgraph:0.5.0")
            .apply {
                withExposedPorts(8182)
                waitingFor(Wait.defaultWaitStrategy())
            }

    lateinit var cluster: Cluster

    @BeforeEach
    internal fun setUp() {
        cluster = Cluster.build(container.containerIpAddress)
                .port(container.firstMappedPort)
                .serializer(GryoMessageSerializerV3d0(GryoMapper.build()
                        .addRegistry(JanusGraphIoRegistry.getInstance())))
                .credentials("", "")
                .maxWaitForConnection(20000)
                .create()


        var client = cluster.connect<Client>().init()

        client.submit("graph.io(graphml()).readGraph('data/grateful-dead.xml')")

        client.close()

    }

    @Test
    internal fun `should fetch only ten elements`() {

        val client = cluster.connect<Client>().init()

        val resultSet = client.submit("g.V().limit(10)").toList()

        assertThat(resultSet).hasSize(10)

    }
}

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

