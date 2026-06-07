package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetClientUseCase {

    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;

    @Transactional(readOnly = true)
    public ClientDetailResponse execute(UUID id, AuthenticatedUser caller) {
        var cliente = clienteRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Cliente", id));

        List<OrdenEntity> ordenes = ordenRepository
                .findByClienteIdAndComedorIdOrderByCreatedAtDesc(id, caller.comedorId());

        BigDecimal totalSpent = ordenes.stream()
                .map(OrdenEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var lastOrderAt = ordenes.stream()
                .map(OrdenEntity::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return ClientDetailResponse.builder()
                .id(cliente.getId())
                .fullName(cliente.getFullName())
                .email(cliente.getEmail())
                .phone(cliente.getPhone())
                .address(cliente.getAddress())
                .customerCode(cliente.getCustomerCode())
                .notes(cliente.getNotes())
                .status(cliente.getStatus())
                .orderCount(ordenes.size())
                .totalSpent(totalSpent)
                .lastOrderAt(lastOrderAt)
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }
}
