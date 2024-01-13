package com.phcworld.boardanswerservice.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("존재하지 않는 엔티티입니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    CONFLICT("이미 존재하는 엔티티입니다.", HttpStatus.CONFLICT);

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
