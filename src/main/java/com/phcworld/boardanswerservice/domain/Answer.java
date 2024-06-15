package com.phcworld.boardanswerservice.domain;

import com.phcworld.boardanswerservice.domain.port.AnswerRequest;
import com.phcworld.boardanswerservice.service.port.LocalDateTimeHolder;
import com.phcworld.boardanswerservice.service.port.UuidHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Answer {
    private Long id;
    private String answerId;
    private String writerId;
    private Long freeBoardId;
    private String contents;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private boolean isDeleted;

    public static Answer from(AnswerRequest request,
                              String userId,
                              UuidHolder uuidHolder,
                              LocalDateTimeHolder timeHolder) {
        return Answer.builder()
                .answerId(uuidHolder.random())
                .writerId(userId)
                .freeBoardId(request.boardId())
                .contents(request.contents())
                .createDate(timeHolder.now())
                .updateDate(timeHolder.now())
                .isDeleted(false)
                .build();
    }

    public boolean matchWriter(String userId) {
        return writerId.equals(userId);
    }

    public Answer update(String contents) {
        return Answer.builder()
                .id(id)
                .answerId(answerId)
                .writerId(writerId)
                .freeBoardId(freeBoardId)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .isDeleted(isDeleted)
                .build();
    }

    public Answer delete() {
        return Answer.builder()
                .id(id)
                .answerId(answerId)
                .writerId(writerId)
                .freeBoardId(freeBoardId)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .isDeleted(true)
                .build();
    }
}
