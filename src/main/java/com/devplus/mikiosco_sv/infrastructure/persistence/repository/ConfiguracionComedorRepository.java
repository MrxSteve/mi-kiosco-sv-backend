package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ConfiguracionComedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracionComedorRepository extends JpaRepository<ConfiguracionComedorEntity, UUID> {

    Optional<ConfiguracionComedorEntity> findByComedor_Id(UUID comedorId);
}
