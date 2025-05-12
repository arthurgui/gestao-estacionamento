package com.mazza.tech.gestao.estacionamento.controller;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazza.tech.gestao.estacionamento.dto.ParkingEventRequest;
import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.Vehicle;
import com.mazza.tech.gestao.estacionamento.entity.VehicleEvent;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/garage")
@Tag(name = "Garage API", description = "Operações de controle da garagem")
public class GarageController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GarageController.class);

	@Autowired
	private ProducerTemplate producerTemplate;

	@Autowired
	private final GarageService garageService;

	private final ParkingEventRepository eventRepository;

	@Autowired
	public GarageController(GarageService garageService, ParkingEventRepository eventRepository) {
		this.garageService = garageService;
		this.eventRepository = eventRepository;

	}

	@PostMapping("/open")
	public ResponseEntity<String> openGarage() {
		garageService.openGarage();
		return ResponseEntity.ok("Garage is now open and ready to accept vehicles.");
	}

	@PostMapping("/entry")
	public ResponseEntity<String> entry(@RequestBody ParkingEventRequest request) {
		garageService.addVehicleToGarage(request.getLicensePlate(), request.getCategory());
		return ResponseEntity.ok("Vehicle entry recorded successfully.");
	}
	
	   // Endpoint para registrar a saída do veículo
    @PostMapping("/exit")
    public ResponseEntity<String> exit(@RequestBody ParkingEventRequest request) {
        garageService.removeVehicleFromGarage(request.getLicensePlate());
        return ResponseEntity.ok("Vehicle exit recorded successfully.");
    }

    // Endpoint para listar todos os veículos ativos na garagem
    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> listVehicles() {
        List<Vehicle> vehicles = garageService.listAllVehicles();
        return ResponseEntity.ok(vehicles);
    }


	@PostMapping
	public void receiveVehicleEvent(@RequestBody VehicleEvent event) {
		log.info("Recebido evento individual de veículo: {} - {}", event.getEventType(), event.getLicensePlate());
		producerTemplate.sendBody("direct:processVehicleEvent", event);
	}

	@PostMapping("/event")
	public String processParkingEvent(@RequestBody ParkingEventRequest request) {
	    return garageService.processParkingEvent(request);
	}

	@PostMapping("/batch")
	public void receiveVehicleEvents(@RequestBody List<VehicleEvent> events) {
		log.info("Recebido lote de eventos de veículos: {} eventos", events.size());
		producerTemplate.sendBody("direct:processBatchVehicleEvents", events);
	}

	@GetMapping("/vehicles/active")
	public ResponseEntity<List<ParkingEvent>> getActiveVehicles() {
		List<ParkingEvent> activeVehicles = eventRepository.findByEventType("entry");
		return ResponseEntity.ok(activeVehicles);
	}

}