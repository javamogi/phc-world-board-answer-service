package com.phcworld.boardanswerservice.mock;


import com.phcworld.boardanswerservice.controller.port.WebClientService;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.service.port.BoardResponse;
import com.phcworld.boardanswerservice.service.port.UserResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeWebClientService implements WebClientService {

    private List<UserResponse> users = new ArrayList<>();
    private List<BoardResponse> boards = new ArrayList<>();

    public FakeWebClientService() {
        UserResponse user1 = UserResponse.builder()
                .name("테스트")
                .profileImage("image")
                .userId("1111")
                .build();
        UserResponse user2 = UserResponse.builder()
                .name("테스트2")
                .profileImage("image")
                .userId("2222")
                .build();
        UserResponse user3 = UserResponse.builder()
                .name("관리자")
                .profileImage("image")
                .userId("admin")
                .build();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        boards.add(BoardResponse.builder()
                        .boardId(1L)
                        .writer("1111")
                        .title("제목")
                        .contents("제목")
                        .createDate("방금전")
                        .count(0)
                        .countOfAnswer(0)
                        .isNew(true)
                        .isDelete(false)
                .build());
    }

    @Override
    public UserResponse getUser(String token, Answer answer) {
        return users.stream()
                .filter(user -> user.userId().equals(token))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Map<String, UserResponse> getUsersMap(String token, List<Answer> answers) {
        List<String> userIds = answers.stream()
                .map(Answer::getWriterId)
                .distinct()
                .toList();
        Map<String, UserResponse> map = new HashMap<>();

        users.stream()
                .filter(user -> userIds.contains(user.userId()))
                .forEach(user -> map.put(user.userId(), user));
        return map;
    }

    @Override
    public BoardResponse existBoard(String token, AnswerRequest request) {
        return boards.stream()
                .filter(board -> board.boardId().equals(request.boardId()))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }

}
