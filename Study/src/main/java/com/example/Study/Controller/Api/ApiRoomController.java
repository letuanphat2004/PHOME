package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.CommentDTO;
import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.Room.RoomFilterDataRequest;
import com.example.Study.Respository.CommentRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.CommentService;
import com.example.Study.Service.RoomService;
import com.example.Study.Service.UserService;
import com.example.Study.entity.Comment;
import com.example.Study.entity.Room;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class ApiRoomController {
    private final RoomService roomService;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ApiRoomController(RoomService roomService, UserService userService, CommentService commentService,
                             CommentRepository commentRepository, UserRepository userRepository) {
        this.roomService = roomService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public PageResponse<RoomDTO> rooms(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "8") int size,
                                       @RequestParam(defaultValue = "") String price,
                                       @RequestParam(defaultValue = "") String area,
                                       @RequestParam(defaultValue = "") String address,
                                       @RequestParam(defaultValue = "") String roomType) {
        RoomFilterDataRequest filter = new RoomFilterDataRequest();
        filter.setPrice(price); filter.setArea(area); filter.setAddress(address); filter.setRoomType(roomType);
        Page<Room> result = roomService.getAllRoomByManyContrains(filter, PageRequest.of(page, Math.min(size, 50)));
        return new PageResponse<>(result.getContent().stream().map(RoomDTO::toDto).toList(),
                result.getNumber(), result.getTotalPages(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public RoomDetails room(@PathVariable long id) {
        RoomDTO room = roomService.getInforRoomByRoom_Id(Long.toString(id));
        if (room == null) throw new IllegalArgumentException("Room not found");
        UserDTO owner = userService.getUserById(room.getUser_id());
        return new RoomDetails(room, roomService.GetAllImageByRoom_Id(Long.toString(id)),
                commentService.getAllCommentsByRoom_id(id),
                new Owner(owner.getFullname(), owner.getTel(), owner.getLinkAvatar()));
    }

    @PostMapping("/{id}/comments")
    public CommentDTO comment(@PathVariable long id, @Valid @RequestBody CommentBody body,
                              Authentication authentication) {
        var user = userRepository.findUserByUsername(authentication.getName()).orElseThrow();
        Comment saved = commentRepository.save(Comment.builder().username(authentication.getName())
                .avatar(user.getLinkAvatar()).content(body.content().trim()).commentTime(LocalDateTime.now())
                .room_id(id).build());
        return CommentDTO.toDto(saved);
    }

    public record PageResponse<T>(List<T> content, int page, int totalPages, long totalElements) {}
    public record RoomDetails(RoomDTO room, List<String> images, List<CommentDTO> comments, Owner owner) {}
    public record Owner(String fullname, String tel, String avatar) {}
    public record CommentBody(@NotBlank String content) {}
}
