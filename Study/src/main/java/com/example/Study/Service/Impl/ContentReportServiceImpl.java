package com.example.Study.Service.Impl;

import com.example.Study.Model.DTO.ContentReportDTO;
import com.example.Study.Respository.ContentReportRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.RoomReviewRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.ContentReportService;
import com.example.Study.Service.NotificationService;
import com.example.Study.entity.ContentReport;
import com.example.Study.entity.Room;
import com.example.Study.entity.RoomReview;
import com.example.Study.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ContentReportServiceImpl implements ContentReportService {
    private static final Set<String> REASONS = Set.of("MISLEADING", "SCAM", "OFFENSIVE", "DUPLICATE", "OTHER");
    private final ContentReportRepository reports;
    private final RoomRepository rooms;
    private final RoomReviewRepository reviews;
    private final UserRepository users;
    private final NotificationService notifications;

    public ContentReportServiceImpl(ContentReportRepository reports, RoomRepository rooms,
                                    RoomReviewRepository reviews, UserRepository users,
                                    NotificationService notifications) {
        this.reports = reports;
        this.rooms = rooms;
        this.reviews = reviews;
        this.users = users;
        this.notifications = notifications;
    }

    @Override
    @Transactional
    public long create(String username, String targetType, long targetId, String reason, String details) {
        User reporter = requiredUser(username);
        String type = normalize(targetType);
        String category = normalize(reason);
        String explanation = details == null ? "" : details.trim();
        if (!Set.of("ROOM", "REVIEW").contains(type) || !REASONS.contains(category)) {
            throw new IllegalArgumentException("Loại nội dung hoặc lý do báo cáo không hợp lệ");
        }
        if (explanation.isEmpty() || explanation.length() > 1000) {
            throw new IllegalArgumentException("Mô tả báo cáo phải có từ 1 đến 1000 ký tự");
        }
        Target target = target(type, targetId);
        if (reporter.getId() == target.ownerUserId()) {
            throw new AccessDeniedException("Bạn không thể báo cáo nội dung của chính mình");
        }
        if (reports.existsByReporterUserIdAndTargetTypeAndTargetIdAndStatus(
                reporter.getId(), type, targetId, "PENDING")) {
            throw new IllegalArgumentException("Bạn đã gửi báo cáo cho nội dung này và đang chờ xử lý");
        }
        ContentReport report = reports.save(ContentReport.builder().reporterUserId(reporter.getId())
                .targetType(type).targetId(targetId).roomId(target.roomId()).reason(category)
                .details(explanation).status("PENDING").createdAt(LocalDateTime.now()).build());
        notifications.notifyRole(3L, "CONTENT_REPORTED", "Có báo cáo vi phạm mới",
                "Nội dung " + target.label() + " cần được kiểm tra.", "/admin");
        return report.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentReportDTO> getForAdmin(String status) {
        String value = normalize(status);
        if (!Set.of("PENDING", "RESOLVED", "DISMISSED").contains(value)) {
            throw new IllegalArgumentException("Trạng thái báo cáo không hợp lệ");
        }
        return reports.findByStatusOrderByCreatedAtAsc(value).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public void resolve(long reportId, String adminUsername, String action, String note) {
        ContentReport report = reports.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo cáo"));
        if (!"PENDING".equals(report.getStatus())) {
            throw new IllegalArgumentException("Báo cáo này đã được xử lý");
        }
        User admin = requiredUser(adminUsername);
        String decision = normalize(action);
        String resolution = note == null ? "" : note.trim();
        if (resolution.isEmpty() || resolution.length() > 1000) {
            throw new IllegalArgumentException("Kết luận xử lý phải có từ 1 đến 1000 ký tự");
        }
        if ("DISMISS".equals(decision)) {
            report.setStatus("DISMISSED");
        } else if ("HIDE_ROOM".equals(decision) && "ROOM".equals(report.getTargetType())) {
            Room room = rooms.findById(report.getRoomId()).orElseThrow();
            room.setIsApproval("rejected");
            room.setModerationNote(resolution);
            rooms.save(room);
            report.setStatus("RESOLVED");
            notifications.notifyUser(room.getUser_id(), "ROOM_HIDDEN", "Phòng đã bị ẩn sau kiểm duyệt",
                    resolution, "/my-rooms");
        } else if ("REMOVE_REVIEW".equals(decision) && "REVIEW".equals(report.getTargetType())) {
            Target target = target(report.getTargetType(), report.getTargetId());
            reviews.deleteById(report.getTargetId());
            report.setStatus("RESOLVED");
            notifications.notifyUser(target.ownerUserId(), "REVIEW_REMOVED", "Đánh giá đã bị gỡ",
                    resolution, "/rooms/" + report.getRoomId());
        } else {
            throw new IllegalArgumentException("Hành động xử lý không phù hợp với nội dung báo cáo");
        }
        report.setResolutionNote(resolution);
        report.setResolvedBy(admin.getId());
        report.setResolvedAt(LocalDateTime.now());
        reports.save(report);
        notifications.notifyUser(report.getReporterUserId(), "REPORT_RESOLVED", "Báo cáo đã được xử lý",
                resolution, "/rooms/" + report.getRoomId());
    }

    @Override
    public long countPending() {
        return reports.countByStatus("PENDING");
    }

    private ContentReportDTO toDto(ContentReport report) {
        User reporter = users.findById(report.getReporterUserId()).orElse(null);
        String label;
        try {
            label = target(report.getTargetType(), report.getTargetId()).label();
        } catch (RuntimeException exception) {
            label = "Nội dung đã bị xóa";
        }
        return new ContentReportDTO(report.getId(), report.getTargetType(), report.getTargetId(),
                report.getRoomId(), label, reporter == null ? "Tài khoản đã xóa" : reporter.getFullname(),
                report.getReason(), report.getDetails(), report.getStatus(), report.getResolutionNote(),
                report.getCreatedAt(), report.getResolvedAt());
    }

    private Target target(String type, long targetId) {
        if ("ROOM".equals(type)) {
            Room room = rooms.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
            return new Target(room.getId(), room.getUser_id(), "phòng tại " + room.getAddress());
        }
        RoomReview review = reviews.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
        return new Target(review.getRoomId(), review.getUserId(), "đánh giá #" + review.getId());
    }

    private User requiredUser(String username) {
        return users.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản"));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private record Target(long roomId, long ownerUserId, String label) {}
}
