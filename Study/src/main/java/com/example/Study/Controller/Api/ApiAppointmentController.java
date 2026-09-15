package com.example.Study.Controller.Api;

import com.example.Study.Model.DTO.AppointmentDTO;
import com.example.Study.Model.Request.AppointmentRequest;
import com.example.Study.Model.Request.Schedule.UpdateScheduleRequest;
import com.example.Study.Service.AppointmentService;
import com.example.Study.Service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class ApiAppointmentController {
    private final AppointmentService appointments;
    private final RoomService rooms;

    public ApiAppointmentController(AppointmentService appointments, RoomService rooms) {
        this.appointments = appointments;
        this.rooms = rooms;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Tenant')")
    public List<AppointmentDTO> mine(Authentication authentication) throws ParseException {
        return appointments.getAllByUsername(authentication.getName(), PageRequest.of(0, 100))
                .getContent().stream().map(this::withRoom).toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Tenant')")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody AppointmentBody body, Authentication authentication) {
        appointments.createAppointment(AppointmentRequest.builder().username(authentication.getName())
                .room_id(Long.toString(body.roomId())).fullname(body.fullname()).email(body.email()).tel(body.tel())
                .numPeople(body.numPeople()).comeDate(body.comeDate()).transportation(body.transportation()).build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('Tenant')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid DateBody body, Authentication authentication) {
        UpdateScheduleRequest request = new UpdateScheduleRequest();
        request.setAppointmentId(Long.toString(id)); request.setComeDate(body.comeDate());
        appointments.updateAppointment(request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Tenant')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, Authentication authentication) {
        appointments.deleteScheduleById(id, authentication.getName());
    }

    @GetMapping("/received")
    @PreAuthorize("hasAuthority('Landlord')")
    public List<AppointmentDTO> received(@RequestParam(defaultValue = "false") String approved,
                                         Authentication authentication) {
        return appointments.getAppointmentsByUsername(approved, authentication.getName(), PageRequest.of(0, 100))
                .getContent().stream().map(this::withRoom).toList();
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('Landlord')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable long id, Authentication authentication) {
        appointments.permitAppointment(id, authentication.getName());
    }

    private AppointmentDTO withRoom(com.example.Study.entity.Appointment appointment) {
        AppointmentDTO dto = AppointmentDTO.toDto(appointment);
        dto.setRoomAddress(rooms.getInforRoomByRoom_Id(Long.toString(appointment.getRoom_id())).getAddress());
        return dto;
    }

    public record AppointmentBody(long roomId, @NotBlank String fullname,
                                  @Email @NotBlank String email, @NotBlank String tel,
                                  @Min(1) int numPeople, @NotBlank String comeDate,
                                  @NotBlank String transportation) {}
    public record DateBody(@NotBlank String comeDate) {}
}
