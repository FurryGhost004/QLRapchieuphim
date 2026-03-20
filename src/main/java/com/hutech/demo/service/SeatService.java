package com.hutech.demo.service;

import com.hutech.demo.dto.SeatDTO;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.Seat;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Tìm tất cả ghế theo danh sách ID.
     * Phương thức này giúp Controller lấy thông tin loại ghế (Type) để tính giá vé (+10%, +30%).
     */
    public List<Seat> findAllById(List<Long> ids) {
        return seatRepository.findAllById(ids);
    }

    /**
     * Lấy danh sách ghế dựa trên Cinema ID thông qua Room.
     */
    public List<Seat> getSeatsByCinema(Long cinemaId) {
        return seatRepository.findByRoomCinemaId(cinemaId);
    }

    /**
     * Lấy toàn bộ danh sách ghế (Thường dùng cho mục đích quản trị hoặc debug).
     */
    public List<Seat> getSeatsByShowtime(Long showtimeId) {
        return seatRepository.findAll();
    }

    /**
     * Lưu một ghế đơn lẻ.
     */
    public void save(Seat seat) {
        seatRepository.save(seat);
    }

    /**
     * Khởi tạo hoặc cập nhật sơ đồ ghế cho một phòng chiếu.
     * Xóa các ghế cũ và tạo mới dựa trên dữ liệu DTO (thường là sơ đồ 10x12).
     */
    @Transactional
    public void saveAllSeatsForRoom(Long roomId, List<SeatDTO> seatData) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + roomId));

        // 1. Xóa toàn bộ ghế cũ của phòng này để làm mới sơ đồ
        seatRepository.deleteByRoomId(roomId);

        // 2. Chuyển đổi từ DTO sang Entity Seat và thiết lập mối quan hệ với Room
        List<Seat> seats = seatData.stream().map(dto -> {
            Seat seat = new Seat();
            seat.setRowName(dto.getRowName());
            seat.setSeatNumber(dto.getSeatNumber());
            seat.setType(dto.getType()); // NORMAL, VIP, SWEETBOX
            seat.setRoom(room);
            return seat;
        }).collect(Collectors.toList());

        // 3. Lưu toàn bộ danh sách ghế mới vào Database
        seatRepository.saveAll(seats);
    }
}