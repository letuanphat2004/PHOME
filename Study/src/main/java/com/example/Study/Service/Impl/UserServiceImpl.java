package com.example.Study.Service.Impl;
import com.example.Study.Config.MyUserDetails;
import com.example.Study.Model.DTO.UserDTO;
import com.example.Study.Model.Request.User.*;
import com.example.Study.Model.Respone.User.*;
import com.example.Study.Respository.RoleRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.FileService;
import com.example.Study.Service.UserService;
import com.example.Study.entity.Role;
import com.example.Study.entity.User;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new RuntimeException("Could not find user by username"));
        Role role = roleRepository.findById(user.getRole_id()).orElse(null);
        return new MyUserDetails(user, role);
    }

    @Override
    public UserDTO findUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) return null;
        return UserDTO.toDto(user);
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, String username) {
        UserDetails userDetails = loadUserByUsername(username);
        if (request.getPassword().equals(userDetails.getPassword())) {
            throw new InvalidParameterException("Wrong password");
        }
        userRepository.updatePassword(passwordEncoder.encode(request.getNewPassword()), username);
        return new ChangePasswordResponse(username);
    }

    @Override
    @Transactional
    public CreateNewPasswordResponse createNewPassword(CreateNewPasswordRequest request) {
        userRepository.updatePassword(passwordEncoder.encode(request.getNewPassword()), request.getUsername());
        return new CreateNewPasswordResponse(request.getUsername());
    }

    @Override
    public Page<UserDTO> getAllUserForAdmin(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        // Chuyển đổi Page<User> sang Page<UserDto>
        Page<UserDTO> userDtoPage = userPage.map(UserDTO::toDto);
        return userDtoPage;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        User user1 = userRepository.findUserByUsername(registerRequest.getUsername()).orElse(null) ;
        if (user1 != null) throw new EntityExistsException("Account existed!");

        var user = User.builder()
                .username(registerRequest.getUsername().toLowerCase())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullname(registerRequest.getFullname())
                .email(registerRequest.getEmail())
                .tel(registerRequest.getTel())
                .role_id(Long.parseLong(registerRequest.getRole_id()))
                .linkAvatar("https://res.cloudinary.com/hoaptit/image/upload/v1714322737/samples/people/bicycle.jpg")
                .build();
        userRepository.save(user);
        return new RegisterResponse(user.getId());
    }

    @Override
    @Transactional
    public ChangeInfoResponse changeInfo(ChangeInfoRequest request) {
        userRepository.updateInfoUser(request.getTel(), request.getFullname(), request.getUsername());
        return new ChangeInfoResponse(request.getUsername());
    }

    @Override
    @Transactional
    public ChangeAvatarResponse changeAvatar(ChangeAvatarRequest request) {
        String linkAvatar = fileService.uploadFile(request.getFile());
        userRepository.updateAvatarUser(linkAvatar, request.getUsername());
        return new ChangeAvatarResponse(request.getUsername());
    }

    @Override
    public UserDTO getUserById(long id) {
        return UserDTO.toDto(userRepository.findById(id).orElse(null));
    }

    @Override
    public List<UserDTO> getAllUser(String name, String tel, Pageable pageable) {
        Page<User> userPage;
        if ((name != null && !name.isEmpty()) || (tel != null && !tel.isEmpty())) {
            userPage = userRepository.findByUsernameContaining(name, tel, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        List<User> userList = userPage.getContent();
        return UserDTO.toDto(userList);
    }
}

