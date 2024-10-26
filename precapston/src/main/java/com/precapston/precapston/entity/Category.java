package com.precapston.precapston.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 5000)
    private String content;

}
