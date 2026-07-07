package com.example.group.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.group.Services.UserService;
import com.example.group.model.User;
import com.example.group.model.UsersRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private UsersRepository repo;

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(@RequestParam String email, @RequestParam String password, Model model) {
        if (repo.findById(email).isPresent()) {
            model.addAttribute("email_error", "That email is already registered to an account");
            return "signup";
        }
        if (password.length() < 8) {
            model.addAttribute("password_error", "Password has to be at least 8 characters");
            return "signup";
        }
        User newUser = new User();
        newUser.setEmail(email);
        final String hash = encoder.encode(password);
        newUser.setPassword(hash);
        newUser.setRole("regular");
        repo.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String Login(Model model) {
        model.addAttribute("loginForm", new User());

        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("loginForm")User loginForm, Model model, HttpSession session) {

        Optional<User> userOpt = repo.findById(loginForm.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (encoder.matches(loginForm.getPassword(), user.getPassword())) {
               
                session.setAttribute("user", user);
                
               
                if ("admin".equals(user.getRole())) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/profile"; 
                }
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
        } else {
            return "redirect:/signup";
        }

       
    }

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
        
        return "userProfile";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
