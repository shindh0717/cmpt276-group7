package com.example.group.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import com.example.group.model.SavedLocation;
import com.example.group.model.SavedLocationRepository;
import com.example.group.model.User;

@Controller
public class SaveLocationController {
    @Autowired
    private SavedLocationRepository repo;

    @GetMapping("/map")
    public String showMapPage(Model model,HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if(user == null)
        {
            return "map";
        }
        List<SavedLocation> locations = repo.findByUserId(user.getId());
        model.addAttribute("savedLocations", locations);
        return "map";
    }
    @GetMapping("/location/delete")
    public String showMapPage(@RequestParam Long id, HttpSession session)
    {
    User user = (User) session.getAttribute("user");
    if (user == null)
    {
        return "redirect:/map";
    }
    repo.findById(id).ifPresent(location->{
        if ((location.getUserId() != null) && location.getUserId().equals(user.getId()))
        {
            repo.delete(location);
        }
    });
    return "redirect:/map";
    }
}
