package com.mazza.tech.gestao.estacionamento.dto;

import java.util.List;

import lombok.Data;

@Data
public class GarageDTO {
 private String name;
 private double basePrice;
 private List<SpotDTO> spots;
}
