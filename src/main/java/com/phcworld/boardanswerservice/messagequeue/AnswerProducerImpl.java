package com.phcworld.boardanswerservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.boardanswerservice.domain.Answer;
import com.phcworld.boardanswerservice.exception.model.InternalServerErrorException;
import com.phcworld.boardanswerservice.messagequeue.port.Field;
import com.phcworld.boardanswerservice.messagequeue.port.KafkaAnswerDto;
import com.phcworld.boardanswerservice.messagequeue.port.Payload;
import com.phcworld.boardanswerservice.messagequeue.port.Schema;
import com.phcworld.boardanswerservice.service.port.AnswerProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerProducerImpl implements AnswerProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("int8", false, "is_deleted"),
            new Field("string", false, "answer_id"),
            new Field("string", false, "writer_id"),
            new Field("int64", false, "free_board_id"),
            new Field("string", false, "contents"),
            new Field("string", false, "update_date"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("answers")
            .build();

    @Override
    public Answer send(String topic, Answer answer){
        Payload payload = Payload.builder()
                .answer_id(answer.getAnswerId())
                .writer_id(answer.getWriterId())
                .free_board_id(answer.getFreeBoardId())
                .contents(answer.getContents())
                .update_date(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .is_deleted((byte)(Boolean.TRUE.equals(answer.isDeleted()) ? 1 : 0))
                .build();

        KafkaAnswerDto kafkaAnswerDto = KafkaAnswerDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaAnswerDto);
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Answer Producer sent data from the Answer microservice: {}", kafkaAnswerDto);

        return answer;
    }
}
