package com.jobsity.webclient.conf;

public class ServiceUnavailableException extends RuntimeException {

    private final int statusCode;

    public ServiceUnavailableException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
