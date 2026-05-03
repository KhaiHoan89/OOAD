package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_participants") // Khớp hoàn toàn với tên bảng đúng
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_id")
    private Integer appId;

    @Column(name = "user_id")
    private Integer userId;

    public Participant() {}

    public Participant(Integer appId, Integer userId) {
        this.appId = appId;
        this.userId = userId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getAppId() { return appId; }
    public void setAppId(Integer appId) { this.appId = appId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}