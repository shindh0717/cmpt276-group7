package com.example.group.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.view.RedirectView;

import com.example.group.Services.UserService;
import com.example.group.model.SavedLocation;
import com.example.group.model.SavedLocationRepository;
import com.example.group.model.User;
import com.example.group.model.UserRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class UsersController {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository repo;

    @Autowired
    private SavedLocationRepository loco;

    @GetMapping("/")
    public RedirectView process(){
        return new RedirectView("/signup");
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

/*     @GetMapping("/map")
    public String showMapPage() {
        return "map";
    }*/

    @PostMapping("/signup")
    public String addUser(@RequestParam String email, @RequestParam String password, Model model) {
        if (repo.findByEmail(email).isPresent()) {
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

        Optional<User> userOpt = repo.findByEmail(loginForm.getEmail());

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

    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        request.getSession().invalidate();
        return "redirect:/login";
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
        User currentUser = (User)session.getAttribute("user");
        if (currentUser == null){
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);
        return "userProfile";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
     @PostMapping("/saveLocation")
    public String saveLocation( @RequestParam String locationName, @RequestParam Double latitude, @RequestParam Double longitude, HttpSession session){
        User loggedUser = (User) session.getAttribute("user");

        if(loggedUser == null){
            return "redirect:/login";
        }

        SavedLocation location = new SavedLocation();
        location.setLocationName(locationName);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setUserId(loggedUser.getId());

        loco.save(location);

       return  "redirect:/map";
}
}
