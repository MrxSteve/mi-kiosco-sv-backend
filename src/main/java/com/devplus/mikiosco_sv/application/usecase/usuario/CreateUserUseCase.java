package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.ForbiddenException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateUserRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import com.devplus.mikiosco_sv.presentation.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final ComedorRepository comedorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(CreateUserRequest request, AuthenticatedUser caller) {
        if (request.getRole() == UserRole.SUPER_ADMIN) {
            throw new ForbiddenException("No se puede asignar el rol super_admin");
        }

        UUID comedorId = resolveComedorId(request, caller);

        if (usuarioRepository.existsByEmailAndComedor_Id(request.getEmail(), comedorId)) {
            throw new ConflictException("El email ya está registrado en este comedor");
        }

        var comedor = comedorRepository.findById(comedorId)
                .orElseThrow(() -> NotFoundException.of("Comedor", comedorId));

        var usuario = UsuarioEntity.builder()
                .comedor(comedor)
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(GenericStatus.ACTIVE)
                .build();

        return userMapper.toResponse(usuarioRepository.save(usuario));
    }

    private UUID resolveComedorId(CreateUserRequest request, AuthenticatedUser caller) {
        if (caller.isSuperAdmin()) {
            if (request.getComedorId() == null) {
                throw new BadRequestException("Debe especificar comedorId al crear usuarios como super_admin");
            }
            return request.getComedorId();
        }
        return caller.comedorId();
    }
}
