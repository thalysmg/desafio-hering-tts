package com.hering.desafiojava.api.controller;

import com.hering.desafiojava.core.exception.BadRequestException;
import com.hering.desafiojava.core.exception.RecordNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail handleRecordNotFoundException(RecordNotFoundException e) {
        log.error(e.getMessage());
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException e) {
        log.error(e.getMessage());
        return ProblemDetail.forStatusAndDetail(SERVICE_UNAVAILABLE, e.getMessage());
    }
}
