package com.example.Study.Controller;

import com.example.Study.Model.DTO.AppointmentDTO;
import com.example.Study.Model.DTO.EmailDTO;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Model.Request.Schedule.UpdateScheduleRequest;
import com.example.Study.Model.Request.User.*;
import com.example.Study.Model.Respone.AppointmentResponse;
import com.example.Study.Model.Respone.User.RegisterResponse;
import com.example.Study.Service.AppointmentService;
//import com.example.Study.Service.Email.EmailProducer;
//import com.example.Study.Service.Email.EmailProducer;
import com.example.Study.Service.Email.EmailService;
import com.example.Study.Service.UserService;
import com.example.Study.entity.Appointment;
import com.example.Study.entity.User;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private EmailProducer emailProducer;
@Autowired
private EmailService emailService;
    @Autowired
    private AppointmentService appointmentService;

    private static final int sizeOfPage = 8;


    @GetMapping("/login")
    public String login(){
        return "user/login";
    }

    @GetMapping("/register")
    public String register(){
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest){
        RegisterResponse userDto = userService.register(registerRequest);
        if(userDto != null){
            return "redirect:/user/login";
        }
        return "redirect:/user/register";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDTO userDto = userService.findUserByUsername(userDetails.getUsername());
            model.addAttribute("username", userDto.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
            model.addAttribute("linkAvatar", userDto.getLinkAvatar());
            model.addAttribute("fullname", userDto.getFullname());
            model.addAttribute("email", userDto.getEmail());
            model.addAttribute("tel", userDto.getTel());
        }
        return "user/profile";
    }

    @PostMapping("/changeInfo")
    public String changeInfo(@NonNull @ModelAttribute ChangeInfoRequest request) {
        userService.changeInfo(request);
        return "redirect:/user/profile";
    }

    @PostMapping("/uploadFile")
    public String changeAvatar(@RequestParam(name = "linkAvatar") MultipartFile file, Authentication authentication) {
        if (authentication != null) {
            var request = ChangeAvatarRequest.builder()
                    .file(file)
                    .username(authentication.getName())
                    .build();
            userService.changeAvatar(request);
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/changePassword")
    public String changePassword(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDTO userDto = userService.findUserByUsername(authentication.getName());
            model.addAttribute("username", userDto.getUsername());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
            sendEmail(model, userDto);
        }
        return "user/changePassword";
    }

    private String encodeEmail(String email) {
        StringBuilder encodedEmail = new StringBuilder();
        encodedEmail.append(email.substring(0,5)).append("*".repeat(email.length() - 15))
                .append("@gmail.com");
        return encodedEmail.toString();
    }

    private String genOTP() {
        StringBuilder otp = new StringBuilder();
        Random rd = new Random();
        otp.append(rd.nextInt(10)).append(rd.nextInt(10)).append(rd.nextInt(10))
                .append(rd.nextInt(10)).append(rd.nextInt(10)).append(rd.nextInt(10));
        return otp.toString();
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute ChangePasswordRequest request, Authentication authentication) {
        if (authentication != null) {
            userService.changePassword(request, authentication.getName());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/retrievePassword")
    public String retrievePassword() {
        return "user/enterUsername";
    }

    @PostMapping("/retrievePassword")
    public String retrievePassword(@RequestParam(name = "username") String username, Model model) {
        UserDTO userDto = userService.findUserByUsername(username.toLowerCase());
        if (userDto == null) {
            model.addAttribute("errorBE", "Tên đăng nhập không tồn tại!");
            return "user/enterUsername";
        }
        model.addAttribute("username", username);
        sendEmail(model, userDto);
        return "user/createNewPassword";
    }

    @GetMapping("/sendEmailAgain")
    public String sendEmailAgain(@RequestParam(name = "username") String username, Model model, Authentication auth) {
        UserDTO userDto = userService.findUserByUsername(username.toLowerCase());
        sendEmail(model, userDto);
        commonFunc(auth, model);
        return "user/createNewPassword";
    }

    private void sendEmail(Model model, UserDTO userDto) {
        String email = userDto.getEmail();
        String encodedEmail = encodeEmail(email);
        model.addAttribute("email", encodedEmail);
        String otp = genOTP();
        model.addAttribute("trueOTP", otp);
        var emailDto = EmailDTO.builder()
                .to(email)
                .subject("Mã bảo mật tài khoản của bạn")
                .body("<body style=\"font-family: Arial, sans-serif;\">\n" +
                        "    <div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0;\">\n" +
                        "        <h2 style=\"font-size: 24px; margin-bottom: 10px;\">Mã bảo mật tài khoản của bạn</h2>\n" +
                        "        <p style=\"margin: 10px 0;\">Xin chào "+userDto.getUsername()+ ",</p>\n" +
                        "        <p style=\"margin: 10px 0;\">Mã bảo mật của bạn là:</p>\n" +
                        "        <div style=\"font-size: 24px; font-weight: bold; padding: 10px; background-color: #f0f0f0; text-align: center; margin: 20px 0;\">\n" +
                        otp +
                        "        </div>\n" +
                        "        <p style=\"margin: 10px 0;\">Để xác nhận danh tính của bạn trên Facebook, chúng tôi cần xác minh địa chỉ email của bạn. Hãy dán mã này vào trình duyệt. Đây là mã dùng một lần.</p>\n" +
                        "        <p style=\"margin: 10px 0;\">Cảm ơn bạn!<br>Đội ngũ bảo mật của nhóm 9</p>\n" +
                        "    </div>\n" +
                        "</body>")
                .build();
        emailService.sendEmail(emailDto);
    }

    @PostMapping("/createNewPassword")
    public String saveNewPassword(@NonNull @ModelAttribute CreateNewPasswordRequest request) {
        userService.createNewPassword(request);
        return "redirect:/user/login";
    }

    @GetMapping("/schedule")
    public String showRoomViewingSchedule(Authentication auth, Model model,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) throws ParseException {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Appointment> pages = appointmentService.getAllByUsername(auth.getName(), pageable);
        commonFunc2(model, pageNo, pages);
        return "user/roomViewingSchedule";
    }

    @GetMapping("/deleteSchedule")
    public String deleteRoomViewingSchedule(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "scheduleId") Long appointmentId) throws ParseException {
        appointmentService.deleteScheduleById(appointmentId);
        return "redirect:/user/schedule?pageNo="+pageNo;
    }

    @PostMapping("/updateSchedule")
    public String updateSchedule(@ModelAttribute UpdateScheduleRequest request,
                                 @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo) {
        appointmentService.updateAppointment(request);
        return "redirect:/user/schedule?pageNo="+pageNo;
    }


        @GetMapping("/appointment")
    public String showAppointment(Authentication auth, Model model,
                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "isApproval", defaultValue = "true") String isApproval) throws ParseException {
        commonFunc(auth, model);
        Pageable pageable = PageRequest.of(pageNo - 1, sizeOfPage);
        Page<Appointment> pages = appointmentService.getAppointmentsByUsername(isApproval, auth.getName(), pageable);
        commonFunc2(model, pageNo, pages);
        model.addAttribute("isApproval", isApproval);
        return "user/viewingAppointment";
    }


    @GetMapping("/permitAppointment")
    public String showAppointment(@RequestParam(name="appointmentId") String appointmentId,
                                  @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo) {
        appointmentService.permitAppointment(Long.parseLong(appointmentId));
        return "redirect:/user/appointment?isApproval=false&pageNo="+pageNo;
    }

    private void commonFunc2(Model model, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, Page<Appointment> pages) {
        List<Appointment> dtoList = new ArrayList<>();
        pages.forEach(dtoList::add);
        model.addAttribute("Appointments", AppointmentDTO.toDto(dtoList));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", pages.getTotalPages() == 0 ? 1 : pages.getTotalPages());
    }

    private static void commonFunc(Authentication auth, Model model) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", auth.getName());
            String roleLength = userDetails.getAuthorities().toString();
            model.addAttribute("role", roleLength.substring(1, roleLength.length() - 1));
        }
    }
}
