package com.phcworld.boardanswerservice.messagequeue.port;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    private Long id;
    private String answer_id;
    private String writer_id;
    private Long free_board_id;
    private String contents;
    private String update_date;
    private byte is_deleted;
}
