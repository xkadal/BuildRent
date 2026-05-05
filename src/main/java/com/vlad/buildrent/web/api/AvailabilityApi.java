package com.vlad.buildrent.web.api;

import com.vlad.buildrent.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityApi {

    private final AvailabilityService availabilityService;

    @GetMapping
    public Map<String, Object> check(
            @RequestParam Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        int available = availabilityService.availableQuantity(equipmentId, start, end);
        int days = AvailabilityService.daysBetween(start, end);
        return Map.of("available", available, "days", days);
    }
}
