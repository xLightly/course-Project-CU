package com.example.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingResponseDto {
    private long id;
    private long userId;
    private String userLogin;
    private String title;
    private String description;
    private long workplaceId;
    private String startTime;
    private String endTime;

}
