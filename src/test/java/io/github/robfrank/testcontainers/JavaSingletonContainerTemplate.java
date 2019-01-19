package io.github.robfrank.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class JavaSingletonContainerTemplate {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();
        POSTGRE_SQL_CONTAINER.start();
    }
}
