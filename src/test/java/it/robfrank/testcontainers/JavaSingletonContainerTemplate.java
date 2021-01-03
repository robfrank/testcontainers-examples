package it.robfrank.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class JavaSingletonContainerTemplate {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(DockerImageName.parse("postgres:11").asCompatibleSubstituteFor("postgres"));
        POSTGRE_SQL_CONTAINER.start();
    }
}
