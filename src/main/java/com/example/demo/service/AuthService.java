package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> authenticate(String email, String password) {
        // Tìm user theo email, sau đó so sánh mật khẩu đã được trim()
        return userRepository.findByEmail(email.trim())
                .filter(user -> user.getPassword().trim().equals(password.trim()));
    }
}