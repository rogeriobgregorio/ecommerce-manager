package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.NotificationService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(NotificationServiceImpl.class);

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   CatchError catchError, DataMapper dataMapper) {

        this.notificationRepository = notificationRepository;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllNotifications(Pageable pageable) {

        return catchError.run(() -> notificationRepository.findAll(pageable))
                .map(notification -> dataMapper.map(notification, NotificationResponse.class));
    }

    @Transactional
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {

        validateNotificationDates(notificationRequest);
        Notification notification = dataMapper.map(notificationRequest, Notification.class);

        Notification savedNotification = catchError.run(() -> notificationRepository.save(notification));
        LOGGER.info("Notification created: {}", savedNotification);
        return dataMapper.map(savedNotification, NotificationResponse.class);
    }

    @Transactional(readOnly = true)
    public NotificationResponse findNotificationById(Long id) {

        return catchError.run(() -> notificationRepository.findById(id))
                .map(notification -> dataMapper.map(notification, NotificationResponse.class))
                .orElseThrow(() -> new NotFoundException("Notification not found with ID: " + id + "."));
    }

    @Transactional
    public NotificationResponse updateNotification(Long id, NotificationRequest notificationRequest) {

        validateNotificationDates(notificationRequest);
        Notification currentNotification = getNotificationIfExists(id);
        dataMapper.map(notificationRequest, currentNotification);

        Notification updatedNotification = catchError.run(() -> notificationRepository.save(currentNotification));
        LOGGER.info("Notification update: {}", updatedNotification);
        return dataMapper.map(updatedNotification, NotificationResponse.class);
    }

    @Transactional
    public void deleteNotification(Long id) {

        Notification notification = getNotificationIfExists(id);

        catchError.run(() -> notificationRepository.delete(notification));
        LOGGER.warn("Notification deleted: {}", notification);
    }

    private Notification getNotificationIfExists(Long id) {

        return catchError.run(() -> notificationRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("Notification not found with ID: " + id + "."));
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
