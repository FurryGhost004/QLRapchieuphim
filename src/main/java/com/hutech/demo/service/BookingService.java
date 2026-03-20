package com.hutech.demo.service;

import com.hutech.demo.model.*;
import com.hutech.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<Long> getBookedSeatIdsByShowtime(Long showtimeId) {
        return ticketRepository.findByShowtimeId(showtimeId).stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toList());
    }

    @Transactional
    public void book(Long showtimeId, List<Long> seatIds) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());

        double ticketPrice = (showtime.getPrice() != 0) ? showtime.getPrice() : 50000;
        double total = ticketPrice * seatIds.size();

        // FIX TẠI ĐÂY: Đổi setTotalPrice thành setTotalAmount
        booking.setTotalAmount(total);

        Booking savedBooking = bookingRepository.save(booking);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế ID: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setShowtime(showtime);
            ticket.setBooking(savedBooking);
            ticket.setPrice(ticketPrice);

            ticketRepository.save(ticket);
        }
    }

    @Transactional
    public Booking confirmBooking(Long showtimeId, List<Long> seatIds, User user) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        // 1. Tạo đơn hàng (Booking) và gắn User vào
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setUser(user); // <--- QUAN TRỌNG: Gắn người dùng vào đây
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalAmount(showtime.getPrice() * seatIds.size());

        Booking savedBooking = bookingRepository.save(booking);

        // 2. Tạo các vé (Ticket) thuộc về đơn hàng này
        for (Long sId : seatIds) {
            Ticket ticket = new Ticket();
            ticket.setBooking(savedBooking); // Ticket trỏ về Booking, Booking trỏ về User
            ticket.setSeat(seatRepository.findById(sId).get());
            ticket.setShowtime(showtime);
            ticket.setPrice(showtime.getPrice());
            ticketRepository.save(ticket);
        }

        return savedBooking;
    }
}