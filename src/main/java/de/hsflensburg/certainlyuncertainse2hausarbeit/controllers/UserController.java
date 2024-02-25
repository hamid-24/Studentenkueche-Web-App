package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.UpdateUserRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // gets the current user information
    @GetMapping
    public String showEditForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("me");
        model.addAttribute("user", user);
        // switches the profile tab as the current active one in frontend
        model.addAttribute("active", "profile");
        return "user-details";
    }

    // posts the new user information
    @PostMapping
    public String update(UpdateUserRequest updateUserRequest, @RequestParam("image") MultipartFile image, HttpSession session) {
        User user = (User) session.getAttribute("me");
        if(!image.isEmpty()){
            userService.updateUserImage(user, image);
        }
        userService.updateUserDetails(user, updateUserRequest);
        return "redirect:/user";
    }
}
