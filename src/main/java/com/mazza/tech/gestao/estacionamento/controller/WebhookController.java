package com.mazza.tech.gestao.estacionamento.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazza.tech.gestao.estacionamento.dto.WebhookEventDTO;
import com.mazza.tech.gestao.estacionamento.service.ParkingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<Void> handleEvent(@RequestBody WebhookEventDTO event) {
        switch (event.getType()) {
            case "ENTRY":
                parkingService.handleEntry(event);
                break;
            case "PARKED":
                parkingService.handleParked(event);
                break;
            case "EXIT":
                parkingService.handleExit(event);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
