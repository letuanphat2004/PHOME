package com.example.Study.Model.DTO;

import com.example.Study.entity.AppNotification;

import java.time.LocalDateTime;

public record NotificationDTO(long id, String type, String title, String message, String link,
                              boolean read, LocalDateTime createdAt) {
    public static NotificationDTO from(AppNotification notification) {
        return new NotificationDTO(notification.getId(), notification.getType(), notification.getTitle(),
                notification.getMessage(), notification.getLink(), notification.getReadAt() != null,
                notification.getCreatedAt());
    }
}
