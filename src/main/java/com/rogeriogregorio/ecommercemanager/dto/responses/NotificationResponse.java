package com.rogeriogregorio.ecommercemanager.dto.responses;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class NotificationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant validFrom;
    private Instant validUntil;
    private String title;
    private String message;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, Instant validFrom,
                                Instant validUntil,
                                String title, String message) {

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

    public boolean isValid() {

        Instant now = Instant.now();

        return now.isAfter(validFrom) && now.isBefore(validUntil);
    }

    public String getRemainingValidityTime() {

        Instant now = Instant.now();
        boolean isExpired = now.isAfter(validUntil);

        long days = Duration.between(now, validUntil).toDays();
        long hours = Duration.between(now, validUntil).toHoursPart();
        long minutes = Duration.between(now, validUntil).toMinutesPart();

        String expiredMessage = "The notification has expired.";
        String remainingTime = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        return isExpired ? expiredMessage : remainingTime;
    }
}