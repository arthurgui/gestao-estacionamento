package com.mazza.tech.gestao.estacionamento.service;

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

    public void importGarageData() {
        ResponseEntity<GarageDTO[]> response = restTemplate.getForEntity(garageApiUrl + "/garage", GarageDTO[].class);
        GarageDTO[] garages = response.getBody();

        if (garages != null) {
            for (GarageDTO dto : garages) {
                GarageSector sector = new GarageSector();
                sector.setName(dto.getName());
                sector.setBasePrice(dto.getBasePrice());
                sector.setMaxCapacity(dto.getSpots().size());
                sectorRepository.save(sector);

                for (SpotDTO spotDTO : dto.getSpots()) {
                    ParkingSpot spot = new ParkingSpot();
                    spot.setLat(spotDTO.getLat());
                    spot.setLng(spotDTO.getLng());
                    spot.setOccupied(false);
                    spot.setSector(sector);
                    spotRepository.save(spot);
                }
            }
        }
    }
}
