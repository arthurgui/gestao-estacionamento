package com.mazza.tech.gestao.estacionamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEventDTO {
    private String type;
    private String plate;
    private String timestamp;
    private String gateId;
    private String spotId;
}
