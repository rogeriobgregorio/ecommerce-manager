package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.NotificationService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ErrorHandlerTemplate errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   ErrorHandlerTemplate errorHandler, Converter converter) {

        this.notificationRepository = notificationRepository;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    public Page<NotificationResponse> findAllNotifications(Pageable pageable) {

        return errorHandler.catchException(() -> notificationRepository.findAll(pageable),
                        "Error while trying to fetch all notifications: ")
                .map(notification -> converter.toResponse(notification, NotificationResponse.class));
    }

    public NotificationResponse createNotification(NotificationRequest notificationRequest) {

        notificationRequest.setId(null);
        Notification notification = buildNotification(notificationRequest);

        errorHandler.catchException(() -> notificationRepository.save(notification),
                "Error while trying to create the notification: ");
        logger.info("Notification created: {}", notification);

        return converter.toResponse(notification, NotificationResponse.class);
    }

    public NotificationResponse findNotificationById(Long id) {

        return errorHandler.catchException(() -> notificationRepository.findById(id),
                        "Error while trying to find the notification by ID: ")
                .map(notification -> converter.toResponse(notification, NotificationResponse.class))
                .orElseThrow(() -> new NotFoundException("Notification not found with ID: " + id + "."));
    }

    public NotificationResponse updateNotification(NotificationRequest notificationRequest) {

        isNotificationExists(notificationRequest.getId());
        Notification notification = buildNotification(notificationRequest);

        errorHandler.catchException(() -> notificationRepository.save(notification),
                "Error while trying to update the notification: ");
        logger.info("Notification update: {}", notification);

        return converter.toResponse(notification, NotificationResponse.class);
    }

    public void deleteNotification(Long id) {

        isNotificationExists(id);

        errorHandler.catchException(() -> {
            notificationRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the notification: ");
        logger.warn("Notification removed with ID: {}", id);
    }

    private void isNotificationExists(Long id) {

        boolean isNotificationExists = errorHandler.catchException(() -> notificationRepository.existsById(id),
                "Error while trying to check the presence of the notification: ");

        if (!isNotificationExists) {
            throw new NotFoundException("Notification not found with ID: " + id + ".");
        }
    }

    private void validateNotificationDates(NotificationRequest notificationRequest) {

        Instant validFrom = notificationRequest.getValidFrom();
        Instant validUntil = notificationRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }

    private Notification buildNotification(NotificationRequest notificationRequest) {

        validateNotificationDates(notificationRequest);

        return converter.toEntity(notificationRequest, Notification.class);
    }
}
