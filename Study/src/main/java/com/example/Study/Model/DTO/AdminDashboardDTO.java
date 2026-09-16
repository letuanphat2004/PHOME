package com.example.Study.Model.DTO;

public record AdminDashboardDTO(
        long pendingRooms,
        long approvedRooms,
        long rejectedRooms,
        long activeUsers,
        long disabledUsers,
        long tenants,
        long landlords
) {}
