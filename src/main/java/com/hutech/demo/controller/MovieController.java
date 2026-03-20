package com.hutech.demo.controller;

import com.hutech.demo.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    // danh sách phim
    @GetMapping("/movies")
    public String list(Model model) {
        model.addAttribute("movies", movieService.findAll());
        return "movie/movie-list";
    }

    // chi tiết phim
    @GetMapping("/movies/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("movie", movieService.findById(id));
        return "movie/movie-detail";
    }
}