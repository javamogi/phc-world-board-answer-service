package com.phcworld.boardanswerservice.domain;

import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.mock.FakeLocalDateTimeHolder;
import com.phcworld.boardanswerservice.mock.TestUuidHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerTest {

    @Test
    @DisplayName("AnswerRequest 정보로 생성할 수 있다.")
    void createByFreeBoardRequest(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        AnswerRequest answerRequest = AnswerRequest.builder()
                .boardId(1L)
                .contents("답변내용")
                .build();
        String userId = "1111";

        // when
        Answer result = Answer.from(answerRequest,
                userId,
                new TestUuidHolder("12345"),
                new FakeLocalDateTimeHolder(time));

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getContents()).isEqualTo("답변내용");
        assertThat(result.getCreateDate()).isEqualTo(time);
        assertThat(result.getAnswerId()).isEqualTo("12345");
        assertThat(result.getFreeBoardId()).isEqualTo(1);
        assertThat(result.getWriterId()).isEqualTo("1111");
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("회원의 ID값으로 작성자와 같은지 확인할 수 있다.")
    void matchWriter(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        Answer answer = Answer.builder()
                .id(1L)
                .answerId("answer-id")
                .writerId("writer-id")
                .freeBoardId(1L)
                .contents("답변내용")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build();

        // when
        boolean result = answer.matchWriter("writer-id");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("내용을 수정할 수 있다.")
    void update(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        Answer answer = Answer.builder()
                .id(1L)
                .answerId("answer-id")
                .writerId("writer-id")
                .freeBoardId(1L)
                .contents("답변내용")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build();

        // when
        Answer result = answer.update("답변내용수정");

        // then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getContents()).isEqualTo("답변내용수정");
        assertThat(result.getCreateDate()).isEqualTo(time);
        assertThat(result.getAnswerId()).isEqualTo("answer-id");
        assertThat(result.getFreeBoardId()).isEqualTo(1);
        assertThat(result.getWriterId()).isEqualTo("writer-id");
        assertThat(result.getUpdateDate()).isEqualTo(time);
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("삭제 할 수 있다.(soft delete-논리삭제)")
    void delete(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        Answer answer = Answer.builder()
                .id(1L)
                .answerId("answer-id")
                .writerId("writer-id")
                .freeBoardId(1L)
                .contents("답변내용")
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build();

        // when
        Answer result = answer.delete();

        // then
        assertThat(result.isDeleted()).isTrue();
    }

}