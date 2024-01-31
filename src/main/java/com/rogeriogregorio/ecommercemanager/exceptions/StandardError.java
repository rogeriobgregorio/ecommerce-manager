package com.rogeriogregorio.ecommercemanager.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;

public class StandardError implements Serializable {

    private Instant timeStamp;
    private Integer status;
    private String error;
    private String message;

    public StandardError() {
    }

    public StandardError(HttpStatus status, String error, String message) {
        this.timeStamp = Instant.now();
        this.status = status.value();
        this.error = error;
        this.message = message;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
