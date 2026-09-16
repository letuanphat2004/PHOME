package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.DTO.AdminDashboardDTO;
import com.example.Study.Service.AdminDashboardService;
import com.example.Study.Service.RoomService;
import com.example.Study.Service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class ApiAdminController {
    private final RoomService rooms;
    private final UserService users;
    private final AdminDashboardService dashboard;

    public ApiAdminController(RoomService rooms, UserService users, AdminDashboardService dashboard) {
        this.rooms = rooms;
        this.users = users;
        this.dashboard = dashboard;
    }

    @GetMapping("/dashboard")
    public AdminDashboardDTO dashboard() { return dashboard.getDashboard(); }

    @GetMapping("/rooms")
    public List<RoomDTO> rooms(@RequestParam(defaultValue = "pending") String status) {
        return rooms.getRoomsForAdmin(status, PageRequest.of(0, 200)).getContent().stream().map(RoomDTO::toDto).toList();
    }

    @PatchMapping("/rooms/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable long id) { rooms.approveRoom(id); }

    @PatchMapping("/rooms/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable long id, @RequestBody @Valid RejectRoomBody body) {
        rooms.rejectRoom(id, body.reason());
    }

    @GetMapping("/users")
    public List<UserDTO> users() { return users.getAllUserForAdmin(PageRequest.of(0, 200)).getContent(); }

    @PatchMapping("/users/{id}/enabled")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEnabled(@PathVariable long id, @RequestBody @Valid AccountStatusBody body,
                           Authentication authentication) {
        users.setAccountEnabled(id, body.enabled(), authentication.getName());
    }

    public record RejectRoomBody(@NotBlank @Size(max = 500) String reason) {}
    public record AccountStatusBody(boolean enabled) {}
}
