package com.example.demo.repository;

import com.example.demo.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    // Kiểu dữ liệu Integer ở đây tương ứng với @Id trong class Participant
}