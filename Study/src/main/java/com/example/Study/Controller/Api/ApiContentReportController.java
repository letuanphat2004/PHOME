package com.example.Study.Controller.Api;

import com.example.Study.Service.ContentReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ApiContentReportController {
    private final ContentReportService reports;

    public ApiContentReportController(ContentReportService reports) {
        this.reports = reports;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> create(@Valid @RequestBody ReportBody body, Authentication authentication) {
        return Map.of("id", reports.create(authentication.getName(), body.targetType(), body.targetId(),
                body.reason(), body.details()));
    }

    public record ReportBody(@NotBlank String targetType, @Positive long targetId,
                             @NotBlank String reason, @NotBlank @Size(max = 1000) String details) {}
}
