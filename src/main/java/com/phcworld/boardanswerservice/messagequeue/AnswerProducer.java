package com.phcworld.boardanswerservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.boardanswerservice.domain.FreeBoardAnswer;
import com.phcworld.boardanswerservice.dto.Field;
import com.phcworld.boardanswerservice.dto.KafkaAnswerDto;
import com.phcworld.boardanswerservice.dto.Payload;
import com.phcworld.boardanswerservice.dto.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("int8", false, "is_deleted"),
            new Field("string", false, "answer_id"),
            new Field("string", false, "writer_id"),
            new Field("string", false, "free_board_id"),
            new Field("string", false, "contents"),
            new Field("string", false, "update_date"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("answers")
            .build();

    public FreeBoardAnswer send(String topic, FreeBoardAnswer answer){
        Payload payload = Payload.builder()
                .answer_id(answer.getAnswerId())
                .writer_id(answer.getWriterId())
                .free_board_id(answer.getFreeBoardId())
                .contents(answer.getContents())
                .update_date(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .build();

        KafkaAnswerDto kafkaAnswerDto = KafkaAnswerDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaAnswerDto);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Answer Producer sent data from the Answer microservice: {}", kafkaAnswerDto);

        return answer;
    }
}
