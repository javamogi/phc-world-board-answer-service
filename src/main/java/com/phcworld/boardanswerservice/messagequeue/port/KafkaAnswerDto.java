package com.phcworld.boardanswerservice.messagequeue.port;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KafkaAnswerDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
