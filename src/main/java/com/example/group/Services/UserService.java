package com.example.group.Services;

import com.example.group.model.User;
import com.example.group.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get all users (for admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // Get user by email (for login)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    // Save user to database
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}