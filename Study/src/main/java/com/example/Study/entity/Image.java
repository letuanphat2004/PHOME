package com.example.Study.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Image {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private Long room_id;
private String url;
}
