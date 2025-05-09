package com.mazza.tech.gestao.estacionamento.service;

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
            ParkingSpot spot = spotRepository.findById(Long.valueOf(event.getSpotId())).orElseThrow();

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

            long minutes = Duration.between(parking.getParkedTime(), parking.getExitTime()).toMinutes();
            GarageSector sector = parking.getSpot().getSector();

            double finalPrice = calculateDynamicPrice(sector, minutes);
            parking.setTotalValue(finalPrice);

            parking.getSpot().setOccupied(false);
            spotRepository.save(parking.getSpot());

            eventRepository.save(parking);
        }
    }

    private double calculateDynamicPrice(GarageSector sector, long minutes) {
        double lotacao = sector.getSpots().stream().filter(ParkingSpot::getOccupied).count() * 1.0 / sector.getMaxCapacity();
        double price = sector.getBasePrice();

        if (lotacao < 0.25) price *= 0.9;
        else if (lotacao < 0.50) price *= 1.0;
        else if (lotacao < 0.75) price *= 1.1;
        else price *= 1.25;

        return (price * (minutes / 60.0)); 
    }
}
