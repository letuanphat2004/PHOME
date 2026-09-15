package com.example.Study.Service.Impl;

import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Model.Request.Schedule.UpdateScheduleRequest;
import com.example.Study.Model.Respone.AppointmentResponse;
import com.example.Study.Model.Respone.DeleteScheduleResponse;
import com.example.Study.Model.Respone.Schedule.UpdateScheduleResponse;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.Service.AppointmentService;
import com.example.Study.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        long roomId = parseRoomId(request.getRoom_id());
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));
        if (!"true".equals(room.getIsApproval())) {
            throw new IllegalArgumentException("Phòng này chưa được duyệt hoặc đã ngừng hiển thị");
        }
        if (request.getNumPeople() > room.getCapacity()) {
            throw new IllegalArgumentException("Số người vượt quá sức chứa của phòng");
        }
        Date comeDate = validFutureDate(request.getComeDate());
        var appointment = Appointment.builder()
                .username(request.getUsername())
                .room_id(roomId)
                .fullname(request.getFullname())
                .email(request.getEmail())
                .tel(request.getTel())
                .numPeople(request.getNumPeople())
                .comeDate(comeDate)
                .transportation(request.getTransportation())
                .isApproval("false")
                .build();
        appointment = appointmentRepository.save(appointment);
        return new AppointmentResponse(appointment.getId());
    }

    @Override
    public Page<Appointment> getAllByUsername(String username, Pageable pageable) {
        return appointmentRepository.getAllByUsernameOrderByComeDateAsc(username, pageable);
    }

    @Override
    @Transactional
    public DeleteScheduleResponse deleteScheduleById(Long scheduleId, String username) {
        Appointment appointment = tenantAppointment(scheduleId, username);
        appointmentRepository.delete(appointment);
        return new DeleteScheduleResponse(scheduleId);
    }

    @Override
    public Page<Appointment> getAppointmentsByUsername(String isApproval, String username, Pageable pageable) {
        if (!"true".equals(isApproval) && !"false".equals(isApproval)) {
            throw new IllegalArgumentException("Trạng thái lịch hẹn không hợp lệ");
        }
        return appointmentRepository.getAppointmentsByUsername(isApproval, username, pageable);
    }

    @Override
    @Transactional
    public void permitAppointment(long appointmentId, String landlordUsername) {
        landlordAppointment(appointmentId, landlordUsername);
        appointmentRepository.updateAppointmentStatus(appointmentId);
    }

    @Override
    @Transactional
    public UpdateScheduleResponse updateAppointment(UpdateScheduleRequest request, String username) {
        long appointmentId = Long.parseLong(request.getAppointmentId());
        Appointment appointment = tenantAppointment(appointmentId, username);
        if ("true".equals(appointment.getIsApproval())) {
            throw new IllegalArgumentException("Lịch hẹn đã duyệt không thể đổi ngày");
        }
        appointmentRepository.updateAppointmentComeDate(appointmentId, validFutureDate(request.getComeDate()));
        return new UpdateScheduleResponse(request.getAppointmentId());
    }

    private Appointment tenantAppointment(long id, String username) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lịch hẹn không tồn tại"));
        if (!appointment.getUsername().equals(username)) {
            throw new AccessDeniedException("Lịch hẹn này không thuộc về bạn");
        }
        return appointment;
    }

    private Appointment landlordAppointment(long id, String username) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lịch hẹn không tồn tại"));
        var room = roomRepository.findById(appointment.getRoom_id())
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));
        var landlord = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản chủ nhà"));
        if (room.getUser_id() != landlord.getId()) {
            throw new AccessDeniedException("Lịch hẹn này không thuộc phòng của bạn");
        }
        return appointment;
    }

    private long parseRoomId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Mã phòng không hợp lệ");
        }
    }

    private Date validFutureDate(String value) {
        try {
            LocalDate date = LocalDate.parse(value);
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày xem phòng không được ở trong quá khứ");
            }
            return Date.valueOf(date);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new IllegalArgumentException("Ngày xem phòng phải có định dạng YYYY-MM-DD");
        }
    }
}
