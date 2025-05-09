package com.mazza.tech.gestao.estacionamento.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ParkingEvent {
    @Id @GeneratedValue
    private Long id;
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime parkedTime;
    private LocalDateTime exitTime;
    private double totalValue;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public LocalDateTime getParkedTime() {
		return parkedTime;
	}

	public void setParkedTime(LocalDateTime parkedTime) {
		this.parkedTime = parkedTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public ParkingSpot getSpot() {
		return spot;
	}

	public void setSpot(ParkingSpot spot) {
		this.spot = spot;
	}
	public double getTotalValue() {
	    return totalValue;
	}

	public void setTotalValue(double totalValue) {
	    this.totalValue = totalValue;
	}

	@ManyToOne
    private ParkingSpot spot;
}