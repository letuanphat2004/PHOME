package com.example.Study.Service;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.User.*;
import com.example.Study.Model.Respone.User.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    RegisterResponse register(RegisterRequest request);

    ChangeInfoResponse changeInfo(ChangeInfoRequest request);

    ChangeAvatarResponse changeAvatar(ChangeAvatarRequest request);

    UserDTO getUserById(long id);

    List<UserDTO> getAllUser(String name, String tel, Pageable pageable);

    UserDTO findUserByUsername(String username);

    ChangePasswordResponse changePassword(ChangePasswordRequest request, String username);

    Page<UserDTO> getAllUserForAdmin(Pageable pageable);

    void setAccountEnabled(long userId, boolean enabled, String adminUsername);
}
