package com.phcworld.boardanswerservice.dto;

import lombok.Builder;

@Builder
public record AnswerResponseDto(
        String answerId,
        UserResponseDto writer,
        String contents,
        String updatedDate
) {
}
