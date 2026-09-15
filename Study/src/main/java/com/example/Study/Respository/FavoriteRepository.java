package com.example.Study.Respository;

import com.example.Study.entity.Favorite;
import com.example.Study.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndRoomId(Long userId, Long roomId);

    @Query("SELECT r FROM Room r, Favorite f WHERE f.roomId = r.id " +
            "AND f.userId = :userId AND r.isApproval = 'true' ORDER BY f.createdAt DESC")
    List<Room> findFavoriteRooms(@Param("userId") Long userId);
}
