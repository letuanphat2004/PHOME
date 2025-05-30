package com.example.Study.Service.Impl;

import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Model.Request.Schedule.UpdateScheduleRequest;
import com.example.Study.Model.Respone.AppointmentResponse;
import com.example.Study.Model.Respone.DeleteScheduleResponse;
import com.example.Study.Model.Respone.Schedule.UpdateScheduleResponse;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Service.AppointmentService;
import com.example.Study.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        var appointment = Appointment.builder()
                .username(request.getUsername())
                .room_id(Long.parseLong(request.getRoom_id()))
                .fullname(request.getFullname())
                .email(request.getEmail())
                .tel(request.getTel())
                .numPeople(request.getNumPeople())
                .comeDate(Date.valueOf(request.getComeDate()))
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
    public DeleteScheduleResponse deleteScheduleById(Long scheduleId) {
        appointmentRepository.deleteById(scheduleId);
        return new DeleteScheduleResponse(scheduleId);
    }

    @Override
    public Page<Appointment> getAppointmentsByUsername(String isApproval, String username, Pageable pageable) {
        return appointmentRepository.getAppointmentsByUsername(isApproval, username, pageable);
    }

    @Override
    @Transactional
    public void permitAppointment(long appointmentId) {
        appointmentRepository.updateAppointmentStatus(appointmentId);
    }

    @Override
    @Transactional
    public UpdateScheduleResponse updateAppointment(UpdateScheduleRequest request) {
        appointmentRepository.updateAppointmentComeDate(Long.parseLong(request.getAppointmentId()), Date.valueOf(request.getComeDate()));
        return new UpdateScheduleResponse(request.getAppointmentId());
    }
}
