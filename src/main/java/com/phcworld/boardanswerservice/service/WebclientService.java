package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.infrastructure.FreeBoardAnswerEntity;
import com.phcworld.boardanswerservice.controller.port.AnswerRequest;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebclientService {

    private final WebClient.Builder webClient;
    private final Environment env;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public boolean existFreeBoard(AnswerRequest request, String token){
        log.info("Before call boards microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        boolean result = Boolean.TRUE.equals(
                circuitBreaker.run(() ->
                                webClient.build()
//                                        .mutate().baseUrl("http://localhost:8080/boards")
                                        .mutate().baseUrl(env.getProperty("board_service.url"))
                                        .build()
                                        .get()
                                        .uri(uriBuilder -> uriBuilder
                                                .path("/{id}/exist")
                                                .build(request.boardId()))
                                        .header("Authorization", token)
                                        .retrieve()
                                        .bodyToMono(Boolean.class)
                                        .block(),
                        throwable -> false)
        );
        log.info("After call boards microservice");
        return result;
    }

    public UserResponse getUserResponseDto(String token, FreeBoardAnswerEntity answer) {
        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        UserResponse user = circuitBreaker.run(
                () -> webClient.build()
//                        .mutate().baseUrl("http://localhost:8080/users")
				        .mutate().baseUrl(env.getProperty("user_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/{id}")
                                .build(answer.getWriterId()))
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(UserResponse.class)
                        .block(),
                throwable -> UserResponse.builder()
                        .email("")
                        .name("")
                        .createDate("")
                        .profileImage("")
                        .userId("")
                        .build());
        log.info("After called users microservice");
        return user;
    }

    public Map<String, UserResponse> getUserResponseDtoMap(String token, List<String> userIds) {
        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<String, UserResponse> users = circuitBreaker.run(
                () -> webClient.build()
//                        .mutate().baseUrl("http://localhost:8080/users")
				        .mutate().baseUrl(env.getProperty("user_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("")
                                .queryParam("userIds", userIds)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, UserResponse>>() {})
                        .block(),
                throwable -> new HashMap<>());
        log.info("After called users microservice");
        return users;
    }

}
