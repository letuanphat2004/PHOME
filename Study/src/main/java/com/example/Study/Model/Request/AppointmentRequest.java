package com.example.Study.Model.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    private String username;

    @Column(nullable = false, length = 100)
    private String fullname;

    @Column(nullable = false, length = 15)
    private String tel;

    @Column(nullable = false, length = 50)
    private String email;

    @Min(1)
    private int numPeople;

    private String comeDate;

    @Column(nullable = false, length = 50)
    private String transportation;

    private String room_id;
}
