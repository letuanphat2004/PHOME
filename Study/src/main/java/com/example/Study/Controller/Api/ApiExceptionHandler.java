package com.example.Study.Controller.Api;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.example.Study.Controller.Api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> badCredentials() { return Map.of("message", "Tên đăng nhập hoặc mật khẩu không đúng"); }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> forbidden(AccessDeniedException exception) {
        return Map.of("message", exception.getMessage() == null ? "Bạn không có quyền thực hiện thao tác này" : exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Map<String, String> uploadTooLarge() {
        return Map.of("message", "Ảnh vượt quá dung lượng cho phép (tối đa 8 MB mỗi ảnh)");
    }

    @ExceptionHandler({IllegalArgumentException.class, EntityExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(RuntimeException exception) { return Map.of("message", exception.getMessage()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validation(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().isEmpty() ? "Dữ liệu không hợp lệ"
                : exception.getFieldErrors().get(0).getField() + ": " + exception.getFieldErrors().get(0).getDefaultMessage();
        return Map.of("message", message);
    }
}
