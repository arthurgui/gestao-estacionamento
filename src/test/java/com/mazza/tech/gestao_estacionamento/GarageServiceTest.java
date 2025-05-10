package com.mazza.tech.gestao_estacionamento;

import com.mazza.tech.gestao.estacionamento.dto.ParkingEventRequest;
import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.entity.ParkingSpot;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.repository.ParkingSpotRepository;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GarageServiceTest {

    @Mock
    private ParkingSpotRepository spotRepository;

    @Mock
    private ParkingEventRepository eventRepository;

    @InjectMocks
    private GarageService garageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateDynamicPriceLowOccupancy() {
        GarageSector sector = new GarageSector();
        sector.setBasePrice(new BigDecimal("10.00"));

        ParkingSpot spot = new ParkingSpot();
        spot.setOccupied(false);
        sector.setSpots(java.util.List.of(spot));

        BigDecimal dynamicPrice = garageService.calculateDynamicPrice(sector);
        assertEquals(new BigDecimal("9.00"), dynamicPrice); // 10% de desconto
    }

    @Test
    void testCalculateDynamicPriceHighOccupancy() {
        GarageSector sector = new GarageSector();
        sector.setBasePrice(new BigDecimal("10.00"));

        ParkingSpot spot = new ParkingSpot();
        spot.setOccupied(true);
        sector.setSpots(java.util.List.of(spot));

        BigDecimal dynamicPrice = garageService.calculateDynamicPrice(sector);
        assertEquals(new BigDecimal("12.50"), dynamicPrice); // 25% de aumento
    }

    @Test
    void testProcessParkingEventEntry() {
        ParkingEventRequest request = new ParkingEventRequest();
        request.setLicensePlate("XYZ-1234");
        request.setEventType("entry");

        ParkingSpot spot = new ParkingSpot();
        spot.setOccupied(false);

    
       // when(spotRepository.findFirstByOccupiedFalse()).thenReturn(Optional.of(spot));

       
        String result = garageService.processParkingEvent(request);

        assertEquals("Vehicle event processed", result);
        assertTrue(spot.isOccupied()); 
    }

    @Test
    void testProcessParkingEventExit() {
        ParkingEventRequest request = new ParkingEventRequest();
        request.setLicensePlate("XYZ-1234");
        request.setEventType("exit");

        ParkingSpot spot = new ParkingSpot();
        spot.setOccupied(true);

       
       // when(spotRepository.findByLicensePlate("XYZ-1234")).thenReturn(Optional.of(spot));

       
        String result = garageService.processParkingEvent(request);

        
        assertEquals("Vehicle event processed", result);
        assertFalse(spot.isOccupied()); 
    }

    @Test
    void testProcessParkingEventNoSpotAvailable() {
        ParkingEventRequest request = new ParkingEventRequest();
        request.setLicensePlate("XYZ-1234");
        request.setEventType("entry");

      
     //   when(spotRepository.findFirstByOccupiedFalse()).thenReturn(Optional.empty());

        
        String result = garageService.processParkingEvent(request);

        assertEquals("No available parking spot", result); 
    }

    @Test
    void testProcessParkingEventNotFound() {
        ParkingEventRequest request = new ParkingEventRequest();
        request.setLicensePlate("XYZ-1234");
        request.setEventType("exit");

       
       // when(eventRepository.findByLicensePlateAndEventType("XYZ-1234", "exit"))
              //  .thenReturn(Optional.empty());

        
        String result = garageService.processParkingEvent(request);

        assertEquals("Parking event not found", result); 
    }

}
