package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.GrupoExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrupoExtraRepository extends JpaRepository<GrupoExtraEntity, UUID> {

    List<GrupoExtraEntity> findByComedorIdOrderByNameAsc(UUID comedorId);

    Optional<GrupoExtraEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    boolean existsByComedorIdAndNameIgnoreCase(UUID comedorId, String name);

    boolean existsByComedorIdAndNameIgnoreCaseAndIdNot(UUID comedorId, String name, UUID id);
}
