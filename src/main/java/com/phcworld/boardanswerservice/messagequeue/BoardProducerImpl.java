package com.phcworld.boardanswerservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.exception.model.InternalServerErrorException;
import com.phcworld.boardanswerservice.infrastructure.FreeBoardAnswerEntity;
import com.phcworld.boardanswerservice.service.port.AnswerRepository;
import com.phcworld.boardanswerservice.service.port.BoardProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardProducerImpl {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public FreeBoardAnswerEntity send(String topic, FreeBoardAnswerEntity answer){
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(answer);
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from the Answer microservice: {}", answer);

        return answer;
    }
}
