package com.jobsity.webclient.base.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ClientErrorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientErrorException.class)
    protected ResponseEntity<ErrorData> handleClientErrorException(ClientErrorException exception) {
        ErrorData responseBody = new ErrorData(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(responseBody);
    }

}
