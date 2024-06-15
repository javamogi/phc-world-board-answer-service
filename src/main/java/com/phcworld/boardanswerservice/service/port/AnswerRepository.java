package com.phcworld.boardanswerservice.service.port;

import com.phcworld.boardanswerservice.domain.Answer;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository {
    List<Answer> findByWriterId(String userId);
    List<Answer> findByFreeBoardIdAndIsDeleted(String boardId, boolean isDelete);
    Optional<Answer> findByAnswerId(String answerId);
    Answer save(Answer answer);
}
