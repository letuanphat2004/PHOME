package com.example.Study.entity;

import com.example.Study.Common.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Role {
@Id
@GeneratedValue (strategy = GenerationType.IDENTITY)
private long role_id;
@Enumerated(EnumType.STRING)
private RoleEnum role_name;

}
