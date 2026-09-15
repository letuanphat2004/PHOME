package com.example.Study.Service;

import com.example.Study.Model.DTO.EmailDTO;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.Email.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {
    private static final int MAX_ATTEMPTS = 5;
    private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public PasswordResetService(UserRepository users, PasswordEncoder encoder, EmailService emailService) {
        this.users = users;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    public String issue(String rawUsername) {
        String username = rawUsername.trim().toLowerCase();
        var user = users.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập không tồn tại"));
        String code = "%06d".formatted(random.nextInt(1_000_000));
        challenges.put(username, new Challenge(encoder.encode(code), Instant.now().plus(10, ChronoUnit.MINUTES), 0));
        emailService.sendEmail(EmailDTO.builder().to(user.getEmail()).subject("Mã xác nhận PHOME")
                .body("<p>Mã xác nhận đặt lại mật khẩu của bạn là:</p><h2>" + code + "</h2><p>Mã có hiệu lực trong 10 phút.</p>")
                .build());
        return maskEmail(user.getEmail());
    }

    @Transactional
    public void reset(String rawUsername, String code, String newPassword) {
        String username = rawUsername.trim().toLowerCase();
        Challenge challenge = challenges.get(username);
        if (challenge == null || challenge.expiresAt().isBefore(Instant.now())) {
            challenges.remove(username);
            throw new IllegalArgumentException("Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới.");
        }
        if (challenge.attempts() >= MAX_ATTEMPTS) {
            challenges.remove(username);
            throw new IllegalArgumentException("Bạn đã nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới.");
        }
        if (!encoder.matches(code, challenge.codeHash())) {
            challenges.put(username, new Challenge(challenge.codeHash(), challenge.expiresAt(), challenge.attempts() + 1));
            throw new IllegalArgumentException("Mã xác nhận không đúng");
        }
        if (newPassword == null || newPassword.length() < 8 || !newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự, gồm chữ và số");
        }
        users.updatePassword(encoder.encode(newPassword), username);
        challenges.remove(username);
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return "***" + email.substring(Math.max(0, at));
        return email.substring(0, 2) + "***" + email.substring(at);
    }

    private record Challenge(String codeHash, Instant expiresAt, int attempts) {}
}
