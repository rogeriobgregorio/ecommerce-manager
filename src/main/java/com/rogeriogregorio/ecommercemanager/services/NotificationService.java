package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface NotificationService {

    Page<NotificationResponse> findAllNotifications(Pageable pageable);

    NotificationResponse createNotification(NotificationRequest notificationRequest);

    NotificationResponse findNotificationById(Long id);

    NotificationResponse updateNotification(Long id, NotificationRequest notificationRequest);

    void deleteNotification(Long id);
}
