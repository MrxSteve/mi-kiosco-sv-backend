package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromocionRepository extends JpaRepository<PromocionEntity, UUID> {

    List<PromocionEntity> findByComedorIdOrderByCreatedAtDesc(UUID comedorId);

    Optional<PromocionEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    Optional<PromocionEntity> findByComedorIdAndCodeIgnoreCase(UUID comedorId, String code);

    boolean existsByComedorIdAndCodeIgnoreCase(UUID comedorId, String code);

    boolean existsByComedorIdAndCodeIgnoreCaseAndIdNot(UUID comedorId, String code, UUID id);
}
