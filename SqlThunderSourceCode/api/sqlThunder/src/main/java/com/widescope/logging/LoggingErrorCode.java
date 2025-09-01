package com.widescope.logging;


import org.springframework.http.HttpStatus;

public enum LoggingErrorCode {

    DEFAULT(1000, HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(1, HttpStatus.MULTI_STATUS);

    private final int statusCode;
    private final HttpStatus status;

    LoggingErrorCode(int statusCode, HttpStatus status) {
        this.statusCode = statusCode;
        this.status = status;
    }

    public int getStatusCode() { return statusCode; }

    public HttpStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.valueOf(statusCode);
    }

}
