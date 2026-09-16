package com.example.Study.entity;



import com.example.Study.Common.RoomType;
import com.example.Study.Common.RoomTypeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Room extends BaseEntity {
    //@OneToOne(cascade = CascadeType.ALL)
    private long user_id;

    @Size(max = 255)
    private String address;

    private long capacity;

    @Min(1)
    private double price;

    @Size(max = 5000)
    private String description;

    //@Enumerated(EnumType.STRING)
    @Convert(converter = RoomTypeConverter.class)
    private RoomType roomType;

    private double area;

    private String isApproval;

    @Size(max = 500)
    private String moderationNote;

    private String image;

}
