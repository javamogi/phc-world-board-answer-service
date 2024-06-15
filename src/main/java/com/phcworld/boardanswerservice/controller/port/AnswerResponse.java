package com.phcworld.boardanswerservice.controller.port;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnswerResponse(
        String answerId,
        UserResponse writer,
        String contents,
        String updatedDate
) {
}
