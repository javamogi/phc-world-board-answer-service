package com.phcworld.boardanswerservice.mock;


import com.phcworld.boardanswerservice.controller.port.WebClientService;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.UserResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeWebClientService implements WebClientService {
    @Override
    public UserResponse getUser(String token, Answer answer) {
        return UserResponse.builder()
                .userId(answer.getWriterId())
                .profileImage("blank.jpg")
                .email("test0@test.test")
                .name("테스트0")
                .build();
    }

    @Override
    public Map<String, UserResponse> getUsersMap(String token, List<Answer> answers) {
        List<String> userIds = answers.stream()
                .map(Answer::getWriterId)
                .distinct()
                .toList();
        Map<String, UserResponse> map = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            UserResponse user = UserResponse.builder()
                    .userId(userId)
                    .profileImage("blank.jpg")
                    .email("test" + i + "@test.test")
                    .name("테스트" + i)
                    .build();
            map.put(userId, user);
        }
        return map;
    }

}
