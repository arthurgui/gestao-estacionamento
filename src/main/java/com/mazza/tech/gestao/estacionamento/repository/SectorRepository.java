package com.mazza.tech.gestao.estacionamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.Sector;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {}
