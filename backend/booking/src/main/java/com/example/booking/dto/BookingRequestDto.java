package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDto {
    @NotBlank
    private String title;
    private String description;

    @NotNull
    private long workplaceId;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")
    private String startTime;

    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")
    private String endTime;
}
