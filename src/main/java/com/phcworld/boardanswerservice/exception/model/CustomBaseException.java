package com.phcworld.boardanswerservice.exception.model;

import lombok.Getter;

@Getter
public class CustomBaseException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomBaseException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public CustomBaseException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
