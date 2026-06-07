package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateClientRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClienteRepository clienteRepository;
    private final ListClientsUseCase listClientsUseCase;

    @Transactional
    public ClientResponse execute(UUID id, UpdateClientRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var cliente = clienteRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Cliente", id));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (clienteRepository.existsByComedorIdAndEmailIgnoreCaseAndIdNot(comedorId, request.getEmail(), id)) {
                throw new ConflictException("Ya existe un cliente registrado con ese email");
            }
            cliente.setEmail(request.getEmail().toLowerCase());
        }
        if (request.getFullName() != null) cliente.setFullName(request.getFullName());
        if (request.getPhone() != null)    cliente.setPhone(request.getPhone());
        if (request.getAddress() != null)  cliente.setAddress(request.getAddress());
        if (request.getNotes() != null)    cliente.setNotes(request.getNotes());

        return listClientsUseCase.toResponse(clienteRepository.save(cliente));
    }
}
