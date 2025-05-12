package com.mazza.tech.gestao.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Entity
public class Vehicle {
	
	    @Id  
	    private String licensePlate;

	    private String make;
	    private String model;
	    private int year; 
	    @Enumerated(EnumType.STRING)
	    private VehicleCategory category;
	    
	    
	    public Vehicle() {}

	
	    public Vehicle(String licensePlate, String category) {
	        this.licensePlate = licensePlate;
	        this.category = VehicleCategory.valueOf(category.toUpperCase()); 
	    }

}
