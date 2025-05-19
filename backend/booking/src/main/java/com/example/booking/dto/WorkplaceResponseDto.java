package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkplaceResponseDto {
    @NotNull
    private long id;
    @NotBlank
    private boolean isBooked;
}
