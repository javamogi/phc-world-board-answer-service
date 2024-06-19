package com.phcworld.boardanswerservice.controller.port;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.service.port.BoardResponse;
import com.phcworld.boardanswerservice.service.port.UserResponse;

import java.util.List;
import java.util.Map;

public interface WebClientService {
    UserResponse getUser(String token, Answer answer);
    Map<String, UserResponse> getUsersMap(String token, List<Answer> answers);

    BoardResponse existBoard(String token, AnswerRequest request);
}
