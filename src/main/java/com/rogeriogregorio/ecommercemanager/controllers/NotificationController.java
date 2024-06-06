package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.findAllNotifications(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> postNotification(
            @Valid @RequestBody NotificationRequest notificationRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.createNotification(notificationRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.findNotificationById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<NotificationResponse> putNotification(@PathVariable Long id,
            @Valid @RequestBody NotificationRequest notificationRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.updateNotification(id, notificationRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {

        notificationService.deleteNotification(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
