package com.jobportal.jobservice.IntegrationTests;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
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
            .withDatabaseName("job_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    @ServiceConnection
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:7.0-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("JOB_DB_URL", mysql::getJdbcUrl);
        registry.add("DB_USERNAME", mysql::getUsername);
        registry.add("DB_PASSWORD", mysql::getPassword);

        // Kafka Handled by @ServiceConnection
        registry.add("KAFKA_BOOTSTRAP_SERVERS", kafka::getBootstrapServers);
        // Missing Kafka Properties from your application.yml
        registry.add("KAFKA_JOB_SERVICE_GROUP_ID", () -> "test-group");
        registry.add("KAFKA_TOPIC_JOB_APPLICATION", () -> "test-topic");

        // Mock Redis
        registry.add("REDIS_HOST", redis::getHost);
        registry.add("REDIS_PORT", () -> redis.getMappedPort(6379).toString());
        registry.add("REDIS_USERNAME", () -> "");
        registry.add("REDIS_PASSWORD", () -> "");

        // Disable services that interfere with tests
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
        registry.add("EUREKA_SERVER_URL", () -> "http://localhost:8761/eureka");

        // Disable Tracing/Zipkin to avoid connection timeouts during tests
        registry.add("management.tracing.enabled", () -> "false");
        registry.add("management.zipkin.tracing.endpoint", () -> "");
    }
}
