package com.example.booking.controller;


import com.example.booking.dto.BookingRequestDto;
import com.example.booking.dto.BookingResponseDto;
import com.example.booking.entity.Booking;
import com.example.booking.interceptor.RequireRole;
import com.example.booking.service.BookingService;
import com.example.booking.service.WorkplaceService;
import com.example.grpc.auth.AuthServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final WorkplaceService workplaceService;
    private final BookingService bookingService;
    private final AuthServiceClient authServiceClient;

    @PostMapping("/workplace/{count}")
    public ResponseEntity<Void> createWorkplace(@PathVariable Integer count) {
        workplaceService.CreateWorkplaces(count);
        return ResponseEntity.ok().build();
    }

    @RequireRole("admin")
    @DeleteMapping("/workplace/{id}")
    public ResponseEntity<Void> deleteWorkplace(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Count должен быть положительным числом");
        }
        workplaceService.DeleteWorkplace(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequireRole("admin")
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getBookings(@RequestHeader("Authorization") String authHeader){
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponseDto> bookingResponseDtos = new ArrayList<>();
        for(Booking booking: bookings){
            bookingResponseDtos.add(convertEntityToResponseDto(booking));
        }
        return new ResponseEntity<>(bookingResponseDtos, HttpStatus.OK);
    }

    @RequireRole("admin")
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequireRole("admin")
    @PostMapping("/promoteToAdmin/{login}")
    public ResponseEntity<Void> promoteToAdmin(@RequestHeader("Authorization") String authHeader, @PathVariable String login){
        authServiceClient.PromoteToAdmin(login);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @RequireRole("admin")
    @GetMapping("/users")
    public ResponseEntity<List<String>> getUsers(@RequestHeader("Authorization") String authHeader){
        List<String> logins = authServiceClient.GetUsers();
        return new ResponseEntity<>(logins, HttpStatus.OK);
    }


    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAdmin(HttpServletRequest request, @RequestHeader("Authorization") String authHeader){
        String role = (String) request.getAttribute("role");
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        return ResponseEntity.ok(isAdmin);
    }
    private BookingResponseDto convertEntityToResponseDto(Booking entity){
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(entity.getId());
        dto.setWorkplaceId(entity.getWorkplace().getId());
        dto.setUserId(entity.getUserId());
        dto.setUserLogin(entity.getUserLogin());
        String startTime = entity.getStartTime().toString();
        String endTime = entity.getEndTime().toString();
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private Booking convertDtoToEntity(BookingRequestDto dto, long userId, String userLogin){
        Booking entity = new Booking();
        entity.setWorkplace(workplaceService.GetWorkplace(dto.getWorkplaceId()));
        entity.setUserId(userId);
        entity.setUserLogin(userLogin);
        LocalDateTime startTime = LocalDateTime.parse(dto.getStartTime());
        LocalDateTime endTime = LocalDateTime.parse(dto.getEndTime());
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
