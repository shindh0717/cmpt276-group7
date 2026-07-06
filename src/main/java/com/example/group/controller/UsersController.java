package com.example.group.controller;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.group.model.User;
import com.example.group.model.UsersRepository;

@Controller
public class UsersController {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsersRepository repo;
    public UsersController(UsersRepository repo) {
        this.repo = repo;
    }
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
    public String Login(){

        return "login.html";
    }
}
