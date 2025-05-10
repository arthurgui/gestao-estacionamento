package com.mazza.tech.gestao.estacionamento.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.service.GarageService;

@RestController
@RequestMapping("/garage")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @GetMapping
    public List<GarageSector> listarSetores() {
        return garageService.listarSetores();
    }
}