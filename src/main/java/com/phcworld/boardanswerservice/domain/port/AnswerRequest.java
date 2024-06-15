package com.phcworld.boardanswerservice.domain.port;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AnswerRequest(
        Long boardId,
        String answerId,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
}
