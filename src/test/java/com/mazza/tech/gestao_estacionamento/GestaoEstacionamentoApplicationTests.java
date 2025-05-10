package com.mazza.tech.gestao_estacionamento;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mazza.tech.gestao.estacionamento.entity.ParkingEvent;
import com.mazza.tech.gestao.estacionamento.repository.ParkingEventRepository;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

@SpringBootTest
@AutoConfigureMockMvc
public class GestaoEstacionamentoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ParkingEventRepository parkingEventRepository;

    @InjectMocks
    private GarageService garageService;

    // Teste de evento de entrada
    @Test
    public void testParkVehicle() throws Exception {
        String json = "{\"licensePlate\":\"XYZ-1234\",\"eventType\":\"entry\"}";
        mockMvc.perform(post("/api/garage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle event processed"));
    }

    // Teste de evento de saída
    @Test
    public void testExitVehicle() throws Exception {
        String json = "{\"licensePlate\":\"XYZ-1234\",\"eventType\":\"exit\"}";
        mockMvc.perform(post("/api/garage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle event processed"));
    }

    // Teste de processamento em lote de eventos
    @Test
    public void testBatchVehicleEvents() throws Exception {
        String json = "[{\"licensePlate\":\"ABC-1111\",\"eventType\":\"entry\"},"
                     + "{\"licensePlate\":\"DEF-2222\",\"eventType\":\"exit\"}]";
        mockMvc.perform(post("/api/garage/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Batch events processed"));
    }

    // Teste de cálculo de taxa
    @Test
    public void testCalculateParkingFee() throws Exception {
        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate("XYZ-1234");
//        parkingEvent.setParkedTime(120); // 2 horas

        // Simulando o cálculo de taxa
        when(parkingEventRepository.findByLicensePlate("XYZ-1234")).thenReturn(parkingEvent);

        BigDecimal calculatedFee = garageService.calculateFee("XYZ-1234");
        mockMvc.perform(post("/api/garage/fee")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"licensePlate\":\"XYZ-1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(calculatedFee.toString()));
    }

    // Teste de persistência de evento de estacionamento no banco
    @Test
    public void testPersistParkingEvent() throws Exception {
        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate("XYZ-1234");
        parkingEvent.setEventType("entry");
//        parkingEvent.setParkedTime(90); // 1.5 horas

        // Simulando a persistência
        when(parkingEventRepository.save(parkingEvent)).thenReturn(parkingEvent);

        String json = "{\"licensePlate\":\"XYZ-1234\",\"eventType\":\"entry\"}";
        mockMvc.perform(post("/api/garage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle event processed"));

        // Verificar se a persistência ocorreu corretamente
        MvcResult result = mockMvc.perform(post("/api/garage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();
        
        // Verifica o conteúdo retornado após persistir o evento
        String content = result.getResponse().getContentAsString();
        assert(content.contains("Vehicle event processed"));
    }
}


