package com.mazza.tech.gestao.estacionamento.entity;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class GarageSector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double basePrice;
    private int maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Integer getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(Integer maxCapacity) {
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

	public Integer getDurationLimitMinutes() {
		return durationLimitMinutes;
	}

	public void setDurationLimitMinutes(Integer durationLimitMinutes) {
		this.durationLimitMinutes = durationLimitMinutes;
	}

	public List<ParkingSpot> getSpots() {
		return spots;
	}

	public void setSpots(List<ParkingSpot> spots) {
		this.spots = spots;
	}

	@OneToMany(mappedBy = "sector")
    private List<ParkingSpot> spots;
}

