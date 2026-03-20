package com.hutech.demo.repository;

import com.hutech.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Truy vấn tất cả vé dựa trên ID của suất chiếu
    List<Ticket> findByShowtimeId(Long showtimeId);
}