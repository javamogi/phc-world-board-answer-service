package com.phcworld.boardanswerservice.controller.port;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AnswerRequest(
        String boardId,
        String answerId,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
}
