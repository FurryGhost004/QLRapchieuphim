package com.hutech.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ngày giờ thực hiện đặt vé
    private LocalDateTime bookingDate;

    // Tổng tiền của đơn hàng (Đã đổi tên thành totalAmount để khớp với logic confirmBooking)
    private double totalAmount;

    // Người đặt vé
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Suất chiếu tương ứng
    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    // Danh sách các vé chi tiết thuộc đơn hàng này
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;


}