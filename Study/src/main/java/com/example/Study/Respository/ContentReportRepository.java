package com.example.Study.Respository;

import com.example.Study.entity.ContentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {
    List<ContentReport> findByStatusOrderByCreatedAtAsc(String status);
    boolean existsByReporterUserIdAndTargetTypeAndTargetIdAndStatus(
            long reporterUserId, String targetType, long targetId, String status);
    long countByStatus(String status);
}
