package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.GrupoExtraEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateExtraGroupRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExtraGroupUseCase {

    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional
    public ExtraGroupResponse execute(CreateExtraGroupRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (grupoExtraRepository.existsByComedorIdAndNameIgnoreCase(comedorId, request.getName())) {
            throw new ConflictException("Ya existe un grupo de extras con ese nombre");
        }

        var entity = grupoExtraRepository.save(GrupoExtraEntity.builder()
                .comedorId(comedorId)
                .name(request.getName())
                .description(request.getDescription())
                .build());

        return ExtraGroupResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
