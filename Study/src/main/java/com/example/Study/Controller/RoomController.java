package com.example.Study.Controller;
import com.example.Study.Model.DTO.CommentDTO;
import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Respository.ImageRepository;
import com.example.Study.Service.AppointmentService;
import com.example.Study.Service.CommentService;
import com.example.Study.Service.RoomService;
import com.example.Study.Service.UserService;
import com.example.Study.entity.Image;
import com.example.Study.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("api/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AppointmentService appointmentService;

    private static final int sizeOfPage = 4;
    @Autowired
    private ImageRepository imageRepository;

    @PreAuthorize("hasRole('Landlord')")
    @GetMapping("/delete")
    public String deteteRoom(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                             @RequestParam(name = "roomId") Long roomId) throws ParseException {
        roomService.deleteRoomByRoomId(roomId);
        return "redirect:/api/room/manage?pageNo="+pageNo;
    }

    @GetMapping("/manage")
    public String showOwnedRooms(Model model, Authentication auth,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "isApproval", defaultValue = "true") String isApproval) throws ParseException  {
        commonFunc(auth, model);
        String username = auth.getName();
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> pages = roomService.getRoomsByUser(isApproval, username, pageable);
        commonFunc2(model, pageNo, pages);
        model.addAttribute("isApproval", isApproval);
        return "manage_room";
    }

    @PostMapping("/addroom")
    public String addRoom(@ModelAttribute RoomDTO roomDto, Model model, Authentication auth,@RequestParam(value = "images", required = false) List<MultipartFile> images )  {
        roomService.addRoom(roomDto, images, auth);
        return "redirect:addroom";
    }

    @GetMapping("/addroom")
    public String showAddRoomForm(@ModelAttribute RoomDTO roomDto, Model model, Authentication auth) {
        commonFunc(auth, model);
        model.addAttribute("room", roomDto);
        return "addroom";
    }

    @PostMapping("/update")
    public String UpdateRoom(@ModelAttribute RoomDTO roomDto, Model model, Authentication auth,
                             @RequestParam(value = "images", required = false) List<MultipartFile> imagesAdd,
                             @RequestParam(value = "imageIdsDel", required = false) List<Long> imageIdsDel)  {
        commonFunc(auth, model);
        roomService.updateRoom(roomDto,auth, imagesAdd, imageIdsDel);
        return "redirect:manage";
    }


    @GetMapping("/update")
    public String showUpdateRoomForm(@ModelAttribute RoomDTO roomDto, Model model, Authentication auth,
                                     @RequestParam(name = "roomId") Long roomId) {
        commonFunc(auth, model);
        roomDto = roomService.getInforRoomByRoom_Id(String.valueOf(roomId));
        List<Image> images = imageRepository.findAllImagesEntityByRoomId(roomId);
        model.addAttribute("images", images);
        model.addAttribute("room", roomDto);
        return "updateroom";
    }



    @GetMapping
    public String showDetailRoom(Authentication auth, @RequestParam(name = "room_id") String room_id, Model model) {
        RoomDTO roomDto = roomService.getInforRoomByRoom_Id(room_id);
        List<String>  imageDtos = roomService.GetAllImageByRoom_Id(room_id);
        model.addAttribute("room", roomDto);
        model.addAttribute("images", imageDtos);

        UserDTO userDto = userService.getUserById(roomDto.getUser_id());
        model.addAttribute("userDto", userDto);

        List<CommentDTO> commentDtos = commentService.getAllCommentsByRoom_id(Long.parseLong(room_id));
        model.addAttribute("comments", commentDtos);

        commonFunc(auth, model);
        return "room/details";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model, @RequestParam(name = "room_id") String room_id) {
        commonFunc(auth, model);
        UserDTO dto = userService.findUserByUsername(auth.getName());
        model.addAttribute("dto",dto);
        model.addAttribute("room_id", room_id);
        return "room/schedule";
    }

    @PostMapping("/schedule")
    public String createAppointment(@ModelAttribute AppointmentRequest request, Authentication auth, Model model) {
        commonFunc(auth, model);
        appointmentService.createAppointment(request);
        return "redirect:/api/home";
    }

    private static void commonFunc(Authentication auth, Model model) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", auth.getName());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }



    private static void commonFunc2(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Room> pages) {
        List<Room> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("rooms", RoomDTO.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }
}
