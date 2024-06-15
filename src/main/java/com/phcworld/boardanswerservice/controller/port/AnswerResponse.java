package com.phcworld.boardanswerservice.controller.port;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.UserResponse;
import com.phcworld.boardanswerservice.utils.LocalDateTimeUtils;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnswerResponse(
        String answerId,
        UserResponse writer,
        String contents,
        String updatedDate,
        boolean isDeleted
) {
    public static AnswerResponse of(Answer answer, UserResponse user) {
        return AnswerResponse.builder()
                .answerId(answer.getAnswerId())
                .writer(user)
                .contents(answer.getContents())
                .updatedDate(LocalDateTimeUtils.getTime(answer.getUpdateDate()))
                .isDeleted(answer.isDeleted())
                .build();
    }

}
