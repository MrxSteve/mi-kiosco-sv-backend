package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.specification.OrdenSpecification;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchOrdersUseCase {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final OrderAssembler assembler;

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> execute(
            Long orderNumber,
            String q,
            Boolean anonymous,
            OrderStatus status,
            Boolean hasPromotion,
            Boolean hasDiscount,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            AuthenticatedUser caller) {

        var comedorId = caller.comedorId();
        var spec = Specification.where(OrdenSpecification.hasComedor(comedorId));

        if (orderNumber != null) {
            spec = spec.and(OrdenSpecification.hasOrderNumber(orderNumber));
        }

        if (status != null) {
            spec = spec.and(OrdenSpecification.hasStatus(status));
        }

        if (Boolean.TRUE.equals(hasPromotion)) {
            spec = spec.and(OrdenSpecification.hasPromotion());
        }

        if (Boolean.TRUE.equals(hasDiscount)) {
            spec = spec.and(OrdenSpecification.hasDiscount());
        }

        if (Boolean.TRUE.equals(anonymous)) {
            spec = spec.and(OrdenSpecification.isAnonymous());
        } else if (q != null && !q.isBlank()) {
            // Buscar en nombre libre Y en clientes registrados que coincidan
            List<UUID> clienteIds = clienteRepository.search(comedorId, q.trim())
                    .stream()
                    .map(c -> c.getId())
                    .toList();

            Specification<com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity> qSpec =
                    Specification.where(OrdenSpecification.customerNameLike(q.trim()));

            if (!clienteIds.isEmpty()) {
                qSpec = qSpec.or(OrdenSpecification.clienteIdIn(clienteIds));
            }
            spec = spec.and(qSpec);
        }

        if (startDate != null) {
            spec = spec.and(OrdenSpecification.createdAfter(
                    startDate.atStartOfDay().atOffset(ZoneOffset.UTC)));
        }
        if (endDate != null) {
            spec = spec.and(OrdenSpecification.createdBefore(
                    endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC)));
        }

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var pageResult = ordenRepository.findAll(spec, pageable);
        var responses = assembler.assembleList(pageResult.getContent());

        return PageResponse.<OrderResponse>builder()
                .content(responses)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }
}
