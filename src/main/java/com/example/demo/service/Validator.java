package com.example.demo.service;

import com.example.demo.dto.AppointmentRequest;
import org.springframework.stereotype.Service;

@Service
public class Validator {
    public boolean validate(AppointmentRequest req) {
        if (req.getTitle() == null || req.getTitle().isEmpty()) return false;
        if (req.getStart() == null || req.getEnd() == null) return false;

        // So sánh theo thứ tự Năm -> Tháng -> Ngày -> Giờ -> Phút
        // Nếu thời gian bắt đầu sau thời gian kết thúc hoặc bằng nhau -> Sai
        if (!req.getStart().isBefore(req.getEnd())) {
            return false;
        }

        return true;
    }
}