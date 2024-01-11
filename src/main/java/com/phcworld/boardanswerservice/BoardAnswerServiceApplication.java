package com.phcworld.boardanswerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BoardAnswerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardAnswerServiceApplication.class, args);
    }

}
