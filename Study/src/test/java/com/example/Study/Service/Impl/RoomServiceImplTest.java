package com.example.Study.Service.Impl;

import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.CommentRepository;
import com.example.Study.Respository.ImageRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.FileService;
import com.example.Study.entity.Image;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoomServiceImplTest {
    private RoomRepository rooms;
    private ImageRepository images;
    private UserRepository users;
    private RoomServiceImpl service;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        rooms = mock(RoomRepository.class);
        images = mock(ImageRepository.class);
        users = mock(UserRepository.class);
        service = new RoomServiceImpl(rooms, mock(FileService.class), images, users,
                mock(CommentRepository.class), mock(AppointmentRepository.class));
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("landlord");
    }

    @Test
    void ownerCannotReadAnotherLandlordsRoom() {
        Room room = room(10L, 2L);
        User landlord = user(1L);
        when(rooms.findById(10L)).thenReturn(Optional.of(room));
        when(users.findUserByUsername("landlord")).thenReturn(Optional.of(landlord));

        assertThrows(AccessDeniedException.class,
                () -> service.getOwnedRoom(10L, authentication));
    }

    @Test
    void ownerCannotDeleteImageThatIsNotAttachedToTheRoom() {
        Room room = room(10L, 1L);
        User landlord = user(1L);
        when(rooms.findById(10L)).thenReturn(Optional.of(room));
        when(users.findUserByUsername("landlord")).thenReturn(Optional.of(landlord));
        when(images.findAllImagesEntityByRoomId(10L))
                .thenReturn(List.of(Image.builder().id(20L).room_id(10L).url("https://example.test/room.jpg").build()));

        assertThrows(AccessDeniedException.class,
                () -> service.updateRoom(nullRoomDto(10L), authentication, List.of(), List.of(99L)));
    }

    private Room room(long id, long userId) {
        Room room = Room.builder().user_id(userId).build();
        room.setId(id);
        return room;
    }

    private User user(long id) {
        User user = User.builder().username("landlord").build();
        user.setId(id);
        return user;
    }

    private com.example.Study.Model.DTO.RoomDTO nullRoomDto(long id) {
        var dto = new com.example.Study.Model.DTO.RoomDTO();
        dto.setRoom_id(id);
        return dto;
    }
}
