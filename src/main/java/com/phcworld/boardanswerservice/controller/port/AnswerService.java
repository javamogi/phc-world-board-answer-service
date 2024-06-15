package com.phcworld.boardanswerservice.controller.port;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.domain.port.AnswerRequest;

import java.util.List;

public interface AnswerService {
    Answer register(AnswerRequest request);
    Answer getAnswer(String answerId);
    Answer update(AnswerRequest request);
    Answer delete(String answerId);
    List<Answer> getAnswerList(Long boardId);
}
