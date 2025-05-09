package com.mazza.tech.gestao.estacionamento.util;

public class PriceCalculator {
    public static double calcular(double base, double occupancyRate) {
        if (occupancyRate >= 0.9) return base * 1.5;
        if (occupancyRate >= 0.8) return base * 1.25;
        return base;
    }
}
