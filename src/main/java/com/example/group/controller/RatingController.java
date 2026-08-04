package com.example.group.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import com.example.group.model.Rating;
import com.example.group.model.RatingRepository;
import com.example.group.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class RatingController {
    @Autowired
    private RatingRepository repo;
    
    @PostMapping("/rateLocation")
    public String rateLocation(@RequestParam String locationName, @RequestParam Double latitude, @RequestParam Double longitude,
            @RequestParam Integer rating, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (rating < 1 || rating > 5) {
            return "redirect:/map";
        }
        Optional<Rating> existing = repo.findByUserIdAndLatitudeAndLongitude(user.getId(), latitude, longitude);
        if (existing.isPresent())
        {
            Rating old = existing.get();
            old.setRating(rating);
            repo.save(old);
        }
        else
        {
            Rating newRating = new Rating();
            newRating.setLocationName(locationName);
            newRating.setLatitude(latitude);
            newRating.setLongitude(longitude);
            newRating.setUserId(user.getId());
            newRating.setRating(rating);
            repo.save(newRating);
        }
        return "redirect:/map";
    }

    @ResponseBody
    @GetMapping("/locationRating")
    public double getAverageRating(@RequestParam Double latitude, @RequestParam Double longitude)
    {
        List<Rating> ratings = repo.findByLatitudeAndLongitude(latitude, longitude);
        if (ratings.isEmpty())
        {
            return 0;
        }
        double total = 0;
        for (Rating r : ratings)
        {
            total += r.getRating();
        }
        return total / ratings.size();
    }
    
    @ResponseBody
    @GetMapping("/userRating")
    public int getUserRating( @RequestParam Double latitude, @RequestParam Double longitude, HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            return 0;
        }
        Optional<Rating> rating = repo.findByUserIdAndLatitudeAndLongitude(user.getId(), latitude, longitude);
        return rating.map(Rating::getRating).orElse(0);
    }

    @ResponseBody
    @GetMapping("/locationReviews")
    public List<Rating> getReviews(@RequestParam Double latitude, @RequestParam Double longitude) {
        return repo.findByLatitudeAndLongitude(latitude, longitude); // ✅ Returns List<Rating>
    }
}