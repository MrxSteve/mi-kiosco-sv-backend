package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ExtraEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateExtraRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExtraUseCase {

    private final ExtraRepository extraRepository;
    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional
    public ExtraResponse execute(CreateExtraRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (extraRepository.existsByComedorIdAndNameIgnoreCase(comedorId, request.getName())) {
            throw new ConflictException("Ya existe un extra con ese nombre");
        }

        String groupName = null;
        if (request.getGroupId() != null) {
            var grupo = grupoExtraRepository.findByIdAndComedorId(request.getGroupId(), comedorId)
                    .orElseThrow(() -> NotFoundException.of("Grupo de extras", request.getGroupId()));
            groupName = grupo.getName();
        }

        var entity = extraRepository.save(ExtraEntity.builder()
                .comedorId(comedorId)
                .grupoId(request.getGroupId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build());

        return ExtraResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .groupId(entity.getGrupoId())
                .groupName(groupName)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
