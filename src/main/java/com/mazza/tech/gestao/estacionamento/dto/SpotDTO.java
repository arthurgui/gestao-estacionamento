package com.mazza.tech.gestao.estacionamento.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.Duration;

@Data
public class SpotDTO {
    private Double lat;
    private Double lng;
    private BigDecimal price;  // Preço da vaga
    private LocalTime availableFrom;  // Horário de início de disponibilidade
    private LocalTime availableTo;  // Horário de término de disponibilidade
    private Duration maxDuration;
}
