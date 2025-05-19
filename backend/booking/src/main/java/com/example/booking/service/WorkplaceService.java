package com.example.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.booking.entity.Workplace;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.WorkplaceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WorkplaceService {
    private final WorkplaceRepository workplaceRepo;
    private final BookingRepository bookingRepo;

    public void CreateWorkplaces(int count) {
        if (count<=0){
            throw new IllegalArgumentException("Minimum 1 workplace are required");
        }
        List<Workplace> workplaces = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Workplace workplace = new Workplace();
            workplace.setBooked(false);
            workplaces.add(workplace);
        }
        workplaceRepo.saveAll(workplaces);
    }

    public Workplace GetWorkplace(Long id){
        return workplaceRepo.findById(id).orElseThrow(()-> new IllegalArgumentException("Workspace not found"));
    }

    public List<Workplace> GetAllWorkplace(){
        return workplaceRepo.findAll();
    }

    public List<Workplace> GetWorkplacesByAvailableTime(LocalDateTime startTime, LocalDateTime endTime){
        return bookingRepo.findAllByAvailableTime(startTime, endTime);
    }

    public void UpdateWorkplace(Long id, Boolean isBooked){
        Workplace workspace = workplaceRepo.findById(id).orElseThrow(()-> new IllegalArgumentException("Workspace not found"));
        workspace.setBooked(isBooked);
    }

    public void DeleteWorkplace(Long id){
        workplaceRepo.deleteById(id);
    }
    @Scheduled(fixedRate = 300000)
    public void updateWorkplacesBasedOnCurrentTime() {
        LocalDateTime now = LocalDateTime.now();

        List<Workplace> workplaces = bookingRepo.findAllWorkplacesWithBookingAtTime(now);

        for (Workplace wp : workplaces) {
            wp.setBooked(true);
        }

        workplaceRepo.saveAll(workplaces);
    }

}
