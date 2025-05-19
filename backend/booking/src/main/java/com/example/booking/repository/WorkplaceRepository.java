package com.example.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booking.entity.Workplace;



@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Long>{
    
}
