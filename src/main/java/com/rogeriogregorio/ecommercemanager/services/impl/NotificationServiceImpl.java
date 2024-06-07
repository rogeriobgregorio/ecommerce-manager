package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.NotificationService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
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

        validateNotificationDates(notificationRequest);
        Notification notification = dataMapper.toEntity(notificationRequest, Notification.class);

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

        validateNotificationDates(notificationRequest);
        Notification currentNotification = getNotificationIfExists(id);
        Notification updatedNotification = dataMapper.copyTo(notificationRequest, currentNotification);

        errorHandler.catchException(() -> notificationRepository.save(updatedNotification),
                "Error while trying to update the notification: ");
        logger.info("Notification update: {}", updatedNotification);

        return dataMapper.toResponse(updatedNotification, NotificationResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteNotification(Long id) {

        Notification notification = getNotificationIfExists(id);

        errorHandler.catchException(() -> {
            notificationRepository.delete(notification);
            return null;
        }, "Error while trying to delete the notification: ");
        logger.warn("Notification deleted: {}", notification);
    }

    private Notification getNotificationIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!notificationRepository.existsById(id)) {
                throw new NotFoundException("Notification not exists with ID: " + id + ".");
            }

            return dataMapper.toEntity(notificationRepository.findById(id), Notification.class);
        }, "Error while trying to verify the existence of the notification by ID: ");
    }

    private void validateNotificationDates(NotificationRequest notificationRequest) {

        Instant validFrom = notificationRequest.getValidFrom();
        Instant validUntil = notificationRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }
}
