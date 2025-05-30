package com.example.Study.Model.Request.User;

import com.example.Study.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EditRequest {
    private String username;

    private String password;

    private String fullname;

    private String tel;

    private String email;

    private long role_id;
    public static EditRequest toDto(User user) {
        return EditRequest.builder()
                .email(user.getEmail())
                .fullname(user.getFullname())
                .password(user.getPassword())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .build();
    }

    public static User toUser(EditRequest user) {
        return User.builder()
                .email(user.getEmail())
                .fullname(user.getFullname())
                .password(user.getPassword())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .build();
    }

    public static List<EditRequest> toDto(List<User> users) {
        return users.stream()
                .map(EditRequest::toDto)
                .collect(Collectors.toList());
    }

    public static List<User> toUser(List<EditRequest> userDtos) {
        return userDtos.stream()
                .map(EditRequest::toUser)
                .collect(Collectors.toList());
    }
}
