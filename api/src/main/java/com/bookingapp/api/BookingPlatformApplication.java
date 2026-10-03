package com.bookingapp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Data's repository/entity auto-scanning defaults to this class's own package,
 * not {@code scanBasePackages} - since the JPA entities and repository interfaces live in the
 * infrastructure module (a different package tree), both need pointing at explicitly.
 * Each feature package there holds its own entities and repositories.
 */
@SpringBootApplication(scanBasePackages = "com.bookingapp")
@EnableJpaRepositories(basePackages = "com.bookingapp.infrastructure")
@EntityScan(basePackages = "com.bookingapp.infrastructure")
@EnableScheduling
public class BookingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingPlatformApplication.class, args);
    }
}
