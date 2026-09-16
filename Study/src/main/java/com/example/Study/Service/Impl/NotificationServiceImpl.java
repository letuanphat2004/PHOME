package com.example.Study.Service.Impl;

import com.example.Study.Model.DTO.NotificationDTO;
import com.example.Study.Respository.NotificationRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.NotificationService;
import com.example.Study.entity.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notifications;
    private final UserRepository users;

    public NotificationServiceImpl(NotificationRepository notifications, UserRepository users) {
        this.notifications = notifications;
        this.users = users;
    }

    @Override
    public Page<NotificationDTO> getNotifications(String username, boolean unreadOnly, Pageable pageable) {
        long userId = userId(username);
        var page = unreadOnly
                ? notifications.findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId, pageable)
                : notifications.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return page.map(NotificationDTO::from);
    }

    @Override
    public long getUnreadCount(String username) {
        return notifications.countByUserIdAndReadAtIsNull(userId(username));
    }

    @Override
    @Transactional
    public void markRead(String username, long notificationId) {
        AppNotification notification = notifications.findByIdAndUserId(notificationId, userId(username))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo"));
        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notifications.save(notification);
        }
    }

    @Override
    @Transactional
    public int markAllRead(String username) {
        return notifications.markAllRead(userId(username), LocalDateTime.now());
    }

    @Override
    @Transactional
    public void notifyUser(long userId, String type, String title, String message, String link) {
        notifications.save(AppNotification.builder().userId(userId).type(type).title(title)
                .message(message).link(link).createdAt(LocalDateTime.now()).build());
    }

    @Override
    @Transactional
    public void notifyRole(long roleId, String type, String title, String message, String link) {
        users.findActiveByRoleId(roleId).forEach(user ->
                notifyUser(user.getId(), type, title, message, link));
    }

    private long userId(String username) {
        return users.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản")).getId();
    }
}
