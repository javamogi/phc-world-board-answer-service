package com.phcworld.boardanswerservice.mock;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.AnswerProducer;

public class FakeAnswerProducer implements AnswerProducer {
    @Override
    public Answer send(String topic, Answer answer) {
        return answer;
    }
}
