package com.example.Study.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class User extends BaseEntity  {
@Column (unique = true)
@Size(max = 100)
@NotNull
private String username;
@Size(max = 100)
@NotNull
private String password;
@Size(max = 100)
@NotNull
private String fullname;
private String tel;
private String email;
private long role_id;
private String linkAvatar;

}
