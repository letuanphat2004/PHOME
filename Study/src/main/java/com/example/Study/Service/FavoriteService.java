package com.example.Study.Service;

import com.example.Study.Model.DTO.RoomDTO;

import java.util.List;

public interface FavoriteService {
    List<RoomDTO> getFavorites(String username);
    boolean isFavorite(String username, long roomId);
    void addFavorite(String username, long roomId);
    void removeFavorite(String username, long roomId);
}
