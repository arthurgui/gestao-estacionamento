package com.mazza.tech.gestao_estacionamento.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mazza.tech.gestao.estacionamento.dto.GarageDTO;
import com.mazza.tech.gestao.estacionamento.dto.SpotDTO;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.repository.GarageSectorRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;
import com.mazza.tech.gestao.estacionamento.service.GarageImportService;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

@SpringBootTest
public class GarageImportServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GarageService garageService;
    
    @Mock
    private GarageSectorRepository sectorRepository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private GarageImportService garageImportService;

    @BeforeEach
    void setUp() {
        garageService = new GarageService(null, sectorRepository, spotRepository);
    }
    
    
    @Test
    public void testImportGarageData() {
        SpotDTO spotDTO = new SpotDTO();
        spotDTO.setLat(40.7128);
        spotDTO.setLng(-74.0060);
        spotDTO.setPrice(new BigDecimal("10.00"));
        spotDTO.setAvailableFrom(LocalTime.of(8, 0));
        spotDTO.setAvailableTo(LocalTime.of(18, 0));
        spotDTO.setMaxDuration(Duration.ofHours(4));

        GarageDTO garageDTO = new GarageDTO();
        garageDTO.setName("Garagem Central");
        garageDTO.setBasePrice(new BigDecimal("5.00"));
        garageDTO.setSpots(List.of(spotDTO));

        when(restTemplate.getForEntity(anyString(), eq(GarageDTO[].class)))
            .thenReturn(ResponseEntity.ok(new GarageDTO[]{garageDTO}));
        when(sectorRepository.findByName(anyString())).thenReturn(Optional.empty());

       
        garageImportService.importGarageData();

        
        verify(sectorRepository, times(1)).save(any(GarageSector.class));
        verify(spotRepository, times(1)).save(any(ParkingSpot.class));
    }
    
    @Test
    void testCalculateDynamicPriceLowOccupancy() {
        GarageSector sector = new GarageSector();
        sector.setBasePrice(new BigDecimal("10.00"));

        List<ParkingSpot> spots = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ParkingSpot spot = new ParkingSpot();
            spot.setOccupied(i < 1); // 1 vaga ocupada = 10%
            spots.add(spot);
        }

        sector.setSpots(spots);

        BigDecimal dynamicPrice = garageService.calculateDynamicPrice(sector);
        assertEquals(new BigDecimal("9.00"), dynamicPrice); // 10% de desconto
    }
}
