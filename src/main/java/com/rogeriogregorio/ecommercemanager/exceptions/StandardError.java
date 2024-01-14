package com.rogeriogregorio.ecommercemanager.exceptions;

import java.io.Serializable;
import java.time.Instant;

public class StandardError  implements Serializable {

    private Instant timeStamp;
    private Integer status;
    private String error;
    private String message;

    public StandardError() {
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
