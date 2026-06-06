package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ComedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComedorRepository extends JpaRepository<ComedorEntity, UUID> {

    Optional<ComedorEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<ComedorEntity> findAllByStatus(GenericStatus status);
}
