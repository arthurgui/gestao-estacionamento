package com.mazza.tech.gestao.estacionamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazza.tech.gestao.estacionamento.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
