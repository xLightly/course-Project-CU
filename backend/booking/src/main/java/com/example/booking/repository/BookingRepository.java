package com.example.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Workplace;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT w FROM Workplace w " +
            "WHERE NOT EXISTS (" +
            "  SELECT b FROM Booking b " +
            "  WHERE b.workplace = w " +
            "  AND b.startTime < :endTime " +
            "  AND b.endTime > :startTime" +
            ")")
    List<Workplace> findAllByAvailableTime(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    List<Booking>findAllByUserId(@Param("userId") Long userId);
    @Query("SELECT DISTINCT b.workplace FROM Booking b WHERE b.startTime <= :now AND b.endTime >= :now")
    List<Workplace> findAllWorkplacesWithBookingAtTime(@Param("now") LocalDateTime now);

}
