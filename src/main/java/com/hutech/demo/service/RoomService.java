package com.hutech.demo.service;

import com.hutech.demo.model.Room;
import com.hutech.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Tìm tất cả các phòng thuộc một Rạp cụ thể
    public List<Room> findByCinemaId(Long cinemaId) {
        return roomRepository.findByCinemaId(cinemaId);
    }

    // Tìm một phòng theo ID
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
    }

    // Lấy tất cả danh sách phòng (nếu cần)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }


    public void save(Room room) {
        roomRepository.save(room);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }
}