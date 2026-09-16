package com.example.Study.Service.Impl;

import com.example.Study.Common.RoomType;
import com.example.Study.Common.RoomTypeConverter;
import com.example.Study.Model.DTO.ImageDTO;
import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.Request.Room.RoomFilterDataRequest;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.CommentRepository;
import com.example.Study.Respository.ImageRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.FileService;
import com.example.Study.Service.NotificationService;
import com.example.Study.Service.RoomService;
import com.example.Study.entity.Image;
import com.example.Study.entity.Room;
import com.example.Study.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoomServiceImpl implements RoomService {
    private static final String APPROVED = "true";
    private static final long MAX_IMAGE_SIZE = 8L * 1024 * 1024;
    private static final int MAX_IMAGES = 8;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final RoomRepository roomRepository;
    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notifications;

    public RoomServiceImpl(RoomRepository roomRepository, FileService fileService,
                           ImageRepository imageRepository, UserRepository userRepository,
                           CommentRepository commentRepository,
                           AppointmentRepository appointmentRepository,
                           NotificationService notifications) {
        this.roomRepository = roomRepository;
        this.fileService = fileService;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.appointmentRepository = appointmentRepository;
        this.notifications = notifications;
    }

    @Override
    public List<RoomDTO> getAllRoomByUser(String username) {
        User user = requiredUser(username);
        return roomRepository.findAllByUserid(user.getId()).stream().map(RoomDTO::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteRoomByRoomId(Long roomId, Authentication authentication) {
        Room room = ownedRoom(roomId, authentication);
        imageRepository.findAllImagesEntityByRoomId(roomId).forEach(image -> fileService.deleteFile(image.getUrl()));
        imageRepository.deleteAllImagesByRoomId(roomId);
        commentRepository.deleteCommentsByRoom_id(roomId);
        appointmentRepository.deleteAppointmentByRoom_id(roomId);
        roomRepository.delete(room);
    }

    @Override
    public Page<Room> getRoomsByUser(String approval, String username, Pageable pageable) {
        if (!"true".equals(approval) && !"false".equals(approval) && !"rejected".equals(approval)) {
            throw new IllegalArgumentException("Trạng thái phòng không hợp lệ");
        }
        return roomRepository.getAllByUserId(approval, requiredUser(username).getId(), pageable);
    }

    @Override
    public RoomDTO getOwnedRoom(Long roomId, Authentication authentication) {
        return RoomDTO.toDto(ownedRoom(roomId, authentication));
    }

    @Override
    public List<ImageDTO> getOwnedRoomImages(Long roomId, Authentication authentication) {
        ownedRoom(roomId, authentication);
        return imageRepository.findAllImagesEntityByRoomId(roomId).stream().map(ImageDTO::toDto).toList();
    }

    @Override
    @Transactional
    public void updateRoom(RoomDTO roomDto, Authentication authentication,
                           List<MultipartFile> imagesAdd, List<Long> imageIdsDel) {
        Room room = ownedRoom(roomDto.getRoom_id(), authentication);
        List<MultipartFile> uploads = validImages(imagesAdd, false);
        List<Image> currentImages = imageRepository.findAllImagesEntityByRoomId(room.getId());
        Set<Long> deleteIds = imageIdsDel == null ? Set.of() : new HashSet<>(imageIdsDel);
        List<Image> imagesToDelete = currentImages.stream()
                .filter(image -> deleteIds.contains(image.getId()))
                .toList();

        if (imagesToDelete.size() != deleteIds.size()) {
            throw new AccessDeniedException("Có ảnh không thuộc căn phòng này");
        }
        if (currentImages.size() - imagesToDelete.size() + uploads.size() == 0) {
            throw new IllegalArgumentException("Căn phòng phải có ít nhất một hình ảnh");
        }
        if (currentImages.size() - imagesToDelete.size() + uploads.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("Mỗi phòng được tải tối đa 8 hình ảnh");
        }

        room.setAddress(roomDto.getAddress().trim());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        room.setDescription(roomDto.getDescription().trim());
        room.setRoomType(parseRoomType(roomDto.getRoomType()));
        room.setArea(roomDto.getArea());
        room.setIsApproval("false");
        room.setModerationNote(null);

        imagesToDelete.forEach(image -> fileService.deleteFile(image.getUrl()));
        imageRepository.deleteAll(imagesToDelete);
        List<String> remainingUrls = currentImages.stream()
                .filter(image -> !deleteIds.contains(image.getId()))
                .map(Image::getUrl)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (MultipartFile file : uploads) {
            String imageUrl = uploadImage(file);
            imageRepository.save(Image.builder().room_id(room.getId()).url(imageUrl).build());
            remainingUrls.add(imageUrl);
        }

        if (!remainingUrls.contains(room.getImage())) {
            room.setImage(remainingUrls.get(0));
        }
        roomRepository.save(room);
        notifications.notifyRole(3L, "ROOM_SUBMITTED", "Phòng cần kiểm duyệt",
                "Chủ nhà đã cập nhật phòng tại " + room.getAddress(), "/admin");
    }

    @Override
    public Page<Room> getAllRoomByManyContrains(RoomFilterDataRequest request, Pageable pageable) {
        if (request.isNull()) return roomRepository.findAllByIsApproval(APPROVED, pageable);
        RoomType roomType;
        try {
            roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
        } catch (Exception exception) {
            roomType = null;
        }
        return roomRepository.findAllByFilterConstraints(request.getPrice(), request.getAddress(),
                request.getArea(), roomType, pageable);
    }

    @Override
    public RoomDTO getInforRoomByRoom_Id(String roomId) {
        return RoomDTO.toDto(roomRepository.findById(Long.parseLong(roomId)).orElse(null));
    }

    @Override
    public List<String> GetAllImageByRoom_Id(String roomId) {
        return imageRepository.findAllImagesByRoom_id(Long.parseLong(roomId));
    }

    @Override
    @Transactional
    public void addRoom(RoomDTO roomDto, List<MultipartFile> images, Authentication authentication) {
        List<MultipartFile> uploads = validImages(images, true);
        User landlord = requiredUser(authentication.getName());
        Room room = RoomDTO.toRoom(roomDto);
        room.setAddress(room.getAddress().trim());
        room.setDescription(room.getDescription().trim());
        room.setUser_id(landlord.getId());
        room.setIsApproval("false");
        room.setModerationNote(null);

        List<String> urls = uploads.stream().map(this::uploadImage).toList();
        room.setImage(urls.get(0));
        roomRepository.save(room);
        imageRepository.saveAll(urls.stream()
                .map(url -> Image.builder().room_id(room.getId()).url(url).build())
                .toList());
        notifications.notifyRole(3L, "ROOM_SUBMITTED", "Phòng mới cần kiểm duyệt",
                "Có phòng mới tại " + room.getAddress(), "/admin");
    }

    @Override
    public Page<Room> getAllRoomsForAdmin(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    public Page<Room> getRoomsForAdmin(String status, Pageable pageable) {
        String databaseStatus = switch (status == null ? "" : status.trim().toLowerCase()) {
            case "pending", "false" -> "false";
            case "approved", "true" -> "true";
            case "rejected" -> "rejected";
            default -> throw new IllegalArgumentException("Trạng thái phòng không hợp lệ");
        };
        return roomRepository.findAllByIsApproval(databaseStatus, pageable);
    }

    @Override
    @Transactional
    public void approveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
        if (!"false".equals(room.getIsApproval())) {
            throw new IllegalArgumentException("Chỉ phòng đang chờ duyệt mới có thể được phê duyệt");
        }
        room.setIsApproval("true");
        room.setModerationNote(null);
        roomRepository.save(room);
        notifications.notifyUser(room.getUser_id(), "ROOM_APPROVED", "Phòng đã được phê duyệt",
                "Phòng tại " + room.getAddress() + " hiện đã được công khai.", "/my-rooms");
    }

    @Override
    @Transactional
    public void rejectRoom(Long roomId, String reason) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
        if (!"false".equals(room.getIsApproval())) {
            throw new IllegalArgumentException("Chỉ phòng đang chờ duyệt mới có thể bị từ chối");
        }
        String feedback = reason == null ? "" : reason.trim();
        if (feedback.isEmpty() || feedback.length() > 500) {
            throw new IllegalArgumentException("Lý do từ chối phải có từ 1 đến 500 ký tự");
        }
        room.setIsApproval("rejected");
        room.setModerationNote(feedback);
        roomRepository.save(room);
        notifications.notifyUser(room.getUser_id(), "ROOM_REJECTED", "Phòng cần chỉnh sửa",
                "Phòng tại " + room.getAddress() + ": " + feedback, "/my-rooms");
    }

    private Room ownedRoom(Long roomId, Authentication authentication) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
        User user = requiredUser(authentication.getName());
        if (room.getUser_id() != user.getId()) {
            throw new AccessDeniedException("Bạn không sở hữu căn phòng này");
        }
        return room;
    }

    private User requiredUser(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản"));
    }

    private List<MultipartFile> validImages(List<MultipartFile> images, boolean required) {
        List<MultipartFile> files = images == null ? List.of() : images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();
        if (required && files.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một hình ảnh");
        }
        if (files.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("Mỗi phòng được tải tối đa 8 hình ảnh");
        }
        for (MultipartFile file : files) {
            if (file.getContentType() == null || !ALLOWED_IMAGE_TYPES.contains(file.getContentType().toLowerCase())) {
                throw new IllegalArgumentException("Ảnh phải có định dạng JPG, PNG, WebP hoặc GIF");
            }
            if (file.getSize() > MAX_IMAGE_SIZE) {
                throw new IllegalArgumentException("Mỗi hình ảnh không được vượt quá 8 MB");
            }
        }
        return files;
    }

    private String uploadImage(MultipartFile file) {
        String url = fileService.uploadFile(file);
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Không thể tải hình ảnh lên");
        }
        return url.replaceFirst("^http://", "https://");
    }

    private RoomType parseRoomType(String value) {
        try {
            return RoomType.valueOf(value);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Loại phòng không hợp lệ");
        }
    }
}
