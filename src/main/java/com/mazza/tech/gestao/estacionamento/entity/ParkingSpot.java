package com.mazza.tech.gestao.estacionamento.entity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Data
@Entity
public class ParkingSpot {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Double lat;
	    private Double lng;
	    private boolean occupied;

	    @ManyToOne
	    @JoinColumn(name = "sector_id")
	    private GarageSector sector;

	    private BigDecimal price;  // Preço da vaga
	    private LocalTime availableFrom;  // Horário de início de disponibilidade
	    private LocalTime availableTo;  // Horário de término de disponibilidade
	    private Duration maxDuration;
}

