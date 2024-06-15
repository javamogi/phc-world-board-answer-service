package com.phcworld.boardanswerservice.controller;

import com.phcworld.boardanswerservice.controller.port.AnswerResponse;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.Authority;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.exception.model.ForbiddenException;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.mock.FakeAuthentication;
import com.phcworld.boardanswerservice.mock.TestContainer;
import com.phcworld.boardanswerservice.mock.TestUuidHolder;
import com.phcworld.boardanswerservice.utils.LocalDateTimeUtils;
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

class AnswerApiControllerTest {

    @Test
    @DisplayName("회원은 게시글의 답변을 등록할 수 있다")
    void register() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(() -> time)
                .uuidHolder(new TestUuidHolder("answer-id"))
                .build();
        AnswerRequest requestDto = AnswerRequest.builder()
                .boardId(1L)
                .contents("contents")
                .build();
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<AnswerResponse> result = testContainer.answerApiController.register(requestDto, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().contents()).isEqualTo("contents");
        assertThat(result.getBody().writer().email()).isEqualTo("test0@test.test");
        assertThat(result.getBody().answerId()).isEqualTo("answer-id");
        assertThat(result.getBody().isDeleted()).isFalse();
    }

//    @Test
//    @DisplayName("가입하지 않은 회원은 게시글의 답변을 등록할 수 업다")
//    void failedRegisterWhenNotFoundUser() {
//        // given
//        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
//        TestContainer testContainer = TestContainer.builder()
//                .localDateTimeHolder(() -> time)
//                .build();
//        User user = User.builder()
//                .id(1L)
//                .email("test@test.test")
//                .name("테스트")
//                .password("test2")
//                .isDeleted(false)
//                .authority(Authority.ROLE_USER)
//                .profileImage("blank-profile-picture.png")
//                .createDate(time)
//                .build();
//        testContainer.userRepository.save(user);
//        FreeBoard freeBoard = FreeBoard.builder()
//                .id(1L)
//                .title("제목")
//                .contents("내용")
//                .countOfAnswer(0)
//                .count(0)
//                .writer(user)
//                .createDate(time)
//                .updateDate(time)
//                .isDeleted(false)
//                .build();
//        testContainer.freeBoardRepository.save(freeBoard);
//        FreeBoardAnswerRequest requestDto = FreeBoardAnswerRequest.builder()
//                .boardId(1L)
//                .contents("contents")
//                .build();
//        Authentication authentication = new FakeAuthentication(2, Authority.ROLE_USER).getAuthentication();
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // when
//        // then
//        assertThatThrownBy(() -> {
//            testContainer.freeBoardAnswerApiController.register(requestDto);
//        }).isInstanceOf(NotFoundException.class);
//    }
//
//    @Test
//    @DisplayName("회원은 존재하지 않는 게시글의 답변을 등록할 수 없다")
//    void failedRegisterWhenNotFoundFreeBoard() {
//        // given
//        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
//        TestContainer testContainer = TestContainer.builder()
//                .localDateTimeHolder(() -> time)
//                .build();
//        User user = User.builder()
//                .id(1L)
//                .email("test@test.test")
//                .name("테스트")
//                .password("test2")
//                .isDeleted(false)
//                .authority(Authority.ROLE_USER)
//                .profileImage("blank-profile-picture.png")
//                .createDate(time)
//                .build();
//        testContainer.userRepository.save(user);
//        FreeBoardAnswerRequest requestDto = FreeBoardAnswerRequest.builder()
//                .boardId(1L)
//                .contents("contents")
//                .build();
//        Authentication authentication = new FakeAuthentication(1, Authority.ROLE_USER).getAuthentication();
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // when
//        // then
//        assertThatThrownBy(() -> {
//            testContainer.freeBoardAnswerApiController.register(requestDto);
//        }).isInstanceOf(NotFoundException.class);
//    }

    @Test
    @DisplayName("회원은 본인이 작성한 답변을 수정할 수 있다")
    void update() {
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
        AnswerRequest requestDto = AnswerRequest.builder()
                .answerId("answer-id-1")
                .contents("내용으로 수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id", "password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<AnswerResponse> result = testContainer.answerApiController.updateFreeBoardAnswer(requestDto, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().answerId()).isEqualTo("answer-id-1");
        assertThat(result.getBody().contents()).isEqualTo("내용으로 수정");
        assertThat(result.getBody().updatedDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().writer().email()).isEqualTo("test0@test.test");
        assertThat(result.getBody().isDeleted()).isFalse();
    }

    @Test
    @DisplayName("회원은 존재하지 않는 게시글의 답변을 수정할 수 없다")
    void failedUpdateWhenNotFoundFreeBoard() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(() -> time)
                .build();
        AnswerRequest requestDto = AnswerRequest.builder()
                .answerId("answer-id-000")
                .contents("내용 수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id","password", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.answerApiController.updateFreeBoardAnswer(requestDto, "token");
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("작성자가 다르면 답변을 수정할 수 없다")
    void failedUpdateWhenNotMatchWriter() {
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
        AnswerRequest requestDto = AnswerRequest.builder()
                .answerId("answer-id-1")
                .contents("내용 수정")
                .build();
        Authentication authentication = new FakeAuthentication("user-id-2","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.answerApiController.updateFreeBoardAnswer(requestDto, "token");
        }).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("회원은 본인이 작성한 답변을 삭제할 수 있다")
    void delete() {
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
        Authentication authentication =
                new FakeAuthentication("user-id","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<AnswerResponse> result = testContainer
                .answerApiController
                .deleteFreeBoardAnswer("answer-id-1", "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().updatedDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().writer().email()).isEqualTo("test0@test.test");
        assertThat(result.getBody().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("관리자는 답변을 삭제할 수 있다")
    void deleteFromAdmin() {
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
        Authentication authentication = new FakeAuthentication("admin","admin", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<AnswerResponse> result = testContainer.answerApiController.deleteFreeBoardAnswer("answer-id-1", "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().updatedDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().writer().email()).isEqualTo("test0@test.test");
        assertThat(result.getBody().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("작성자는 존재하지 않는 답변을 삭제할 수 없다")
    void failedDeleteWhenNotFoundFreeBoard() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 15, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(() -> time)
                .build();
        Authentication authentication = new FakeAuthentication("user-id","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.answerApiController.deleteFreeBoardAnswer("answer-id-00", "token");
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("회원은 본인이 작성하지 않은 답변은 삭제할 수 없다")
    void failedDeleteWhenDifferentAnswer() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
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
        Authentication authentication = new FakeAuthentication("user-id-2","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.answerApiController.deleteFreeBoardAnswer("answer-id-1", "token");
        }).isInstanceOf(ForbiddenException.class);
    }

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
        ResponseEntity<List<AnswerResponse>> result = testContainer.answerApiController.getFreeBoardAnswers(1L, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
    }
}