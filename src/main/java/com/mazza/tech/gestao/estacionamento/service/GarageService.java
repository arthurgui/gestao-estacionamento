package com.mazza.tech.gestao.estacionamento.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private final ParkingEventRepository eventRepository;
	@Autowired
	private final GarageSectorRepository sectorRepository;
	private final ParkingSpotRepository spotRepository;

	private boolean garageOpen = false;

	@Autowired
	public GarageService(ParkingEventRepository eventRepository, ParkingSpotRepository spotRepository) {
		this.eventRepository = eventRepository;
		this.sectorRepository = null;
		this.spotRepository = spotRepository;
	}

	// Processa a entrada do veículo
	@Transactional
	public void processEntry(String licensePlate) {
		// Verifica se o setor está cheio
		if (isSectorFull()) {
			throw new RuntimeException("Garage sector is full, cannot accept new vehicles until one leaves.");
		}

		// Obtém uma vaga disponível
		ParkingSpot availableSpot = spotRepository.findFirstByOccupiedFalse();
		if (availableSpot == null) {
			throw new RuntimeException("No available parking spots.");
		}

		// Atualiza o sensor de presença da vaga
		updateParkingSpotStatus(availableSpot, true);

		// Aciona a cancela para permitir a entrada
		triggerGate(true); // true = abre a cancela para entrada

		// Cria e salva o evento de estacionamento
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

	// Processa a saída do veículo
	@Transactional
	public void processExit(String licensePlate) {
		// Encontra o evento de entrada associado ao veículo
		ParkingEvent event = eventRepository.findByLicensePlateAndEventType(licensePlate, "entry");
		if (event == null) {
			throw new RuntimeException("Vehicle not found.");
		}

		// Atualiza o sensor de presença da vaga
		ParkingSpot spot = spotRepository.findBySectorAndOccupiedTrue(event.getSector()).stream()
				.filter(ParkingSpot::isOccupied).findFirst()
				.orElseThrow(() -> new RuntimeException("Occupied spot not found."));
		updateParkingSpotStatus(spot, false);

		// Aciona a cancela para permitir a saída
		triggerGate(false); // false = abre a cancela para saída

		// Atualiza o evento de saída e calcula a taxa
		LocalDateTime exitTime = LocalDateTime.now();
		event.setExitTime(exitTime);
		Duration duration = Duration.between(event.getEntryTime(), exitTime);
		BigDecimal total = calculateFee(event.getSector(), duration);
		event.setTotalValue(total);
		event.setEventType("exit");

		// Salva as alterações no evento e na vaga
		eventRepository.save(event);
		spotRepository.save(spot);
	}

	// Atualiza o status de ocupação da vaga
	private void updateParkingSpotStatus(ParkingSpot spot, boolean occupied) {
		spot.setOccupied(occupied);
		spotRepository.save(spot);
	}

	// Lógica para acionar a cancela (simulação)
	private void triggerGate(boolean open) {
		// Aqui você colocaria a lógica para acionar a cancela física
		// Isso pode envolver uma integração com um hardware ou uma API externa
		if (open) {
			System.out.println("Gate opened for entry.");
		} else {
			System.out.println("Gate opened for exit.");
		}
	}

	// Calcula a taxa do estacionamento com base na duração
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
			adjustment = base.multiply(BigDecimal.valueOf(-0.10)); // 10% de desconto
		} else if (occupancyRate <= 0.50) {
			adjustment = BigDecimal.ZERO; // Sem ajuste
		} else if (occupancyRate <= 0.75) {
			adjustment = base.multiply(BigDecimal.valueOf(0.10)); // Aumento de 10%
		} else {
			adjustment = base.multiply(BigDecimal.valueOf(0.25)); // Aumento de 25%
		}

		return base.add(adjustment);
	}

	// verificar se o setor está cheio
	private boolean isSectorFull() {
		List<ParkingSpot> spots = spotRepository.findAll();
		long occupied = spots.stream().filter(ParkingSpot::isOccupied).count();
		return occupied == spots.size();
	}

	// regra de liberar a cancela e sensores
	public void openGarage() {
		this.garageOpen = true;
		triggerAllGates(true); // Abrir todas as cancelas.
	}

	public boolean isGarageOpen() {
		return this.garageOpen;
	}

	// regra para as cancelas podendo haver varias cancelas para abrir
	private void triggerAllGates(boolean open) {
		System.out.println("Triggering gates. Open: " + open);
	}

	public String processParkingEvent(ParkingEventRequest request) {
		if ("entry".equalsIgnoreCase(request.getEventType())) {
			processEntry(request.getLicensePlate()); // void
			return "Entry processed successfully.";
		} else if ("exit".equalsIgnoreCase(request.getEventType())) {
			processExit(request.getLicensePlate()); // void
			return "Exit processed successfully.";
		} else {
			return "Invalid event type.";
		}
	}

//    public String processParkingEvent(ParkingEventRequest request) {
//        if ("entry".equalsIgnoreCase(request.getEventType())) {
//            processEntry(request.getLicensePlate());
//            return "Entry processed successfully.";
//        } else if ("exit".equalsIgnoreCase(request.getEventType())) {
//            processExit(request.getLicensePlate());
//            return "Exit processed successfully.";
//        } else {
//            return "Invalid event type.";
//        }
//    }
}
