package com.phcworld.boardanswerservice.exception.handler;

import com.phcworld.boardanswerservice.exception.model.CustomBaseException;
import com.phcworld.boardanswerservice.exception.model.ErrorCode;
import com.phcworld.boardanswerservice.exception.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CommonsExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handlerBadCredentialsException(){
        return createErrorResponseEntity(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(CustomBaseException.class)
    public ResponseEntity<ErrorResponse> handle(CustomBaseException e){
        log.error("Exception");
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity handle(BindException e){
        log.error("Exception");
        Map<String, Object> map = new HashMap<>();
        List<FieldError> errors = e.getFieldErrors();
        List<String> errorMessages = new ArrayList<>();
        for (int i = 0; i < errors.size(); i++){
            FieldError error = errors.get(i);
            errorMessages.add(error.getDefaultMessage());
        }
        map.put("messages", errorMessages);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(ErrorResponse.of(errorCode), errorCode.getHttpStatus());
    }
}
