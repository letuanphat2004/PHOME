package com.example.Study.Service.Impl;

import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.RoomReviewRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.NotificationService;
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

class RoomReviewServiceImplTest {
    @Test
    void rejectsReviewWithoutApprovedVisit() {
        Fixture fixture = new Fixture();
        fixture.approvedRoomAndTenant();
        when(fixture.appointments.existsApprovedVisit("tenant", 8L)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> fixture.service.saveReview(8L, "tenant", 5, "Phòng sạch và yên tĩnh"));

        verify(fixture.reviews, never()).save(any(RoomReview.class));
    }

    @Test
    void createsReviewAndNotifiesLandlord() {
        Fixture fixture = new Fixture();
        fixture.approvedRoomAndTenant();
        when(fixture.appointments.existsApprovedVisit("tenant", 8L)).thenReturn(true);
        when(fixture.reviews.findByRoomIdAndUserId(8L, 4L)).thenReturn(Optional.empty());
        when(fixture.reviews.save(any(RoomReview.class))).thenAnswer(invocation -> {
            RoomReview review = invocation.getArgument(0);
            review.setId(12L);
            return review;
        });

        fixture.service.saveReview(8L, "tenant", 5, "  Phòng sạch và yên tĩnh  ");

        ArgumentCaptor<RoomReview> saved = ArgumentCaptor.forClass(RoomReview.class);
        verify(fixture.reviews).save(saved.capture());
        assertEquals("Phòng sạch và yên tĩnh", saved.getValue().getContent());
        verify(fixture.notifications).notifyUser(3L, "ROOM_REVIEWED", "Phòng có đánh giá mới",
                "Nguyễn An đã đánh giá 5 sao cho phòng tại Quận 3", "/my-rooms");
    }

    @Test
    void deletesOnlyCurrentTenantsReview() {
        Fixture fixture = new Fixture();
        fixture.approvedRoomAndTenant();
        RoomReview review = RoomReview.builder().roomId(8L).userId(4L).build();
        when(fixture.reviews.findByRoomIdAndUserId(8L, 4L)).thenReturn(Optional.of(review));

        fixture.service.deleteReview(8L, "tenant");

        verify(fixture.reviews).delete(review);
    }

    private static class Fixture {
        final RoomReviewRepository reviews = mock(RoomReviewRepository.class);
        final RoomRepository rooms = mock(RoomRepository.class);
        final UserRepository users = mock(UserRepository.class);
        final AppointmentRepository appointments = mock(AppointmentRepository.class);
        final NotificationService notifications = mock(NotificationService.class);
        final RoomReviewServiceImpl service = new RoomReviewServiceImpl(
                reviews, rooms, users, appointments, notifications);

        void approvedRoomAndTenant() {
            Room room = Room.builder().user_id(3L).address("Quận 3").isApproval("true").build();
            room.setId(8L);
            User tenant = User.builder().username("tenant").fullname("Nguyễn An").build();
            tenant.setId(4L);
            when(rooms.findById(8L)).thenReturn(Optional.of(room));
            when(users.findUserByUsername("tenant")).thenReturn(Optional.of(tenant));
            when(users.findById(4L)).thenReturn(Optional.of(tenant));
        }
    }
}
