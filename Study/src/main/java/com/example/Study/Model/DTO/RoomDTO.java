package com.example.Study.Model.DTO;

import com.example.Study.Common.RoomType;
import com.example.Study.entity.Room;
import lombok.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private long room_id;
    private long user_id;
    @NotBlank
    @Size(max = 255)
    private String address;
    @Min(1)
    private long capacity;
    @DecimalMin("1.0")
    private double price;
    @NotBlank
    @Size(max = 5000)
    private String description;
    @NotBlank
    @Pattern(regexp = "^(CHUNG_CHU|KHONG_CHUNG_CHU)$")
    private String roomType;
    @DecimalMin("1.0")
    private double area;
    private String isApproval;
    private String image;

    public static RoomDTO toDto(Room room) {
        if (room == null) return null;
        return RoomDTO.builder()
                .room_id(room.getId()).user_id(room.getUser_id()).address(room.getAddress())
                .capacity(room.getCapacity()).price(room.getPrice()).description(room.getDescription())
                .roomType(room.getRoomType() == RoomType.CHUNG_CHU ? "Chung chủ" : "Không chung chủ")
                .area(room.getArea()).isApproval(room.getIsApproval()).image(room.getImage()).build();
    }

    public static Room toRoom(RoomDTO room) {
        if (room == null) return null;
        return Room.builder().user_id(room.user_id).address(room.address).capacity(room.capacity)
                .price(room.price).description(room.description).roomType(parseRoomType(room.roomType))
                .area(room.area).isApproval(room.isApproval).image(room.image).build();
    }

    private static RoomType parseRoomType(String value) {
        if ("CHUNG_CHU".equals(value) || "Chung chủ".equalsIgnoreCase(value)) return RoomType.CHUNG_CHU;
        return RoomType.KHONG_CHUNG_CHU;
    }

    public static List<RoomDTO> toDto(List<Room> rooms) { return rooms.stream().map(RoomDTO::toDto).toList(); }
    public static List<Room> toRoom(List<RoomDTO> rooms) { return rooms.stream().map(RoomDTO::toRoom).toList(); }
}
