package com.example.Study.Service;

import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Model.Request.Schedule.UpdateScheduleRequest;
import com.example.Study.Model.Respone.AppointmentResponse;
import com.example.Study.Model.Respone.DeleteScheduleResponse;
import com.example.Study.Model.Respone.Schedule.UpdateScheduleResponse;
import com.example.Study.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface AppointmentService {
    AppointmentResponse createAppointment (AppointmentRequest request);

    Page<Appointment> getAllByUsername(String username, Pageable pageable) throws ParseException;

    DeleteScheduleResponse deleteScheduleById (Long scheduleId);

    Page<Appointment> getAppointmentsByUsername (String isApproval, String username, Pageable pageable);

    void permitAppointment(long appointmentId);

    UpdateScheduleResponse updateAppointment (UpdateScheduleRequest request);
}
