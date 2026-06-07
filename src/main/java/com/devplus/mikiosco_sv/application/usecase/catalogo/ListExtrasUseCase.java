package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ExtraEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListExtrasUseCase {

    private final ExtraRepository extraRepository;
    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional(readOnly = true)
    public List<ExtraResponse> execute(UUID groupId, boolean onlyActive, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        List<ExtraEntity> extras;
        if (groupId != null) {
            extras = extraRepository.findByComedorIdAndGrupoIdOrderByNameAsc(comedorId, groupId);
        } else if (onlyActive) {
            extras = extraRepository.findByComedorIdAndStatusOrderByNameAsc(comedorId, GenericStatus.ACTIVE);
        } else {
            extras = extraRepository.findByComedorIdOrderByNameAsc(comedorId);
        }

        // Cargar nombres de grupos en un solo query
        Set<UUID> grupoIds = extras.stream()
                .map(ExtraEntity::getGrupoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> grupoNames = grupoIds.isEmpty()
                ? Map.of()
                : grupoExtraRepository.findAllById(grupoIds).stream()
                        .collect(Collectors.toMap(g -> g.getId(), g -> g.getName()));

        return extras.stream()
                .map(e -> ExtraResponse.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .description(e.getDescription())
                        .price(e.getPrice())
                        .groupId(e.getGrupoId())
                        .groupName(e.getGrupoId() == null ? null : grupoNames.get(e.getGrupoId()))
                        .status(e.getStatus())
                        .createdAt(e.getCreatedAt())
                        .updatedAt(e.getUpdatedAt())
                        .build())
                .toList();
    }
}
