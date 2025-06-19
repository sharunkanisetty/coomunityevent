package com.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.community.model")
@EnableJpaRepositories("com.community.repository")
@EnableScheduling
public class CommunityEventsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityEventsApplication.class, args);
    }
} 