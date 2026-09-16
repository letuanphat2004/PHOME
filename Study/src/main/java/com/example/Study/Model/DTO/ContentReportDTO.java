package com.example.Study.Model.DTO;

import java.time.LocalDateTime;

public record ContentReportDTO(long id, String targetType, long targetId, long roomId, String targetLabel,
                               String reporter, String reason, String details, String status,
                               String resolutionNote, LocalDateTime createdAt, LocalDateTime resolvedAt) {}
