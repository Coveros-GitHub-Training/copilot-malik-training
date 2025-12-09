package com.coveros.training.flavorhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for serving the main web pages
 */
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    /**
     * Display the recipes browsing page with pagination support
     * @param page Current page number (default: 1, 1-indexed for display)
     * @param difficulty Optional difficulty filter
     * @param cuisine Optional cuisine filter
     * @param search Optional search term
     * @param model Model to pass data to the view
     * @return the recipes view template
     */
    @GetMapping("/recipes")
    public String recipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String search,
            Model model) {
        // Add page and filter parameters to model for JavaScript to use
        model.addAttribute("currentPage", page);
        model.addAttribute("difficulty", difficulty != null ? difficulty : "");
        model.addAttribute("cuisine", cuisine != null ? cuisine : "");
        model.addAttribute("search", search != null ? search : "");
        return "recipes";
    }
}
