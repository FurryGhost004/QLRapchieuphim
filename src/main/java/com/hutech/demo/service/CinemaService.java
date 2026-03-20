package com.hutech.demo.service;

import com.hutech.demo.model.Cinema;
import com.hutech.demo.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    // Lấy tất cả danh sách rạp
    public List<Cinema> findAll() {
        return cinemaRepository.findAll();
    }

    // Tìm rạp theo ID
    public Cinema findById(Long id) {
        return cinemaRepository.findById(id).orElse(null);
    }

    // Lưu rạp (Thêm mới hoặc Cập nhật)
    public void save(Cinema cinema) {
        cinemaRepository.save(cinema);
    }

    // Xóa rạp theo ID
    public void delete(Long id) {
        cinemaRepository.deleteById(id);
    }
}