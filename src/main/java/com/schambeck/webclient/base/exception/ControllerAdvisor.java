package com.schambeck.webclient.base.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
class ControllerAdvisor {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ErrorData> handleClientErrorException(ClientErrorException exception) {
        ErrorData responseBody = new ErrorData(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(responseBody);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorData> handleServerUnavailableException(ServiceUnavailableException exception) {
        ErrorData responseBody = new ErrorData(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(responseBody);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }

}
