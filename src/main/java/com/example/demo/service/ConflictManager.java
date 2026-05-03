package com.example.demo.service;

import com.example.demo.model.Appointment;
import com.example.demo.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ConflictManager {

    @Autowired
    private AppointmentRepository appRepo;

    /**
     * Kiểm tra xem một khoảng thời gian có trùng với lịch nào của User không.
     * Trả về Appointment gây xung đột hoặc null nếu trống lịch.
     */
    public Appointment checkConflict(LocalDateTime start, LocalDateTime end, Integer userId) {
        // Sử dụng findOwnConflict để tìm các lịch hiện có của userId trong khoảng start-end
        return appRepo.findOwnConflict(start, end, userId).orElse(null);
    }
}