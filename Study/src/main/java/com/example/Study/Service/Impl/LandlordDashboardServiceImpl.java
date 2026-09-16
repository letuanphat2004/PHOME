package com.example.Study.Service.Impl;

import com.example.Study.Common.BookingStatusEnum;
import com.example.Study.Model.DTO.LandlordDashboardDTO;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.LandlordDashboardService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class LandlordDashboardServiceImpl implements LandlordDashboardService {
    private final UserRepository users;
    private final RoomRepository rooms;
    private final AppointmentRepository appointments;

    public LandlordDashboardServiceImpl(UserRepository users, RoomRepository rooms,
                                        AppointmentRepository appointments) {
        this.users = users;
        this.rooms = rooms;
        this.appointments = appointments;
    }

    @Override
    public LandlordDashboardDTO getDashboard(String username) {
        long landlordId = users.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản chủ nhà"))
                .getId();
        return new LandlordDashboardDTO(
                rooms.countByUserIdAndIsApproval(landlordId, "true"),
                rooms.countByUserIdAndIsApproval(landlordId, "false"),
                rooms.countByUserIdAndIsApproval(landlordId, "rejected"),
                appointments.countByLandlordAndStatus(username, BookingStatusEnum.PENDING.databaseValue()),
                appointments.countByLandlordAndStatus(username, BookingStatusEnum.APPROVED.databaseValue()),
                appointments.countByLandlordAndStatus(username, BookingStatusEnum.REJECTED.databaseValue()),
                appointments.countUpcomingApprovedByLandlord(username, Date.valueOf(LocalDate.now()))
        );
    }
}
