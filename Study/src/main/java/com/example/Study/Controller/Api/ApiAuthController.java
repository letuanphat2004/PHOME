package com.example.Study.Controller.Api;

import com.example.Study.Model.Request.User.RegisterRequest;
import com.example.Study.Service.UserService;
import com.example.Study.Service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class ApiAuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final HttpSessionSecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();

    public ApiAuthController(AuthenticationManager authenticationManager, UserService userService,
                             PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }

    @PostMapping("/login")
    public CurrentUser login(@Valid @RequestBody LoginBody body,
                             HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(body.username().trim().toLowerCase(), body.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
        return CurrentUser.from(authentication);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
    }

    @GetMapping("/me")
    public CurrentUser me(Authentication authentication) {
        return CurrentUser.from(authentication);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterBody body) {
        String roleId = switch (body.role().toUpperCase()) {
            case "TENANT" -> "1";
            case "LANDLORD" -> "2";
            default -> throw new IllegalArgumentException("Role must be TENANT or LANDLORD");
        };
        userService.register(RegisterRequest.builder()
                .username(body.username()).password(body.password()).email(body.email())
                .fullname(body.fullname()).tel(body.tel()).role_id(roleId).build());
    }

    @PostMapping("/password/request")
    public Map<String, String> requestPasswordReset(@RequestBody @Valid PasswordRequest body) {
        return Map.of("email", passwordResetService.issue(body.username()));
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody @Valid PasswordReset body) {
        passwordResetService.reset(body.username(), body.otp(), body.newPassword());
    }

    public record LoginBody(@NotBlank String username, @NotBlank String password) {}
    public record PasswordRequest(@NotBlank String username) {}
    public record PasswordReset(@NotBlank String username, @Pattern(regexp = "^[0-9]{6}$") String otp,
                                @Size(min = 8) String newPassword) {}
    public record RegisterBody(
            @NotBlank String username,
            @Size(min = 8) @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$") String password,
            @Email @NotBlank String email,
            @NotBlank String fullname,
            @Pattern(regexp = "^[0-9]{10,12}$") String tel,
            @NotBlank String role) {}

    public record CurrentUser(String username, String role) {
        static CurrentUser from(Authentication authentication) {
            String role = authentication.getAuthorities().stream().findFirst()
                    .map(Object::toString).orElse("");
            return new CurrentUser(authentication.getName(), role);
        }
    }
}
