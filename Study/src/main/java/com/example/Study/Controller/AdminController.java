package com.example.Study.Controller;

import com.example.Study.Model.DTO.RoomDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Service.RoomService;
import com.example.Study.Service.UserService;
import com.example.Study.entity.Room;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    private static final int sizeOfPage = 5;


    private void func_common(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }
    @GetMapping("/RoomManagement")
    public String roomanagement(Authentication authentication, Model model
            , @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Room> roomPage = roomService.getAllRoomsForAdmin(pageable);
        List<Room> roomList = new LinkedList<>();
        roomPage.forEach(roomList::add);
        model.addAttribute("rooms", RoomDTO.toDto(roomList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", roomPage.getTotalPages() == 0 ? 1 : roomPage.getTotalPages());

        return "RoomManagement";
    }

    @GetMapping("/UserManagement")
    public String usermanagement(Authentication authentication, Model model,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        func_common(authentication, model);

        if (pageNo < 1) {
            pageNo = 1; // Đảm bảo pageNo luôn bắt đầu từ 1
        }

        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<UserDTO> userPage = userService.getAllUserForAdmin(pageable);
        List<UserDTO> userList = new LinkedList<>();
        userPage.forEach(userList::add);
        model.addAttribute("users", userList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", userPage.getTotalPages() == 0 ? 1 : userPage.getTotalPages());


        return "UserManagement";
    }
    @PostMapping("/approveRoom")
    public String approveRoom(@RequestParam Long roomId, Authentication authentication, Model model) {
        func_common(authentication, model);
        roomService.approveRoom(roomId);
        return "redirect:/admin/RoomManagement";
    }

    @PostMapping("/disapproveRoom")
    public String disapproveRoom(@RequestParam Long roomId, Authentication authentication, Model model) {
        func_common(authentication, model);
        roomService.disapproveRoom(roomId);
        return "redirect:/admin/RoomManagement";
    }


}