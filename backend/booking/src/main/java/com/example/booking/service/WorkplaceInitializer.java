package com.example.booking.service;

import com.example.booking.entity.Workplace;
import com.example.booking.repository.WorkplaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkplaceInitializer implements CommandLineRunner {
    private final WorkplaceRepository workplaceRepo;

    public WorkplaceInitializer(WorkplaceRepository workplaceRepo) {
        this.workplaceRepo = workplaceRepo;
    }

    @Override
    public void run(String... args) {
        if (workplaceRepo.count() == 0) {
            List<Workplace> workplaces = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Workplace workplace = new Workplace();
                workplace.setBooked(false);
                workplaces.add(workplace);
            }
            workplaceRepo.saveAll(workplaces);
        }
    }
}