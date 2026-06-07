package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleExtraStatusUseCase {

    private final ExtraRepository extraRepository;
    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional
    public ExtraResponse execute(UUID id, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var extra = extraRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Extra", id));

        GenericStatus newStatus = extra.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;
        extra.setStatus(newStatus);

        var saved = extraRepository.save(extra);

        String groupName = null;
        if (saved.getGrupoId() != null) {
            groupName = grupoExtraRepository.findById(saved.getGrupoId())
                    .map(g -> g.getName()).orElse(null);
        }

        return ExtraResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .groupId(saved.getGrupoId())
                .groupName(groupName)
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
