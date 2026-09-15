package com.example.Study.Model.DTO;

import com.example.Study.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    public long id;

    private String username;

    private String password;

    private String fullname;

    private String tel;

    private String email;

    private long role_id;

    private String linkAvatar;

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .linkAvatar(user.getLinkAvatar())
                .build();
    }

    public static User toUser(UserDTO user) {
        if (user == null) {
            return null;
        }
        return User.builder()
                .email(user.getEmail())
                .fullname(user.getFullname())
                .password(user.getPassword())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .linkAvatar(user.getLinkAvatar())
                .build();
    }

    public static List<UserDTO> toDto(List<User> users) {
        return users.stream()
                .map(UserDTO::toDto)
                .collect(Collectors.toList());
    }

    public static List<User> toUser(List<UserDTO> userDtos) {
        return userDtos.stream()
                .map(UserDTO::toUser)
                .collect(Collectors.toList());
    }
}
