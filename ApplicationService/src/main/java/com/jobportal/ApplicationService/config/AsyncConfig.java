package com.jobportal.ApplicationService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The type Async config.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    /**
     * Async executor executor.
     *
     * @return the executor
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor asyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
