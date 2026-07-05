package com.example.group.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController {
    @GetMapping("/users/view")
    public String getAllusers() {
        return "Authentication/login";
    }

    @GetMapping("/login")
    public String login() {
        return "Authentication/login";
    }

    @GetMapping("/signin")
    public String signin() {
        return "Authentication/signin";
    }
}