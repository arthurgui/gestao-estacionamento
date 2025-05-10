package com.mazza.tech.gestao.estacionamento.dto;

import lombok.Data;

@Data
public class ParkingEventRequest {
    private String licensePlate;
    private String eventType; // "entry" ou "exit"
}