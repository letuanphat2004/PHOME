package com.example.Study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long reporterUserId;
    @Column(nullable = false, length = 20)
    private String targetType;
    @Column(nullable = false)
    private Long targetId;
    @Column(nullable = false)
    private Long roomId;
    @Column(nullable = false, length = 50)
    private String reason;
    @Column(nullable = false, length = 1000)
    private String details;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(length = 1000)
    private String resolutionNote;
    private Long resolvedBy;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
