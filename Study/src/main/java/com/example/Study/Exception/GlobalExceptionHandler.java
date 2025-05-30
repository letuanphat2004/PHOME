package com.example.Study.Exception;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidParameterException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityExistsException.class)
    public String handleExistsException(EntityExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "user/register";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidExceptions(
            MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public String handleInvalidParameterException(InvalidParameterException ex, Model model) {
        model.addAttribute("errorBE", ex.getMessage());
        return "user/changePassword";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException (HttpRequestMethodNotSupportedException ex) {
        return "error/accessDenied";
    }

}
