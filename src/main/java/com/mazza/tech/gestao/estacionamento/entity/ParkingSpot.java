package com.mazza.tech.gestao.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ParkingSpot {
    @Id @GeneratedValue
    private Long id;
    private Double lat;
    private Double lng;
    private Boolean occupied;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Boolean getOccupied() {
		return occupied;
	}

	public void setOccupied(Boolean occupied) {
		this.occupied = occupied;
	}

	public GarageSector getSector() {
		return sector;
	}

	public void setSector(GarageSector sector) {
		this.sector = sector;
	}

	@ManyToOne
    private GarageSector sector;
}

