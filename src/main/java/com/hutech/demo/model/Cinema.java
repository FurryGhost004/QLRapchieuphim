package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Room> rooms; // Một rạp có nhiều phòng chiếu

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Showtime> showtimes;
}