package com.hutech.demo.controller;

import com.hutech.demo.dto.SeatDTO;
import com.hutech.demo.dto.RoomDTO;
import com.hutech.demo.model.*;
import com.hutech.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private MovieService movieService;
    @Autowired private GenreService genreService;
    @Autowired private CinemaService cinemaService;
    @Autowired private RoomService roomService;
    @Autowired private ShowtimeService showtimeService;
    @Autowired private SeatService seatService;

    // ==========================================
    // QUẢN LÝ PHIM (MOVIES)
    // ==========================================
    @GetMapping("/movies")
    public String listMovies(Model model) {
        model.addAttribute("movies", movieService.findAll());
        return "admin/movie-manage";
    }

    @GetMapping("/movies/add")
    public String addMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreService.findAll());
        return "admin/movie-form";
    }

    @GetMapping("/movies/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id);
        if (movie == null) return "redirect:/admin/movies?error=notfound";

        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreService.findAll());
        return "admin/movie-form";
    }

    @PostMapping(value = "/movies/save", consumes = {"multipart/form-data"})
    public String saveMovie(@ModelAttribute Movie movie,
                            @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String rootPath = System.getProperty("user.dir");
                String uploadDir = rootPath + File.separator + "uploads";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File saveFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                file.transferTo(saveFile);
                movie.setImage(fileName);
            } else if (movie.getId() != null) {
                Movie oldMovie = movieService.findById(movie.getId());
                if (oldMovie != null) movie.setImage(oldMovie.getImage());
            }
            movieService.save(movie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/movies";
    }

    @GetMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return "redirect:/admin/movies";
    }

    // ==========================================
    // QUẢN LÝ RẠP & PHÒNG (CINEMAS & ROOMS)
    // ==========================================
    @GetMapping("/cinemas")
    public String listCinemas(Model model) {
        model.addAttribute("cinemas", cinemaService.findAll());
        model.addAttribute("cinema", new Cinema());
        return "admin/cinema-manage";
    }

    @PostMapping("/cinemas/save")
    public String saveCinema(@ModelAttribute Cinema cinema) {
        cinemaService.save(cinema);
        return "redirect:/admin/cinemas";
    }

    @GetMapping("/cinemas/{cinemaId}/rooms")
    public String listRooms(@PathVariable Long cinemaId, Model model) {
        Cinema cinema = cinemaService.findById(cinemaId);
        if (cinema == null) return "redirect:/admin/cinemas";

        model.addAttribute("cinema", cinema);
        model.addAttribute("rooms", roomService.findByCinemaId(cinemaId));
        model.addAttribute("room", new Room()); // Để Modal thêm phòng có object bám vào
        return "admin/room-list";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(@ModelAttribute Room room, @RequestParam Long cinemaId) {
        Cinema cinema = cinemaService.findById(cinemaId);
        room.setCinema(cinema);
        roomService.save(room);
        return "redirect:/admin/cinemas/" + cinemaId + "/rooms";
    }

    @GetMapping("/rooms/edit/{id}")
    @ResponseBody
    public RoomDTO getRoomToEdit(@PathVariable Long id) {
        Room room = roomService.findById(id);
        return new RoomDTO(room.getId(), room.getName());
    }

    @GetMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        Room room = roomService.findById(id);
        Long cinemaId = room.getCinema().getId();
        roomService.delete(id);
        return "redirect:/admin/cinemas/" + cinemaId + "/rooms";
    }

    // ==========================================
    // QUẢN LÝ GHẾ (SEATS SETUP)
    // ==========================================
    @GetMapping("/rooms/{roomId}/seats/setup")
    public String setupSeats(@PathVariable Long roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "admin/seat-setup";
    }

    @PostMapping("/rooms/{roomId}/seats/save")
    @ResponseBody
    public String saveSeats(@PathVariable Long roomId, @RequestBody List<SeatDTO> seatData) {
        seatService.saveAllSeatsForRoom(roomId, seatData);
        return "Success";
    }

    // ==========================================
    // QUẢN LÝ SUẤT CHIẾU (SHOWTIMES)

    @GetMapping("/showtimes")
    public String listShowtimes(Model model) {
        model.addAttribute("showtimes", showtimeService.findAll());
        // SỬA TẠI ĐÂY: Phải trả về trang QUẢN LÝ (manage), không phải trang FORM
        return "admin/showtime-manage";
    }

    @GetMapping("/showtimes/add")
    public String showAddShowtimeForm(Model model) {
        model.addAttribute("showtime", new Showtime());
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        return "admin/showtime-form";
    }

    @GetMapping("/showtimes/edit/{id}")
    public String editShowtimeForm(@PathVariable Long id, Model model) {
        Showtime showtime = showtimeService.findById(id);
        if (showtime == null) return "redirect:/admin/showtimes?error=notfound";

        model.addAttribute("showtime", showtime);
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        model.addAttribute("rooms", roomService.findByCinemaId(showtime.getCinema().getId()));

        return "admin/showtime-form";
    }

    @PostMapping("/showtimes/save")
    public String saveShowtime(@ModelAttribute Showtime showtime) {
        showtimeService.save(showtime);
        return "redirect:/admin/showtimes";
    }

    @GetMapping("/showtimes/delete/{id}")
    public String deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return "redirect:/admin/showtimes";
    }

    // API phục vụ AJAX lấy phòng theo rạp
    @GetMapping("/api/rooms/{cinemaId}")
    @ResponseBody
    public List<RoomDTO> getRoomsByCinema(@PathVariable Long cinemaId) {
        return roomService.findByCinemaId(cinemaId).stream()
                .map(room -> new RoomDTO(room.getId(), room.getName()))
                .collect(Collectors.toList());
    }
}