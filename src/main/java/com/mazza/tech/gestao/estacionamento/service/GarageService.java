package com.mazza.tech.gestao.estacionamento.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mazza.tech.gestao.estacionamento.dto.ParkingEventRequest;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.repository.GarageSectorRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final ParkingEventRepository eventRepository;
    private final GarageSectorRepository sectorRepository;
    private final ParkingSpotRepository spotRepository;

    // Processa o evento de entrada de veículo
    @Transactional
    public void processEntry(String licensePlate) {
        // Verifica se o setor está fechado
        if (isSectorFull()) {
            throw new RuntimeException("Garage sector is full, cannot accept new vehicles until one leaves.");
        }

        ParkingSpot availableSpot = spotRepository.findFirstByOccupiedFalse();
        if (availableSpot == null) {
            throw new RuntimeException("No available parking spots.");
        }

        GarageSector sector = availableSpot.getSector();
        BigDecimal price = calculateDynamicPrice(sector);

        ParkingEvent event = new ParkingEvent();
        event.setLicensePlate(licensePlate);
        event.setEventType("entry");
        event.setEntryTime(LocalDateTime.now());
        event.setSector(sector);
        event.setTotalValue(price);

        availableSpot.setOccupied(true);

        spotRepository.save(availableSpot);
        eventRepository.save(event);
    }

    // Processa o evento de saída de veículo
    @Transactional
    public void processExit(String licensePlate) {
        ParkingEvent event = eventRepository.findByLicensePlateAndEventType(licensePlate, "entry");
        if (event == null) {
            throw new RuntimeException("Vehicle not found.");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        event.setExitTime(exitTime);

        Duration duration = Duration.between(event.getEntryTime(), exitTime);
        BigDecimal total = calculateFee(event.getSector(), duration);

        event.setTotalValue(total);
        event.setEventType("exit");

        // Libera a vaga
        ParkingSpot spot = spotRepository.findBySectorAndOccupiedTrue(event.getSector()).stream()
            .filter(ParkingSpot::isOccupied)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Occupied spot not found."));
        spot.setOccupied(false);

        eventRepository.save(event);
        spotRepository.save(spot);
    }

    // Calcula a taxa com base no tempo de permanência
    public BigDecimal calculateFee(GarageSector sector, Duration duration) {
        BigDecimal hours = BigDecimal.valueOf(Math.ceil(duration.toMinutes() / 60.0));
        return sector.getBasePrice().multiply(hours);
    }

    // Calcula o preço dinâmico com base na ocupação
    public BigDecimal calculateDynamicPrice(GarageSector sector) {
        List<ParkingSpot> spots = sector.getSpots();
        if (spots == null || spots.isEmpty()) {
            return sector.getBasePrice();
        }

        long total = spots.size();
        long occupied = spots.stream().filter(s -> Boolean.TRUE.equals(s.isOccupied())).count();
        double occupancyRate = (double) occupied / total;

        BigDecimal base = sector.getBasePrice();
        BigDecimal adjustment;

        if (occupancyRate < 0.25) {
            adjustment = base.multiply(BigDecimal.valueOf(-0.10)); // 10% desconto
        } else if (occupancyRate <= 0.50) {
            adjustment = BigDecimal.ZERO; // Preço normal
        } else if (occupancyRate <= 0.75) {
            adjustment = base.multiply(BigDecimal.valueOf(0.10)); // 10% aumento
        } else {
            adjustment = base.multiply(BigDecimal.valueOf(0.25)); // 25% aumento
        }

        return base.add(adjustment);
    }

    // Processa o evento de estacionamento (entrada ou saída)
    public String processParkingEvent(ParkingEventRequest request) {
        
        if (isSectorFull()) {
            return "Garage sector is full, cannot accept new vehicles until one leaves.";
        }

        // Processa o evento dependendo do tipo (entrada ou saída)
        if ("entry".equals(request.getEventType())) {
            processEntry(request.getLicensePlate());
            return "Vehicle parked successfully.";
        } else if ("exit".equals(request.getEventType())) {
            processExit(request.getLicensePlate());
            return "Vehicle exited successfully.";
        } else {
            return "Invalid event type.";
        }
    }

    // Verifica se o setor está 100% ocupado
    private boolean isSectorFull() {
        List<ParkingSpot> spots = spotRepository.findAll(); 
        long occupied = spots.stream().filter(ParkingSpot::isOccupied).count(); 
        return occupied == spots.size(); 
    }
}