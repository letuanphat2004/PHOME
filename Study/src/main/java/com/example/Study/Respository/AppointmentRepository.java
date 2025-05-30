package com.example.Study.Respository;

import com.example.Study.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> getAllByUsernameOrderByComeDateAsc(String username, Pageable pageable);

    @Query("SELECT a FROM Appointment AS a " +
            "INNER JOIN Room as r " +
            "ON r.id = a.room_id " +
            "INNER join User as u " +
            "ON u.id = r.user_id " +
            "WHERE u.username = :username AND a.isApproval = :isApproval " +
            "ORDER BY a.comeDate, a.room_id")
    Page<Appointment> getAppointmentsByUsername (@Param("isApproval") String isApproval, @Param("username") String username, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Appointment as a " +
            "SET a.isApproval = 'true' " +
            "WHERE a.id = :appointmentId")
    void updateAppointmentStatus(@Param("appointmentId") long appointmentId);

    @Modifying
    @Transactional
    @Query("UPDATE Appointment as a " +
            "SET a.comeDate = :comeDate " +
            "WHERE a.id = :appointment_id")
    void updateAppointmentComeDate(@Param("appointment_id") long appointment_id, @Param("comeDate") Date comeDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.room_id = :room_id")
    void deleteAppointmentByRoom_id(@Param("room_id") long room_id);

}
