package com.example.group.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController {
    @GetMapping("/users/view")
    public String getAllusers() {
        return "users/showAll";
    }

    @GetMapping("/showAll")
    public String showAll() {
        return "users/showAll";
    }

    @GetMapping("/signin")
    public String signin() {
        return "users/signin";
    }
}