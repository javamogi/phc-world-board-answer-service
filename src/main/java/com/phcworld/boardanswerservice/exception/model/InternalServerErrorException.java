package com.phcworld.boardanswerservice.exception.model;

public class InternalServerErrorException extends CustomBaseException{
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public InternalServerErrorException(){
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
