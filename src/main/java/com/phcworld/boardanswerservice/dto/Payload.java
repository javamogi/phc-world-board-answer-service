package com.phcworld.boardanswerservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    private String writer_id;
    private long free_board_id;
    private String contents;
    private long create_date;
    private long update_date;
}
