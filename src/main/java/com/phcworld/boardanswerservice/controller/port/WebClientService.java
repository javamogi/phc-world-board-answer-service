package com.phcworld.boardanswerservice.controller.port;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.UserResponse;

import java.util.List;
import java.util.Map;

public interface WebClientService {
    UserResponse getUser(String token, Answer answer);
    Map<String, UserResponse> getUsersMap(String token, List<Answer> answers);
}
