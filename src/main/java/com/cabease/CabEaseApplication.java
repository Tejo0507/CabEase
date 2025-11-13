package com.cabease;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication
@EnableAsync
@EntityScan({"com.cabease.models", "com.cabease.entity"})
@EnableJpaRepositories("com.cabease.repository")
public class CabEaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(CabEaseApplication.class, args);
    }
}
