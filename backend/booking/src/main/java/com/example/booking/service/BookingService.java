package com.example.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.booking.entity.Booking;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.WorkplaceRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepo;
    private final WorkplaceRepository workspaceRepo;

    public void createBooking(Booking booking){
        bookingRepo.save(booking);
    }

    public Booking getBooking(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Count должен быть положительным числом");
        }
        return bookingRepo.findById(id).orElseThrow(()-> new IllegalArgumentException("booking not found"));
    }

    public List<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    public void deleteBooking(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID бронирования некорректен");
        }
        bookingRepo.deleteById(id);
    }

    public List<Booking> GetMyWorkplaces(Long userId){
        return bookingRepo.findAllByUserId(userId);
    }

}
