package com.phcworld.boardanswerservice.dto;

import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import lombok.Builder;

@Builder
public record FreeBoardAnswerResponseDto(
        String answerId,
        UserResponseDto writer,
        String contents,
        String updatedDate
) {
}
