package com.phcworld.boardanswerservice.mock;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.service.port.BoardProducer;

public class FakeBoardProducer implements BoardProducer {
    @Override
    public Answer send(String topic, Answer answer) {
        return answer;
    }
}
