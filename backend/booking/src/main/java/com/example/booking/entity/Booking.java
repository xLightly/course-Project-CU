package com.example.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "booking")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String userLogin;

    @NotNull
    private String title;
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    

    @ManyToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;
}
