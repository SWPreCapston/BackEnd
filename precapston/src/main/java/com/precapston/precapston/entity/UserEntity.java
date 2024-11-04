package com.precapston.precapston.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 20, unique = true)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    private String role;
}
