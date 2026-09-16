package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.RoomReviewDTO;
import com.example.Study.Service.RoomReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/reviews")
public class ApiRoomReviewController {
    private final RoomReviewService reviews;

    public ApiRoomReviewController(RoomReviewService reviews) {
        this.reviews = reviews;
    }

    @GetMapping
    public RoomReviewDTO.Page reviews(@PathVariable long roomId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      Authentication authentication) {
        boolean tenant = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "Tenant".equals(authority.getAuthority()));
        return reviews.getReviews(roomId, authentication == null ? null : authentication.getName(), tenant,
                PageRequest.of(Math.max(0, page), Math.clamp(size, 1, 20)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Tenant')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomReviewDTO create(@PathVariable long roomId, @Valid @RequestBody ReviewBody body,
                                Authentication authentication) {
        return reviews.saveReview(roomId, authentication.getName(), body.rating(), body.content());
    }

    @PutMapping("/mine")
    @PreAuthorize("hasAuthority('Tenant')")
    public RoomReviewDTO update(@PathVariable long roomId, @Valid @RequestBody ReviewBody body,
                                Authentication authentication) {
        return reviews.saveReview(roomId, authentication.getName(), body.rating(), body.content());
    }

    @DeleteMapping("/mine")
    @PreAuthorize("hasAuthority('Tenant')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long roomId, Authentication authentication) {
        reviews.deleteReview(roomId, authentication.getName());
    }

    public record ReviewBody(@Min(1) @Max(5) int rating, @NotBlank @Size(max = 1000) String content) {}
}
