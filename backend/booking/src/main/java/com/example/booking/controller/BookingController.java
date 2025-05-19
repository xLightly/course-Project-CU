package com.example.booking.controller;


import com.example.booking.dto.BookingRequestDto;
import com.example.booking.dto.BookingResponseDto;
import com.example.booking.dto.WorkplaceResponseDto;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Workplace;
import com.example.booking.service.BookingService;
import com.example.booking.service.WorkplaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;
    private final WorkplaceService workplaceService;

    @PostMapping
    public ResponseEntity<Void> createBooking(HttpServletRequest request, @RequestHeader("Authorization") String authHeader,
                                                 @RequestBody BookingRequestDto bookingDto) {
        long userId = (long) request.getAttribute("userId");
        String userLogin = (String) request.getAttribute("login");
        Booking booking = convertDtoToEntity(bookingDto, userId, userLogin);
        bookingService.createBooking(booking);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBooking(HttpServletRequest request, @RequestHeader("Authorization") String authHeader, @RequestParam Long id){
        BookingResponseDto bookingResponseDto = convertEntityToResponseDto(bookingService.getBooking(id));
        return new ResponseEntity<>(bookingResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(HttpServletRequest request, @RequestHeader("Authorization") String authHeader){
        long id = (long) request.getAttribute("userId");
        List<Booking> bookings = bookingService.GetMyWorkplaces(id);
        List<BookingResponseDto> bookingResponseDtos = new ArrayList<>();
        for(Booking booking:bookings){
            bookingResponseDtos.add(convertEntityToResponseDto(booking));
        }
        return new ResponseEntity<>(bookingResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(HttpServletRequest request, @RequestHeader("Authorization") String authHeader){
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponseDto> responseDtos = new ArrayList<>();
        for(Booking req: bookings){
            responseDtos.add(convertEntityToResponseDto(req));
        }
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/findByTime/{startTime}/{endTime}")
    public ResponseEntity<List<WorkplaceResponseDto>> getAllByAvailableTime(@RequestHeader("Authorization") String authHeader, @PathVariable String startTime, @PathVariable String endTime){
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            List<Workplace> workplaces = workplaceService.GetWorkplacesByAvailableTime(start, end);
            List<WorkplaceResponseDto> workplaceDtos = new ArrayList<>();
            for (Workplace workplace : workplaces) {
                workplaceDtos.add(convertEntityToDto(workplace));
            }
            return new ResponseEntity<>(workplaceDtos, HttpStatus.OK);
        }
        catch(DateTimeParseException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(HttpServletRequest request, @RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        long userId = (long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("role");
        Booking booking = bookingService.getBooking(id);
        if (booking.getUserId() != userId && !userRole.equalsIgnoreCase("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.OK);
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
    private WorkplaceResponseDto convertEntityToDto(Workplace entity){
        WorkplaceResponseDto dto = new WorkplaceResponseDto();
        dto.setId(entity.getId());
        dto.setBooked(entity.isBooked());
        return dto;
    }
}
