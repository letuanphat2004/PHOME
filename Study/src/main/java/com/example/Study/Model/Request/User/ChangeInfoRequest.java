package com.example.Study.Model.Request.User;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ChangeInfoRequest {
    private String username, fullname, tel;
}
