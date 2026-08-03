package com.example.group.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.group.model.Route;
import com.example.group.model.RouteRepository;
import com.example.group.model.User;


@RestController
@RequestMapping("/api/routes")
public class RouteAPIController {


    private final RouteRepository routeRepository;


    public RouteAPIController(RouteRepository routeRepository){
        this.routeRepository = routeRepository;
    }



    @PostMapping("/create")
        public Route createRoute(@RequestBody Route route,
                         HttpSession session) {

        User currentUser =
        (User) session.getAttribute("user");

        if (currentUser != null) {
        route.setCreatedBy(currentUser);
    }

    return routeRepository.save(route);
}
}