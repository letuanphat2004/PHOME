package com.example.Study.Model.Request.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RegisterRequest {
    @NonNull
    private String username;

    @NonNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Password must contain both letters and numbers")
    private String password;

    @Pattern(regexp = "^.+@gmail.com$", message = "Email must contain @gmail.com on the bottom")
    private String email;

    @NonNull
    private String fullname;

    @Length(max = 12, min = 10)
    private String tel;

    private String role_id;
}
