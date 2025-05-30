package com.example.Study.Model.Request.Schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScheduleRequest {
    private String appointmentId, comeDate;
}
