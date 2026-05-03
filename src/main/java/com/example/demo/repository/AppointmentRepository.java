package com.example.demo.repository;

import com.example.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Logic: (Bắt đầu mới < Kết thúc cũ) VÀ (Kết thúc mới > Bắt đầu cũ)
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId " +
            "AND :start < a.end AND :end > a.start")
    Optional<Appointment> findOwnConflict(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("userId") Integer userId);

    @Query("SELECT a FROM Appointment a WHERE a.userId != :userId " +
            "AND :start < a.end AND :end > a.start")
    List<Appointment> findOthersAtSameTime(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("userId") Integer userId);

    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId " +
            "OR EXISTS (SELECT 1 FROM Participant p WHERE p.appId = a.appId AND p.userId = :userId) " +
            "ORDER BY a.start ASC")
    List<Appointment> findAllRelatedToUser(@Param("userId") Integer userId);
}