package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientOrderHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetClientOrderHistoryUseCase {

    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;

    @Transactional(readOnly = true)
    public List<ClientOrderHistoryResponse> execute(UUID clienteId, AuthenticatedUser caller) {
        clienteRepository.findByIdAndComedorId(clienteId, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Cliente", clienteId));

        return ordenRepository
                .findByClienteIdAndComedorIdOrderByCreatedAtDesc(clienteId, caller.comedorId())
                .stream()
                .map(o -> ClientOrderHistoryResponse.builder()
                        .orderNumber(o.getOrderNumber())
                        .status(o.getStatus())
                        .total(o.getTotalAmount())
                        .createdAt(o.getCreatedAt())
                        .build())
                .toList();
    }
}
