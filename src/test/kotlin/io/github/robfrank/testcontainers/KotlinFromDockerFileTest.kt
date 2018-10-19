package io.github.robfrank.testcontainers

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinFromDockerFileTest {

    private var log = LoggerFactory.getLogger(KotlinFromDockerFileTest::class.java)

    private val container: GenericContainer<Nothing> = GenericContainer<Nothing>(
            ImageFromDockerfile("robfrank/ngnix")
                    .withFileFromPath("Dockerfile", Paths.get("./src/main/docker/nginx/Dockerfile"))
    ).apply {
        withExposedPorts(80)
        waitingFor(Wait.forListeningPort())
        start()
        followOutput(Slf4jLogConsumer(log))
    }


    @Test
    fun `should get 200 OK`() {
        Assertions.assertThat(container.isRunning).isTrue()

        val url = URL("http://${container.containerIpAddress}:${container.firstMappedPort}")

        val conn = url.openConnection() as HttpURLConnection

        Assertions.assertThat(conn.responseCode).isEqualTo(200)
    }

}