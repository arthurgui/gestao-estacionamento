package com.mazza.tech.gestao.estacionamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mazza.tech.gestao.estacionamento.entity.Revenue;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {}