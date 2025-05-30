package com.example.Study.Model.Request.User;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CreateNewPasswordRequest {
    private String username, newPassword;
}
