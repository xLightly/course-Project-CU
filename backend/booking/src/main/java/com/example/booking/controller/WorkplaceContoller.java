package com.example.booking.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.booking.dto.BookingRequestDto;
import com.example.booking.interceptor.RequireRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.dto.WorkplaceResponseDto;
import com.example.booking.entity.Workplace;
import com.example.booking.service.WorkplaceService;
import com.example.grpc.auth.AuthValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplace")
public class WorkplaceContoller { //Это киллер фича - контоллер
    private final WorkplaceService workplaceService;
    private final AuthValidator authValidator;


    @GetMapping("/{id}")
    public ResponseEntity<WorkplaceResponseDto> getWorkplace(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        WorkplaceResponseDto workplaceDto = convertEntityToDto(workplaceService.GetWorkplace(id));
        return new ResponseEntity<>(workplaceDto, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<WorkplaceResponseDto>> getAllWorkplace(@RequestHeader("Authorization") String authHeader) {
        List<WorkplaceResponseDto> workplaceDtos = new ArrayList<>();
        for(Workplace workplace:workplaceService.GetAllWorkplace()){
            WorkplaceResponseDto workplaceDto = convertEntityToDto(workplace);
            workplaceDtos.add(workplaceDto);
        }
        return new ResponseEntity<>(workplaceDtos, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateWorkplace(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @PathVariable boolean isBooked) {
        workplaceService.UpdateWorkplace(id, Boolean.valueOf(isBooked));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    
    private WorkplaceResponseDto convertEntityToDto(Workplace entity){
        WorkplaceResponseDto dto = new WorkplaceResponseDto();
        dto.setId(entity.getId());
        dto.setBooked(entity.isBooked());
        return dto;
    }

}
