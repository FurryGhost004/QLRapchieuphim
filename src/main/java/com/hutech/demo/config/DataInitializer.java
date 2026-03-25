package com.hutech.demo.config;

import com.hutech.demo.model.*;
import com.hutech.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            User user = new User();
            user.setUsername("test");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setPhone("0123456789");
            user.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setPhone("0987654321");
            admin.setEnabled(true);
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }

        if (movieRepository.count() == 0) {
            Genre action = new Genre();
            action.setName("Hành Động");
            genreRepository.save(action);

            Movie movie = new Movie();
            movie.setTitle("Avenger: Endgame");
            movie.setDescription(
                    "Siêu phẩm điện ảnh vĩ đại nhất của vũ trụ Marvel. Cuộc chiến cuối cùng chống lại Thanos.");
            movie.setDirector("Anthony Russo, Joe Russo");
            movie.setActors("Robert Downey Jr., Chris Evans, Mark Ruffalo");
            movie.setLanguage("Tiếng Anh - Phụ đề tiếng Việt");
            movie.setDuration(181);
            movie.setRated("T13");
            movie.setReleaseDate(LocalDate.now().minusDays(10));
            // Use a valid image URL for testing
            movie.setImage("https://upload.wikimedia.org/wikipedia/vi/a/a9/Avengers_Endgame_poster.jpg");
            movie.setGenre(action);
            movieRepository.save(movie);

            Cinema cinema = new Cinema();
            cinema.setName("CGV Vincom Center");
            cinema.setAddress("72 Lê Thánh Tôn, Bến Nghé, Quận 1, Hồ Chí Minh");
            cinemaRepository.save(cinema);

            Room room = new Room();
            room.setName("Phòng chiếu số 1");
            room.setCinema(cinema);
            roomRepository.save(room);

            List<Seat> seats = new ArrayList<>();
            for (int r = 0; r < 2; r++) { // Row A, B
                String rowName = String.valueOf((char) ('A' + r));
                for (int i = 1; i <= 5; i++) { // Seat 1 to 5
                    Seat seat = new Seat();
                    seat.setRowName(rowName);
                    seat.setSeatNumber(i);
                    seat.setType((r == 0 && i > 3) ? "VIP" : "NORMAL");
                    seat.setRoom(room);
                    seats.add(seat);
                }
            }
            seatRepository.saveAll(seats);

            Showtime showtime = new Showtime();
            showtime.setMovie(movie);
            showtime.setCinema(cinema);
            showtime.setRoom(room);
            showtime.setDate(LocalDate.now());
            // Make showtime available soon
            showtime.setStartTime(LocalTime.of(20, 0));
            showtime.setPrice(100000.0);
            showtimeRepository.save(showtime);
        }
    }
}
