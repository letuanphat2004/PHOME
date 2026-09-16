package com.example.Study.Service.Impl;

import com.example.Study.Respository.ContentReportRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.RoomReviewRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.NotificationService;
import com.example.Study.entity.ContentReport;
import com.example.Study.entity.Room;
import com.example.Study.entity.RoomReview;
import com.example.Study.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentReportServiceImplTest {
    @Test
    void createsPendingRoomReportAndNotifiesAdmins() {
        Fixture fixture = new Fixture();
        fixture.usersAndRoom();
        when(fixture.reports.save(any(ContentReport.class))).thenAnswer(invocation -> {
            ContentReport report = invocation.getArgument(0);
            report.setId(30L);
            return report;
        });

        long id = fixture.service.create("tenant", "room", 8L, "scam", "Yêu cầu chuyển tiền trước");

        assertEquals(30L, id);
        ArgumentCaptor<ContentReport> saved = ArgumentCaptor.forClass(ContentReport.class);
        verify(fixture.reports).save(saved.capture());
        assertEquals("PENDING", saved.getValue().getStatus());
        assertEquals("SCAM", saved.getValue().getReason());
        verify(fixture.notifications).notifyRole(3L, "CONTENT_REPORTED", "Có báo cáo vi phạm mới",
                "Nội dung phòng tại Quận 3 cần được kiểm tra.", "/admin");
    }

    @Test
    void ownerCannotReportOwnRoom() {
        Fixture fixture = new Fixture();
        fixture.usersAndRoom();

        assertThrows(AccessDeniedException.class,
                () -> fixture.service.create("landlord", "ROOM", 8L, "OTHER", "Nội dung thử"));

        verify(fixture.reports, never()).save(any(ContentReport.class));
    }

    @Test
    void removesReportedReviewAndNotifiesBothSides() {
        Fixture fixture = new Fixture();
        fixture.usersAndRoom();
        ContentReport report = ContentReport.builder().reporterUserId(4L).targetType("REVIEW")
                .targetId(12L).roomId(8L).status("PENDING").build();
        report.setId(30L);
        RoomReview review = RoomReview.builder().roomId(8L).userId(5L).build();
        review.setId(12L);
        when(fixture.reports.findById(30L)).thenReturn(Optional.of(report));
        when(fixture.reviews.findById(12L)).thenReturn(Optional.of(review));

        fixture.service.resolve(30L, "admin", "REMOVE_REVIEW", "Đánh giá vi phạm quy tắc cộng đồng");

        verify(fixture.reviews).deleteById(12L);
        assertEquals("RESOLVED", report.getStatus());
        verify(fixture.notifications).notifyUser(5L, "REVIEW_REMOVED", "Đánh giá đã bị gỡ",
                "Đánh giá vi phạm quy tắc cộng đồng", "/rooms/8");
        verify(fixture.notifications).notifyUser(4L, "REPORT_RESOLVED", "Báo cáo đã được xử lý",
                "Đánh giá vi phạm quy tắc cộng đồng", "/rooms/8");
    }

    private static class Fixture {
        final ContentReportRepository reports = mock(ContentReportRepository.class);
        final RoomRepository rooms = mock(RoomRepository.class);
        final RoomReviewRepository reviews = mock(RoomReviewRepository.class);
        final UserRepository users = mock(UserRepository.class);
        final NotificationService notifications = mock(NotificationService.class);
        final ContentReportServiceImpl service = new ContentReportServiceImpl(
                reports, rooms, reviews, users, notifications);

        void usersAndRoom() {
            User tenant = User.builder().username("tenant").build();
            tenant.setId(4L);
            User landlord = User.builder().username("landlord").build();
            landlord.setId(3L);
            User admin = User.builder().username("admin").build();
            admin.setId(6L);
            Room room = Room.builder().user_id(3L).address("Quận 3").isApproval("true").build();
            room.setId(8L);
            when(users.findUserByUsername("tenant")).thenReturn(Optional.of(tenant));
            when(users.findUserByUsername("landlord")).thenReturn(Optional.of(landlord));
            when(users.findUserByUsername("admin")).thenReturn(Optional.of(admin));
            when(rooms.findById(8L)).thenReturn(Optional.of(room));
        }
    }
}
