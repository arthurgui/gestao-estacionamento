package com.mazza.tech.gestao.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Sensor {
    @Id @GeneratedValue
    private Long id;

    private String spotCode;
    private boolean vehicleDetected;
}