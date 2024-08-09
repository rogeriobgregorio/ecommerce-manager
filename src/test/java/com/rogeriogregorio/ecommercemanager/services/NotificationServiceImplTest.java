package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.AddressServiceImpl;
import com.rogeriogregorio.ecommercemanager.services.impl.NotificationServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static Notification notification;
    private static NotificationRequest notificationRequest;
    private static NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {

        notification = new Notification(1L, Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"), "Title", "Message");

        notificationRequest = new NotificationRequest(Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"), "Title", "Message");

        notificationResponse = new NotificationResponse(1L, Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"), "Title", "Message");

        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl(notificationRepository, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllNotifications - Busca bem-sucedida retorna lista de notificações")
    void findAllNotifications_SuccessfulSearch_ReturnsNotificationsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> notificationList = Collections.singletonList(notification);
        List<NotificationResponse> expectedResponses = Collections.singletonList(notificationResponse);
        PageImpl<Notification> page = new PageImpl<>(notificationList, pageable, notificationList.size());

        when(dataMapper.map(notification, NotificationResponse.class)).thenReturn(notificationResponse);
        when(notificationRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findAll(pageable));

        // Act
        Page<NotificationResponse> actualResponse = notificationService.findAllNotifications(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(notification, NotificationResponse.class);
        verify(notificationRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findAllNotifications - Exceção no repositório tentar buscar lista de notificações")
    void findAllNotifications_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(notificationRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> notificationService.findAllNotifications(pageable),
                "Expected RepositoryException to be thrown");
        verify(notificationRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }


}
