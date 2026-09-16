package com.example.Study.Service.Impl;

import com.example.Study.Respository.NotificationRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.AppNotification;
import com.example.Study.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {
    @Test
    void marksOwnedNotificationAsRead() {
        NotificationRepository notifications = mock(NotificationRepository.class);
        UserRepository users = mock(UserRepository.class);
        NotificationServiceImpl service = new NotificationServiceImpl(notifications, users);
        User user = User.builder().username("tenant").build();
        user.setId(4L);
        AppNotification notification = AppNotification.builder().userId(4L).build();
        notification.setId(10L);
        when(users.findUserByUsername("tenant")).thenReturn(Optional.of(user));
        when(notifications.findByIdAndUserId(10L, 4L)).thenReturn(Optional.of(notification));

        service.markRead("tenant", 10L);

        assertNotNull(notification.getReadAt());
        verify(notifications).save(notification);
    }

    @Test
    void cannotReadAnotherUsersNotification() {
        NotificationRepository notifications = mock(NotificationRepository.class);
        UserRepository users = mock(UserRepository.class);
        NotificationServiceImpl service = new NotificationServiceImpl(notifications, users);
        User user = User.builder().username("tenant").build();
        user.setId(4L);
        when(users.findUserByUsername("tenant")).thenReturn(Optional.of(user));
        when(notifications.findByIdAndUserId(10L, 4L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.markRead("tenant", 10L));

        verify(notifications, never()).save(org.mockito.ArgumentMatchers.any(AppNotification.class));
    }
}
