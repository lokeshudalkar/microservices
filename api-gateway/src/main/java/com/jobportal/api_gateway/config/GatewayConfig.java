package com.jobportal.api_gateway.config;

import com.jobportal.api_gateway.Filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Configuration for routing requests to appropriate microservices
 * Defines public and protected routes with authentication filters
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()


//                .route("user-public-register", r -> r
//                        .path("/api/auth/register")
//                        .filters(f -> f.stripPrefix(1))
//                        .uri("lb://user-service"))
//
//                .route("user-public-login", r -> r
//                        .path("/api/auth/login")
//                        .filters(f -> f.stripPrefix(1))
//                        .uri("lb://user-service"))

                .route("user-public-register", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))


                .route("user-protected", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new Object())))
                        .uri("lb://user-service"))

                .route("job-service-protected", r -> r
                        .path("/api/jobs/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new Object())))
                        .uri("lb://jobservice"))

                .route("job-service-public", r -> r
                        .path("/api/public/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://jobservice"))

                .route("job-application-protected", r -> r
                        .path("/api/job-applications/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new Object())))
                        .uri("lb://application-service"))

                .route("job-application-public", r -> r
                        .path("/api/all/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://application-service"))


                .route("fallback", r -> r
                        .path("/**")
                        .uri("lb://user-service/fallback"))

                .build();
    }

}
