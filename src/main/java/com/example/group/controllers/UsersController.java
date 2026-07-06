package com.example.group.controllers;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.group.models.UserRepository;
import com.example.group.models.Users;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UsersController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/administrators/Dashboard")
    public String Dashboard(Model model) {
        List<Users> users = userRepo.findAll();
        model.addAttribute("us",users);
        return "administrators/Dashboard";
    }

    @GetMapping("/Home")
    public String Home() {
        return "user/Home";
    }

    @GetMapping("/login")
    public String login() {
        return "Authentication/login";
    }

    @GetMapping("/signin")
    public String signin() {
        return "Authentication/signin";
    }

    @PostMapping("Authentication/signin")
    public String addUser (@RequestParam Map<String, String> newuser, HttpServletResponse response) {
        String newName = newuser.get("uname");
        String newpwd = newuser.get("p");
        userRepo.save(new Users(newName,newpwd));
        response.setStatus(201);
        return "Authentication/login";
    }
}