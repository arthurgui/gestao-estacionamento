package com.mazza.tech.gestao.estacionamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.GarageSector;

@Repository
public interface GarageSectorRepository extends JpaRepository<GarageSector, String> {}





