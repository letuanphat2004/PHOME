package com.example.Study.Respository;

import com.example.Study.Common.RoomType;
import com.example.Study.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findAllByIsApproval(String isApproval, Pageable pageable);

    long countByIsApproval(String isApproval);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.user_id = :userId AND r.isApproval = :isApproval")
    long countByUserIdAndIsApproval(@Param("userId") long userId,
                                    @Param("isApproval") String isApproval);

    //    @Modifying -- Có cái này để DB biết file này để update|delete (Kh có mặc định là đọc)
    @Query("SELECT r FROM Room AS r WHERE " +
            "(r.isApproval = 'true')" +
            "    AND (:price = '' OR " +
            "        (:price = '1' AND r.price < 1) OR" +
            "        (:price = '1-2' AND r.price >= 1 AND r.price <= 2) OR" +
            "        (:price = '2-3' AND r.price >= 2 AND r.price <= 3) OR" +
            "        (:price = '3' AND r.price > 3)" +
            "    ) " +
            "    AND (:address = '' OR r.address LIKE CONCAT('%', :address, '%'))" +
            "    AND (:area = '' OR " +
            "        (:area = '20' AND r.area < 20) OR" +
            "        (:area = '20-30' AND r.area >= 20 AND r.area <= 30) OR" +
            "        (:area = '30-40' AND r.area >= 30 AND r.area <= 40) OR" +
            "        (:area = '40' AND r.area > 40)" +
            "    ) " +
            "    AND (:roomType IS NULL OR r.roomType = :roomType)")
    Page<Room> findAllByFilterConstraints(@Param("price") String price, @Param("address") String address, @Param("area") String area, @Param("roomType") RoomType roomType, Pageable pageable);

    @Query("SELECT r FROM Room AS r WHERE " +
            "(r.isApproval = 'true')" +
            "    AND (:price = '' OR " +
            "        (:price = '1' AND r.price < 1) OR" +
            "        (:price = '1-2' AND r.price >= 1 AND r.price <= 2) OR" +
            "        (:price = '2-3' AND r.price >= 2 AND r.price <= 3) OR" +
            "        (:price = '3' AND r.price > 3)" +
            "    ) " +
            "    AND (:address = '' OR r.address LIKE CONCAT('%', :address, '%'))" +
            "    AND (:area = '' OR " +
            "        (:area = '20' AND r.area < 20) OR" +
            "        (:area = '20-30' AND r.area >= 20 AND r.area <= 30) OR" +
            "        (:area = '30-40' AND r.area >= 30 AND r.area <= 40) OR" +
            "        (:area = '40' AND r.area > 40)" +
            "    ) " +
            "    AND (:roomType IS NULL OR (:roomType IS NOT NULL AND r.roomType = :roomType))")
    List<Room> findAllByFilterConstraintsWithoutPagination(@Param("price") String price, @Param("address") String address, @Param("area") String area, @Param("roomType") RoomType roomType);

    @Query("SELECT r FROM Room r WHERE r.user_id = :userId")
    List<Room> findAllByUserid(@Param("userId") Long userId);

    @Query("SELECT r FROM Room r WHERE r.user_id = :userId and r.isApproval = :isApproval ")
    Page<Room> getAllByUserId(@Param("isApproval") String isApproval, @Param("userId") Long userId, Pageable pageable);


    @Query("SELECT r FROM Room AS r WHERE " +
            "(:price = '' OR " +
            "    (:price = '1' AND r.price < 1) OR" +
            "    (:price = '1-2' AND r.price >= 1 AND r.price <= 2) OR" +
            "    (:price = '2-3' AND r.price >= 2 AND r.price <= 3) OR" +
            "    (:price = '3' AND r.price > 3)" +
            ") " +
            "AND (:address = '' OR r.address LIKE CONCAT('%', :address, '%')) " +
            "AND (:area = '' OR " +
            "    (:area = '20' AND r.area < 20) OR" +
            "    (:area = '20-30' AND r.area >= 20 AND r.area <= 30) OR" +
            "    (:area = '30-40' AND r.area >= 30 AND r.area <= 40) OR" +
            "    (:area = '40' AND r.area > 40)" +
            ") " +
            "AND (:roomType IS NULL OR r.roomType = :roomType)")
    Page<Room> findAllByFilterConstraintsWithoutApproval(@Param("price") String price, @Param("address") String address, @Param("area") String area, @Param("roomType") RoomType roomType, Pageable pageable);
}
