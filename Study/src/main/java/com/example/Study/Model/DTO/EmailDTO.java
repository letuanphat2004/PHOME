package com.example.Study.Model.DTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EmailDTO {
    private String to;
    private String subject;
    private String body;
}
