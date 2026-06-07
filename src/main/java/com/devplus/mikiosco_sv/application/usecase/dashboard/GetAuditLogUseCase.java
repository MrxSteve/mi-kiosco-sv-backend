package com.devplus.mikiosco_sv.application.usecase.dashboard;

import com.devplus.mikiosco_sv.domain.model.AuditAction;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.AuditoriaLogRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.AuditLogResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAuditLogUseCase {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> execute(AuditAction action, int page, int size,
                                                  AuthenticatedUser caller) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var comedorId = caller.comedorId();

        var pageResult = action != null
                ? auditoriaLogRepository.findByComedorIdAndActionOrderByCreatedAtDesc(
                        comedorId, action, pageable)
                : auditoriaLogRepository.findByComedorIdOrderByCreatedAtDesc(comedorId, pageable);

        var content = pageResult.getContent().stream()
                .map(log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .entityName(log.getEntityName())
                        .entityId(log.getEntityId())
                        .detail(log.getDetail())
                        .userId(log.getUserId())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<AuditLogResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }
}
