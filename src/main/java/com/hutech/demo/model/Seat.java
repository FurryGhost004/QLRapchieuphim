package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rowName;
    private int seatNumber;
    private String type;

    // Đổi từ Cinema sang Room để khớp với mappedBy trong class Room
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}