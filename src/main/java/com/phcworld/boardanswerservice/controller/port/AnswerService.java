package com.phcworld.boardanswerservice.controller.port;

import com.phcworld.boardanswerservice.domain.Answer;

import java.util.List;

public interface AnswerService {
    Answer register(AnswerRequest request, String token);
    Answer getAnswer(String answerId, String token);
    Answer update(AnswerRequest request, String token);
    Answer delete(String answerId);
    List<Answer> getAnswerList(Long boardId, String token);
}
