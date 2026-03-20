package com.hutech.demo.service;

import com.hutech.demo.model.Ticket;
import com.hutech.demo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Lấy danh sách ID của các ghế đã được mua cho một suất chiếu.
     * Dùng để hiển thị trạng thái 'booked' trên sơ đồ ghế.
     */
    public List<Long> getBookedSeatIds(Long showtimeId) {
        // Tìm tất cả vé thuộc suất chiếu này
        List<Ticket> tickets = ticketRepository.findByShowtimeId(showtimeId);

        // Chuyển đổi danh sách Ticket thành danh sách Long (Seat ID)
        return tickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toList());
    }

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}