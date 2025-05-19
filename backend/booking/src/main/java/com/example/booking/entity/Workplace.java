package com.example.booking.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workplace")
@AllArgsConstructor
public class Workplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private boolean isBooked = false;
    @OneToMany(mappedBy = "workplace", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public Workplace(){
        this.bookings = new ArrayList<>();
    }
}
