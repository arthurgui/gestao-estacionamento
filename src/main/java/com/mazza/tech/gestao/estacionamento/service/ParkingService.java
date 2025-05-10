package com.mazza.tech.gestao.estacionamento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazza.tech.gestao.estacionamento.dto.WebhookEventDTO;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingSpotRepository spotRepository;
    private final ParkingEventRepository eventRepository;

    public void handleEntry(WebhookEventDTO event) {
        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate(event.getPlate());
        parkingEvent.setEntryTime(LocalDateTime.now());
        eventRepository.save(parkingEvent);
    }

    public void handleParked(WebhookEventDTO event) {
        Optional<ParkingEvent> optional = eventRepository.findTopByLicensePlateOrderByIdDesc(event.getPlate());
        if (optional.isPresent()) {
            ParkingEvent parking = optional.get();
            ParkingSpot spot = spotRepository.findById(Long.valueOf(event.getSpotId()))
                .orElseThrow(() -> new IllegalArgumentException("Spot not found"));

            spot.setOccupied(true);
            spotRepository.save(spot);

            parking.setParkedTime(LocalDateTime.now());
            parking.setSpot(spot);
            eventRepository.save(parking);
        }
    }

    public void handleExit(WebhookEventDTO event) {
        Optional<ParkingEvent> optional = eventRepository.findTopByLicensePlateOrderByIdDesc(event.getPlate());
        if (optional.isPresent()) {
            ParkingEvent parking = optional.get();
            parking.setExitTime(LocalDateTime.now());

            if (parking.getParkedTime() == null) {
                throw new IllegalStateException("Vehicle was never parked");
            }

            long minutes = Duration.between(parking.getParkedTime(), parking.getExitTime()).toMinutes();
            GarageSector sector = parking.getSpot().getSector();

            BigDecimal finalPrice = calculateDynamicPrice(sector, minutes);
            parking.setTotalValue(finalPrice);

            parking.getSpot().setOccupied(false);
            spotRepository.save(parking.getSpot());

            eventRepository.save(parking);
        }
    }

    private BigDecimal calculateDynamicPrice(GarageSector sector, long minutes) {
        long totalSpots = sector.getSpots().size();
        long occupiedSpots = sector.getSpots().stream()
        	    .filter(spot -> Boolean.TRUE.equals(spot.isOccupied()))
        	    .count();
        BigDecimal occupancy = BigDecimal.valueOf(occupiedSpots)
                .divide(BigDecimal.valueOf(totalSpots), 2, RoundingMode.HALF_UP);

        BigDecimal multiplier;
        if (occupancy.compareTo(new BigDecimal("0.25")) < 0) {
            multiplier = new BigDecimal("0.90");
        } else if (occupancy.compareTo(new BigDecimal("0.50")) < 0) {
            multiplier = BigDecimal.ONE;
        } else if (occupancy.compareTo(new BigDecimal("0.75")) < 0) {
            multiplier = new BigDecimal("1.10");
        } else {
            multiplier = new BigDecimal("1.25");
        }

        BigDecimal hours = BigDecimal.valueOf(minutes).divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
        return sector.getBasePrice().multiply(multiplier).multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }
}