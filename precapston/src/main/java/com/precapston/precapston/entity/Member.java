package com.precapston.precapston.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name = "birth", columnDefinition = "DATETIME(6)")
    private LocalDateTime birth;

    @Column(length=15,nullable = false)
    private String phone_num;

    @Column(length=20, nullable = false)
    private String login_id;

    @Column(length=20, nullable = false)
    private String login_pw;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updated_at;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(name = "inactive_date", columnDefinition = "DATETIME(6)")
    private LocalDateTime inactive_date;

    @Column(length=50, nullable = false)
    private String email;
}
