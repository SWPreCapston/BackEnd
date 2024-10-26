package com.precapston.precapston.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String url;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;



}
