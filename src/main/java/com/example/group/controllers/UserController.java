package com.example.group.controllers;

import com.example.group.model.User;
import com.example.group.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // ===== SHOW LOGIN PAGE =====
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    // ===== SHOW REGISTER PAGE =====
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    // ===== PROCESS LOGIN =====
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        
        User user = userService.getUserByEmail(email);
        
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password!");
            return "login";
        }
        
        session.setAttribute("user", user);
        
        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/profile";
        }
    }
    
    // ===== PROCESS REGISTRATION =====
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        
        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }
        
        User user = new User(username, email, password);
        userService.saveUser(user);
        
        model.addAttribute("message", "Registration successful! Please login.");
        return "login";
    }
    
    // ===== ADMIN DASHBOARD (Shows ALL users) =====
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
    
    // ===== USER PROFILE (Shows ONLY own info) =====
    @GetMapping("/profile")
    public String userProfile(Model model, HttpSession session) {
        
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        
        return "user-profile";
    }
    
    // ===== LOGOUT =====
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
    
    // ===== ACCESS DENIED =====
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}