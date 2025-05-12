package com.mazza.tech.gestao.estacionamento.dto;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import lombok.Data;

@Data
public class SpotDTO {
    private Double lat;
    private Double lng;
    private BigDecimal price;  // Preço da vaga
    private LocalTime availableFrom;  // Horário de início de disponibilidade
    private LocalTime availableTo;  // Horário de término de disponibilidade
    private Duration maxDuration;
}
