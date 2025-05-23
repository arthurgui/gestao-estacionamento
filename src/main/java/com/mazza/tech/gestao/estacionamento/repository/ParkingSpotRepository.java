package com.mazza.tech.gestao.estacionamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    ParkingSpot findFirstByOccupiedFalse();

    List<ParkingSpot> findBySectorAndOccupiedTrue(GarageSector sector);

    
    Optional<ParkingSpot> findFirstBySectorIdAndOccupiedFalse(Long sectorId);

  
    @Query("SELECT p FROM ParkingSpot p WHERE p.occupied = false AND p.sector.id = :sectorId")
    Optional<ParkingSpot> findAvailableSpotInSector(@Param("sectorId") Long sectorId);
}