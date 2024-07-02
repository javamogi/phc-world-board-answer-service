package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.controller.port.WebClientService;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.exception.model.DeletedEntityException;
import com.phcworld.boardanswerservice.exception.model.ErrorCode;
import com.phcworld.boardanswerservice.exception.model.InternalServerErrorException;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.security.utils.SecurityUtil;
import com.phcworld.boardanswerservice.service.port.BoardResponse;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebclientServiceImpl implements WebClientService {

    private final WebClient.Builder webClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${user_service.url}")
    private String userUrl;

    @Value("${board_service.url}")
    private String boardUrl;

    @Override
    public UserResponse getUser(String token, Answer answer) {
        String userId = "";
        if(answer == null){
            userId = SecurityUtil.getCurrentMemberId();
        } else {
            userId = answer.getWriterId();
        }
        String finalUserId = userId;
        return webClient.build()
                .mutate().baseUrl(userUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{id}")
                        .build(finalUserId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.just(new NotFoundException(ErrorCode.USER_NOT_FOUND)))
                .bodyToMono(UserResponse.class)
                .block();
    }

    @Override
    public Map<String, UserResponse> getUsersMap(String token, List<Answer> answers) {
        List<String> userIds = answers.stream()
				.map(Answer::getWriterId)
				.distinct()
				.toList();

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<String, UserResponse> users = circuitBreaker.run(
                () -> webClient.build()
				        .mutate().baseUrl(userUrl)
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

    @Override
    public BoardResponse existBoard(String token, AnswerRequest request){
        long boardId = request.boardId();
        BoardResponse result = webClient.build()
                .mutate().baseUrl(boardUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{id}/exist")
                        .build(boardId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new NotFoundException(ErrorCode.BOARD_NOT_FOUND)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.just(new InternalServerErrorException()))
                .bodyToMono(BoardResponse.class)
                .block();
        if(result.isDelete()){
            throw new DeletedEntityException(ErrorCode.BOARD_ALREADY_DELETED);
        }
        return result;
    }

}
