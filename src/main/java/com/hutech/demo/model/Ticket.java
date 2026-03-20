package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Booking
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Ghế
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // Suất chiếu
    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    private double price;
}