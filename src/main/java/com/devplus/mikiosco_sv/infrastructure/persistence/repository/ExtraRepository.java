package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExtraRepository extends JpaRepository<ExtraEntity, UUID> {

    List<ExtraEntity> findByComedorIdOrderByNameAsc(UUID comedorId);

    List<ExtraEntity> findByComedorIdAndStatusOrderByNameAsc(UUID comedorId, GenericStatus status);

    List<ExtraEntity> findByComedorIdAndGrupoIdOrderByNameAsc(UUID comedorId, UUID grupoId);

    List<ExtraEntity> findByIdInAndComedorId(List<UUID> ids, UUID comedorId);

    Optional<ExtraEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    boolean existsByComedorIdAndNameIgnoreCase(UUID comedorId, String name);

    boolean existsByComedorIdAndNameIgnoreCaseAndIdNot(UUID comedorId, String name, UUID id);
}
