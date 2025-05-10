package com.mazza.tech.gestao.estacionamento.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mazza.tech.gestao.estacionamento.dto.GarageDTO;
import com.mazza.tech.gestao.estacionamento.dto.SpotDTO;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.repository.GarageSectorRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarageImportService {

	private final RestTemplate restTemplate;
	private final GarageSectorRepository sectorRepository;
	private final ParkingSpotRepository spotRepository;

	@Value("${garage.api.url}")
	private String garageApiUrl;

	private static final Logger log = LoggerFactory.getLogger(GarageImportService.class);

	public void importGarageData() {
		ResponseEntity<GarageDTO[]> response = restTemplate.getForEntity(garageApiUrl + "/garage", GarageDTO[].class);
		GarageDTO[] garages = response.getBody();

		if (garages != null) {
			for (GarageDTO dto : garages) {
				
				GarageSector existingSector = sectorRepository.findByName(dto.getName()).orElse(null);
				GarageSector sector = existingSector != null ? existingSector : new GarageSector();

				sector.setName(dto.getName());
				sector.setBasePrice(dto.getBasePrice());
				sector.setMaxCapacity(dto.getSpots().size());
				sectorRepository.save(sector);

				for (SpotDTO spotDTO : dto.getSpots()) {
					ParkingSpot spot = new ParkingSpot();
					spot.setLat(spotDTO.getLat());
					spot.setLng(spotDTO.getLng());
					spot.setOccupied(false);
					spot.setPrice(spotDTO.getPrice());
					spot.setAvailableFrom(spotDTO.getAvailableFrom());
					spot.setAvailableTo(spotDTO.getAvailableTo());
					spot.setMaxDuration(spotDTO.getMaxDuration());
					spot.setSector(sector);
					spotRepository.save(spot);
				}

				log.info("Dados da garagem '{}' importados com sucesso!", dto.getName());
			}
		}
	}
}
