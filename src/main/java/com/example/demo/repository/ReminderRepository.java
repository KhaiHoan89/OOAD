package com.example.demo.repository;

import com.example.demo.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    // Lưu thông tin nhắc nhở vào SQL_Server
}