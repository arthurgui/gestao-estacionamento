package com.mazza.tech.gestao.estacionamento.controller;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazza.tech.gestao.estacionamento.entity.VehicleEvent;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/garage")
@Tag(name = "Garage API", description = "Operações de controle da garagem")
public class GarageController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GarageController.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public void receiveVehicleEvent(@RequestBody VehicleEvent event) {
        log.info("Recebido evento individual de veículo: {} - {}", event.getEventType(), event.getLicensePlate());
        producerTemplate.sendBody("direct:processVehicleEvent", event);
    }

    @PostMapping("/batch")
    public void receiveVehicleEvents(@RequestBody List<VehicleEvent> events) {
        log.info("Recebido lote de eventos de veículos: {} eventos", events.size());
        producerTemplate.sendBody("direct:processBatchVehicleEvents", events);
    }
}