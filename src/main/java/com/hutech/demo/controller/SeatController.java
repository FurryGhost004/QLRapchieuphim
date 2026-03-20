package com.hutech.demo.controller;

import com.hutech.demo.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/{showtimeId}")
    public Object getSeats(@PathVariable Long showtimeId) {
        return seatService.getSeatsByShowtime(showtimeId);
    }
}