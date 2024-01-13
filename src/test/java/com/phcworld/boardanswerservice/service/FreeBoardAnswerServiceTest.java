package com.phcworld.boardanswerservice.service;

import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerRequestDto;
import com.phcworld.boardanswerservice.dto.FreeBoardAnswerResponseDto;
import com.phcworld.boardanswerservice.dto.SuccessResponseDto;
import com.phcworld.boardanswerservice.dto.UserResponseDto;
import com.phcworld.boardanswerservice.exception.model.NotFoundException;
import com.phcworld.boardanswerservice.exception.model.NotMatchUserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeBoardAnswerServiceTest {

    @Mock
    private FreeBoardAnswerService answerService;

    private static UserResponseDto user;

    private static String userId;

    private static String token;

    private static String boardId;
    private static String answerId;

    @BeforeAll
    static void 회원_초기화(){
        user = UserResponseDto.builder()
                .email("test@test.test")
                .name("테스트")
                .createDate("방금전")
                .userId(userId)
                .build();

        userId = UUID.randomUUID().toString();
        token = "token";
        boardId = UUID.randomUUID().toString();
        answerId = UUID.randomUUID().toString();
    }

    @Test
    void 답변_등록() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .boardId(boardId)
                .contents("contents")
                .build();
        FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
                .id(1L)
                .answerId(answerId)
                .writerId(userId)
                .freeBoardId(boardId)
                .contents(request.contents())
                .build();

        FreeBoardAnswerResponseDto response =  FreeBoardAnswerResponseDto.builder()
                .answerId(freeBoardAnswer.getAnswerId())
                .writer(user)
                .contents(freeBoardAnswer.getContents())
                .updatedDate(freeBoardAnswer.getFormattedUpdateDate())
                .build();

        when(answerService.register(request, token)).thenReturn(response);
        FreeBoardAnswerResponseDto result = answerService.register(request, token);
        assertThat(response).isEqualTo(result);
    }

    @Test
    void 답변_등록_오류_없는_게시글() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .boardId(boardId)
                .contents("contents")
                .build();
        when(answerService.register(request, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.register(request, token);
        });
    }

    @Test
    void 하나의_답변_조회() {
        FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
                .id(1L)
                .answerId(answerId)
                .writerId(userId)
                .freeBoardId(boardId)
                .contents("answer contents")
                .build();

        FreeBoardAnswerResponseDto response = FreeBoardAnswerResponseDto.builder()
                .answerId(freeBoardAnswer.getAnswerId())
                .writer(user)
                .contents(freeBoardAnswer.getContents())
                .updatedDate(freeBoardAnswer.getFormattedUpdateDate())
                .build();

        when(answerService.getFreeBoardAnswer(answerId, token)).thenReturn(response);
        FreeBoardAnswerResponseDto result = answerService.getFreeBoardAnswer(answerId, token);
        assertThat(response).isEqualTo(result);
    }

    @Test
    void 하나의_답변_조회_오류_없는_답변() {
        when(answerService.getFreeBoardAnswer(answerId, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.getFreeBoardAnswer(answerId, token);
        });
    }

    @Test
    void 답변_수정_성공() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .answerId(answerId)
                .contents("contents")
                .build();
        FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
                .id(1L)
                .writerId(userId)
                .freeBoardId(boardId)
                .contents(request.contents())
                .build();

        FreeBoardAnswerResponseDto response = FreeBoardAnswerResponseDto.builder()
                .answerId(freeBoardAnswer.getAnswerId())
                .writer(user)
                .contents(freeBoardAnswer.getContents())
                .updatedDate(freeBoardAnswer.getFormattedUpdateDate())
                .build();

        when(answerService.updateFreeBoardAnswer(request, token)).thenReturn(response);
        FreeBoardAnswerResponseDto result = answerService.updateFreeBoardAnswer(request, token);
        assertThat(response).isEqualTo(result);
    }

    @Test
    void 답변_수정_실패_없는_답변() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .answerId(answerId)
                .contents("contents")
                .build();
        when(answerService.updateFreeBoardAnswer(request, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.updateFreeBoardAnswer(request, token);
        });
    }

    @Test
    void 답변_수정_실패_다른_작성자() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .answerId(answerId)
                .contents("contents")
                .build();
        when(answerService.updateFreeBoardAnswer(request, token)).thenThrow(NotMatchUserException.class);
        Assertions.assertThrows(NotMatchUserException.class, () -> {
            answerService.updateFreeBoardAnswer(request, token);
        });
    }

    @Test
    void 답변_삭제_성공() {
        SuccessResponseDto response = SuccessResponseDto.builder()
                .message("삭제성공")
                .statusCode(200)
                .build();

        when(answerService.deleteFreeBoardAnswer(answerId)).thenReturn(response);
        SuccessResponseDto result = answerService.deleteFreeBoardAnswer(answerId);
        assertThat(response).isEqualTo(result);
    }
}