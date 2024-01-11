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

    @BeforeAll
    static void 회원_초기화(){
        user = UserResponseDto.builder()
                .id(1L)
                .email("test@test.test")
                .name("테스트")
                .createDate("방금전")
                .userId(userId)
                .build();

        userId = UUID.randomUUID().toString();
        token = "token";
    }

    @Test
    void 답변_등록() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .boardId(1L)
                .contents("contents")
                .build();
        FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
                .id(1L)
                .writerId(userId)
                .freeBoardId(1L)
                .contents(request.contents())
                .build();

        FreeBoardAnswerResponseDto response =  FreeBoardAnswerResponseDto.builder()
                .id(freeBoardAnswer.getId())
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
                .boardId(1L)
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
                .writerId(userId)
                .freeBoardId(1L)
                .contents("answer contents")
                .build();

        FreeBoardAnswerResponseDto response = FreeBoardAnswerResponseDto.builder()
                .id(freeBoardAnswer.getId())
                .writer(user)
                .contents(freeBoardAnswer.getContents())
                .updatedDate(freeBoardAnswer.getFormattedUpdateDate())
                .build();

        when(answerService.getFreeBoardAnswer(1L, token)).thenReturn(response);
        FreeBoardAnswerResponseDto result = answerService.getFreeBoardAnswer(1L, token);
        assertThat(response).isEqualTo(result);
    }

    @Test
    void 하나의_답변_조회_오류_없는_답변() {
        when(answerService.getFreeBoardAnswer(1L, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            answerService.getFreeBoardAnswer(1L, token);
        });
    }

    @Test
    void 답변_수정_성공() {
        FreeBoardAnswerRequestDto request = FreeBoardAnswerRequestDto.builder()
                .answerId(1L)
                .contents("contents")
                .build();
        FreeBoardAnswer freeBoardAnswer = FreeBoardAnswer.builder()
                .id(1L)
                .writerId(userId)
                .freeBoardId(1L)
                .contents(request.contents())
                .build();

        FreeBoardAnswerResponseDto response = FreeBoardAnswerResponseDto.builder()
                .id(freeBoardAnswer.getId())
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
                .answerId(1L)
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
                .answerId(1L)
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

        when(answerService.deleteFreeBoardAnswer(1L)).thenReturn(response);
        SuccessResponseDto result = answerService.deleteFreeBoardAnswer(1L);
        assertThat(response).isEqualTo(result);
    }
}