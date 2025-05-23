package com.mazza.tech.gestao.estacionamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;

@Repository
public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {

    Optional<ParkingEvent> findTopByLicensePlateOrderByIdDesc(String licensePlate);
    ParkingEvent findByLicensePlate(String licensePlate); //consulta baseado na placa do veículo
    ParkingEvent findByLicensePlateAndEventType(String licensePlate, String eventType);
    List<ParkingEvent> findByEventType(String eventType);
    List<ParkingSpot> findBySector_Id(Long sectorId);
    Optional<ParkingEvent> findTopByLicensePlateAndExitTimeIsNullOrderByIdDesc(String licensePlate);

}