package com.phcworld.boardanswerservice.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
}
