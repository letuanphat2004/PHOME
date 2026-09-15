package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.User.ChangeAvatarRequest;
import com.example.Study.Model.Request.User.ChangeInfoRequest;
import com.example.Study.Model.Request.User.ChangePasswordRequest;
import com.example.Study.Service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
public class ApiProfileController {
    private final UserService users;

    public ApiProfileController(UserService users) { this.users = users; }

    @GetMapping
    public Profile profile(Authentication authentication) {
        UserDTO user = users.findUserByUsername(authentication.getName());
        return new Profile(user.getUsername(), user.getFullname(), user.getEmail(), user.getTel(), user.getLinkAvatar());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid ProfileUpdate body, Authentication authentication) {
        users.changeInfo(ChangeInfoRequest.builder().username(authentication.getName())
                .fullname(body.fullname()).tel(body.tel()).build());
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public Avatar avatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        users.changeAvatar(ChangeAvatarRequest.builder().username(authentication.getName()).file(file).build());
        return new Avatar(users.findUserByUsername(authentication.getName()).getLinkAvatar());
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid PasswordUpdate body, Authentication authentication) {
        users.changePassword(ChangePasswordRequest.builder().password(body.currentPassword())
                .newPassword(body.newPassword()).build(), authentication.getName());
    }

    public record Profile(String username, String fullname, String email, String tel, String avatar) {}
    public record ProfileUpdate(@NotBlank String fullname, @NotBlank String tel) {}
    public record PasswordUpdate(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    public record Avatar(String url) {}
}
