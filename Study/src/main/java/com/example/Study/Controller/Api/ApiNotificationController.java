package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.NotificationDTO;
import com.example.Study.Service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class ApiNotificationController {
    private final NotificationService notifications;

    public ApiNotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public Page<NotificationDTO> notifications(@RequestParam(defaultValue = "false") boolean unreadOnly,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               Authentication authentication) {
        return notifications.getNotifications(authentication.getName(), unreadOnly,
                PageRequest.of(Math.max(0, page), Math.clamp(size, 1, 50)));
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication authentication) {
        return Map.of("count", notifications.getUnreadCount(authentication.getName()));
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable long id, Authentication authentication) {
        notifications.markRead(authentication.getName(), id);
    }

    @PatchMapping("/read-all")
    public Map<String, Integer> markAllRead(Authentication authentication) {
        return Map.of("updated", notifications.markAllRead(authentication.getName()));
    }
}
