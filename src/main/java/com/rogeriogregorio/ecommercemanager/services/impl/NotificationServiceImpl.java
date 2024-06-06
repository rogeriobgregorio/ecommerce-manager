package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.NotificationService;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(NotificationServiceImpl.class);

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   ErrorHandler errorHandler, DataMapper dataMapper) {

        this.notificationRepository = notificationRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllNotifications(Pageable pageable) {

        return errorHandler.catchException(() -> notificationRepository.findAll(pageable),
                        "Error while trying to fetch all notifications: ")
                .map(notification -> dataMapper.toResponse(notification, NotificationResponse.class));
    }

    @Transactional(readOnly = false)
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {

        Notification notification = validateNotificationDates(notificationRequest);

        errorHandler.catchException(() -> notificationRepository.save(notification),
                "Error while trying to create the notification: ");
        logger.info("Notification created: {}", notification);

        return dataMapper.toResponse(notification, NotificationResponse.class);
    }

    @Transactional(readOnly = true)
    public NotificationResponse findNotificationById(Long id) {

        return errorHandler.catchException(() -> notificationRepository.findById(id),
                        "Error while trying to find the notification by ID: ")
                .map(notification -> dataMapper.toResponse(notification, NotificationResponse.class))
                .orElseThrow(() -> new NotFoundException("Notification not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public NotificationResponse updateNotification(Long id, NotificationRequest notificationRequest) {

        verifyNotificationExists(id);
        Notification notification = validateNotificationDates(notificationRequest);

        errorHandler.catchException(() -> notificationRepository.save(notification),
                "Error while trying to update the notification: ");
        logger.info("Notification update: {}", notification);

        return dataMapper.toResponse(notification, NotificationResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteNotification(Long id) {

        verifyNotificationExists(id);

        errorHandler.catchException(() -> {
            notificationRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the notification: ");
        logger.warn("Notification removed with ID: {}", id);
    }

    private void verifyNotificationExists(Long id) {

        boolean isNotificationExists = errorHandler.catchException(() -> notificationRepository.existsById(id),
                "Error while trying to check the presence of the notification: ");

        if (!isNotificationExists) {
            throw new NotFoundException("Notification not found with ID: " + id + ".");
        }
    }

    private Notification validateNotificationDates(NotificationRequest notificationRequest) {

        Instant validFrom = notificationRequest.getValidFrom();
        Instant validUntil = notificationRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }

        return dataMapper.toEntity(notificationRequest, Notification.class);
    }
}
