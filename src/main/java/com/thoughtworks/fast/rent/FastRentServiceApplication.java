package com.thoughtworks.fast.rent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories
@EnableJpaAuditing
public class FastRentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastRentServiceApplication.class, args);
    }

}
