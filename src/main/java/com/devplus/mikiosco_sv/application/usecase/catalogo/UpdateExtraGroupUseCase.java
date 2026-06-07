package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateExtraGroupRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateExtraGroupUseCase {

    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional
    public ExtraGroupResponse execute(UUID id, UpdateExtraGroupRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var grupo = grupoExtraRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Grupo de extras", id));

        if (request.getName() != null) {
            if (grupoExtraRepository.existsByComedorIdAndNameIgnoreCaseAndIdNot(comedorId, request.getName(), id)) {
                throw new ConflictException("Ya existe un grupo de extras con ese nombre");
            }
            grupo.setName(request.getName());
        }
        if (request.getDescription() != null) grupo.setDescription(request.getDescription());

        var saved = grupoExtraRepository.save(grupo);
        return ExtraGroupResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
