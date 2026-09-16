package com.example.Study.Service;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.ImageDTO;
import com.example.Study.Model.Request.Room.RoomFilterDataRequest;
import com.example.Study.entity.Room;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {
    Page<Room> getAllRoomByManyContrains(RoomFilterDataRequest request, Pageable pageable);
    RoomDTO getInforRoomByRoom_Id(String room_id);
    List<String> GetAllImageByRoom_Id(String room_id);
    void addRoom(RoomDTO roomDto, List<MultipartFile> images, Authentication auth);
    List<RoomDTO> getAllRoomByUser(String username);
    void deleteRoomByRoomId(Long room_id, Authentication authentication);
    Page<Room> getRoomsByUser(String isApproval, String username, Pageable pageable);
    RoomDTO getOwnedRoom(Long roomId, Authentication authentication);
    List<ImageDTO> getOwnedRoomImages(Long roomId, Authentication authentication);
    void updateRoom(RoomDTO roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel);
    Page<Room> getAllRoomsForAdmin(Pageable pageable);
    Page<Room> getRoomsForAdmin(String status, Pageable pageable);
    void approveRoom(Long roomId);
    void rejectRoom(Long roomId, String reason);


}
