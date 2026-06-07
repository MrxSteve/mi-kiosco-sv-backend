package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<PagoEntity, UUID> {

    Optional<PagoEntity> findByOrdenId(UUID ordenId);

    List<PagoEntity> findByOrdenIdIn(List<UUID> ordenIds);

    boolean existsByOrdenId(UUID ordenId);
}
