package com.example.Study.Respository;

import com.example.Study.entity.RoomReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomReviewRepository extends JpaRepository<RoomReview, Long> {
    Page<RoomReview> findByRoomIdOrderByCreatedAtDesc(long roomId, Pageable pageable);
    Optional<RoomReview> findByRoomIdAndUserId(long roomId, long userId);
    long countByRoomId(long roomId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM RoomReview r WHERE r.roomId = :roomId")
    double averageRating(@Param("roomId") long roomId);
}
