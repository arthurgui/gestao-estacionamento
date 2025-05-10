package com.mazza.tech.gestao.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Gate {
    @Id @GeneratedValue
    private Long id;

    private String identifier;
    private boolean isOpen;
    
    @ManyToOne 
    private Sector sector; //para ter mais de uma cancela
}
