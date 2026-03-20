package com.hutech.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để dễ test, nhưng lưu ý khi deploy thực tế

                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép truy cập công khai các tài nguyên tĩnh và trang chủ
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**" // Quan trọng: Để hiển thị ảnh poster phim
                        ).permitAll()

                        // 2. Cho phép MỌI NGƯỜI xem chi tiết phim và lịch chiếu (CGV Style)
                        .requestMatchers("/movies/**").permitAll()
                        .requestMatchers("/showtimes/**").permitAll()

                        // 3. Chỉ ADMIN mới được vào trang quản lý
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        // 4. Các thao tác đặt vé, chọn ghế phải ĐĂNG NHẬP
                        // Lưu ý: Dùng hasAnyAuthority để cho phép cả ADMIN và USER đều đặt được vé
                        .requestMatchers("/booking/**").hasAnyAuthority("USER", "ADMIN")

                        // 5. Tất cả các request khác phải xác thực
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}