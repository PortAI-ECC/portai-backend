package com.portai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PortaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortaiApplication.class, args);
    }
}
