package com.example.Study.Service;

import com.example.Study.Model.DTO.ContentReportDTO;

import java.util.List;

public interface ContentReportService {
    long create(String username, String targetType, long targetId, String reason, String details);
    List<ContentReportDTO> getForAdmin(String status);
    void resolve(long reportId, String adminUsername, String action, String note);
    long countPending();
}
