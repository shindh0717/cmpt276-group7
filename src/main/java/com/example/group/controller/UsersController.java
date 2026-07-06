package com.example.group.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.group.model.User;
import com.example.group.model.UsersRepository;

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
        return "redirect:/profile";
    }

    @GetMapping("/login")
    public String Login() {

        return "login.html";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User LoginForm, Model model) {

        Optional<User> user = repo.findById(LoginForm.getEmail());

        if (user.isPresent()) {

            if (encoder.matches(LoginForm.getPassword(), user.get().getPassword())) {
                if (user.get().getRole() == "admin") {
                    return "admin.html";
                } else {
                    return "homepage.html";

                }

            } else {
                model.addAttribute("error", " Invalid email or password");
                return "login.html";
            }
        }

        return "signup.html";
    }

}
