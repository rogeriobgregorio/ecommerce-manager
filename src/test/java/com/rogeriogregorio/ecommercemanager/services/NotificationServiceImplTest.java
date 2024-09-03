package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.NotificationRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.NotificationResponse;
import com.rogeriogregorio.ecommercemanager.entities.Notification;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.NotificationRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.NotificationServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
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
import java.util.Optional;

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
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findAll(pageable));

        // Act
        Page<NotificationResponse> actualResponse = notificationService.findAllNotifications(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(notification, NotificationResponse.class);
        verify(notificationRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllNotifications - Exceção no repositório tentar buscar lista de notificações")
    void findAllNotifications_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(notificationRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> notificationService.findAllNotifications(pageable),
                "Expected RepositoryException to be thrown");
        verify(notificationRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createNotification - Criação bem-sucedida retorna notificação criada")
    void createNotification_SuccessfulCreation_ReturnsNotification() {
        // Arrange
        NotificationResponse expectedResponse = notificationResponse;

        when(dataMapper.map(notificationRequest, Notification.class)).thenReturn(notification);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.save(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(dataMapper.map(notification, NotificationResponse.class)).thenReturn(expectedResponse);

        // Act
        NotificationResponse actualResponse = notificationService.createNotification(notificationRequest);

        // Assert
        assertNotNull(actualResponse, "Notification should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(notificationRequest, Notification.class);
        verify(notificationRepository, times(1)).save(notification);
        verify(dataMapper, times(1)).map(notification, NotificationResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createNotification - Exceção no repositório ao tentar criar notificação")
    void createNotification_RepositoryExceptionHandling() {
        // Arrange
        when(dataMapper.map(notificationRequest, Notification.class)).thenReturn(notification);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.save(notification));
        when(notificationRepository.save(notification)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> notificationService.createNotification(notificationRequest),
                "Expected RepositoryException to be thrown");
        verify(dataMapper, times(1)).map(notificationRequest, Notification.class);
        verify(notificationRepository, times(1)).save(notification);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findNotificationById - Busca bem-sucedida retorna notificação")
    void findNotificationById_SuccessfulSearch_ReturnsNotification() {
        // Arrange
        NotificationResponse expectedResponse = notificationResponse;

        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(dataMapper.map(notification, NotificationResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findById(notification.getId()));

        // Act
        NotificationResponse actualResponse = notificationService.findNotificationById(notification.getId());

        // Assert
        assertNotNull(actualResponse, "Notification should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(dataMapper, times(1)).map(notification, NotificationResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findNotificationById - Exceção ao tentar buscar notificação inexistente")
    void findNotificationById_NotFoundExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findById(notification.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> notificationService.findNotificationById(notification.getId()),
                "Expected NotFoundException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findNotificationById - Exceção no repositório ao tentar buscar notificação")
    void findNotificationById_RepositoryExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> notificationRepository.findById(notification.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> notificationService.findNotificationById(notification.getId()),
                "Expected RepositoryException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateNotification - Atualização bem-sucedida retorna notificação atualizada")
    void updateNotification_SuccessfulUpdate_ReturnsNotification() {
        // Arrange
        NotificationResponse expectedResponse = notificationResponse;

        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(notificationRequest), any(Notification.class))).thenReturn(notification);
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(dataMapper.map(eq(notification), eq(NotificationResponse.class))).thenReturn(expectedResponse);

        // Act
        NotificationResponse actualResponse = notificationService.updateNotification(notification.getId(), notificationRequest);

        // Assert
        assertNotNull(actualResponse, "Notification should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(dataMapper, times(1)).map(eq(notificationRequest), any(Notification.class));
        verify(notificationRepository, times(1)).save(notification);
        verify(dataMapper, times(1)).map(eq(notification), eq(NotificationResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateNotification - Exceção ao tentar atualizar notificação inexistente")
    void updateNotification_NotFoundExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> notificationRepository.findById(notification.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> notificationService.updateNotification(notification.getId(), notificationRequest),
                "Expected NotFoundException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(notificationRepository, never()).save(notification);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateNotification - Exceção no repositório ao tentar atualizar notificação")
    void updateNotification_RepositoryExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(notificationRequest), any(Notification.class))).thenReturn(notification);
        when(notificationRepository.save(notification)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> notificationService.updateNotification(notification.getId(), notificationRequest),
                "Expected RepositoryException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(dataMapper, times(1)).map(eq(notificationRequest), any(Notification.class));
        verify(notificationRepository, times(1)).save(notification);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteNotification - Exclusão bem-sucedida da notificação")
    void deleteNotification_DeletesAddressSuccessfully() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> notificationRepository.findById(notification.getId()));
        doAnswer(invocation -> {
            notificationRepository.delete(notification);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(notificationRepository).delete(notification);

        // Act
        notificationService.deleteNotification(notification.getId());

        // Assert
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(notificationRepository, times(1)).delete(notification);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteNotification - Exceção ao tentar excluir notificação inexistente")
    void deleteNotification_NotFoundExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> notificationRepository.findById(notification.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> notificationService.deleteNotification(notification.getId()),
                "Expected NotFoundException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(notificationRepository, never()).delete(notification);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteNotification - Exceção no repositório ao tentar excluir notificação")
    void deleteNotification_RepositoryExceptionHandling() {
        // Arrange
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> notificationRepository.findById(notification.getId()));
        doAnswer(invocation -> {
            notificationRepository.delete(notification);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(notificationRepository).delete(notification);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> notificationService.deleteNotification(notification.getId()),
                "Expected RepositoryException to be thrown");
        verify(notificationRepository, times(1)).findById(notification.getId());
        verify(notificationRepository, times(1)).delete(notification);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}
