package com.example.Study.Model.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record RoomReviewDTO(long id, int rating, String content, String author, String avatar,
                            boolean mine, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public record Page(List<RoomReviewDTO> content, int page, int totalPages, long totalElements,
                       double averageRating, boolean eligible, RoomReviewDTO mine) {}
}
