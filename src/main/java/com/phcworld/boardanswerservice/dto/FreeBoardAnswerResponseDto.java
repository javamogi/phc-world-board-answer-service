package com.phcworld.boardanswerservice.dto;

import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import lombok.Builder;

@Builder
public record FreeBoardAnswerResponseDto(
        Long id,
        UserResponseDto writer,
        String contents,
        String updatedDate
) {
}
