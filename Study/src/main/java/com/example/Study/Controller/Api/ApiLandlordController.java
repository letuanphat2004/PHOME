package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.ImageDTO;
import com.example.Study.Service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landlord/rooms")
@PreAuthorize("hasAuthority('Landlord')")
public class ApiLandlordController {
    private final RoomService rooms;

    public ApiLandlordController(RoomService rooms) { this.rooms = rooms; }

    @GetMapping
    public List<RoomDTO> rooms(@RequestParam(defaultValue = "true") String approved,
                               Authentication authentication) {
        return rooms.getRoomsByUser(approved, authentication.getName(), PageRequest.of(0, 100))
                .getContent().stream().map(RoomDTO::toDto).toList();
    }

    @GetMapping("/{id}")
    public RoomEditor room(@PathVariable long id, Authentication authentication) {
        return new RoomEditor(rooms.getOwnedRoom(id, authentication),
                rooms.getOwnedRoomImages(id, authentication));
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @ModelAttribute RoomDTO room, @RequestParam List<MultipartFile> images,
                       Authentication authentication) {
        rooms.addRoom(room, images, authentication);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @Valid @ModelAttribute RoomDTO room,
                       @RequestParam(required = false) List<MultipartFile> images,
                       @RequestParam(required = false) List<Long> imageIdsDel,
                       Authentication authentication) {
        room.setRoom_id(id);
        rooms.updateRoom(room, authentication, images, imageIdsDel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, Authentication authentication) {
        rooms.deleteRoomByRoomId(id, authentication);
    }

    public record RoomEditor(RoomDTO room, List<ImageDTO> images) {}
}
