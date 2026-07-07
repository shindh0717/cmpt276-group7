package com.example.group.controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UsersController {
    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
