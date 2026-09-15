package com.example.Study.Service.Impl;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Respository.FavoriteRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.FavoriteService;
import com.example.Study.entity.Favorite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favorites;
    private final UserRepository users;
    private final RoomRepository rooms;

    public FavoriteServiceImpl(FavoriteRepository favorites, UserRepository users, RoomRepository rooms) {
        this.favorites = favorites;
        this.users = users;
        this.rooms = rooms;
    }

    @Override
    public List<RoomDTO> getFavorites(String username) {
        return favorites.findFavoriteRooms(userId(username)).stream().map(RoomDTO::toDto).toList();
    }

    @Override
    public boolean isFavorite(String username, long roomId) {
        return favorites.findByUserIdAndRoomId(userId(username), roomId).isPresent();
    }

    @Override
    @Transactional
    public void addFavorite(String username, long roomId) {
        long userId = userId(username);
        var room = rooms.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));
        if (!"true".equals(room.getIsApproval())) {
            throw new IllegalArgumentException("Phòng này hiện không còn hiển thị");
        }
        if (favorites.findByUserIdAndRoomId(userId, roomId).isEmpty()) {
            favorites.save(Favorite.builder().userId(userId).roomId(roomId).createdAt(LocalDateTime.now()).build());
        }
    }

    @Override
    @Transactional
    public void removeFavorite(String username, long roomId) {
        long userId = userId(username);
        favorites.findByUserIdAndRoomId(userId, roomId).ifPresent(favorites::delete);
    }

    private long userId(String username) {
        return users.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản")).getId();
    }
}
