package com.mazza.tech.gestao.estacionamento.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mazza.tech.gestao.estacionamento.dto.ParkingEventRequest;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.entity.Vehicle;
import com.mazza.tech.gestao.estacionamento.entity.VehicleCategory;
import com.mazza.tech.gestao.estacionamento.repository.GarageSectorRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;
import com.mazza.tech.gestao.estacionamento.repository.VehicleRepository;

@Service
public class GarageService {

    @Autowired
    private final ParkingEventRepository eventRepository;
    @Autowired
    private final GarageSectorRepository sectorRepository;
    private final ParkingSpotRepository spotRepository;
    private final ParkingEventRepository parkingEventRepository;
    private final VehicleRepository vehicleRepository;
    

    private boolean garageOpen = false;

    // Mapa para armazenar veículos ativos na garagem
    private final Map<String, Vehicle> activeVehicles = new ConcurrentHashMap<>();
    private static final int MAX_CAPACITY = 100; 
  

    public GarageService(ParkingEventRepository eventRepository, ParkingSpotRepository spotRepository,
            ParkingEventRepository parkingEventRepository, VehicleRepository vehicleRepository) {
        this.eventRepository = eventRepository;
        this.sectorRepository = null;
        this.parkingEventRepository = parkingEventRepository;
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // Processa a entrada do veículo
    @Transactional
    public void processEntry(ParkingEventRequest request) {
        if (request == null || request.getLicensePlate() == null || request.getLicensePlate().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "License plate is required.");
        }

        if (isSectorFull()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Garage sector is full, cannot accept new vehicles until one leaves.");
        }

        if (activeVehicles.size() >= MAX_CAPACITY) {
            throw new IllegalStateException("Garage sector is full, cannot accept new vehicles until one leaves.");
        }

        String licensePlate = request.getLicensePlate();

        // Cria ou atualiza o veículo
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setCategory(VehicleCategory.CAR);
        vehicle.setMake("Marca Padrão");
        vehicle.setModel("Modelo Padrão");
        vehicle.setYear(2020);
        vehicleRepository.save(vehicle);

        activeVehicles.put(licensePlate, vehicle);

        ParkingSpot availableSpot = spotRepository.findFirstByOccupiedFalse();
        if (availableSpot == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available parking spots.");
        }

        updateParkingSpotStatus(availableSpot, true);
        triggerGate(true);

        GarageSector sector = availableSpot.getSector();
        BigDecimal price = calculateDynamicPrice(sector);

        ParkingEvent event = new ParkingEvent();
        event.setLicensePlate(licensePlate);
        event.setEventType("entry");
        event.setEntryTime(LocalDateTime.now());
        event.setSector(sector);
        event.setTotalValue(price);
        eventRepository.save(event);
    }

    // Processa a saída do veículo por placa
    @Transactional
    public void processExitByLicensePlate(String licensePlate) {
        ParkingEvent event = eventRepository.findByLicensePlateAndEventType(licensePlate, "entry");
        if (event == null) {
            throw new RuntimeException("Vehicle not found.");
        }

        ParkingSpot spot = spotRepository.findBySectorAndOccupiedTrue(event.getSector()).stream()
                .filter(ParkingSpot::isOccupied).findFirst()
                .orElseThrow(() -> new RuntimeException("Occupied spot not found."));
        updateParkingSpotStatus(spot, false);
        triggerGate(false);

        LocalDateTime exitTime = LocalDateTime.now();
        event.setExitTime(exitTime);
        Duration duration = Duration.between(event.getEntryTime(), exitTime);
        BigDecimal total = calculateFee(event.getSector(), duration);
        event.setTotalValue(total);
        event.setEventType("exit");

        eventRepository.save(event);
        spotRepository.save(spot);
    }

    // Atualiza o status de ocupação da vaga
    private void updateParkingSpotStatus(ParkingSpot spot, boolean occupied) {
        spot.setOccupied(occupied);
        spotRepository.save(spot);
    }

    // Simula acionamento da cancela
    private void triggerGate(boolean open) {
        if (open) {
            System.out.println("Gate opened for entry.");
        } else {
            System.out.println("Gate opened for exit.");
        }
    }

    // Calcula taxa baseada na duração
    public BigDecimal calculateFee(GarageSector sector, Duration duration) {
        BigDecimal hours = BigDecimal.valueOf(Math.ceil(duration.toMinutes() / 60.0));
        return sector.getBasePrice().multiply(hours);
    }

    // Calcula valor dinâmico com base na ocupação
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
            adjustment = base.multiply(BigDecimal.valueOf(-0.10));
        } else if (occupancyRate <= 0.50) {
            adjustment = BigDecimal.ZERO;
        } else if (occupancyRate <= 0.75) {
            adjustment = base.multiply(BigDecimal.valueOf(0.10));
        } else {
            adjustment = base.multiply(BigDecimal.valueOf(0.25));
        }

        return base.add(adjustment);
    }

    // Verifica se o setor está cheio
    private boolean isSectorFull() {
        List<ParkingSpot> spots = spotRepository.findAll();

        spots.forEach(spot -> {
            System.out.println("Spot ID: " + spot.getId() + " | Ocupada? " + spot.isOccupied());
        });

        long occupied = spots.stream().filter(ParkingSpot::isOccupied).count();

        System.out.println("Vagas ocupadas: " + occupied);
        System.out.println("Total de vagas: " + spots.size());

        return occupied == spots.size();
    }

    // Processa saída pela vaga (ex: sensores)
    public void processExitBySpotId(ParkingEventRequest request) {
        ParkingSpot spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        spot.setOccupied(false);
        spotRepository.save(spot);
    }

    // Lista todos os eventos de entrada
    public List<ParkingEvent> getAllEntries() {
        return parkingEventRepository.findByEventType("ENTRY");
    }

    public void openGarage() {
        this.garageOpen = true;
        triggerAllGates(true);
    }

    public boolean isGarageOpen() {
        return this.garageOpen;
    }

    private void triggerAllGates(boolean open) {
        System.out.println("Triggering gates. Open: " + open);
    }

    public void addVehicleToGarage(String licensePlate, String category) {
        if (activeVehicles.size() >= MAX_CAPACITY) {
            throw new IllegalStateException("Garage sector is full, cannot accept new vehicles until one leaves.");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setCategory(VehicleCategory.valueOf(category.toUpperCase()));
        vehicle.setMake("Manual");
        vehicle.setModel("Manual");
        vehicle.setYear(2022);
        vehicleRepository.save(vehicle);
        activeVehicles.put(licensePlate, vehicle);
    }

    // Remove um veículo da garagem
    public void removeVehicleFromGarage(String licensePlate) {
        activeVehicles.remove(licensePlate);
    }

    // Lista todos os veículos ativos na garagem
    public List<Vehicle> listAllVehicles() {
        return new ArrayList<>(activeVehicles.values());
    }

    public String processParkingEvent(ParkingEventRequest request) {
        if ("entry".equalsIgnoreCase(request.getEventType())) {
            processEntry(request);
            return "Entry processed successfully.";
        } else if ("exit".equalsIgnoreCase(request.getEventType())) {
            processExitByLicensePlate(request.getLicensePlate());
            return "Exit processed successfully.";
        } else {
            return "Invalid event type.";
        }
    }
}