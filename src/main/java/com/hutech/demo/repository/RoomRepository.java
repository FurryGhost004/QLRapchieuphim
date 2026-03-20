package com.hutech.demo.repository;

import com.hutech.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Spring Data JPA sẽ tự hiểu là tìm Room có cinema.id = cinemaId
    List<Room> findByCinemaId(Long cinemaId);
}