package com.example.group.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userLogin {

    @GetMapping("/login")
    public String Login(){

        return "login.html";
    }

    
}
