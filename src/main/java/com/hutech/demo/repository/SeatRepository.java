package com.hutech.demo.repository;

import com.hutech.demo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Tìm ghế dựa trên ID của Cinema (Seat -> Room -> Cinema)
    List<Seat> findByRoomCinemaId(Long cinemaId);

    // Tìm ghế của một phòng và sắp xếp để hiển thị đúng sơ đồ
    List<Seat> findByRoomIdOrderByRowNameAscSeatNumberAsc(Long roomId);

    // Xóa ghế theo Room ID (Cần @Modifying nếu dùng Query thủ công)
    @Modifying
    @Query("DELETE FROM Seat s WHERE s.room.id = :roomId")
    void deleteByRoomId(@Param("roomId") Long roomId);
}