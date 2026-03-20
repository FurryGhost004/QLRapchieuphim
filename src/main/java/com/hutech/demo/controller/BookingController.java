package com.hutech.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hutech.demo.model.*;
import com.hutech.demo.repository.BookingRepository;
import com.hutech.demo.repository.SeatRepository;
import com.hutech.demo.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    @Autowired private UserService userService;
    @Autowired private MovieService movieService;
    @Autowired private ShowtimeService showtimeService;
    @Autowired private BookingService bookingService;
    @Autowired private SeatService seatService; // ĐÃ THÊM: Hết lỗi 'Cannot resolve symbol'
    @Autowired private SeatRepository seatRepository;
    @Autowired private TicketService ticketService;
    @Autowired private BookingRepository bookingRepository;

    // 1. Xem lịch chiếu
    @GetMapping("/booking/movie/{movieId}")
    public String getShowtimes(@PathVariable Long movieId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {
        Movie movie = movieService.findById(movieId);
        if (movie == null) return "error/404";

        List<LocalDate> dates = IntStream.range(0, 15)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .collect(Collectors.toList());

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        Map<String, List<Showtime>> cinemas = showtimeService.getShowtimesGroupedByCinema(movieId, selectedDate);

        model.addAttribute("movie", movie);
        model.addAttribute("dates", dates);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("cinemas", cinemas);

        return "showtime/showtime-list";
    }

    // 2. Trang chọn ghế
    @GetMapping("/booking/seats/{showtimeId}")
    public String selectSeat(@PathVariable Long showtimeId, Model model) {
        Showtime showtime = showtimeService.findById(showtimeId);
        List<Seat> seats = seatRepository.findByRoomIdOrderByRowNameAscSeatNumberAsc(showtime.getRoom().getId());
        List<Long> bookedSeatIds = ticketService.getBookedSeatIds(showtimeId);

        model.addAttribute("seats", seats);
        model.addAttribute("showtime", showtime);
        model.addAttribute("bookedSeatIds", bookedSeatIds);

        return "booking/select-seat";
    }

    // 3. Hiển thị trang Checkout (Xử lý tính toán giá VIP +10%, SWEETBOX +30%)
    @PostMapping("/booking/checkout")
    public String showCheckout(@RequestParam Long showtimeId,
                               @RequestParam("seatIds") String seatIdsStr, // Nhận chuỗi ID từ form
                               Model model) {

        Showtime showtime = showtimeService.findById(showtimeId);

        // Chuyển chuỗi "1,2,3" thành List<Long>
        List<Long> seatIds = Arrays.stream(seatIdsStr.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Seat> selectedSeats = seatService.findAllById(seatIds);

        double total = 0;
        double basePrice = showtime.getPrice();

        for (Seat seat : selectedSeats) {
            double currentSeatPrice = basePrice;

            // Logic tính giá đồng bộ với JavaScript trang chọn ghế
            String type = (seat.getType() != null) ? seat.getType().toUpperCase().trim() : "NORMAL";

            if ("VIP".equals(type)) {
                currentSeatPrice = basePrice * 1.1;
            } else if ("SWEETBOX".equals(type)) {
                currentSeatPrice = basePrice * 1.3; // 150,000 * 1.3 = 195,000
            }
            total += currentSeatPrice;
        }

        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", selectedSeats);
        model.addAttribute("seatIds", seatIds);
        model.addAttribute("totalAmount", total); // Truyền giá trị 195,000 sang View

        return "booking/checkout";
    }

    // 4. Lưu chính thức đặt vé vào Database
    @PostMapping("/booking/confirm")
    public String confirmBooking(@RequestParam Long showtimeId,
                                 @RequestParam List<Long> seatIds,
                                 Principal principal) {
        User user = userService.findByUsername(principal.getName());
        bookingService.confirmBooking(showtimeId, seatIds, user);
        return "redirect:/booking/success";
    }

    // 5. Trang thông báo thành công
    @GetMapping("/booking/success")
    public String successPage() {
        return "booking/success";
    }

    // 6. Xem lịch sử vé đã đặt của tôi
    @GetMapping("/tickets/my-tickets")
    public String myTickets(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Booking> myBookings = bookingRepository.findByUserOrderByBookingDateDesc(user);

        model.addAttribute("bookings", myBookings);
        return "booking/my-tickets";
    }
}