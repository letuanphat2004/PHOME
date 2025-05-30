package com.example.Study.Model.DTO;

import com.example.Study.Common.RoomType;
import com.example.Study.entity.Room;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Builder

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomDTO {

    private long room_id;

    private long user_id;

    private String address;

    private long capacity;

    private double price;

    private String description;

    private String roomType;

    private double area;

    private String isApproval;

    private String image;

    public static RoomDTO toDto(Room room) {
        if (room == null) {
            return null;
        }
        var roomDto = RoomDTO.builder()
                .room_id(room.getId())
                .user_id(room.getUser_id())
                .address(room.getAddress())
                .capacity(room.getCapacity())
                .price(room.getPrice())
                .description(room.getDescription())
                .roomType(room.getRoomType().name())
                .area(room.getArea())
                .isApproval(room.getIsApproval())
                .image(room.getImage())
                .build();
        if (room.getRoomType().name().equals("CHUNG_CHU")) {
            roomDto.setRoomType("Chung chủ");
        } else {
            roomDto.setRoomType("Không chung chủ");
        }
        return roomDto;
    }

    public static Room toRoom(RoomDTO room) {
        if (room == null) {
            return null;
        }
        return Room.builder()
                .user_id(room.user_id)
                .address(room.address)
                .capacity(room.capacity)
                .price(room.price)
                .description(room.description)
                .roomType(RoomType.valueOf(room.roomType))
                .area(room.area)
                .isApproval(room.isApproval)
                .image(room.image)
                .build();
    }

    public static List<RoomDTO> toDto(List<Room> rooms) {
        return rooms.stream()
                .map(RoomDTO::toDto)
                .collect(Collectors.toList());
    }

    public static List<Room> toRoom(List<RoomDTO> roomDtos) {
        return roomDtos.stream()
                .map(RoomDTO::toRoom)
                .collect(Collectors.toList());
    }
}
