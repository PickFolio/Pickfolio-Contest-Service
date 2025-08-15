package com.pickfolio.contest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PickFolioContestApplication {
    public static void main(String[] args) {
        SpringApplication.run(PickFolioContestApplication.class, args);
    }
}