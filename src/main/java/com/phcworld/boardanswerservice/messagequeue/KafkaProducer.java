package com.phcworld.boardanswerservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public FreeBoardAnswer send(String topic, FreeBoardAnswer answer){
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(answer);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from the Answer microservice: {}", answer);

        return answer;
    }
}
