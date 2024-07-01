package com.phcworld.boardanswerservice.controller;

import com.phcworld.boardanswerservice.controller.port.AnswerResponse;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.mock.FakeAuthentication;
import com.phcworld.boardanswerservice.mock.TestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnswerQueryApiControllerTest {


    @Test
    @DisplayName("게시글 ID로 게시글 답변 목록을 가져올 수 있다.")
    void getAnswersByBoardId() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(() -> time)
                .build();
        testContainer.answerRepository.save(Answer.builder()
                .id(1L)
                .answerId("answer-id-1")
                .freeBoardId(1L)
                .writerId("user-id")
                .contents("contents")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.answerRepository.save(Answer.builder()
                .id(2L)
                .answerId("answer-id-2")
                .freeBoardId(1L)
                .writerId("user-id")
                .contents("contents2")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.answerRepository.save(Answer.builder()
                .id(3L)
                .answerId("answer-id-2")
                .freeBoardId(2L)
                .writerId("user-id")
                .contents("contents2")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<List<AnswerResponse>> result = testContainer.answerQueryApiController.getAnswersByBoardId(1L, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
    }
}