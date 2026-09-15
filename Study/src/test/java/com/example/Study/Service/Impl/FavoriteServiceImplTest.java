package com.example.Study.Service.Impl;

import com.example.Study.Respository.FavoriteRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.Favorite;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FavoriteServiceImplTest {
    private FavoriteRepository favorites;
    private RoomRepository rooms;
    private UserRepository users;
    private FavoriteServiceImpl service;

    @BeforeEach
    void setUp() {
        favorites = mock(FavoriteRepository.class);
        rooms = mock(RoomRepository.class);
        users = mock(UserRepository.class);
        service = new FavoriteServiceImpl(favorites, users, rooms);
        User tenant = User.builder().username("tenant").build();
        tenant.setId(7L);
        when(users.findUserByUsername("tenant")).thenReturn(Optional.of(tenant));
    }

    @Test
    void cannotFavoriteRoomThatIsNotApproved() {
        Room room = Room.builder().isApproval("false").build();
        room.setId(3L);
        when(rooms.findById(3L)).thenReturn(Optional.of(room));

        assertThrows(IllegalArgumentException.class, () -> service.addFavorite("tenant", 3L));
        verify(favorites, never()).save(any(Favorite.class));
    }

    @Test
    void addingExistingFavoriteIsIdempotent() {
        Room room = Room.builder().isApproval("true").build();
        room.setId(3L);
        when(rooms.findById(3L)).thenReturn(Optional.of(room));
        when(favorites.findByUserIdAndRoomId(7L, 3L)).thenReturn(Optional.of(Favorite.builder().build()));

        service.addFavorite("tenant", 3L);

        verify(favorites, never()).save(any(Favorite.class));
    }
}
