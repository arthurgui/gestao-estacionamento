package com.mazza.tech.gestao.estacionamento.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GarageSector {
    @Id
    private String sector;

    private double basePrice;
    private int maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private int durationLimitMinutes;
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public LocalTime getOpenHour() {
		return openHour;
	}
	public void setOpenHour(LocalTime openHour) {
		this.openHour = openHour;
	}
	public LocalTime getCloseHour() {
		return closeHour;
	}
	public void setCloseHour(LocalTime closeHour) {
		this.closeHour = closeHour;
	}
	public int getDurationLimitMinutes() {
		return durationLimitMinutes;
	}
	public void setDurationLimitMinutes(int durationLimitMinutes) {
		this.durationLimitMinutes = durationLimitMinutes;
	}

  
}
