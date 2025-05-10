package com.mazza.tech.gestao.estacionamento.controller;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazza.tech.gestao.estacionamento.dto.ParkingEventRequest;
import com.mazza.tech.gestao.estacionamento.entity.VehicleEvent;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/garage")
@Tag(name = "Garage API", description = "Operações de controle da garagem")
public class GarageController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GarageController.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    private final GarageService garageService;
    
    @Autowired
    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }
    

    @PostMapping("/open")
    public ResponseEntity<String> openGarage() {
        garageService.openGarage();
        return ResponseEntity.ok("Garage is now open and ready to accept vehicles.");
    }
    
    @PostMapping("/entry")
    public ResponseEntity<String> entry(@RequestBody ParkingEventRequest request) {
        request.setEventType("entry"); 
        String response = garageService.processParkingEvent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exit(@RequestBody ParkingEventRequest request) {
        request.setEventType("exit"); 
        String response = garageService.processParkingEvent(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public void receiveVehicleEvent(@RequestBody VehicleEvent event) {
        log.info("Recebido evento individual de veículo: {} - {}", event.getEventType(), event.getLicensePlate());
        producerTemplate.sendBody("direct:processVehicleEvent", event);
    }
    
    @PostMapping("/event")
    public ResponseEntity<Void> handleEvent(@RequestBody ParkingEventRequest request) {
        garageService.processParkingEvent(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    public void receiveVehicleEvents(@RequestBody List<VehicleEvent> events) {
        log.info("Recebido lote de eventos de veículos: {} eventos", events.size());
        producerTemplate.sendBody("direct:processBatchVehicleEvents", events);
    }
}