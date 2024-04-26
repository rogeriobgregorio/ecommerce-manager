package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_notifications")
public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "valid_from")
    @NotNull(message = "The notification's validity start date cannot be null")
    @FutureOrPresent(message = "The notification's validity start date must not be earlier than today's date")
    private Instant validFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "valid_until")
    @NotNull(message = "The notification's expiration date cannot be null")
    private Instant validUntil;

    @Column(name = "title")
    @NotBlank(message = "The notification title cannot be blank")
    private String title;

    @Column(name = "message")
    @NotBlank(message = "Notification message cannot be blank")
    private String message;

    public Notification() {
    }

    public Notification(Instant validFrom, Instant validUntil,
                        String title, String message) {

        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.title = title;
        this.message = message;
    }

    public Notification(Long id, Instant validFrom, Instant validUntil,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Notification: id= " + id
                + ", validFrom=" + validFrom
                + ", validUntil=" + validUntil
                + ", title= " + title
                + ", message= " + message +"]";
    }
}
