package com.example.demo.service;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.model.Appointment;
import com.example.demo.model.Participant;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalendarService {

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private ParticipantRepository participantRepo;

    @Autowired
    private ConflictManager conflictManager;

    /**
     * Lưu lịch cá nhân mới.
     */
    @Transactional
    public void saveIndividual(AppointmentRequest req, Integer userId) {
        Appointment app = new Appointment();
        app.setTitle(req.getTitle());
        app.setStart(req.getStart());
        app.setEnd(req.getEnd());
        app.setUserId(userId);
        appRepo.save(app);
    }

    /**
     * Tham gia vào Group Meeting và KIỂM TRA XUNG ĐỘT với lịch cá nhân.
     */
    @Transactional
    public void joinGroup(Integer appId, Integer userId) {
        // 1. Tìm thông tin lịch nhóm để biết thời gian của nó
        Appointment groupApp = appRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn nhóm."));

        // 2. Kiểm tra xem thời gian của nhóm này có trùng với lịch bận của người dùng không
        // Bước này ngăn chặn lỗi "chồng lịch" như trong image_1900c1.png
        Appointment conflict = conflictManager.checkConflict(groupApp.getStart(), groupApp.getEnd(), userId);

        if (conflict != null) {
            throw new RuntimeException("Xung đột: Bạn đã bận lịch '" + conflict.getTitle() + "' vào lúc này.");
        }

        // 3. Nếu không bận, thực hiện thêm vào danh sách tham gia
        Participant p = new Participant();
        p.setAppId(appId);
        p.setUserId(userId);
        participantRepo.save(p);
    }

    /**
     * Xóa lịch cũ và thay thế bằng lịch mới (Dùng cho chức năng Replace).
     */
    @Transactional
    public void deleteAndReplace(Integer oldAppId, AppointmentRequest req, Integer userId) {
        appRepo.deleteById(oldAppId);
        saveIndividual(req, userId);
    }
}