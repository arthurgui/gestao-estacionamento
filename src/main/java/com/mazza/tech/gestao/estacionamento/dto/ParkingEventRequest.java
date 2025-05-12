package com.mazza.tech.gestao.estacionamento.dto;

import lombok.Data;

@Data
public class ParkingEventRequest {
    private String licensePlate;
    private String eventType;
    private Long spotId;
    private Long sectorId;
    private String category; 
}