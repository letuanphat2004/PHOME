package com.example.Study.Service.Impl;

import com.example.Study.Model.DTO.RoomReviewDTO;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.RoomReviewRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.NotificationService;
import com.example.Study.Service.RoomReviewService;
import com.example.Study.entity.RoomReview;
import com.example.Study.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RoomReviewServiceImpl implements RoomReviewService {
    private final RoomReviewRepository reviews;
    private final RoomRepository rooms;
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final NotificationService notifications;

    public RoomReviewServiceImpl(RoomReviewRepository reviews, RoomRepository rooms, UserRepository users,
                                 AppointmentRepository appointments, NotificationService notifications) {
        this.reviews = reviews;
        this.rooms = rooms;
        this.users = users;
        this.appointments = appointments;
        this.notifications = notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public RoomReviewDTO.Page getReviews(long roomId, String username, boolean tenant, Pageable pageable) {
        requireApprovedRoom(roomId);
        User viewer = username == null ? null : users.findUserByUsername(username).orElse(null);
        var page = reviews.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        var mine = viewer == null ? null : reviews.findByRoomIdAndUserId(roomId, viewer.getId())
                .map(review -> toDto(review, viewer.getId())).orElse(null);
        boolean eligible = tenant && viewer != null
                && appointments.existsApprovedVisit(username, roomId);
        return new RoomReviewDTO.Page(page.getContent().stream()
                .map(review -> toDto(review, viewer == null ? null : viewer.getId())).toList(),
                page.getNumber(), page.getTotalPages(), page.getTotalElements(),
                roundRating(reviews.averageRating(roomId)), eligible, mine);
    }

    @Override
    @Transactional
    public RoomReviewDTO saveReview(long roomId, String username, int rating, String content) {
        var room = requireApprovedRoom(roomId);
        User user = requiredUser(username);
        if (!appointments.existsApprovedVisit(username, roomId)) {
            throw new AccessDeniedException("Bạn chỉ có thể đánh giá phòng sau khi lịch xem được xác nhận");
        }
        String reviewContent = content == null ? "" : content.trim();
        if (rating < 1 || rating > 5 || reviewContent.isEmpty() || reviewContent.length() > 1000) {
            throw new IllegalArgumentException("Đánh giá cần từ 1 đến 5 sao và nội dung từ 1 đến 1000 ký tự");
        }
        LocalDateTime now = LocalDateTime.now();
        RoomReview review = reviews.findByRoomIdAndUserId(roomId, user.getId()).orElse(null);
        boolean created = review == null;
        if (created) {
            review = RoomReview.builder().roomId(roomId).userId(user.getId()).createdAt(now).build();
        }
        review.setRating(rating);
        review.setContent(reviewContent);
        review.setUpdatedAt(now);
        review = reviews.save(review);
        if (created) {
            notifications.notifyUser(room.getUser_id(), "ROOM_REVIEWED", "Phòng có đánh giá mới",
                    user.getFullname() + " đã đánh giá " + rating + " sao cho phòng tại " + room.getAddress(),
                    "/my-rooms");
        }
        return toDto(review, user.getId());
    }

    @Override
    @Transactional
    public void deleteReview(long roomId, String username) {
        User user = requiredUser(username);
        RoomReview review = reviews.findByRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá của bạn"));
        reviews.delete(review);
    }

    private com.example.Study.entity.Room requireApprovedRoom(long roomId) {
        var room = rooms.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
        if (!"true".equals(room.getIsApproval())) {
            throw new IllegalArgumentException("Phòng chưa được phê duyệt");
        }
        return room;
    }

    private User requiredUser(String username) {
        return users.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản"));
    }

    private RoomReviewDTO toDto(RoomReview review, Long viewerId) {
        User author = users.findById(review.getUserId()).orElse(null);
        return new RoomReviewDTO(review.getId(), review.getRating(), review.getContent(),
                author == null ? "Người thuê PHOME" : author.getFullname(),
                author == null ? null : author.getLinkAvatar(), viewerId != null && viewerId.equals(review.getUserId()),
                review.getCreatedAt(), review.getUpdatedAt());
    }

    private double roundRating(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
