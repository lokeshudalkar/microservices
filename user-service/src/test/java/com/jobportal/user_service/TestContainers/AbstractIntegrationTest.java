package com.jobportal.user_service.TestContainers;


import org.springframework.test.context.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql =new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("user_db")
            .withUsername("root")
            .withPassword("root");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Start fresh schema
        registry.add("app.jwt-secret" , () -> "TaK+HaV^uvCHEFsEVfypW7g9^k*Z8$V12ABCDEFGHadditionalchars1234567890");
    }

}
