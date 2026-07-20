package com.example.group.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;



import com.example.group.model.Route;
import com.example.group.model.RouteRepository;
import com.example.group.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class RouteController {

    private final RouteRepository routeRepository;

    public RouteController(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @PostMapping("/routes")
    public String saveRoute(
            @RequestBody Route route,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        route.setCreatedBy(currentUser);

        routeRepository.save(route);

        return "redirect:/routes/"+route.getId();
    }

@PostMapping("/routes/{id}/share")
public String shareRoute(@PathVariable Long id, HttpSession session) {
    User currentUser = (User) session.getAttribute("user");
    Route route = routeRepository.findById(id).orElseThrow();
    if (currentUser == null) {
        return "redirect:/login";
    }
    if (!route.getCreatedBy().equals(currentUser)) {
        return "redirect:/access-denied";
    }
    route.setShared(true);
    routeRepository.save(route);
    return "redirect:/routes/"+id;
}

@GetMapping("/routes/{id}")
public String viewRoute(@PathVariable Long id, Model model, HttpServletRequest request){
    Route route = routeRepository.findById(id).orElseThrow();
    String shareUrl = request.getRequestURL().toString();
    model.addAttribute("route", route);
    model.addAttribute("shareUrl", shareUrl);
    return "shared-route";
}
}
