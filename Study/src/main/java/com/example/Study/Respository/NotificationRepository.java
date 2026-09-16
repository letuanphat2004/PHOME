package com.example.Study.Respository;

import com.example.Study.entity.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<AppNotification, Long> {
    Page<AppNotification> findByUserIdOrderByCreatedAtDesc(long userId, Pageable pageable);
    Page<AppNotification> findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(long userId, Pageable pageable);
    long countByUserIdAndReadAtIsNull(long userId);
    Optional<AppNotification> findByIdAndUserId(long id, long userId);

    @Modifying
    @Query("UPDATE AppNotification n SET n.readAt = :readAt WHERE n.userId = :userId AND n.readAt IS NULL")
    int markAllRead(@Param("userId") long userId, @Param("readAt") LocalDateTime readAt);
}
