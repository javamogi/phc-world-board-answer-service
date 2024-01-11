package com.phcworld.boardanswerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FreeBoardAnswerRequestDto(
        Long boardId,
        Long answerId,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
}
