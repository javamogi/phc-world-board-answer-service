package com.phcworld.boardanswerservice.exception.handler;

import com.phcworld.boardanswerservice.exception.model.CustomBaseException;
import com.phcworld.boardanswerservice.exception.model.ErrorCode;
import com.phcworld.boardanswerservice.exception.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CommonsExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handlerBadCredentialsException(){
        return createErrorResponseEntity(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(CustomBaseException.class)
    public ResponseEntity<ErrorResponse> handle(CustomBaseException e){
        log.error("Exception");
        return createErrorResponseEntity(e.getErrorCode());
    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(ErrorResponse.of(errorCode), errorCode.getHttpStatus());
    }
}
