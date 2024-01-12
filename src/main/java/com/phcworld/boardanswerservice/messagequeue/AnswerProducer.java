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

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("string", false, "writer_id"),
            new Field("int64", false, "free_board_id"),
            new Field("string", true, "contents"),
            new Field("int64", true, "create_date"){public String name="org.apache.kafka.connect.data.Timestamp"; public int version = 1;},
            new Field("int64", true, "update_date"){public String name="org.apache.kafka.connect.data.Timestamp"; public int version = 1;});
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("answers")
            .build();

    public FreeBoardAnswer send(String topic, FreeBoardAnswer answer){
        ZoneId zoneid = ZoneId.of("Asia/Seoul");
        Payload payload = Payload.builder()
                .writer_id(answer.getWriterId())
                .free_board_id(answer.getFreeBoardId())
                .contents(answer.getContents())
                .create_date(answer.getCreateDate().atZone(zoneid).toInstant().toEpochMilli())
                .update_date(answer.getUpdateDate().atZone(zoneid).toInstant().toEpochMilli())
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
