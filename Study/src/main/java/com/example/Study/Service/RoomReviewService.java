package com.example.Study.Service;

import com.example.Study.Model.DTO.RoomReviewDTO;
import org.springframework.data.domain.Pageable;

public interface RoomReviewService {
    RoomReviewDTO.Page getReviews(long roomId, String username, boolean tenant, Pageable pageable);
    RoomReviewDTO saveReview(long roomId, String username, int rating, String content);
    void deleteReview(long roomId, String username);
}
