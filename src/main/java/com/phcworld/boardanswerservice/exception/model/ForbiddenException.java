package com.phcworld.boardanswerservice.exception.model;

public class ForbiddenException extends CustomBaseException{
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ForbiddenException(){
        super(ErrorCode.FORBIDDEN);
    }
}
