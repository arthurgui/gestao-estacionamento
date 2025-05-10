package com.mazza.tech.gestao.estacionamento.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;

@Service
public class GarageService {

    private final ParkingEventRepository parkingEventRepository;

    public GarageService(ParkingEventRepository parkingEventRepository) {
        this.parkingEventRepository = parkingEventRepository;
    }

    // Método para calcular a taxa com base na duração do estacionamento
    public BigDecimal calculateFee(String licensePlate) {
        ParkingEvent event = parkingEventRepository.findByLicensePlate(licensePlate);
        if (event != null) {
            int parkedDurationInMinutes = event.calculateParkedDuration();
            // Definir o valor da taxa  R$1 por minuto de estacionamento
            BigDecimal fee = new BigDecimal(parkedDurationInMinutes).multiply(new BigDecimal(1));
            return fee;
        }
        return BigDecimal.ZERO;
    }
}