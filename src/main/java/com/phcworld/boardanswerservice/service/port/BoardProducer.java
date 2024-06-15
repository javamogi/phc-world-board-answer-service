package com.phcworld.boardanswerservice.service.port;

import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.infrastructure.FreeBoardAnswerEntity;

public interface BoardProducer {
    Answer send(String topic, Answer answer);
}
