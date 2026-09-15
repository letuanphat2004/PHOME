package com.example.Study.Service.Impl;

import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Respository.AppointmentRepository;
import com.example.Study.Respository.RoomRepository;
import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.Appointment;
import com.example.Study.entity.Room;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentServiceImplTest {
    @Test
    void rejectsDuplicateBookingForSameRoomAndDate() {
        AppointmentRepository appointments = mock(AppointmentRepository.class);
        RoomRepository rooms = mock(RoomRepository.class);
        AppointmentServiceImpl service = new AppointmentServiceImpl(appointments, rooms, mock(UserRepository.class));
        Room room = Room.builder().capacity(2).isApproval("true").build();
        room.setId(8L);
        LocalDate visitDate = LocalDate.now().plusDays(2);
        when(rooms.findById(8L)).thenReturn(Optional.of(room));
        when(appointments.existsSameBooking("tenant", 8L, Date.valueOf(visitDate))).thenReturn(true);
        AppointmentRequest request = AppointmentRequest.builder().username("tenant").room_id("8")
                .fullname("Nguyen An").email("tenant@phome.vn").tel("0901000001")
                .numPeople(1).comeDate(visitDate.toString()).transportation("Xe may").build();

        assertThrows(IllegalArgumentException.class, () -> service.createAppointment(request));
        verify(appointments, never()).save(any(Appointment.class));
    }
}
