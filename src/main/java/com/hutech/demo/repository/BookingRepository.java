package com.hutech.demo.repository;

import com.hutech.demo.model.Booking;
import com.hutech.demo.model.User; // Import Model User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Thêm dòng này để hết lỗi findByUser... trong Controller
    List<Booking> findByUserOrderByBookingDateDesc(User user);

    List<Booking> findAllByOrderByIdDesc();

    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.showtime.id = :showtimeId")
    void deleteByShowtimeId(@Param("showtimeId") Long showtimeId);
}