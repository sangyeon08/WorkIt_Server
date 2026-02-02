package com.jubilee.workit.entity;

import jakarta.persistence.*;
//카테고리에 들어가는 값들을 내가 하나하나 다 넣어야하나? -> 아니면 enum같은걸로 만들어서 넣어야하나?
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
