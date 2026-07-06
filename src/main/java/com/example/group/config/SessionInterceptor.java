package com.example.group.config;

import com.example.group.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        String requestURI = request.getRequestURI();
        
        // If trying to access admin page, check if user is admin
        if (requestURI.startsWith("/admin")) {
            if (!user.isAdmin()) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }
        
        return true;
    }
}