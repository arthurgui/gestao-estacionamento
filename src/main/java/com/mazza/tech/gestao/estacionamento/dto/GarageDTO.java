package com.mazza.tech.gestao.estacionamento.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class GarageDTO {
	private String name;
	private BigDecimal basePrice;
	private List<SpotDTO> spots;
}
