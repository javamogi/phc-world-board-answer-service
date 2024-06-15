package com.phcworld.boardanswerservice.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST),
    TOKEN_BAD_REQUEST("TOKEN_BAD_REQUEST", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("INVALID_PARAMETER", HttpStatus.BAD_REQUEST),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN),
    ALREADY_EXIST("ALREADY_EXIST", HttpStatus.CONFLICT),
    ALREADY_DELETED("ALREADY_DELETED", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
