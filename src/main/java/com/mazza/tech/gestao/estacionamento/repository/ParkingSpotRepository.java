package com.mazza.tech.gestao.estacionamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    ParkingSpot findFirstByOccupiedFalse();
    List<ParkingSpot> findBySectorAndOccupiedTrue(GarageSector sector);
}