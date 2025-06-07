package com.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.community.model")
@EnableJpaRepositories("com.community.repository")
public class CommunityEventsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityEventsApplication.class, args);
    }
} 