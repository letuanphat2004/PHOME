package com.example.Study.Service;

import com.example.Study.Model.DTO.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationDTO> getNotifications(String username, boolean unreadOnly, Pageable pageable);
    long getUnreadCount(String username);
    void markRead(String username, long notificationId);
    int markAllRead(String username);
    void notifyUser(long userId, String type, String title, String message, String link);
    void notifyRole(long roleId, String type, String title, String message, String link);
}
