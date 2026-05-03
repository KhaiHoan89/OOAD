package com.example.demo.controller;

import com.example.demo.dto.AppointmentRequest;
import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.CalendarService;
import com.example.demo.service.ConflictManager;
import com.example.demo.service.Validator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ConflictManager conflictManager;

    @Autowired
    private Validator validator;

    @PostMapping("/submit")
    public ResponseEntity<?> handleSubmit(@RequestBody AppointmentRequest req,
                                          @RequestParam(required = false) Integer joinId,
                                          @RequestParam(required = false) String option,
                                          HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập!");

        if (!validator.validate(req)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thời gian không hợp lệ!");
        }

        // XỬ LÝ REPLACE - PHÁ VÒNG LẶP
        if ("Replace".equals(option)) {
            Appointment ownConflict = conflictManager.checkConflict(req.getStart(), req.getEnd(), currentUser.getUserId());
            if (ownConflict != null) {
                appRepo.deleteById(ownConflict.getAppId());
            }
            if (joinId != null) {
                calendarService.joinGroup(joinId, currentUser.getUserId());
                return ResponseEntity.ok("Ghi đè và tham gia nhóm thành công!");
            } else {
                calendarService.saveIndividual(req, currentUser.getUserId());
                return ResponseEntity.ok("Ghi đè và tạo lịch mới thành công!");
            }
        }

        // CHECK GROUP MEETING (GM) - TRẢ VỀ DANH SÁCH
        if (joinId == null && !"Ignore".equals(option)) {
            List<Appointment> others = appRepo.findOthersAtSameTime(req.getStart(), req.getEnd(), currentUser.getUserId());
            if (!others.isEmpty()) {
                return ResponseEntity.status(HttpStatus.SEE_OTHER).body(others);
            }
        }

        // CHECK CONFLICT CÁ NHÂN
        Appointment ownConflict = conflictManager.checkConflict(req.getStart(), req.getEnd(), currentUser.getUserId());
        if (ownConflict != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Trùng lịch: " + ownConflict.getTitle());
        }

        // LƯU DỮ LIỆU
        if (joinId != null) {
            calendarService.joinGroup(joinId, currentUser.getUserId());
            return ResponseEntity.ok("Tham gia nhóm thành công!");
        } else {
            calendarService.saveIndividual(req, currentUser.getUserId());
            return ResponseEntity.ok("Tạo lịch thành công!");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(appRepo.findAllRelatedToUser(user.getUserId()));
    }
}