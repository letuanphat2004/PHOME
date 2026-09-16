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

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.username = :username " +
            "AND a.room_id = :roomId AND a.comeDate = :comeDate AND a.isApproval <> 'rejected'")
    boolean existsSameBooking(@Param("username") String username, @Param("roomId") long roomId,
                              @Param("comeDate") Date comeDate);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.username = :username " +
            "AND a.room_id = :roomId AND a.comeDate = :comeDate AND a.id <> :appointmentId " +
            "AND a.isApproval <> 'rejected'")
    boolean existsOtherSameBooking(@Param("username") String username, @Param("roomId") long roomId,
                                   @Param("comeDate") Date comeDate,
                                   @Param("appointmentId") long appointmentId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.username = :username " +
            "AND a.room_id = :roomId AND a.isApproval = 'true'")
    boolean existsApprovedVisit(@Param("username") String username, @Param("roomId") long roomId);

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
    @Query("UPDATE Appointment a SET a.isApproval = 'rejected' WHERE a.id = :appointmentId")
    void rejectAppointment(@Param("appointmentId") long appointmentId);

    @Query("SELECT COUNT(a) FROM Appointment a JOIN Room r ON r.id = a.room_id " +
            "JOIN User u ON u.id = r.user_id WHERE u.username = :username AND a.isApproval = :status")
    long countByLandlordAndStatus(@Param("username") String username, @Param("status") String status);

    @Query("SELECT COUNT(a) FROM Appointment a JOIN Room r ON r.id = a.room_id " +
            "JOIN User u ON u.id = r.user_id WHERE u.username = :username " +
            "AND a.isApproval = 'true' AND a.comeDate >= :today")
    long countUpcomingApprovedByLandlord(@Param("username") String username, @Param("today") Date today);

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
