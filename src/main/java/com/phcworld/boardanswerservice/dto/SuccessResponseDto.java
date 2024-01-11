package com.phcworld.boardanswerservice.dto;

import lombok.Builder;

@Builder
public record SuccessResponseDto(
        Integer statusCode,
        String message
) {
}
