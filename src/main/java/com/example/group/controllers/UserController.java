package com.example.group.controllers;

import com.example.group.model.User;
import com.example.group.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/access-denied";
        }
        
        List<User> allUsers = userService.getAllUsers();
        
        model.addAttribute("users", allUsers);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalUsers", allUsers.size());
        
        return "admin-dashboard";
    }
    
    @GetMapping("/profile")
    public String userProfile(Model model, HttpSession session) {
        
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        
        return "user-profile";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}