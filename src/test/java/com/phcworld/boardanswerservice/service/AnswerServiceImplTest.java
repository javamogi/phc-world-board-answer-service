package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.controller.port.AnswerService;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.exception.model.DeletedEntityException;
import com.phcworld.boardanswerservice.exception.model.ForbiddenException;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.mock.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerServiceImplTest {

    private AnswerService answerService;
    private LocalDateTime localDateTime = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);

    @BeforeEach
    void init() {
        FakeAnswerRepository fakeAnswerRepository = new FakeAnswerRepository();
        FakeBoardProducer fakeBoardProducer = new FakeBoardProducer();
        FakeAnswerProducer answerProducer = new FakeAnswerProducer();
        this.answerService = AnswerServiceImpl.builder()
                .answerRepository(fakeAnswerRepository)
                .boardProducer(fakeBoardProducer)
                .answerProducer(answerProducer)
                .timeHolder(new FakeLocalDateTimeHolder(localDateTime))
                .uuidHolder(new TestUuidHolder("answer-id-3"))
                .build();
        Answer answer = Answer.builder()
                .answerId("answer-id-1")
                .freeBoardId(1L)
                .writerId("user-id")
                .contents("답변 내용")
                .createDate(localDateTime)
                .updateDate(localDateTime)
                .isDeleted(false)
                .build();
        fakeAnswerRepository.save(answer);
        Answer deletedAnswer = Answer.builder()
                .answerId("answer-id-2")
                .freeBoardId(1L)
                .writerId("user-id")
                .contents("삭제된 답변입니다.")
                .createDate(localDateTime)
                .updateDate(localDateTime)
                .isDeleted(true)
                .build();
        fakeAnswerRepository.save(deletedAnswer);
    }

    @Test
    @DisplayName("회원은 답변을 등록할 수 있다.")
    void successRegister(){
        // given
        AnswerRequest request = AnswerRequest.builder()
                .boardId(1L)
                .contents("답변 내용입니다.")
                .build();
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Answer result = answerService.register(request);

        // then
        assertThat(result.getContents()).isEqualTo("답변 내용입니다.");
        assertThat(result.getCreateDate()).isEqualTo(localDateTime);
        assertThat(result.getAnswerId()).isEqualTo("answer-id-3");
        assertThat(result.getFreeBoardId()).isEqualTo(1);
        assertThat(result.getWriterId()).isEqualTo("user-id");
        assertThat(result.isDeleted()).isFalse();
    }

//    @Test
//    @DisplayName("등록되지 않은 게시글에 답변을 등록할 수 없다.")
//    void failedRegisterWhenNotFoundFreeBoard(){
//        // given
//        FreeBoardAnswerRequest request = FreeBoardAnswerRequest.builder()
//                .boardId(2L)
//                .contents("답변내용")
//                .build();
//        Authentication authentication = new FakeAuthentication(1, Authority.ROLE_USER).getAuthentication();
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // when
//        // then
//        Assertions.assertThrows(NotFoundException.class, () -> {
//            freeBoardAnswerService.register(request);
//        });
//    }
//
//    @Test
//    @DisplayName("가입하지 않은 회원은 답변을 등록할 수 없다.")
//    void failedRegisterWhenNotFoundUser(){
//        // given
//        FreeBoardAnswerRequest request = FreeBoardAnswerRequest.builder()
//                .boardId(1L)
//                .contents("답변내용")
//                .build();
//        Authentication authentication = new FakeAuthentication(2, Authority.ROLE_USER).getAuthentication();
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // when
//        // then
//        Assertions.assertThrows(NotFoundException.class, () -> {
//            freeBoardAnswerService.register(request);
//        });
//    }

    @Test
    @DisplayName("FreeBoardAnswerRequest로 답변을 수정할 수 있다.")
    void update(){
        // given
        AnswerRequest request = AnswerRequest.builder()
                .answerId("answer-id-1")
                .contents("답변 내용 수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Answer result = answerService.update(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContents()).isEqualTo("답변 내용 수정");
        assertThat(result.getCreateDate()).isEqualTo(localDateTime);
        assertThat(result.getAnswerId()).isEqualTo("answer-id-1");
        assertThat(result.getFreeBoardId()).isEqualTo(1);
        assertThat(result.getWriterId()).isEqualTo("user-id");
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("answer id의 답변이 없는 경우 수정할 수 없다.")
    void failedUpdateWhenNotFound(){
        // given
        AnswerRequest request = AnswerRequest.builder()
                .answerId("not-answer-id")
                .contents("답변 내용 수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.update(request);
        });
    }

    @Test
    @DisplayName("작성자가 다를 경우 수정할 수 없다.")
    void failedUpdateWhenNotMatchWriter(){
        // given
        AnswerRequest request = AnswerRequest.builder()
                .answerId("answer-id-1")
                .contents("답변내용수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id-2","password2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            answerService.update(request);
        });
    }

    @Test
    @DisplayName("답변의 작성자는 삭제할 수 있다.")
    void delete(){
        // given
        Authentication authentication = new FakeAuthentication("user-id","password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Answer result = answerService.delete("answer-id-1");

        // then
        assertThat(result.getAnswerId()).isEqualTo("answer-id-1");
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("관리자는 삭제할 수 있다.")
    void deleteByAdmin(){
        // given
        Authentication authentication = new FakeAuthentication("admin","admin", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Answer result = answerService.delete("answer-id-1");

        // then
        assertThat(result.getAnswerId()).isEqualTo("answer-id-1");
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("등록되지 않은 답변은 삭제할 수 없다.")
    void failedDeleteWhenNotFound(){
        // given
        String answerId = "answer-id-9999";

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.delete(answerId);
        });
    }

    @Test
    @DisplayName("작성자가 다르면 삭제할 수 없다.")
    void failedDeleteWhenNotMatchWriter(){
        // given
        String answerId = "answer-id-1";
        Authentication authentication = new FakeAuthentication("user-id-2", "pass-2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            answerService.delete(answerId);
        });
    }

    @Test
    @DisplayName("삭제된 답변은 다시 삭제할 수 없다.")
    void failedDeleteWhenDeleted(){
        // given
        String answerId = "answer-id-2";
        Authentication authentication = new FakeAuthentication("user-id-2","pass-2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            answerService.delete(answerId);
        });
    }

    @Test
    @DisplayName("게시글의 답변 목록을 가져올 수 있다.")
    void getAnswersByBoardId(){
        // given
        Long boardId = 1L;
        Authentication authentication = new FakeAuthentication("user-id-2","pass-2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        List<Answer> result = answerService.getAnswerList(boardId);

        // then
        assertThat(result).hasSize(1);
    }

}