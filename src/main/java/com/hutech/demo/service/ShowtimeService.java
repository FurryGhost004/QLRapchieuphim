package com.hutech.demo.service;

import com.hutech.demo.model.Showtime;
import com.hutech.demo.repository.ShowtimeRepository;
import com.hutech.demo.repository.BookingRepository; // Cần thêm repo này để xóa dữ liệu liên quan
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository; // Inject để xử lý lỗi khóa ngoại khi xóa

    // Tìm tất cả suất chiếu
    public List<Showtime> findAll() {
        return showtimeRepository.findAll();
    }

    // Tìm suất chiếu theo ID
    public Showtime findById(Long id) {
        return showtimeRepository.findById(id).orElse(null);
    }

    // Tìm theo phim (Fix lỗi gạch đỏ ở Controller)
    public List<Showtime> getByMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    // Nhóm suất chiếu theo rạp để hiển thị giao diện chọn lịch chiếu
    public Map<String, List<Showtime>> getShowtimesGroupedByCinema(Long movieId, LocalDate date) {
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndDate(movieId, date);
        return showtimes.stream()
                .collect(Collectors.groupingBy(st -> st.getCinema().getName()));
    }

    // Lưu suất chiếu (Thêm mới hoặc Cập nhật)
    @Transactional
    public void save(Showtime showtime) {
        showtimeRepository.save(showtime);
    }

    /**
     * PHƯƠNG THỨC XÓA AN TOÀN:
     * Giải quyết lỗi: DataIntegrityViolationException (Foreign Key Constraint)
     */
    @Transactional
    public void deleteShowtime(Long id) {
        // 1. Xóa tất cả các Booking (đơn hàng) liên quan đến suất chiếu này trước
        // Điều này gỡ bỏ ràng buộc khóa ngoại trong DB
        bookingRepository.deleteByShowtimeId(id);

        // 2. Sau đó mới xóa Suất chiếu chính
        showtimeRepository.deleteById(id);
    }

    // Giữ lại hàm delete cũ nếu cần (nhưng nên dùng deleteShowtime ở trên)
    public void delete(Long id) {
        this.deleteShowtime(id);
    }
}