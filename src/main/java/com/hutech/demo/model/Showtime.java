package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    // BẮT BUỘC PHẢI CÓ DÒNG NÀY ĐỂ LIÊN KẾT VỚI PHÒNG
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDate date;      // Ngày chiếu
    private LocalTime startTime; // Giờ chiếu
    private Double price;
}