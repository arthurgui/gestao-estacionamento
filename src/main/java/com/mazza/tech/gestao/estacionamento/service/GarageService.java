package com.mazza.tech.gestao.estacionamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mazza.tech.gestao.estacionamento.entity.GarageSector;
import com.mazza.tech.gestao.estacionamento.repository.GarageSectorRepository;

@Service
public class GarageService {

    @Autowired
    private GarageSectorRepository garageSectorRepository;

    public List<GarageSector> listarSetores() {
        return garageSectorRepository.findAll();
    }

    public GarageSector salvar(GarageSector sector) {
        return garageSectorRepository.save(sector);
    }
}