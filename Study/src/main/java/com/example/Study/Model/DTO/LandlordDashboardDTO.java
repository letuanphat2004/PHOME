package com.example.Study.Model.DTO;

public record LandlordDashboardDTO(
        long approvedRooms,
        long pendingRooms,
        long pendingAppointments,
        long approvedAppointments,
        long rejectedAppointments,
        long upcomingAppointments
) {}
