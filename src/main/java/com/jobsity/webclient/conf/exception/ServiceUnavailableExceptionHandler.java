package com.jobsity.webclient.conf.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceUnavailableExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    protected ResponseEntity<ErrorData> handleServerUnavailableException(ServiceUnavailableException exception) {
        ErrorData responseBody = new ErrorData(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(responseBody);
    }

}
