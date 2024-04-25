package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class NotificationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant validFrom;
    private Instant validUntil;
    private String title;
    private String message;

    public NotificationRequest() {
    }

    public NotificationRequest(Instant validFrom, Instant validUntil, String title, String message) {
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.title = title;
        this.message = message;
    }

    public NotificationRequest(Long id, Instant validFrom, Instant validUntil, String title, String message) {
        this.id = id;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.title = title;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
