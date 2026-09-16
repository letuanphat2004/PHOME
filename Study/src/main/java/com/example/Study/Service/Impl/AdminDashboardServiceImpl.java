package com.example.Study.Service.Impl;

import com.example.Study.Model.DTO.AdminDashboardDTO;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.AdminDashboardService;
import com.example.Study.Service.ContentReportService;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final RoomRepository rooms;
    private final UserRepository users;
    private final ContentReportService reports;

    public AdminDashboardServiceImpl(RoomRepository rooms, UserRepository users, ContentReportService reports) {
        this.rooms = rooms;
        this.users = users;
        this.reports = reports;
    }

    @Override
    public AdminDashboardDTO getDashboard() {
        return new AdminDashboardDTO(
                rooms.countByIsApproval("false"),
                rooms.countByIsApproval("true"),
                rooms.countByIsApproval("rejected"),
                users.countByDeleted(false),
                users.countByDeleted(true),
                users.countByRoleId(1L),
                users.countByRoleId(2L),
                reports.countPending()
        );
    }
}
