package com.mazza.tech.gestao.estacionamento.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParkingEvent {
    @Id @GeneratedValue
    private Long id;

    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private LocalDateTime parkedTime;
    
    private String eventType;
    private BigDecimal totalValue;
    
    @ManyToOne
    private ParkingSpot spot;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private GarageSector sector;
   
    
    public BigDecimal calculateFee() {
    	return BigDecimal.valueOf(10.00);
    }
    
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public int calculateParkedDuration() {
        if (entryTime != null && exitTime != null) {
            return (int) java.time.Duration.between(entryTime, exitTime).toMinutes();
        }
        return 0;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
}