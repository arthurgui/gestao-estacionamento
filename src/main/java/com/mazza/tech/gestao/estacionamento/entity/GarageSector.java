package com.mazza.tech.gestao.estacionamento.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class GarageSector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal basePrice;
    private int maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;

    
	@OneToMany(mappedBy = "sector")
    private List<ParkingSpot> spots;
	
	
}

