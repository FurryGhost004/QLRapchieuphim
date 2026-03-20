package com.hutech.demo.repository;

import com.hutech.demo.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieId(Long movieId);

    // Tìm suất chiếu theo ID phim và Ngày chiếu
    List<Showtime> findByMovieIdAndDate(Long movieId, LocalDate date);

}