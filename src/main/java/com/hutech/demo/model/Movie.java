package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // tên phim

    @Column(columnDefinition = "TEXT")
    private String description;    // mô tả

    private String director;       // đạo diễn
    private String actors;         // diễn viên

    private String language;       // ngôn ngữ

    private int duration;          // thời lượng (phút)

    // Đã thay đổi từ double rating sang String rated
    private String rated;          // Phân loại độ tuổi (Ví dụ: P, K, T13, T16, T18)

    private LocalDate releaseDate; // ngày khởi chiếu
    private String image;

    // thể loại
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;
}