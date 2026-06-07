package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {

    List<ClienteEntity> findByComedorIdOrderByFullNameAsc(UUID comedorId);

    @Query("""
            SELECT c FROM ClienteEntity c
            WHERE c.comedorId = :comedorId
              AND (LOWER(c.fullName)     LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.email)        LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY c.fullName ASC
            """)
    List<ClienteEntity> search(@Param("comedorId") UUID comedorId, @Param("q") String q);

    Optional<ClienteEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    boolean existsByComedorIdAndEmailIgnoreCase(UUID comedorId, String email);

    boolean existsByComedorIdAndEmailIgnoreCaseAndIdNot(UUID comedorId, String email, UUID id);

    boolean existsByComedorIdAndCustomerCode(UUID comedorId, String customerCode);
}
