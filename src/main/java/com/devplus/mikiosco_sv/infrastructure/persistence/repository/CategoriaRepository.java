package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, UUID> {

    List<CategoriaEntity> findByComedorIdOrderByDisplayOrderAscNameAsc(UUID comedorId);

    List<CategoriaEntity> findByComedorIdAndStatusOrderByDisplayOrderAscNameAsc(UUID comedorId, GenericStatus status);

    boolean existsByComedorIdAndNameIgnoreCase(UUID comedorId, String name);

    boolean existsByComedorIdAndNameIgnoreCaseAndIdNot(UUID comedorId, String name, UUID id);

    Optional<CategoriaEntity> findByIdAndComedorId(UUID id, UUID comedorId);
}
