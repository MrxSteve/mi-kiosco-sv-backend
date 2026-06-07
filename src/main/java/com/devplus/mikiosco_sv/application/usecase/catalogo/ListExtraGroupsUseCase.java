package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.infrastructure.persistence.repository.GrupoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListExtraGroupsUseCase {

    private final GrupoExtraRepository grupoExtraRepository;

    @Transactional(readOnly = true)
    public List<ExtraGroupResponse> execute(AuthenticatedUser caller) {
        return grupoExtraRepository.findByComedorIdOrderByNameAsc(caller.comedorId())
                .stream()
                .map(g -> ExtraGroupResponse.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .description(g.getDescription())
                        .status(g.getStatus())
                        .createdAt(g.getCreatedAt())
                        .updatedAt(g.getUpdatedAt())
                        .build())
                .toList();
    }
}
