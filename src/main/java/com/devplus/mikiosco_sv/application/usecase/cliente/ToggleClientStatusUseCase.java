package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleClientStatusUseCase {

    private final ClienteRepository clienteRepository;
    private final ListClientsUseCase listClientsUseCase;

    @Transactional
    public ClientResponse execute(UUID id, AuthenticatedUser caller) {
        var cliente = clienteRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Cliente", id));

        GenericStatus newStatus = cliente.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;

        cliente.setStatus(newStatus);
        return listClientsUseCase.toResponse(clienteRepository.save(cliente));
    }
}
