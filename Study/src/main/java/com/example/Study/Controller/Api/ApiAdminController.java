package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Service.RoomService;
import com.example.Study.Service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class ApiAdminController {
    private final RoomService rooms;
    private final UserService users;

    public ApiAdminController(RoomService rooms, UserService users) { this.rooms = rooms; this.users = users; }

    @GetMapping("/rooms")
    public List<RoomDTO> rooms() {
        return rooms.getAllRoomsForAdmin(PageRequest.of(0, 200)).getContent().stream().map(RoomDTO::toDto).toList();
    }

    @PatchMapping("/rooms/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable long id) { rooms.approveRoom(id); }

    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable long id) { rooms.disapproveRoom(id); }

    @GetMapping("/users")
    public List<UserDTO> users() { return users.getAllUserForAdmin(PageRequest.of(0, 200)).getContent(); }
}
