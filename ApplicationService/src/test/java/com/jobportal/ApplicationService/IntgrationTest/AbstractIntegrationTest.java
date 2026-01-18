package com.jobportal.ApplicationService.IntgrationTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("application_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    @ServiceConnection
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Map to the EXACT placeholders used in your ApplicationService application.yml
        registry.add("APPLICATION_DB_URL", mysql::getJdbcUrl);
        registry.add("DB_USERNAME", mysql::getUsername);
        registry.add("DB_PASSWORD", mysql::getPassword);

        registry.add("KAFKA_BOOTSTRAP_SERVERS", kafka::getBootstrapServers);
        registry.add("KAFKA_APPLICATION_SERVICE_GROUP_ID", () -> "test-group");
        registry.add("KAFKA_TOPIC_JOB_APPLICATION", () -> "job-application-events");

        // Disable external discovery services during tests
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("EUREKA_SERVER_URL", () -> "http://localhost:8761/eureka");

        // Disable Tracing to avoid connection errors with Zipkin
        registry.add("management.tracing.enabled", () -> "false");
    }
}
