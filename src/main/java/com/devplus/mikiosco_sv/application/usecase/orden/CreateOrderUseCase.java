package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateOrderRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(CreateOrderRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (request.getClienteId() != null) {
            clienteRepository.findByIdAndComedorId(request.getClienteId(), comedorId)
                    .orElseThrow(() -> NotFoundException.of("Cliente", request.getClienteId()));
        }

        var orden = ordenRepository.save(OrdenEntity.builder()
                .comedorId(comedorId)
                .usuarioId(caller.userId())
                .clienteId(request.getClienteId())
                .customerName(request.getCustomerName())
                .notes(request.getNotes())
                .build());

        return assembler.assembleSingle(orden);
    }
}
