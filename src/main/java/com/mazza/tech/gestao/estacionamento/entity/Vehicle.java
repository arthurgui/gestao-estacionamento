package com.mazza.tech.gestao.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
	    private String category;
	    
	    // Default constructor for JPA
	    public Vehicle() {}

	    // Constructor to initialize Vehicle with licensePlate and make
	    public Vehicle(String licensePlate, String make) {
	        this.licensePlate = licensePlate;
	        this.make = make;
	    }
}
