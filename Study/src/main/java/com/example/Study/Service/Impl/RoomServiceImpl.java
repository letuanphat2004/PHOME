package com.example.Study.Service.Impl;

import com.example.Study.Common.*;
import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.Request.Room.RoomFilterDataRequest;
import com.example.Study.Respository.*;
import com.example.Study.Service.FileService;
import com.example.Study.Service.RoomService;
import com.example.Study.entity.Image;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageRepository imageRepository;

    private static final String isApproval = "true";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Override
    public List<RoomDTO> getAllRoomByUser(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        List<Room> rooms = roomRepository.findAllByUserid(user.get().getId());
        List<RoomDTO> roomDtos = new ArrayList<>();
        for (Room room : rooms) {
            RoomDTO roomDto = RoomDTO.toDto(room);
            roomDtos.add(roomDto);
        }
        return roomDtos;
    }
    @Override
    public void deleteRoomByRoomId(Long room_id) {
        imageRepository.deleteAllImagesByRoomId(room_id);
        commentRepository.deleteCommentsByRoom_id(room_id);
        appointmentRepository.deleteAppointmentByRoom_id(room_id);
        roomRepository.deleteById(room_id);
    }
    @Override
    public Page<Room> getRoomsByUser(String isApproval, String username, Pageable pageable) {
        Optional<User> user = userRepository.findUserByUsername(username);
        return roomRepository.getAllByUserId(isApproval, user.get().getId(), pageable);
    }

    @Modifying
    @Transactional
    @Override
    public void updateRoom(RoomDTO roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel) {
        Room room = RoomDTO.toRoom(roomDto);
        room.setIsApproval("false");
        Room oldroom = roomRepository.findById(roomDto.getRoom_id()).orElse(null);
        room.setId(roomDto.getRoom_id());
        room.setCreatedAt(oldroom.getCreatedAt());
        if (auth != null) {
            String username = auth.getName();
            Optional<User> user = userRepository.findUserByUsername(username);
            room.setUser_id(user.get().getId());
        }

        commentRepository.deleteCommentsByRoom_id(roomDto.getRoom_id());
        appointmentRepository.deleteAppointmentByRoom_id(roomDto.getRoom_id());

        if (imageIdsDel != null) {
            for (Long id : imageIdsDel) {
                Optional<Image> image = imageRepository.findById(id);

                // Sửa phần so sánh null trước khi gọi equals()
                if (image.isPresent() && room.getImage() != null && room.getImage().equals(image.get().getUrl())) {
                    room.setImage(""); // Nếu trùng, xóa ảnh
                    break;
                }
            }
            imageRepository.deleteAllById(imageIdsDel);
        }

        if (imagesAdd != null && !imagesAdd.isEmpty()) {
            for (MultipartFile file : imagesAdd) {
                if (file.isEmpty()) {
                    continue;
                }
                Image image = new Image();
                image.setRoom_id(room.getId());
                String imageUrl = fileService.uploadFile(file); // Upload file lên Cloudinary
                image.setUrl(imageUrl);
                imageRepository.save(image);
            }
        }

        // Kiểm tra nếu room.getImage() là null hoặc rỗng và thiết lập ảnh đầu tiên từ imageRepository
        if (room.getImage() == null || room.getImage().isEmpty()) {
            List<String> images = imageRepository.findAllImagesByRoom_id(room.getId());

            // Nếu có ít nhất một ảnh, gán ảnh đầu tiên cho room
            if (!images.isEmpty()) {
                room.setImage(images.get(0)); // Lưu ảnh vào room entity
            }
        }

        roomRepository.save(room);
    }


    @Override
    public Page<Room> getAllRoomByManyContrains(RoomFilterDataRequest request, Pageable pageable) {
        Page<Room> roomPage;
        if (request.isNull()) {
            roomPage = roomRepository.findAllByIsApproval(isApproval, pageable);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraints(request.getPrice(), request.getAddress(), request.getArea(), roomType, pageable);
        }
        return roomPage;
    }

    @Override
    public RoomDTO getInforRoomByRoom_Id(String room_id) {
        return RoomDTO.toDto(roomRepository.findById(Long.parseLong(room_id)).orElse(null));
    }

    @Override
    public List<String> GetAllImageByRoom_Id(String room_id) {
        return imageRepository.findAllImagesByRoom_id(Long.parseLong(room_id));
    }

    @Modifying
    @Override
    @Transactional
    public void addRoom(RoomDTO roomDto, List<MultipartFile> images, Authentication auth) {
        Room room = RoomDTO.toRoom(roomDto);
        room.setIsApproval("false");
        if (auth != null) {
            String username = auth.getName();
            Optional<User> user = userRepository.findUserByUsername(username);
            room.setUser_id(user.get().getId());
        }


        room.setImage(fileService.uploadFile((MultipartFile) images.get(0)));//luu anh vao roomentity
        roomRepository.save(room);
        Image roomImage = new Image();
        roomImage.setRoom_id(room.getId());
        roomImage.setUrl(room.getImage());
        imageRepository.save(roomImage);
        for (int i = 1; i < images.size(); i++) {//luu anh vao bang image
            Image image = new Image();
            image.setRoom_id(room.getId());
            String imageUrl = fileService.uploadFile(images.get(i));
            image.setUrl(imageUrl);
            imageRepository.save(image);
        }
        roomRepository.save(room);
    }

    @Override
    public Page<Room> getAllRoomsForAdmin(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void approveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setIsApproval("true");
        roomRepository.save(room);
    }

    // Không duyệt phòng
    @Override
    @Transactional
    public void disapproveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.deleteById(roomId);
    }

}
