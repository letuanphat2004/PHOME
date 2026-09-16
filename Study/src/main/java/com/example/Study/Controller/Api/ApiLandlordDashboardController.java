package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.LandlordDashboardDTO;
import com.example.Study.Service.LandlordDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/landlord/dashboard")
@PreAuthorize("hasAuthority('Landlord')")
public class ApiLandlordDashboardController {
    private final LandlordDashboardService dashboard;

    public ApiLandlordDashboardController(LandlordDashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping
    public LandlordDashboardDTO dashboard(Authentication authentication) {
        return dashboard.getDashboard(authentication.getName());
    }
}
